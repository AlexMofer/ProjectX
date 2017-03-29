
# Android自动化构建之使用Gradle下载与处理文件
一般情况下，我们的项目构建并不需要再去导入其他文件。但如果自己项目正在维护一个自己的library module，而这个library内又维护着大量会经常更新的SO库，SO库文件很小倒无所谓，但是SO库又多又大时，直接将SO库放入Git中，一个版本更新下来，Git库估计就要炸掉了。而此时，我们利用Gradle的task来执行下载与导入即可解决这一尴尬。  
## Gradle下载插件
Gradle原生并不支持文件下载，使用Gradle命令执行文件下载则需要安装Gradle插件：[Gradle Download Task](https://github.com/michel-kraemer/gradle-download-task)。
插件的安装必须在项目根目录下的build.gradle文件中配置（版本最好参考官方说明）：
```java
plugins {
    id "de.undercouch.download" version "3.2.0"
}
```

## 配置文件删除task
既然涉及到文件更新，那么针对项目情况，显然需要删除旧的文件，示例：
```java
task cleanFile(type: Delete) {
    // 清除SO库文件
    delete 'library/src/main/jniLibs'
    // 清除文件
    delete new File('library/src/main/jniLibs/my.so')
}
```

## 拆分Gradle文件
在根项目或者Module的build.gradle中做配置，会导致Gradle又臭又长又不便于阅读，一修改还需要不停刷新，因此个人推荐，将比较集中的功能，提取到另一个gradle文件，apply到build.gradle中；再将需要经常修改，而跟处理逻辑毫无关联的字段提取到另一个properties文件之中，便于修改与配置。
示例：
build.gradle文件：
```java
apply from: "update.gradle"
```
update.gradle文件：
```java
// 读取update.properties文件
Properties psUpdate = new Properties()
psUpdate.load(project.file("update.properties").newDataInputStream())

def filename = psCore.getProperty("filename")
def filepath = psCore.getProperty("filepath")

task cleanFile(type: Delete) {
    delete new File(filepath, filename)
}
```
update.properties文件
```text
# 文件名
filename=my.so
# 文件路径
filepath=library\\src\\main\\jniLibs
```

 > **注意：** update.properties文件中的路径是双反斜杠

## 配置下载
参照官方指出的方案即可，并无难处。比较复杂的下载方案参考官方文档，以下是取自官方的简单示例：
```java
import de.undercouch.gradle.tasks.download.Download

task downloadFile(type: Download) {
    src 'http://www.example.com/index.html'
    dest buildDir
}
```
**在Android Studio上使用此方案时，存在一个大坑。**如果使用该方案，那么必须写在build.gradle文件之中，而不能写在拆分出来用于apply的gradle文件之中，如果写在其中，会在import处报错，可能是第三方文件导入的不能再次导包吧。于是在我们的方案中就只能选择第二方案：
```java
task downloadFile << {
    download {
        src 'http://www.example.com/index.html'
        dest buildDir
    }
}
```
 > **注意：** 在使用插件以前，必须先apply：`apply plugin: 'de.undercouch.download'`

## 处理下载文件
我们下载的文件，多半是压缩包，如果文件供给者可以支持zip文件，则我们可以直接使用该插件进行下载解压：
```java
task downloadZipFile(type: Download) {
    src 'https://github.com/michel-kraemer/gradle-download-task/archive/1.0.zip'
    dest new File(buildDir, '1.0.zip')
}

task downloadAndUnzipFile(dependsOn: downloadZipFile, type: Copy) {
    from zipTree(downloadZipFile.dest)
    into buildDir
}
```
 > **注意：** 使用dependsOn，即便你的主task写在build.gradle文件中，而依赖的task写在apply的gradle文 件之中也是可行的。但是依赖的task必须在主task之前，也就是你得在你的主task之前先apply含有依赖task的gradle文件。

Gradle原生对压缩包的支持较少，因此很多情况下我们的下载文件是其他格式的文件，比如开源的7z。为了满足这一情况，我们需要使用7z command来处理这些文件。

## 执行CMD来解压更多格式的压缩包
对于打包机而言，我们仅仅需要下载安装[Standalone版本](http://www.7-zip.org/download.html)的7zip。
下载解压后，将7zip的主解压程序所在的目录添加到系统的环境变量的Path条目里。
配置解压task：
```java
// 解压下载文件
task unzipDownloadFile(dependsOn: downloadFile, type: Exec) {
    commandLine 'cmd', '/c', '7za x "%cd%\\download.7z" -y -aoa -o"%cd%\\temp"'
}
```
 > **提示：** 更多详细的7z命令可参照此链接[https://sevenzip.osdn.jp/chm/cmdline/commands/index.htm](https://sevenzip.osdn.jp/chm/cmdline/commands/index.htm)。需要注意的是，解压文件的task需要依赖下载文件的task。

当你需要解压多个文件的时候，或者执行多条语句的时候：
```java
task unzipDownloadFile(dependsOn: downloadFile, type: Exec) {
    commandLine 'cmd', '/c', '7za x "%cd%\\download.7z" -y -aoa -o"%cd%\\temp"'
    doLast {
        exec {
            commandLine 'cmd', '/c', 'echo do more things.'
        }
        exec {
            commandLine 'cmd', '/c', 'echo do more things.'
        }
        exec {
            commandLine 'cmd', '/c', 'echo do more things.'
        }
        ...
    }
}
```
有时我们需要考虑先判断文件是否存在，再去解压文件，你可以考虑使用Gradle来new一个File来判断其是否存在，但这存在一个大问题，在task中判断文件是否存在，这是编译时进行的，而不是运行时进行的。所以这么判断是错误的。因为你文件是从网络或者其他途径临时获取的。因此我们可以通过执行CMD命令来判断文件是否存在：
```java
task unzipDownloadFile(dependsOn: downloadFile, type: Exec) {
    commandLine 'cmd', '/c', 'if exist "%cd%\\download.7z" (7za x "%cd%\\temp\\download.7z" -y -aoa -o"-o"%cd%\\temp") else echo There is no interface file here.'
}
```

## 完整的示例

### build.gradle文件
```java
plugins {
    id "de.undercouch.download" version "3.2.0"
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

task cleanSOFile(type: Delete) {
    delete 'library/src/main/jniLibs'
}

apply from: "update.gradle"

// 更新SO库
task updateSOFile(dependsOn: [cleanSOFile, update]) {
}
```

### update.gradle文件
```java
apply plugin: 'de.undercouch.download'

def soPath = 'library\\src\\main\\jniLibs'

// 读取SO库配置
Properties psCore = new Properties()
psCore.load(project.file("update.properties").newDataInputStream())

def filename = psCore.getProperty("filename")
def soFile = new File(filename)

// 下载SO库压缩文件
task downloadFile << {
    download {
        src psCore.getProperty("url")
        dest soFile
    }
}

// 解压下载文件
task unzipDownloadFile(dependsOn: downloadFile, type: Exec) {
    commandLine 'cmd', '/c', '7za x "%cd%\\' + filename + '" -y -aoa -o"%cd%\\temp"'
    def zipFile
    doLast {
        // 解压arm64-v8a
        exec {
            zipFile = psCore.getProperty("arm64-v8a")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\arm64-v8a") else echo There is no arm64-v8a file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no arm64-v8a path here.'
            }
        }
        // 解压armeabi
        exec {
            zipFile = psCore.getProperty("armeabi")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\armeabi") else echo There is no armeabi file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no armeabi path here.'
            }
        }
        // 解压armeabi-v7a
        exec {
            zipFile = psCore.getProperty("armeabi-v7a")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\armeabi-v7a") else echo There is no armeabi-v7a file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no armeabi-v7a path here.'
            }
        }
        // 解压mips
        exec {
            zipFile = psCore.getProperty("mips")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\mips") else echo There is no mips file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no mips path here.'
            }
        }
        // 解压mips64
        exec {
            zipFile = psCore.getProperty("mips64")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\mips64") else echo There is no mips64 file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no mips64 path here.'
            }
        }
        // 解压x86
        exec {
            zipFile = psCore.getProperty("x86")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\x86") else echo There is no x86 file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no x86 path here.'
            }
        }
        // 解压x86_64
        exec {
            zipFile = psCore.getProperty("x86_64")
            if (zipFile != null && zipFile.length() > 0) {
                commandLine 'cmd', '/c', 'if exist "%cd%\\temp' + zipFile + '" (7za e "%cd%\\temp' +
                        zipFile + '" -y -aoa -o"%cd%\\' + soPath +
                        '\\x86_64") else echo There is no x86_64 file here.'
            } else {
                commandLine 'cmd', '/c', 'echo There is no x86_64 path here.'
            }
        }
    }
}

// 完成并删除临时文件及文件夹
task update(dependsOn: unzipDownloadFile, type: Delete) {
    delete soFile
    delete 'temp'
}
```

### update.properties文件
```text
## 此文件用于配置SO库下载版本
#Mon Mar 30 01:35:23 CST 2017
# 固定地址
url=http://www.example.com/so.7z
# 文件名
filename=so.7z
# 不同类型CPU对应的SO文件压缩包文件名称及路径（在解压目录中的路径，不包含则为空）
# arm64-v8a
arm64-v8a=\\ARM_V8A\\so.7z
# armeabi
armeabi=\\ARM\\so.7z
# armeabi-v7a
armeabi-v7a=\\ARM_V7A\\so.7z
# mips
mips=\\MIPS\\so.7z
# mips64
mips64=\\MIPS64\\so.7z
# x86
x86=\\X86\\so.7z
# x86_64
x86_64=\\X86_64\\so.7z
```
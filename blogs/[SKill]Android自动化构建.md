
# Android自动化构建
*本文的目的不是介绍如何搭建Jenkins，而是介绍如何在Jenkins中搭建Android自动化构建环境。*

## 安装Jenkins Gradle插件
Jenkins原生并不支持Gradle语法，因此需要安装Gradle插件。

 + 下载：[Gradle插件](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+Plugin)
 + 上传并安装。

## 创建任务配置
安装好Gradle插件之后，跟创建常规任务一样，新建一个自动化构建任务，需要注意的几个点：

 + 构建工具选择：Invoke Gradle script。
 + 构建工具配置：  
   Gradle版本选择：Gradle Wrapper，方便版本统一。  
   Tasks填写：
```java
clean
assembleRelease
```
 + 用于存档的APK文件：app/build/outputs/apk/*.apk
 + 用于存档的mapping文件：app/build/outputs/mapping/*/release/mapping.txt

## 为打包机配置打包环境
确保SDK具备编译所需的版本，才能正确编译。

 + 下载打包环境：[下载Android Studio](https://developer.android.com/studio/index.html#downloads)（下载无 Android SDK版本），[下载SDK Tools](https://developer.android.com/studio/index.html#downloads)
 + 安装环境：解压SDK，下载好的SDK Tools仅包含tools文件夹，解压出来。创建android-sdk-windows文件夹（MAC：android-sdk-mac），将tools放置其中，尽量将android-sdk-windows置于磁盘根目录。配置环境变量ANDROID_HOME，值为android-sdk-windows文件夹目录（配置环境变量需要重启Jenkins才会有效）。安装Android Studio并运行，其会自行定义到SDK目录，安装过程中会下载剩余的必须的SDK，过程较长。
 + 安装其余的SDK：API（至少包含跟项目targetSdkVersion一致的API，其他视情况而定）、Build-Tools（至少包含跟项目buildToolsVersion一致的Build-Tools，其他视情况而定，文件较小建议全部安装）、其他extras工具。

## Gradle配置
自动化构建用到了两个Task：`clean`与`assembleRelease`，其中`assembleRelease`为编译正式版的命令，`clean`则是清理目录。  

### 根目录Gradle配置
编辑项目根目录下的build.gradle文件，增加clean task：
```java
    task clean(type: Delete) {
        delete rootProject.buildDir
    }
```

### Gradle配置打包签名
签名的Gradle配置则在app module下的build.gradle上：

 + Gradle配置示例：  
```java
    signingConfigs {
        // keystore properties
        Properties keystoreProperties = new Properties()
        File keystorePropertiesFile = project.file("../keystore.properties");
        // read properties
        if (keystorePropertiesFile.exists()) {
            keystoreProperties.load(keystorePropertiesFile.newDataInputStream())
        }
        def storeFileRelease = keystoreProperties.getProperty("storeFile")
        def storePasswordRelease = keystoreProperties.getProperty("storePassword")
        def keyAliasRelease = keystoreProperties.getProperty("keyAlias")
        def keyPasswordRelease = keystoreProperties.getProperty("keyPassword")
        release {
            storeFile file(storeFileRelease)
            storePassword storePasswordRelease
            keyAlias keyAliasRelease
            keyPassword keyPasswordRelease
        }
    }
```
 + 签名文件处理:  
   将打包用的Keystore放置于项目根目录，并创建keystore.properties文件用于配置签名数据，文件内容（键值对）：  
```xml
   storeFile=../filename.keystore
   storePassword=storePassword
   keyAlias=alias
   keyPassword=keyPassword
```
   建议将keystore.properties与keystore文件仅放置于打包机的本地仓库之中（正式包仅允许打包机打），也可将其直接放置Git仓库里（所有人可以发布正式包）。

## 后续事项
打包失败时:

 + 检查打包机SDK是否安装及更新。
 + 本地尝试打包是否存在问题。
 + 根据输出日志查找问题。
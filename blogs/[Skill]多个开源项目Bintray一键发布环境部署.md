#多个开源项目Bintray一键发布环境部署

　　我们发布到Bintray上共享的一般是一些库，而不是完整的App，而这些库是依附在我的主项目之中，如果我们主项目只维护一个共享库，那没什么问题，但维护多个开源库呢？不规划一下打包发布的流程，那么就会浪费我更很多的时间在打包发布上。截至至撰文时，笔者的ProjectX主项目已经管理维护者16个开源库，不规划一套打包方案，那么妥妥的能把笔者累死。

##基础Plugin载入

　　需要实现自动化发包，就必须载入[***gradle-bintray-plugin***](https://github.com/bintray/gradle-bintray-plugin/releases)与[***android-maven-gradle-plugin***](https://github.com/dcendents/android-maven-gradle-plugin/releases)（点击链接查看最新版本号，使用最新版本插件）。载入方式有两种：

- 传统方式

    ```java
    dependencies {
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.1'
        classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
    }
    ```
    
- 新型方式(Gradle 2.1)

    ```java
    plugins {
        id "com.jfrog.bintray" version "1.7.1"
        id "com.github.dcendents.android-maven" version "1.5"
    }
    ```
    
　　使用新型方式导入的gradle-bintray-plugin会提交不成功，不知AS更新以后是否解决，但是笔者出错的版本是1.7.1，新版本没出来前gradle-bintray-plugin还是建议使用传统方式，android-maven-gradle-plugin可以选择新型方式。

##部署方案

1. 在库根目录（不是项目根目录）创建bintray.gradle文件，文件内容（可以直接拷贝给其他项目使用）：

    ```java
    apply plugin: 'com.github.dcendents.android-maven'
    apply plugin: 'com.jfrog.bintray'
    
    // load properties
    Properties properties = new Properties()
    File localPropertiesFile = project.file("local.properties");
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.newDataInputStream())
    }
    File projectPropertiesFile = project.file("project.properties");
    if (projectPropertiesFile.exists()) {
        properties.load(projectPropertiesFile.newDataInputStream())
    }
    
    // read properties
    def projectName = properties.getProperty("project.name")
    def projectDesc = properties.getProperty("project.desc")
    def projectGroupId = properties.getProperty("project.groupId")
    def projectArtifactId = properties.getProperty("project.artifactId")
    def projectVersionName = android.defaultConfig.versionName
    def projectPackaging = properties.getProperty("project.packaging")
    def projectSiteUrl = properties.getProperty("project.siteUrl")
    def projectGitUrl = properties.getProperty("project.gitUrl")
    def projectIssueTrackerUrl = properties.getProperty("project.issueTrackerUrl")
    def developerId = properties.getProperty("developer.id")
    def developerName = properties.getProperty("developer.name")
    def developerEmail = properties.getProperty("developer.email")
    
    def bintrayUser = properties.getProperty("bintray.user")
    def bintrayApikey = properties.getProperty("bintray.apikey")
    
    // This generates POM.xml with proper parameters
    install {
        repositories.mavenInstaller {
            pom.project {
                name projectName
                groupId projectGroupId
                artifactId projectArtifactId
                version projectVersionName
                packaging projectPackaging
                url projectSiteUrl
                licenses {
                    license {
                        name 'The Apache Software License, Version 2.0'
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                developers {
                    developer {
                        id developerId
                        name developerName
                        email developerEmail
                    }
                }
                scm {
                    connection projectGitUrl
                    developerConnection projectGitUrl
                    url projectSiteUrl
                }
            }
        }
    }
    
    task androidJavadocs(type: Javadoc) {
        source = android.sourceSets.main.java.source
        classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
        options {
            encoding "UTF-8"
            charSet 'UTF-8'
            author true
            version projectVersionName
            links "http://docs.oracle.com/javase/7/docs/api"
            title projectName
        }
    }
    
    task androidJavadocsJar(type: Jar, dependsOn: androidJavadocs) {
        from androidJavadocs.destinationDir
        classifier = 'javadoc'
    }
    
    task androidSourcesJar(type: Jar) {
        from android.sourceSets.main.java.source
        classifier = 'sources'
    }
    
    if (JavaVersion.current().isJava8Compatible()) {
        allprojects {
            tasks.withType(Javadoc) {
                options.addStringOption('Xdoclint:none', '-quiet')
            }
        }
    }
    
    artifacts {
        archives androidSourcesJar
        archives androidJavadocsJar
    }
    
    // bintray configuration
    bintray {
        user = bintrayUser
        key = bintrayApikey
        group = projectGroupId
        configurations = ['archives']
        pkg {
            repo = "maven"
            name = projectName
            websiteUrl = projectSiteUrl
            vcsUrl = projectGitUrl
            desc = projectDesc
            issueTrackerUrl = projectIssueTrackerUrl
            licenses = ["Apache-2.0"]
            publish = true
            publicDownloadNumbers = true
        }
    }
    ```
    
2. 在库根目录创建project.properties用于配置项目信息（不同项目需要配置不同值）：

    ```java
    #project
    project.name=BaseTabStrip
    project.groupId=am.widget
    project.artifactId=basetabstrip
    project.packaging=aar
    project.desc=项目描述
    project.siteUrl=https://github.com/AlexMofer/ProjectX/tree/master/basetabstrip
    project.gitUrl=https://github.com/AlexMofer/ProjectX.git
    project.issueTrackerUrl=https://github.com/AlexMofer/ProjectX/issues
    ```
    
3. 在库根目录创建local.properties用于配置bintray登录信息（可以直接拷贝给其他项目使用，需要加入git忽略列表）：

    ```java
    ##必须Git忽略此文件，其包含隐私信息
    #bintray
    bintray.user=你的bintray账户
    bintray.apikey=API Key
    
    #developer
    developer.id=*******
    developer.name=***
    developer.email=*******@****.com
    ```
    
4. git添加bintray.gradle与project.properties，忽略local.properties ：

    ```java
    # Local configuration file (sdk path, etc)
    local.properties
    ```
    
5. 在库的build.gradle最后加入：

    ```java
    //apply from: "bintray.gradle"
    ```
    
##发布

　　将要提交的开源库的build.gradle中的 apply from: "bintray.gradle" 去掉注释，保证 versionName 是你想要发布的，那么控制台输入gradlew bintrayUpload就可以了，成功以后再将 apply from: "bintray.gradle" 注释掉就不会干扰其他开源项目的提交了。

##注意
- Git一定要忽略掉local.properties文件
- build.gradle中的 apply from: "bintray.gradle" 要记得注释掉
- bintray.gradle不单单是发布处理，还包括中文注释乱码处理
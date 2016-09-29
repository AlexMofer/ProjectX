#Bintray发布开源库小技巧
　　我们发布到Bintray上共享的一般是一些库，而不是完整的App，而这些库是依附在我的主项目之中，如果我们主项目只维护一个共享库，那没什么问题，但维护多个开源库呢？不规划一下打包发布的流程，那么就会浪费我更很多的时间在打包发布上。截至至撰文时，笔者的ProjectX主项目已经管理维护者16个开源库，不规划一套打包方案，那么妥妥的能把笔者累死。
##基础Plugin载入
　　需要实现自动化发包，就必须载入[***gradle-bintray-plugin***](https://github.com/bintray/gradle-bintray-plugin)与[***android-maven-gradle-plugin***](https://github.com/dcendents/android-maven-gradle-plugin)。载入方式有两种：

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
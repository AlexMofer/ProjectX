# SupportPlus
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/supportplus/icon.png)

对com.android.support:support-v4包进行拓展，将包内部分包内可见的类暴露出来，用于实现特定功能，其次增加一些工具类。
## 要求
- minSdkVersion 4
- com.android.support:support-v4:24.0.0

## 引用
```java
dependencies {
    ⋯
    compile 'am.project:supportplus:24.1.1'
    ⋯
}
```
## 详情
- **MaterialLoadingProgressDrawable**

    暴露android.support.v4.widget.MaterialProgressDrawable类，实现官方下拉刷新中的转动Drawable
- **RecyclePagerAdapter**

    可回收的PagerAdapter

- **ViewsPagerAdapter**

    普通View列表PagerAdapter
- **MaterialProgressCircleImageView**

    官方下拉刷新转动提示ImageView

## 注意
- 最好是相同版本号的com.android.support:support-v4使用相同的版本
- 最好使用com.android.support:support-v4:24.1.1

## 历史
- [**24.1.0**](https://bintray.com/alexmofer/maven/SupportPlus/24.1.0)
- [**24.0.0**](https://bintray.com/alexmofer/maven/SupportPlus/24.0.0)
- [**23.4.1**](https://bintray.com/alexmofer/maven/SupportPlus/23.4.1)
- [**23.4.0**](https://bintray.com/alexmofer/maven/SupportPlus/23.4.0)（- [**说明**](https://github.com/AlexMofer/ProjectX/tree/master/supportplus/history/23.4.0)）
# SupportPlus
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/supportplus/icon.png)

对com.android.support:support-v4包进行拓展，将包内部分包内可见的类暴露出来，用于实现特定功能，其次增加一些工具类。
## 要求
- minSdkVersion 9
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7）

## 引用
```java
dependencies {
    ⋯
    compile 'am.project:supportplus:24.2.1.1'
    ⋯
}
```
## 详情
- **MaterialLoadingProgressDrawable**

    暴露android.support.v4.widget.MaterialProgressDrawable类，实现官方下拉刷新中的转动Drawable
- **RecyclePagerAdapter**

    可复用回收的PagerAdapter

- **ViewsPagerAdapter**

    普通View列表PagerAdapter
- **MaterialProgressCircleImageView**

    官方下拉刷新转动提示ImageView

## 注意
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7），否则可能出现错误

## 历史
- [**24.2.1**](https://bintray.com/alexmofer/maven/SupportPlus/24.2.1)
- [**24.2.0**](https://bintray.com/alexmofer/maven/SupportPlus/24.2.0)
- [**24.1.1**](https://bintray.com/alexmofer/maven/SupportPlus/24.1.1)（- [**说明**](https://github.com/AlexMofer/ProjectX/tree/master/supportplus/history/24.1.1)）
- [**24.1.0**](https://bintray.com/alexmofer/maven/SupportPlus/24.1.0)
- [**24.0.0**](https://bintray.com/alexmofer/maven/SupportPlus/24.0.0)
- [**23.4.1**](https://bintray.com/alexmofer/maven/SupportPlus/23.4.1)
- [**23.4.0**](https://bintray.com/alexmofer/maven/SupportPlus/23.4.0)（- [**说明**](https://github.com/AlexMofer/ProjectX/tree/master/supportplus/history/23.4.0)）
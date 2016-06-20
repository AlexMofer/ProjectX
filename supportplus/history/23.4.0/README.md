# SupportPlus
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/supportplus/icon.png)

对com.android.support:support-v4包进行拓展，将包内部分包内可见的类及接口暴露出来，用于实现特定功能，其次增加一些工具类。
## 要求
- minSdkVersion 4
- com.android.support:support-v4:23.0.0(<24.0.0)

## 警告
不支持com.android.support:support-v4:24.0.0及以上版本，需要支持24.0.0，请使用最新版
## 引用
```java
dependencies {
    ⋯
    compile 'am.project:supportplus:23.4.0'
    ⋯
}
```
## 详情
- **ViewPagerDecor**

    暴露android.support.v4.view.ViewPager.Decor接口，实现BaseTabStrip作为ViewPager子项实现自动捆绑ViewPager功能
- **MaterialLoadingProgressDrawable**

    暴露android.support.v4.widget.MaterialProgressDrawable类，实现官方下拉刷新中的转动Drawable
- **ProgressImageView**

    暴露android.support.v4.widget.CircleImageView类，实现官方下拉刷新中的圆形白色ImageView
- **RecyclePagerAdapter**

    可回收的PagerAdapter

- **ViewsPagerAdapter**

    普通View列表PagerAdapter
- **MaterialCircleImageView**

    官方下拉刷新转动提示ImageView
## 注意
- 最好是相同版本号的com.android.support:support-v4使用相同的版本
- 不支持com.android.support:support-v4:24.0.0及以上版本
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
    compile 'am.project:supportplus:24.0.0'
    ⋯
}
```
## 详情
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
- 不支持com.android.support:support-v4:24.0.0以下的所有版本

## 历史
- [**23.4.0**](https://github.com/AlexMofer/ProjectX/tree/master/supportplus/history/23.4.0)
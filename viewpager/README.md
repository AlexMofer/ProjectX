# ViewPager
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/support/icon.png)

与ViewPager相关的一些工具类。
## 要求
- minSdkVersion 9
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7）

## 引用
```java
dependencies {
    ⋯
    compile 'am.util:viewpager:25.1.1'
    ⋯
}
```
## 详情
- **RecyclePagerAdapter**

    回收复用的PagerAdapter，实现方式类似于RecyclerView.Adapter。
- **ViewsPagerAdapter**

    普通View列表PagerAdapter
- **FragmentRemovePagerAdapter**

    FragmentPagerAdapter 与 FragmentStatePagerAdapter的结合体。

## 注意
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7），否则可能出现错误。

## 历史
- [**25.1.0**](https://bintray.com/alexmofer/maven/ViewPager/25.1.0)
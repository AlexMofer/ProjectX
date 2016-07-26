# WrapLayout
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/wraplayout/icon.png)

自动换行布局，水平排列子项，并自动换行，支持不等长不等宽子项，且可以设置垂直间距与水平间距及子项对齐模式。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/wraplayout/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:wraplayout:1.1.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.wraplayout.WrapLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/wly_lyt_warp"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="10dp"
    android:background="@drawable/bg_wraplayout_content"
    android:horizontalSpacing="10dp"
    android:padding="10dp"
    android:verticalSpacing="10dp"
    app:wlyHorizontalSpacing="10dp"
    app:wlyVerticalSpacing="10dp">
    ⋯
</am.widget.wraplayout.WrapLayout>
```
- 基本代码
```java
WrapLayout lytWrap = (WrapLayout) findViewById(R.id.wly_lyt_warp);
lytWrap.setHorizontalSpacing(20);
lytWrap.setVerticalSpacing(20);
lytWrap.setGravity(WrapLayout.GRAVITY_CENTER);
```
## 注意
- 继承自ViewGroup
- android:horizontalSpacing 与 app:wlyHorizontalSpacing只定义一份即可
- android:verticalSpacing 与 app:wlyVerticalSpacing只定义一份即可
- 通过getNumRows()方法获取行数目
- 通过getNumColumns(int)方法获取某一行的列数目
- 通过setGravity(int)方法设置子项对齐模式，仅支持上中下，左右对齐是无意义的。

## 历史
- [**1.0.1**](https://bintray.com/alexmofer/maven/WrapLayout/1.0.1)
- [**1.0.0**](https://bintray.com/alexmofer/maven/WrapLayout/1.0.0)
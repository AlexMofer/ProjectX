# ShapeImageView
----------
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/shapeimageview/icon.png)

图形裁剪ImageView，API 21 及以上 使用 setOutlineProvider 方式实现，支持动态图；以下使用 BitmapShader 方式实现。
支持固定高宽缩放比缩放，支持前景Drawable，支持ImageView的所有ScaleType，且API 21具备更高性能。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/shapeimageview/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:shapeimageview:1.1.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.shapeimageview.ShapeImageView
    android:id="@+id/siv_image_c"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    android:layout_weight="1"
    android:clickable="true"
    android:scaleType="centerCrop"
    android:src="@drawable/bg_welcome"
    app:sivBorderColor="@color/colorAccent"
    app:sivBorderWidth="2dp"
    app:sivForeground="@drawable/bg_common_press_dark"
    app:sivHeightScale="1"
    app:sivScaleTarget="height"
    app:sivShape="Circle"
    app:sivWidthScale="1" />
```
- 基本代码
```java
ShapeImageView sivCircle = (ShapeImageView) findViewById(id);
sivCircle.setBorderColor(color);
sivCircle.setFixedSize(width, height);
sivCircle.setBorderWidth(border);
sivCircle.setImageShape(new RoundRectImageShape(radius));
```
## 注意
- 基本可以通过布局实现基础代码
- 布局sivRoundRectRadius属性只在sivShape="RoundRect"时有效
# DrawableRatingBar
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/drawableratingbar/icon.png)

图片评级,双图片评级控件，可设置图片间距，支持拖动进度及点击进度，可控制最大值最小值，及是否可手动。
## 预览
![Screenshots](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/drawableratingbar/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:drawableratingbar:1.1.2'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.drawableratingbar.DrawableRatingBar
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:drawablePadding="3dp"
    android:minHeight="120dp"
    android:padding="10dp"
    android:progressDrawable="@drawable"
    app:drbGravity="center"
    app:drbManually="true"
    app:drbMax="6"
    app:drbMin="1"
    app:drbOnlyItemTouchable="true"
    app:drbRating="4" />
```
- 基本代码
```java
mRating.setRatingDrawable(Drawable, Drawable);
mRating.setDrawablePadding(int);
mRating.setGravity(Gravity.CENTER);
mRating.setMax(int);
mRating.setMin(int);
mRating.setRating(int);
mRating.setManually(boolean);
mRating.setOnlyItemTouchable(boolean);
mRating.setOnRatingChangeListener(OnRatingChangeListener);
```
## 注意
- 继承自View
- android:progressDrawable与ProgressBar控件的属性一致，图片的ID定义方式也一致
- 可用app:drbProgressDrawable和app:drbSecondaryProgress替代android:progressDrawable
- 可用app:drbDrawablePadding替代android:drawablePadding
- app:drbGravity定义对齐方式
- app:drbManually定义是否可手动控制，默认不可以
- app:drbMax定义最大值，默认5
- app:drbMin定义最小值，默认0
- app:drbOnlyItemTouchable定义是否仅图片区域触发评级修改，默认否
- app:drbRating定义评级

## 历史
- [**1.1.1**](https://bintray.com/alexmofer/maven/DrawableRatingBar/1.1.1)
- [**1.1.0**](https://bintray.com/alexmofer/maven/DrawableRatingBar/1.1.0)
- [**1.0.1**](https://bintray.com/alexmofer/maven/DrawableRatingBar/1.0.1)
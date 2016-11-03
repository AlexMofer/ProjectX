# CircleProgressBar
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/circleprogressbar/icon.png)

带载入动画的环形进度条，可高度配置，支持配置成表盘。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/circleprogressbar/screenshots.gif)
## 要求
minSdkVersion 11
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:circleprogressbar:1.0.2'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.circleprogressbar.CircleProgressBar
    android:layout_width="match_parent"
    android:layout_height="480dp"
    app:cpbGravity="center"
    app:cpbRadius="160dp"
    app:cpbStartAngle="135"
    app:cpbSweepAngle="270"
    app:cpbBackgroundSize="2dp"
    app:cpbBackgroundColor="@color/colorPrimary"
    app:cpbProgressSize="20dp"
    app:cpbMax="810"
    app:cpbProgress="315"
    app:cpbFirstGradientColors="#ff33b5e5"
    app:cpbSecondGradientColors="#ff99cc00"
    app:cpbThirdGradientColors="#ffffbb33"
    app:cpbFourthGradientColors="#ffff4444"
    app:cpbDialGap="4dp"
    app:cpbDialAngle="5"
    app:cpbDialHeight="10dp"
    app:cpbDialWidth="2dp"
    app:cpbDialColor="@color/colorPrimary"
    app:cpbDialSpecialUnit="3"
    app:cpbDialSpecialHeight="15dp"
    app:cpbDialSpecialColor="@color/colorAccent"
    app:cpbShowSpecialDialValue="true"
    app:cpbSpecialDialValueGap="2dp"
    app:cpbSpecialDialValueTextSize="12sp"
    app:cpbSpecialDialValueTextColor="#ff99cc00"
    app:cpbShowProgressValue="true"
    app:cpbProgressValueTextSize="72sp"
    app:cpbProgressValueTextColor="#ff33b5e5"
    app:cpbTopText="速度"
    app:cpbTopTextSize="20sp"
    app:cpbTopTextGap="10dp"
    app:cpbTopTextColor="@color/colorPrimary"
    app:cpbBottomText="km/s"
    app:cpbBottomTextSize="16sp"
    app:cpbBottomTextGap="15dp"
    app:cpbBottomTextColor="#ffffbb33"
    app:cpbScaleType="None"
    app:cpbProgressDuration="2000"
    app:cpbProgressMode="Loading"
    app:cpbLoadingDuration="3000"
    app:cpbLoadingRepeatMode="Reverse"
    app:cpbLoadingDrawOther="true"
    app:cpbLoadingText="载入"/>
```
- 基本代码
```java

```
## 属性说明
xml布局属性|属性值|对应方法|说明
---|---|---|---
cpbGravity|给定值|setGravity(int gravity)|设置排版方式，当高宽超过绘制所需尺寸时，此属性定义其绘制位置
cpbRadius|dimension|setRadius(float radius)|设置环形半径，其包括了绘制的进度的尺寸
cpbStartAngle|integer|setStartAngle(int angle)|设置起始角度
cpbSweepAngle|integer|setSweepAngle(int angle)|设置扫描角度
cpbBackgroundSize|dimension|setBackgroundSize(float size)|设置背景尺寸
cpbBackgroundColor|color|setBackgroundColor(int color)|设置背景颜色

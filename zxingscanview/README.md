# ZxingScanView
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/zxingscanview/icon.png)

zxing条码扫描视图
## 预览
![Screenshots](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/zxingscanview/screenshots.gif)
## 要求
minSdkVersion 15

需要危险权限：android.permission.CAMERA

需要普通权限：android.permission.VIBRATE

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:zxingscanview:3.3.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.zxingscanview.ZxingScanView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/zxingscanview_zsv_scan"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:zsvAmbientLight="close"
    app:zsvFeedback="auto"
    app:zsvScanHeight="400dp"
    app:zsvScanWidth="300dp" />

<am.widget.zxingscanview.ZxingForegroundView
    android:id="@+id/zxingscanview_zfv_foreground"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:zfvCoverColor="@color/colorRipple"
    app:zfvErrorDrawable="@drawable/ic_drawable_center"
    app:zfvOpenDrawable="@drawable/ic_drawable_center"
    app:zfvResultPointsColor="@color/colorAccent"
    app:zfvScanFlagDrawable="@drawable/ic_zxing_scan"
    app:zfvScanRectDrawable="@drawable/ic_zxing_rect"
    app:zfvZxingScanView="@id/zxingscanview_zsv_scan" />
```
- 基本代码
```java
ZxingScanView scanView = (ZxingScanView) findViewById(R.id.zxingscanview_zsv_scan);
scanView.addOnScanListener(new ZxingScanView.OnScanListener() {
    @Override
    public void onError(ZxingScanView scanView) {
        // 处理错误，要么硬件打不开错误，最主要还是缺乏权限错误
    }

    @Override
    public void onResult(ZxingScanView scanView, Result result, Bitmap barcode, float scaleFactor) {
        // 处理扫描结果
    }
});
// 当获取相机权限以后
scanView.open();// 重新打开扫描视图
// 当扫描结果处理完成以后
scanView.restartScanDelay(3000);// 重新开始扫描
```
## 注意
- 兼容6.0以后的新的权限管理机制。
- 大部分属性可通过xml定义。
- ZxingForegroundView为非必需组建。
- 目前建议全屏使用，暂时不支持其他任意尺寸，使用其他任意尺寸会导致画面压扁或拉伸，后续将支持。
- 一维条码扫描暂时需要画面与条码垂直，后续将修改此问题，二维码扫描不存在该问题。
# Support
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/support/icon.png)

支持库，一些工具类及版本控制兼容类，一些特殊监听器。
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.project:support:1.0.5'
    ⋯
}
```
## 详情
- **AMActivityManagerCompat**

    ActivityManager 版本兼容器，兼容getLargeMemoryClass
- **AMApplicationInfoCompat**

    ApplicationInfo 版本兼容器，兼容isLargeHeap
- **AMViewCompat**

    View 版本兼容器，弥补ViewCompat不足的方法，setBackground、setOutlineProvider、setClipToOutline、setStateListAnimator
- **AMWebViewCompat**

    WebView 版本兼容器，判断是否支持 evaluateJavascript、执行evaluateJavascript
- **AMWindowCompat**

    Window 版本兼容器，设置透明的状态栏、设置透明的导航栏、全屏、设置状态栏颜色
- **ValueCallback**

    JS回调
- **ViewOutlineProvider**

    View裁剪回调
- **EdgeScrollListener**

    滚动边缘监听
- **ScrollDirectionTouchListener**

    滑动方向触摸监听
- **BigDecimalUtils**

    不丢失精度的加减乘除及四舍五入
- **InputMethodUtils**

    输入法工具类
- **AMCompatScrollView**

    ScrollView的OnScrollChangeListener兼容版本

## 历史
- [**1.0.4**](https://bintray.com/alexmofer/maven/Support/1.0.4)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/support/history/1.0.4)）
- [**1.0.3**](https://bintray.com/alexmofer/maven/Support/1.0.3)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/support/history/1.0.3)）
- [**1.0.0**](https://bintray.com/alexmofer/maven/Support/1.0.0)
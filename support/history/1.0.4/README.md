# Support
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/support/icon.png)

支持库，一些工具类及版本控制兼容类，一些特殊监听器。
## 要求
minSdkVersion 4
## 引用
```
dependencies {
    ⋯
    compile 'am.project:support:1.0.4'
    ⋯
}
```
## 详情
- **~~AnimatorCallback~~**

    使用 ValueAnimator.AnimatorUpdateListener 配合ValueAnimator来实现（将在下一个版本去除）
- **~~BaseAnimator~~**

    使用ValueAnimator来实现（将在下一个版本去除）
- **~~BaseNextAnimator~~**

    使用ValueAnimator替代，在其设置的AnimatorUpdateListener的onAnimationUpdate(ValueAnimator animator)方法中对动画进行pause()操作，在其他适当的地方进行resume()操作。（将在下一个版本去除）
- **~~CompatPlus~~**

    使用AMViewCompat替代（将在下一个版本去除）
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
- [**1.0.3**](https://bintray.com/alexmofer/maven/Support/1.0.3)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/support/history/1.0.3)）
- [**1.0.0**](https://bintray.com/alexmofer/maven/Support/1.0.0)
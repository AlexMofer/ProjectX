# Support
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/support/icon.png)

支持库，一些工具类及版本控制兼容类，一些特殊监听器，动画。
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.project:support:1.0.3'
    ⋯
}
```
## 详情
- **AnimatorCallback**

    动画回调
- **BaseAnimator**

    基础动画
- **BaseNextAnimator**

    需要执行下一步操作的基础动画，没执行下一步操作则动画循环
- **EdgeScrollListener**

    滚动边缘监听
- **ScrollDirectionTouchListener**

    滑动方向触摸监听
- **CompatPlus**

    版本兼容控制器
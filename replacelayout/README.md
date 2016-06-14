# ReplaceLayout
 交替布局，配合TabStrip使用，达到伴随ViewPager动作而进行改变的效果

![ICON](https://github.com/AlexMofer/ProjectX/blob/master/replacelayout/icon.png)
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/replacelayout/screenshots.gif)

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:replacelayout:1.0.0'
    ⋯
}
```

## 功能
继承自FrameLayout，通过设置ReplaceAdapter来完成管理子项View的变化，并通过move(int, int, float)方法和moveTo(int)方法达到变化的效果

## 使用
- 基本布局
```xml
⋯
```
- 基本代码
```java
⋯
```

## 注意
- 继承自FrameLayout，仅修改必要的触摸拦截，不建议通过xml方式在其内部添加View
- 仅有设置ReplaceAdapter并实现其中的变化方法才能实现子View变化效果
- move(int, int, float)中最后一个参数为0-1的偏移值
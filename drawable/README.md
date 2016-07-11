# Drawable
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/drawable/icon.png)

实现特定功能的Drawable合集。
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.drawable:drawable:1.0.1'
    ⋯
}
```
## 详情
- **DoubleCircleDrawable**

    双圈动图，用于载入提示。
- **CenterDrawable**

    中心图片，背景可绘制形状，一般用于ImageView的src，保证缩放后，中心的Drawable不变形。用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
- **CombinationDrawable**

    双层图片，与CenterDrawable类似，背景为另一Drawable，一般用于ImageView的src，保证缩放后，中心的Drawable不变形。用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
- **LineDrawable**

    横线图片，主要是底色为透明或半透明色时有用，为不透明时，通过layer-list即可实现。
- **LinearDrawable**
    
    线性图片，多张图片排列，支持设置间隔，主要用于替代多个ImageView排列，节省性能。
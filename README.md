# ProjectX
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/ProjectX.png)

整体项目合集，包括Widget、Drawable以及一些工具类。
## Widget
- [***BaseTabStrip***](https://github.com/AlexMofer/ProjectX/tree/master/basetabstrip)

    TabStrip基础类，其实现了类似于PagerTabStrip的一些基础方法，继承该类型并实现相应方法达到自定义的PagerTabStrip同类型的效果。
- [***GradientTabStrip***](https://github.com/AlexMofer/ProjectX/tree/master/gradienttabstrip)

    继承自BaseTabStrip，实现微信式渐变底部Tab效果
- [***TagTabStrip***](https://github.com/AlexMofer/ProjectX/tree/master/tagtabstrip)

    继承自BaseTabStrip，实现ViewPager滚动标记点
- [***IndicatorTabStrip***](https://github.com/AlexMofer/ProjectX/tree/master/indicatortabstrip)

    继承自BaseTabStrip，移动式下标渐变缩放Tab，Item不建议超过5个
- [***ShapeImageView***](https://github.com/AlexMofer/ProjectX/tree/master/shapeimageview)

    图形裁剪ImageView，API 21 及以上 使用 setOutlineProvider 方式实现，低版本使用 BitmapShader 方式实现，支持固定高宽比缩放，支持前景Drawable
- [***StateFrameLayout***](https://github.com/AlexMofer/ProjectX/tree/master/stateframelayout)

    状态帧布局，通常用于网络请求的四种状态，普通、载入、错误、空白。支持Drawable或者View来展示，也可以混搭
- [***WrapLayout***](https://github.com/AlexMofer/ProjectX/tree/master/wraplayout)

    自动换行布局，支持不等长不等宽子项，且可以设置垂直间距与水平间距及子项对齐模式
- [***ReplaceLayout***](https://github.com/AlexMofer/ProjectX/tree/master/replacelayout)

    交替布局，配合TabStrip使用，达到伴随ViewPager动作而进行改变的效果
- [***DrawableRatingBar***](https://github.com/AlexMofer/ProjectX/tree/master/drawableratingbar)

    双图片评级控件，可设置图片间距，支持拖动进度及点击进度，可控制最大值最小值，及是否可手动。
- [***HeaderFooterGridView***](https://github.com/AlexMofer/ProjectX/tree/master/headerfootergridview)

    头尾GridView，支持AUTO_FIT模式，头尾模式有两种，无反射代码。

## Drawable
- [***DoubleCircleDrawable***](https://github.com/AlexMofer/ProjectX/tree/master/drawable)

    双圈动图，用作载入动画

## Other
- [***Printer***](https://github.com/AlexMofer/ProjectX/tree/master/printer)

    标准ES-POS命令打印，固定IP或蓝牙打印，支持黑白图片打印
- [***Security***](https://github.com/AlexMofer/ProjectX/tree/master/security)

    加密解密，主要为3DES、AES、RSA加密算法的整理
- [***SupportPlus***](https://github.com/AlexMofer/ProjectX/tree/master/supportplus)

    v4支持包拓展
- [***Support***](https://github.com/AlexMofer/ProjectX/tree/master/support)

    支持包

ProjectX
========

<img src="images/svg/ic_launcher.svg" width="192" height="192" alt="Icon"/>

所有个人开源项目合集，便于管理及维护。

Widget
------

- ***[BaseTabStrip][1]***

    TabStrip基础类，其实现了类似于PagerTabStrip的一些基础方法，继承该类型并实现相应方法达到自定义的PagerTabStrip同类型的效果。
- ***[GradientTabStrip][2]***

    继承自BaseTabStrip，实现微信式渐变底部Tab效果
- ***[TagTabStrip][3]***

    继承自BaseTabStrip，实现ViewPager滚动标记点
- ***[IndicatorTabStrip][4]***

    继承自BaseTabStrip，移动式下标渐变缩放Tab，Item不建议超过5个
- ***[ShapeImageView][5]***

    图形裁剪ImageView，API 21 及以上 使用 View.setOutlineProvider(ViewOutlineProvider) 方式实现，API 18 及以上 使用 Canvas.clipPath(Path) 方式实现，API 18 以下   使用 Paint.setXfermode(Xfermode) 方式实现（使用的是PorterDuffXfermode），均支持动态图；支持固定高宽缩放比缩放，支持前景Drawable，支持ImageView的所有ScaleType
- ***[StateFrameLayout][6]***

    状态帧布局，通常用于网络请求的四种状态，普通、载入、错误、空白。支持Drawable或者View来展示，也可以混搭
- ***[WrapLayout][7]***

    自动换行布局，支持不等长不等宽子项，且可以设置垂直间距与水平间距及子项对齐模式
- ***[ReplaceLayout][8]***

    交替布局，配合TabStrip使用，达到伴随ViewPager动作而进行改变的效果
- ***[DrawableRatingBar][9]***

    双图片评级控件，可设置图片间距，支持拖动进度及点击进度，可控制最大值最小值，及是否可手动。
- ***[HeaderFooterGridView][10]***

    头尾GridView，支持AUTO_FIT模式，头尾模式有两种，无反射代码。
- ***[MultiActionTextView][11]***

    文字可点击TextView，设置文字部分可点击，点击执行不同操作。
- ***[CircleProgressBar][13]***

    带载入动画的环形进度条，可高度配置，支持配置成表盘。
- ***[ZxingScanView][14]***

    一个View实现zxing条码扫描视图。
- ***[SmoothInputLayout][15]***

    仿微信式，平滑输入面板，防止键盘的出现与消失导致特殊输入面板的顶起与塌陷。

[1]: https://github.com/AlexMofer/ProjectX/tree/master/basetabstrip
[2]: https://github.com/AlexMofer/ProjectX/tree/master/gradienttabstrip
[3]: https://github.com/AlexMofer/ProjectX/tree/master/tagtabstrip
[4]: https://github.com/AlexMofer/ProjectX/tree/master/indicatortabstrip
[5]: https://github.com/AlexMofer/ProjectX/tree/master/shapeimageview
[6]: https://github.com/AlexMofer/ProjectX/tree/master/stateframelayout
[7]: https://github.com/AlexMofer/ProjectX/tree/master/wraplayout
[8]: https://github.com/AlexMofer/ProjectX/tree/master/replacelayout
[9]: https://github.com/AlexMofer/ProjectX/tree/master/drawableratingbar
[10]: https://github.com/AlexMofer/ProjectX/tree/master/headerfootergridview
[11]: https://github.com/AlexMofer/ProjectX/tree/master/multiactiontextview
[12]: https://github.com/AlexMofer/ProjectX/tree/master/selectionview
[13]: https://github.com/AlexMofer/ProjectX/tree/master/circleprogressbar
[14]: https://github.com/AlexMofer/ProjectX/tree/master/zxingscanview
[15]: https://github.com/AlexMofer/ProjectX/tree/master/smoothinputlayout

Drawable
--------

- ***[DoubleCircleDrawable][100]***

    双圈动图，用于载入提示。
- ***[CirclingDrawable][100]***

    外围小点转圈动图。
- ***[CenterDrawable][100]***

    中心图片，背景可绘制形状，一般用于ImageView的src，保证缩放后，中心的Drawable不变形。用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
- ***[CombinationDrawable][100]***

    双层图片，与CenterDrawable类似，背景为另一Drawable，一般用于ImageView的src，保证缩放后，中心的Drawable不变形。用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
- ***[CornerDrawable][100]***
    
    尖角框，使用该Drawable时，会改变View的Padding值。
- ***[LineDrawable][100]***

    横线图片，主要是底色为透明或半透明色时有用，为不透明时，通过layer-list即可实现。
- ***[LinearDrawable][100]***
    
    线性图片，多张图片排列，支持设置间隔，主要用于替代多个ImageView排列，节省性能。
- ***[TextDrawable][100]***
    
    文字图片。
- ***[CircleExpandDrawable][100]***
    
    圆圈扩大图片。
- ***[MaterialProgressDrawable][100]***

    SwipeRefreshLayout载入动图。

[100]: https://github.com/AlexMofer/ProjectX/tree/master/drawable

Other
-----

- ***[Printer][200]***

    标准ES-POS命令打印，固定IP或蓝牙打印，支持黑白图片打印
- ***[Security][201]***

    加密解密，主要为3DES、AES、RSA加密算法的整理
- ***[Support][202]***

    支持包

[200]: https://github.com/AlexMofer/ProjectX/tree/master/printer
[201]: https://github.com/AlexMofer/ProjectX/tree/master/security
[202]: https://github.com/AlexMofer/ProjectX/tree/master/support

Demo
----

[<img src="https://play.google.com/intl/zh_cn/badges/images/apps/en-play-badge.png" width="164" height="48" alt="Google Play"/>][300]
[<img src="release/ic_download.png" alt="Download"/>][301]

[300]: https://play.google.com/store/apps/details?id=am.project.x
[301]: https://raw.githubusercontent.com/AlexMofer/ProjectX/master/release/ProjectX.apk

Support
-------

- Google+: https://plus.google.com/114728839435421501183
- Gmail: moferalex@gmail.com

如果发现错误，请在此处提出:
https://github.com/AlexMofer/ProjectX/issues

License
-------

Copyright (C) 2015 AlexMofer

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
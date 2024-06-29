
ProjectX
========

Android功能示例工程

Java库
-----

- ***[MVPCore][1]***  
MVP设计模式基础核心，与平台无关，便于进一步实现Android或者鸿蒙的MVP设计模式。
- ***[JobCore][2]***  
线程异步基础核心，与平台无关，便于进一步实现Android或者鸿蒙的线程异步。
- ***[RetrofitHelper][3]***  
Retrofit辅助库，Gson解析，请求日志打印，解决回调强引用问题。
- ***[OpenType][4]***  
OpenType字体解析。

[1]: https://github.com/AlexMofer/MVPCore
[2]: https://github.com/AlexMofer/JobCore
[3]: https://github.com/AlexMofer/RetrofitHelper
[4]: https://github.com/AlexMofer/OpenType

Android库
--------

- ***[MVPAndroid][100]***  
Android的MVP设计模式。
- ***[JobAndroid][101]***  
Android线程异步辅助库。
- ***[FontAndroid][102]***  
Android字体解析库。
- ***[FTPServerAndroid][103]***  
Android平台的FTP服务器。
- ***[SupportAndroid][104]***  
Android 支持库，一些杂七杂八的辅助工具。
- ***[AppCompat][105]***  
对AndroidX的AppCompat增强，增加MVP设计模式及部分常用方法。
- ***[Clipboard][106]***  
超级剪切板，利用内容提供者实现剪切板复制粘贴任何数据。
- ***[MultifunctionalImageView][107]***  
多功能ImageView。
- ***[FloatingActionMode][108]***  
悬浮菜单。
- ***[MultifunctionalRecyclerView][109]***  
多功能RecyclerView。
- ***[TabStrip][110]***  
ViewPager的页面栏，包括访微信式渐变底部Tab效果、ViewPager滚动标记点、移动式下标渐变缩放Tab。
- ***[PagerAdapter][111]***  
ViewPager的复用回收的PagerAdapter及支持View集合的PagerAdapter。
- ***[WrapLayout][112]***  
自动换行布局，支持不等长不等宽子项，且可以设置垂直间距与水平间距及子项对齐模式。
- ***[StateLayout][113]***  
状态布局，包括状态帧布局。
- ***[DrawableRatingBar][114]***  
双图片评级控件，可设置图片间距，支持拖动进度及点击进度，可控制最大值最小值，及是否可手动。
- ***[HeaderFooterGridView][115]***  
头尾GridView，支持AUTO_FIT模式，头尾模式有两种，无反射代码。
- ***[MultiActionTextView][116]***  
文字可点击TextView，设置文字部分可点击，点击执行不同操作。
- ***[CircleProgressBar][117]***  
带载入动画的环形进度条，可高度配置，支持配置成表盘。
- ***[ZxingScanView][118]***  
zxing条码扫描视图。
- ***[SmoothInputLayout][119]***  
仿微信式，平滑输入面板，防止键盘的出现与消失导致特殊输入面板的顶起与塌陷。
- ***[MultiProcessSharedPreferences][120]***  
支持多进程的SharedPreferences。
- ***[Drawable][121]***  
Android Drawables。
- ***[Printer][122]***  
标准ESC-POS命令打印，固定IP或蓝牙打印，支持黑白图片打印。后续将考虑将其修改为平台无关的Java库。

[100]: https://github.com/AlexMofer/MVPAndroid
[101]: https://github.com/AlexMofer/JobAndroid
[102]: https://github.com/AlexMofer/FontAndroid
[103]: https://github.com/AlexMofer/FTPServerAndroid
[104]: https://github.com/AlexMofer/SupportAndroid
[105]: https://github.com/AlexMofer/AppCompat
[106]: https://github.com/AlexMofer/Clipboard
[107]: https://github.com/AlexMofer/MultifunctionalImageView
[108]: https://github.com/AlexMofer/FloatingActionMode
[109]: https://github.com/AlexMofer/MultifunctionalRecyclerView
[110]: https://github.com/AlexMofer/TabStrip
[111]: https://github.com/AlexMofer/PagerAdapter
[112]: https://github.com/AlexMofer/WrapLayout
[113]: https://github.com/AlexMofer/StateLayout
[114]: https://github.com/AlexMofer/DrawableRatingBar
[115]: https://github.com/AlexMofer/HeaderFooterGridView
[116]: https://github.com/AlexMofer/MultiActionTextView
[117]: https://github.com/AlexMofer/CircleProgressBar
[118]: https://github.com/AlexMofer/ZxingScanView
[119]: https://github.com/AlexMofer/SmoothInputLayout
[120]: https://github.com/AlexMofer/MultiProcessSharedPreferences
[121]: https://github.com/AlexMofer/Drawable
[122]: https://github.com/AlexMofer/Printer

其他
---

- ***[CompressPlugin][200]***  
Gradle Compress Plugin为基于Apache Commons Compress的Gradle解压插件，用于解压大多数类型的归档型压缩文件及压缩型压缩文件。

[200]: https://github.com/AlexMofer/CompressPlugin

Demo
----

[<img src="https://play.google.com/intl/zh_cn/badges/images/apps/en-play-badge.png" width="164" height="48" alt="Google Play"/>][300]
[<img src="release/ic_download.png" alt="Download"/>][301]

[300]: https://play.google.com/store/apps/details?id=am.project.x
[301]: https://raw.githubusercontent.com/AlexMofer/ProjectX/master/release/ProjectX.apk

支持
---

- Gmail: <mailto:moferalex@gmail.com>

许可
---

Copyright 2021 AlexMofer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
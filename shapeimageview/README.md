ShapeImageView
==============

<img src="icon.png" alt="Icon"/>

图形裁剪ImageView

介绍
---

图形裁剪ImageView，API 21 及以上 使用 View.setOutlineProvider(
ViewOutlineProvider) 方式实现，API 18 及以上 使用 Canvas.clipPath(Path)
方式实现，API 18 以下   使用 Paint.setXfermode(Xfermode) 方式实现
（使用的是PorterDuffXfermode），均支持动态图；支持固定高宽缩放比缩放，支持前景
Drawable，支持ImageView的所有ScaleType。

**属性及方法说明**

xml布局属性|属性值|对应方法|说明
---|---|---|---
custom:sivShape|Circle、RoundRect|void setImageShape(ImageShape shape)|设置图像形状
|||ImageShape getImageShape()|获取图像形状
custom:sivRoundRectRadius|dimension|void setRoundRectRadius(float radius)|设置圆角矩形圆角半径
|||float getRoundRectRadius()|获取圆角矩形圆角半径
custom:sivBorderColor|color|void setBorderColor(int color)|设置边框颜色
|||int getBorderColor()|获取边框颜色
custom:sivForeground|reference|void setForeground(Drawable foreground)|设置前景
|||Drawable getForeground()|获取前景图
custom:sivWidthScale|integer|void setFixedSize(int widthScale, int heightScale)|设置缩放比（任意值小于等于0则关闭该功能）
custom:sivHeightScale|integer|void setFixedSize(int widthScale, int heightScale)|设置缩放比（任意值小于等于0则关闭该功能）
custom:sivScaleTarget|height、width、expand、inside|void setScaleTarget(int target)|设置缩放目标（缩放高、缩放宽、扩大、缩小）
|||int getScaleTarget()|获取缩放目标
|||void invalidateImageShape()|刷新Shape

截图
---

<img src="screenshots.gif" alt="Screenshots"/>

先决条件
----

minSdkVersion 4

入门
---

**引用:**

```java
dependencies {
    ...
    implementation 'am.widget:shapeimageview:2.0.1'
    ...
}
```

**布局:**

```xml
<am.widget.shapeimageview.ShapeImageView
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    android:id="@+id/siv_image_c"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    android:layout_weight="1"
    android:clickable="true"
    android:scaleType="centerCrop"
    android:src="@drawable/bg_welcome"
    custom:sivBorderColor="@color/colorAccent"
    custom:sivBorderWidth="2dp"
    custom:sivForeground="@drawable/bg_common_press_dark"
    custom:sivHeightScale="1"
    custom:sivScaleTarget="height"
    custom:sivShape="Circle"
    custom:sivWidthScale="1" />
```

**代码：**

```java
ShapeImageView sivCircle = (ShapeImageView) findViewById(id);
sivCircle.setBorderColor(borderColor);// 设置边线颜色
sivCircle.setFixedSize(width, height);// 设置缩放比
sivCircle.setBorderWidth(borderWidth);// 设置边线宽
sivCircle.setImageShape(new CircleImageShape()); 设置裁剪形状
```

注意
---

- 基本完全可以通过布局实现基础代码
- 布局sivRoundRectRadius属性只在sivShape="RoundRect"时有效
- 各种模式下都会对前景图跟背景图都进行形状处理
- 重写ImageShape的isOutlineEnable()方法并返回true，则会强制开启Outline模式，
在低于API 21的设备上不会报错，但无裁剪效果
- 重写ImageShape的isOutlineEnable()方法并返回false，则会强制关闭Outline模式，
其会降级使用ClipPath模式（如果开启的话）。
- 重写ImageShape的isClipPathEnable()方法并返回true，则会强制开启ClipPath
模式，在低于API 18的设备上需要关闭View的硬件加速才可能达到效果，但有些设备即便
是在关闭硬件加速的条件下依旧无效。
- 重写ImageShape的isClipPathEnable()方法并返回false，则会强制关闭ClipPath
模式，其会降级使用PorterDuff模式。
- PorterDuff模式下会根据View的尺寸创建一张一样大小的全透明Bitmap，上面会根据
你需要的形状绘制一个黑色的该形状，当你View的大小改变以后，其随之会重新创建一张
Bitmap并回收之前的，在View从窗口消失后Bitmap会回收掉。
- 增加宽高固定比的目的是，在图片尚未下载之前已知道尺寸，则可以控制好View的高宽，
在图片下载好载入后不会导致View尺寸变化。
- 兼容低版本的前景图片保证View在点击时可触发前景图的状态变化，从此省去给src做
状态处理。
- ImageShape的参数变化以后，需调用ShapeImageView的invalidateImageShape()
方法刷新。

支持
---

- Google+: https://plus.google.com/114728839435421501183
- Gmail: moferalex@gmail.com

如果发现错误，请在此处提出:
https://github.com/AlexMofer/ProjectX/issues

许可
---

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
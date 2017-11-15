Support
=======

<img src="icon.png" alt="Icon"/>

支持库

介绍
---

支持库，一些工具类及版本控制兼容类，一些特殊监听器。
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

先决条件
----

minSdkVersion 4

入门
---

**引用:**

```java
dependencies {
    ...
    implementation 'am.project:support:1.1.3'
    ...
}
```

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
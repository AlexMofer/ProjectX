DrawableRatingBar
=================

<img src="icon.png" alt="Icon"/>

图片评级

介绍
---

双图片评级控件，可设置图片间距，支持拖动进度及点击进度，可控制最大值最小值，及是否可手动。

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
    implementation 'am.widget:drawableratingbar:1.1.3'
    ...
}
```

**布局:**

```xml
<am.widget.drawableratingbar.DrawableRatingBar
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:drawablePadding="3dp"
    android:minHeight="120dp"
    android:padding="10dp"
    android:progressDrawable="@drawable"
    app:drbGravity="center"
    app:drbManually="true"
    app:drbMax="6"
    app:drbMin="1"
    app:drbOnlyItemTouchable="true"
    app:drbRating="4" />
```

**代码：**

```java
mRating.setRatingDrawable(Drawable, Drawable);
mRating.setDrawablePadding(int);
mRating.setGravity(Gravity.CENTER);
mRating.setMax(int);
mRating.setMin(int);
mRating.setRating(int);
mRating.setManually(boolean);
mRating.setOnlyItemTouchable(boolean);
mRating.setOnRatingChangeListener(OnRatingChangeListener);
```

注意
---

- 继承自View
- android:progressDrawable与ProgressBar控件的属性一致，图片的ID定义方式也一致
- 可用app:drbProgressDrawable和app:drbSecondaryProgress替代android:progressDrawable
- 可用app:drbDrawablePadding替代android:drawablePadding
- app:drbGravity定义对齐方式
- app:drbManually定义是否可手动控制，默认不可以
- app:drbMax定义最大值，默认5
- app:drbMin定义最小值，默认0
- app:drbOnlyItemTouchable定义是否仅图片区域触发评级修改，默认否
- app:drbRating定义评级

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
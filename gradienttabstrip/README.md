GradientTabStrip
================

<img src="icon.png" alt="Icon"/>

渐变底部栏

介绍
---

继承自BaseTabStrip，实现微信式渐变底部Tab效果，为ViewPager添加如
PagerTitleStrip一样的Tab，但支持更多自定义功能，并支持为Tab增加标记点功能，
并可以自定义标记点各自的位置及显示状态以及背景等。

截图
---

<img src="screenshots.gif" alt="Screenshots"/>

先决条件
----

- minSdkVersion 14
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7）

入门
---

**引用:**

```java
dependencies {
    ...
    compile 'am.widget:gradienttabstrip:26.1.0
    ...
}
```

**布局:**

```xml
<am.widget.gradienttabstrip.GradientTabStrip
    android:id="@+id/gts_gts_tabs"
    android:layout_width="match_parent"
    android:layout_height="64dp"
    android:textColor="@color/color_gradienttabstrip_tab"
    android:textSize="12sp"
    app:gtsBackground="@drawable/bg_common_press"/>
```

**代码：**
```java
GradientTabStrip tabStrip = (GradientTabStrip) findViewById(id);
GradientTabStrip.GradientTabAdapter adapter = new GradientTabStrip.GradientTabAdapter () {
    @Override
    public Drawable getNormalDrawable(int position, Context context) {
        return null;
    }

    @Override
    public Drawable getSelectedDrawable(int position, Context context) {
        return null;
    }

    @Override
    public boolean isTagEnable(int position) {
        return false;
    }

    @Override
    public String getTag(int position) {
        return null;
    }
};
tabStrip.bindViewPager(viewPager);
```

注意
---

- 不要使用ViewPage的setCurrentItem(int)方法，其不会通知到GradientTabStrip进行刷新，使用GradientTabStrip的performClick(int)方法
- 布局时，android:textColor指定的颜色可以使用选择器，其中android:state_selected="true"状态下的颜色会与普通状态下的颜色进行渐变
- GradientTabAdapter中进行了改变GradientTabAdapter，需要手动通知GradientTabStrip进行刷新
- 不需要Tag小红点，可以使用SimpleGradientTabAdapter替代GradientTabAdapter
- 要实现微博底部栏效果，可为其添加中间间隔，对应布局属性gtsCenterGap
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7），否则可能出现错误

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
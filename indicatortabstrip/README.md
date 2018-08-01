IndicatorTabStrip
=================

<img src="icon.png" alt="Icon"/>

游标渐变栏

介绍
---

继承自BaseTabStrip，移动式下标渐变缩放Tab，Item不建议超过5个，为ViewPager添加，
如PagerTitleStrip一样的Tab，但支持更多自定义功能，并支持为Tab增加标记点功能，并
可以自定义标记点各自的位置及显示状态以及背景等。

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

```
dependencies {
    ...
    compile 'am.widget:indicatortabstrip:27.0.2'
    ...
}
```

**布局:**

```xml
<am.widget.indicatortabstrip.IndicatorTabStrip
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/its_its_tabs"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:padding="5dp"
    android:textColor="@color/color_main_tabs"
    android:textSize="16sp"
    app:ttsTextScale="1.2"
    app:ttsDivider="@drawable/divider_indicator_under"
    app:ttsInterval="@drawable/divider_indicator_interval"
    app:ttsIndicator="@drawable/ic_indicator_indicator"
    app:ttsBackground="@drawable/bg_common_press"
    app:ttsGradient="@color/color_indicator"
    app:ttsTagMargin="5dp"
    app:ttsTagMinWidth="15dp"
    app:ttsTagMinHeight="15dp"/>
```

**代码：**

```
IndicatorTabStrip tabs = (IndicatorTabStrip) findViewById(R.id.its_its_tabs);
BaseTabStrip.ItemTabAdapter adapter = new BaseTabStrip.ItemTabAdapter() {

    @Override
    public boolean isTagEnable(int position) {
        if (position == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTag(int position) {
        switch (position) {
            default:
            case 0:
                return null;
            case 1:
                return "";
            case 2:
                return "3";
        }
    }
};
tabs.setAdapter(adapter);
tabs.bindViewPager(viewPager);
```

注意
---

- 不要使用ViewPage的setCurrentItem(int)方法，其不会通知到IndicatorTabStrip进行刷新，使用IndicatorTabStrip的performClick(int)方法
- 布局时，android:textColor指定的颜色可以使用选择器，其中android:state_selected="true"状态下的颜色会与普通状态下的颜色进行渐变
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
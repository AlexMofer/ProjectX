BaseTabStrip
============

<img src="icon.png" alt="Icon"/>

基础的ViewPager标志控件

介绍
---

继承自View，可自动捆绑ViewPager，BaseTabStrip为基础类，仅实现了一些通用基础
逻辑。具体的实现效果需要实现与重写部分方法。

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
         compile 'am.widget:basetabstrip:27.0.2'
         ...
     }
     ```

**实现:**

必须实现的方法（实现子类独有的效果）：

     ```java
         /**
          * 直接跳转到
          *
          * @param current 位置
          */
         protected abstract void jumpTo(int current);

         /**
          * 滑向左边
          *
          * @param current 当前页
          * @param next    目标页
          * @param offset  偏移
          */
         protected abstract void gotoLeft(int current, int next, float offset);

         /**
          * 滑向右边
          *
          * @param current 当前页
          * @param next    目标页
          * @param offset  偏移
          */
         protected abstract void gotoRight(int current, int next, float offset);
     ```

可重写的方法：

完成PagerAdapter绑定，但并未刷新界面及布局：

     ```java
         /**
          * 捆绑PagerAdapter
          */
         protected void onBindPagerAdapter() {

         }
     ```

若子类要实现点击事件，需要实现将点击的点转化为子项Position：

     ```java
         /**
          * 由触摸点转为Position
          *
          * @param x X坐标
          * @param y Y坐标
          * @return 坐标对应位置
          */
         protected int pointToPosition(float x, float y) {
             return 0;
         }
     ```

若设置了子项的Background，需要设置Background的hotspot，则需要重写：

     ```java
         /**
          * set hotspot's x location
          *
          * @param background 背景图
          * @param position   图片Position
          * @param motionX    点击位置X
          * @param motionY    点击位置Y
          * @return x location
          */
         protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
             return background.getIntrinsicWidth() * 0.5f;
         }

         /**
          * set hotspot's y location
          *
          * @param background 背景图
          * @param position   图片Position
          * @param motionX    点击位置X
          * @param motionY    点击位置Y
          * @return y location
          */
         protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
             return background.getIntrinsicHeight() * 0.5f;
         }
     ```

子项类实现\@ViewPager.DecorView接口用于达到作为ViewPager子项显示

注意
---

- 如果子类实现子项点击事件，需要在构造方法中setItemClickable(ture)，且同时可设置点击时是否平滑滚动setClickSmoothScroll(boolean)；
- 可通过setItemBackground()来给子项设置背景，但背景并未在基类中绘画出来，因此需要在子类中自行将其绘制出来，如果不需要则可以忽略
- OnItemClickListener中实现了：单击、点击已选中子项、双击三种点击事件
- ItemTabAdapter为角标基本容器，需要实现角标，可以基于该接口
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
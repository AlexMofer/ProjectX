ViewPager
=========

<img src="icon.png" alt="Icon"/>

ViewPager辅助库

介绍
---

与ViewPager相关的一些工具类：
- **RecyclePagerAdapter**

    回收复用的PagerAdapter，实现方式类似于RecyclerView.Adapter。
    ViewPager最多构造四个相同类型的页面，但是显示时最多需要当前页面及左右两个
    页面，第四个页面就可以存起来复用。Adapter使用一个ArrayList\<VH\>来存放所有
    的Holder；再用一个SparseArray\<ArrayList\<VH\>\>来根据viewType存放在
    destroyItem时候被回收的不同类型的Holder集合，在instantiateItem时候优先从
    其内部获取，在没有时再重新创建。

- **ViewsPagerAdapter**

    普通View列表PagerAdapter
- **FragmentRemovePagerAdapter**

    FragmentPagerAdapter 与 FragmentStatePagerAdapter的结合体。

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
    compile 'am.util:viewpager:26.1.0'
    ...
}
```

**使用**

整体实现其实不难，使用过RecycleView的话，就可以轻车熟路，跟其实现方案一模一样。

实现自己的PagerViewHolder，个人习惯在实例化时进行布局inflate，这样打开
PagerViewHolder便可以直接找到使用的布局文件：

```java
public class MyPagerViewHolder extends RecyclePagerAdapter.PagerViewHolder {

    public MyPagerViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclepager_page, parent, false));
    }
    //应用到页面上的数据
    public void setData(String data) {
        ((TextView) itemView).setText(data);
    }
}
```

实现自己的RecyclePagerAdapter：

```java
public class MyRecyclePagerAdapter extends RecyclePagerAdapter<MyPagerViewHolder> {

    private int itemCount = 5;
    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public MyPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //一般同viewType的Holder创建不会超过四个
        return new MyPagerViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(MyPagerViewHolder holder, int position) {
        //处理不同页面的不同数据
        holder.setData(String.format(Locale.getDefault(),"第%d页", position + 1));
    }

    @Override
    public int getItemViewType(int position) {
        //设置不同类型的页面。
        return 0;
    }

    @Override
    public void onViewRecycled(VH holder) {
        //当ViewPager执行destroyItem时，会回收Holder,此时会调用该方法，你可以重写该方法实现你要的效果
    }
}
```

注意
---

- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7），否则可能出现错误。

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
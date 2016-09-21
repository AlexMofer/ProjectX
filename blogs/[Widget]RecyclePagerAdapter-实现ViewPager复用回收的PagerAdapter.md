# RecyclePagerAdapter-实现ViewPager复用回收的PagerAdapter
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/images/icons/recyclepager.png)

实现ViewPager页卡View复用回收的PagerAdapter，只要是页面构造一样，则可以使用复用回收机制，同时也支持设置不同的viewType来实现多种样式的页卡回收复用，套用RecycleView的Adapter实现机制。
## 要求
- com.android.support:support-v4
- minSdkVersion 9
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7）

## 链接
- [***Github***](https://github.com/AlexMofer/ProjectX/tree/master/supportplus)
- [***Bintray***](https://bintray.com/alexmofer/maven/SupportPlus)

## 引用
```java
dependencies {
    ⋯
    compile 'am.project:supportplus:24.2.1.1'
    ⋯
}
```
## 使用
整体实现其实不难，使用过RecycleView的话，就可以轻车熟路，跟其实现方案一模一样。
实现自己的PagerViewHolder，个人习惯在实例化时进行布局inflate，这样打开PagerViewHolder便可以直接找到使用的布局文件：
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
    
    public void add() {
        itemCount++;
        notifyDataSetChanged();
    }
    
    public void remove() {
        itemCount--;
        itemCount = itemCount < 0 ? 0 : itemCount;
        notifyDataSetChanged();
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
## 原理
ViewPager最多构造四个相同类型的页面，但是现实时最多需要当前页面及左右两个页面，第四个页面就可以存起来复用。
Adapter使用一个ArrayList<VH>来存放所有的Holder，当刷新页面时需要重新一个个onBindViewHolder；
再用一个SparseArray<ArrayList<VH>>来根据viewType存放在destroyItem时候被回收的不同类型的Holder集合，在instantiateItem时候优先从其内部获取，在没有时再重新创建。

## 注意
- notifyDataSetChanged()方法会将所有的未被回收的Holder重新onBindViewHolder一遍，并不是每一个ViewPager的页面都会刷新，但当前显示的绝对会刷新；
- notifyItemChanged(int position)用于刷新指定的页面坐标的Holder，只有在这个页面处于激活状态时，其才会被刷新。

# GradientTabStrip
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/gradienttabstrip/icon.png)

继承自BaseTabStrip，实现微信式渐变底部Tab效果，为ViewPager添加如PagerTitleStrip一样的Tab，但支持更多自定义功能，并支持为Tab增加标记点功能，并可以自定义标记点各自的位置及显示状态以及背景等。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/gradienttabstrip/screenshots.gif)
## 要求
- minSdkVersion 4
- com.android.support:support-v4:24.0.0

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:gradienttabstrip:3.0.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.gradienttabstrip.GradientTabStrip
    android:id="@+id/gts_gts_tabs"
    android:layout_width="match_parent"
    android:layout_height="64dp"
    android:textColor="@color/color_gradienttabstrip_tab"
    android:textSize="12sp"
    app:gtsBackground="@drawable/bg_common_press"/>
```
- 基本代码
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
## 注意
- 不要使用ViewPage的setCurrentItem(int)方法，其不会通知到GradientTabStrip进行刷新，使用GradientTabStrip的performClick(int)方法
- 布局时，android:textColor指定的颜色可以使用选择器，其中android:state_selected="true"状态下的颜色会与普通状态下的颜色进行渐变
- GradientTabAdapter中进行了改变GradientTabAdapter，需要手动通知GradientTabStrip进行刷新
- 不需要Tag小红点，可以使用SimpleGradientTabAdapter替代GradientTabAdapter
- 不支持com.android.support:support-v4:24.0.0以下的所有版本

## 历史
- [**2.1.0**](https://github.com/AlexMofer/ProjectX/tree/master/gradienttabstrip/history/2.1.0)
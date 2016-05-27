# IndicatorTabStrip
 继承自BaseTabStrip，移动式下标渐变缩放Tab，Item不建议超过5个

![ICON](https://github.com/AlexMofer/ProjectX/blob/master/indicatortabstrip/icon.png)
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/indicatortabstrip/screenshots.gif)

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:indicatortabstrip:2.1.0'
    ⋯
}
```

## 功能
为ViewPager添加如PagerTitleStrip一样的Tab，但支持更多自定义功能
并支持为Tab增加标记点功能，并可以自定义标记点各自的位置及显示状态以及背景等

## 使用
- 基本布局
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
⋯
```
- 基本代码
```java
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
tabs.bindViewPager(viewPager);// IndicatorTabStrip放置于ViewPager内部则不需要此行
```

## 注意
- 不要使用ViewPage的setCurrentItem(int)方法，其不会通知到IndicatorTabStrip进行刷新，使用IndicatorTabStrip的performClick(int)方法
- 布局时，android:textColor指定的颜色可以使用选择器，其中android:state_selected="true"状态下的颜色会与普通状态下的颜色进行渐变
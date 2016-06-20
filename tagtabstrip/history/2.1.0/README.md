# TagTabStrip
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/tagtabstrip/icon.png)

继承自BaseTabStrip，实现ViewPager标志小点，一般用于功能引导页面及新功能简介页，为ViewPager添加标志小点，并不仅限于小点，标志由设置的Drawable决定，普通模式为双Drawable交替模式，亦可设置为单Drawable缩放模式。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/tagtabstrip/screenshots.gif)
## 要求
- minSdkVersion 4
- com.android.support:support-v4:23.0.0(<24.0.0)

## 警告
不支持com.android.support:support-v4:24.0.0及以上版本，需要支持24.0.0，请使用最新版
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:tagtabstrip:2.1.0'
    ⋯
}
```
## 使用
- 基本布局

```xml
<am.widget.tagtabstrip.TagTabStrip
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:drawablePadding="6dp"
    android:gravity="center"
    app:ttsScale="1.6"
    app:ttsDrawable="@drawable/ic_tag"/>
```
- 基本代码
```java
TagTabStrip ttsTags = (TagTabStrip) findViewById(id);
ttsTags.bindViewPager(viewpager);
```
## 注意
- 不要使用ViewPage的setCurrentItem(int)方法，其不会通知到TagTabStrip进行刷新，使用TagTabStrip的performClick(int)方法
- 布局时，app:ttsDrawable指定的颜色可以使用选择器，其中android:state_selected="true"状态下的颜色会与普通状态下的Drawable进行渐变
- app:ttsScale指定的值需要大于等于1才有效
- android:drawablePadding与app:ttsDrawablePadding等效，只需设置其中一个
- 默认状态是颜色为0xff808080与0x80808080的大小为8dp的两个小圆点Drawable，之间没有间距
- android:gravity默认值就是center，可以不设置
- 不支持com.android.support:support-v4:24.0.0及以上版本
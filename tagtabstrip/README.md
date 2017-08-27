# TagTabStrip
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/tagtabstrip/icon.png)

继承自BaseTabStrip，实现ViewPager标志小点，一般用于功能引导页面及新功能简介页，为ViewPager添加标志小点，并不仅限于小点，标志由设置的Drawable决定，普通模式为双Drawable交替模式，亦可设置为单Drawable缩放模式。
## 预览
![Screenshots](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/tagtabstrip/screenshots.gif)
## 要求
- minSdkVersion 14
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7）

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:tagtabstrip:26.0.1'
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
- 保持跟其他官方支持库版本一致（如：com.android.support:appcompat-v7），否则可能出现错误

## 历史
- [**26.0.0**](https://bintray.com/alexmofer/maven/TagTabStrip/26.0.0)
- [**26.0.0-beta2**](https://bintray.com/alexmofer/maven/TagTabStrip/26.0.0-beta2)
- [**26.0.0-beta1**](https://bintray.com/alexmofer/maven/TagTabStrip/26.0.0-beta1)
- [**25.4.0**](https://bintray.com/alexmofer/maven/TagTabStrip/25.4.0)
- [**26.0.0-alpha1**](https://bintray.com/alexmofer/maven/TagTabStrip/26.0.0-alpha1)
- [**25.3.1**](https://bintray.com/alexmofer/maven/TagTabStrip/25.3.1)
- [**25.3.0**](https://bintray.com/alexmofer/maven/TagTabStrip/25.3.0)
- [**25.2.0**](https://bintray.com/alexmofer/maven/TagTabStrip/25.2.0)
- [**25.1.1**](https://bintray.com/alexmofer/maven/TagTabStrip/25.1.1)
- [**25.1.0**](https://bintray.com/alexmofer/maven/TagTabStrip/25.1.0)
- [**25.0.0**](https://bintray.com/alexmofer/maven/TagTabStrip/25.0.0)
- [**24.2.1**](https://bintray.com/alexmofer/maven/TagTabStrip/24.2.1)
- [**24.2.0**](https://bintray.com/alexmofer/maven/TagTabStrip/24.2.0)
- [**3.1.1**](https://bintray.com/alexmofer/maven/TagTabStrip/3.1.1)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/tagtabstrip/history/3.1.1)）
- [**3.1.0**](https://bintray.com/alexmofer/maven/TagTabStrip/3.1.0)
- [**3.0.0**](https://bintray.com/alexmofer/maven/TagTabStrip/3.0.0)
- [**2.1.0**](https://bintray.com/alexmofer/maven/TagTabStrip/2.1.0)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/tagtabstrip/history/2.1.0)）
- [**2.0.3**](https://bintray.com/alexmofer/maven/TagTabStrip/2.0.3)
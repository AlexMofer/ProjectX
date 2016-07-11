# HeaderFooterGridView
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/headerfootergridview/icon.png)

头尾GridView，支持AUTO_FIT模式，头尾模式有两种，无反射代码。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/headerfootergridview/screenshots.gif)
## 要求
minSdkVersion 4

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:headerfootergridview:1.0.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.headerfootergridview.HeaderFooterGridView
    android:id="@+id/gird_hfg_content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:columnWidth="64dp"
    android:gravity="center"
    android:horizontalSpacing="10dip"
    android:numColumns="auto_fit"
    android:stretchMode="columnWidth"
    android:verticalSpacing="10dip" />
```
- 基本代码
```java
HeaderFooterGridView hfgContent = (HeaderFooterGridView) findViewById(id);
hfgContent.addHeaderView(headerView);
hfgContent.removeHeaderView(headerView);
hfgContent.addHeaderItem(headerItem);
hfgContent.removeHeaderItem(headerItem);
hfgContent.addHeaderItem(headerItem, null, true);
hfgContent.addFooterItem(footerItem);
hfgContent.removeFooterItem(footerItem);
hfgContent.addFooterItem(footerItem, null, true);
hfgContent.addFooterView(footerView);
hfgContent.removeFooterView(footerView);
```
## 注意
- 空白站位View占用GridView的子项position，处理position时需要小心
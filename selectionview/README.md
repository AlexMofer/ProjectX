# SelectionView
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/selectionview/icon.png)

快速跳选View，与列表视图搭配使用（ListView、RecyclerView），常用于联系人列表快速选取。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/selectionview/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:selectionview:1.0.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.selectionview.SelectionView xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/selection_sv_selection"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:svBarBackground="@drawable/bg_selection_bar"
    app:svBarItemHeight="32dp"
    app:svBarSlider="@drawable/ic_selection_slider"
    app:svBarStyle="List"
    app:svBarWidth="32dp"
    app:svNoticeAnimation="true"
    app:svNoticeBackground="@drawable/bg_selection_notice"
    app:svNoticeHeight="64dp"
    app:svNoticeLocation="ViewCenter"
    app:svNoticePadding="10dp"
    app:svNoticeWidth="64dp" />
```
- 基本代码
```java
SelectionView selection = (SelectionView) findViewById(R.id.selection_sv_selection);
selection.setBarStyle(SelectionView.STYLE_SLIDER);
selection.setBarBackground(0);
selection.setNoticeBackground(R.drawable.bg_selection_notice_slider);
selection.setNoticeLocation(SelectionView.LOCATION_SLIDER_TOP);
selection.setSelection(this);
selection.setOnSelectedListener(this);
```
## 注意
- 基本可以通过布局实现基础代码
- Selection在不同模式下计算方式不同，列表模式下，子项总数为类别数。滑块模式下，子项总数为列表子项总数。
# MultiActionTextView
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/multiactiontextview/icon.png)

文字可点击TextView，设置文字部分可点击，点击执行不同操作。
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/multiactiontextview/screenshots.gif)
## 要求
无

## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:multiactiontextview:1.0.1'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.multiactiontextview.MultiActionTextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
- 基本代码
```java
MultiActionTextView textView = (MultiActionTextView) findViewById(id);
MultiActionClickableSpan action1 = new MultiActionClickableSpan(0, 7, colorPrimary, true, false, listener);
MultiActionClickableSpan action2 = new MultiActionClickableSpan(10, 15, colorAccent, false, true, listener);
MultiActionClickableSpan action3 = new MultiActionClickableSpan(134, 140, colorRipple, false, true, listener);
textView.setText(text, action1, action2, action3);
```
## 注意
- MultiActionClickableSpan可设置是否拦截View的OnClickListener响应
- MultiActionClickableSpan可以设置不同的颜色

## 历史
- [**1.0.0**](https://bintray.com/alexmofer/maven/MultiActionTextView/1.0.0)
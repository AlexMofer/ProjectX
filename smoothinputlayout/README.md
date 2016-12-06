# SmoothInputLayout
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/smoothinputlayout/icon.png)

仿微信式，平滑输入面板，防止键盘的出现与消失导致特殊输入面板的顶起于塌陷。
## 预览
![Screenshots](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/smoothinputlayout/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:smoothinputlayout:1.0.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.smoothinputlayout.SmoothInputLayout
    android:id="@+id/sil_lyt_content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    app:silInputView="@+id/sil_edt_input"
    app:silInputPane="@+id/sil_lyt_pane">

    <View
        android:id="@+id/sil_v_list"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_main_tab_bar"
        android:orientation="horizontal"
        android:padding="5dp">

        <android.support.v7.widget.AppCompatImageButton
            android:id="@+id/sil_ibtn_voice"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="@color/transparent"
            android:padding="5dp"
            android:contentDescription="@string/cd_smoothinputlayout_voice"
            android:src="@drawable/ic_smoothinputlayout_voice" />

        <android.support.v7.widget.AppCompatEditText
            android:id="@id/sil_edt_input"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:maxLines="4"
            android:inputType="textMultiLine"
            android:layout_weight="1" />

        <android.support.v7.widget.AppCompatImageButton
            android:id="@+id/sil_ibtn_emoji"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="@color/transparent"
            android:padding="5dp"
            android:contentDescription="@string/cd_smoothinputlayout_emoji"
            android:src="@drawable/ic_smoothinputlayout_emoji" />

        <android.support.v7.widget.AppCompatButton
            android:id="@+id/sil_btn_send_voice"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:text="@string/smoothinputlayout_voice"
            android:background="@drawable/bg_smoothinputlayout_voice"
            android:textColor="#ff959595"
            android:visibility="gone"
            android:layout_weight="1"/>

        <android.support.v7.widget.AppCompatImageButton
            android:id="@+id/sil_ibtn_more"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="@color/transparent"
            android:padding="5dp"
            android:contentDescription="@string/cd_smoothinputlayout_more"
            android:src="@drawable/ic_smoothinputlayout_more" />

        <android.support.v7.widget.AppCompatImageButton
            android:id="@+id/sil_ibtn_send"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="@color/transparent"
            android:padding="5dp"
            android:visibility="gone"
            android:contentDescription="@string/cd_smoothinputlayout_send"
            android:src="@drawable/ic_smoothinputlayout_send" />
    </LinearLayout>

    <FrameLayout
        android:id="@id/sil_lyt_pane"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:visibility="gone">

        <LinearLayout
            android:id="@+id/sil_lyt_emoji"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <include layout="@layout/merge_smoothinputlayout_emoji"/>

        </LinearLayout>

        <LinearLayout
            android:id="@+id/sil_lyt_more"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <include layout="@layout/merge_smoothinputlayout_more"/>

        </LinearLayout>
    </FrameLayout>

</am.widget.smoothinputlayout.SmoothInputLayout>
```
- 基本代码
```java
SmoothInputLayout lytContent = (SmoothInputLayout) findViewById(R.id.sil_lyt_content);
lytContent.closeInputPane();// 关闭面板
lytContent.closeKeyboard(true);// 关闭键盘
lytContent.showKeyboard();// 显示键盘
lytContent.showInputPane(true);//显示面板
```
## 注意
- app:silInputView 用于指定输入框，一般是EditText，此处用+id，否则会报错，在该EditText上用@id即可
- app:silInputPane 用于指定输入面板，此处也是用+id，否则会报错，在该面板View上用@id即可
- 控件按钮间的逻辑关系参照样例Activity
- 控件的目的是防止键盘的出现与消失导致特殊输入面板的顶起于塌陷。
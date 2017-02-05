# StateFrameLayout
![ICON](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/stateframelayout/icon.png)

状态帧布局，通常用于网络请求的四种状态，普通、载入、错误、空白。支持Drawable或者View来展示，也可以混搭。即可以通过指定状态布局文件来实现，也可以为子项添加状态属性标志来实现。
## 预览
![Screenshots](https://raw.githubusercontent.com/AlexMofer/ProjectX/master/stateframelayout/screenshots.gif)
## 要求
minSdkVersion 4
## 引用
```java
dependencies {
    ⋯
    compile 'am.widget:stateframelayout:2.0.0'
    ⋯
}
```
## 使用
- 基本布局
```xml
<am.widget.stateframelayout.StateFrameLayout
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    custom:sflLoadingDrawable="@drawable/"
    custom:sflErrorDrawable="@drawable/"
    custom:sflEmptyDrawable="@drawable/"
    custom:sflState="loading">
    ⋯
</am.widget.stateframelayout.StateFrameLayout>
```
- 基本代码
```java
lytState = (StateFrameLayout) findViewById(R.id.sfl_lyt_state);
lytState.setOnStateClickListener(listener);
lytState.setStateDrawables(mLoadingDrawable, mErrorDrawable, mEmptyDrawable);
lytState.setStateViews(mLoadingView, mErrorView, mEmptyView);
```
## 注意
- 继承自帧布局
- 各种状态下都不拦截子View的点击事件，除Normal状态且isAlwaysDrawChild() == true时，内容子项不会被绘制及显示
- setAlwaysDrawChild(true)强制各种状态下都显示内容子项
- 可通过sflLoadingLayout指定载入状态下的View布局
- 可通过sflErrorLayout指定错误状态下的View布局
- 可通过sflEmptyLayout指定空状态下的View布局
- 布局内部子项可通过添加sflLayout_state属性来定义其为那种状态下的状态View。（如：custom:sflLayout_state="loading"）

## 历史
- [**1.0.3**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.3)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/stateframelayout/history/1.0.3)）
- [**1.0.2**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.2)
- [**1.0.1**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.1)
- [**1.0.0**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.0)
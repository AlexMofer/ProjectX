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
## 属性及方法说明
xml布局属性|属性值|对应方法|说明
---|---|---|---
custom:sflAlwaysDrawChild|boolean|void setAlwaysDrawChild(boolean draw)|设置是否始终绘制子项
|||boolean isAlwaysDrawChild()|是否始终绘制子项
custom:sflState|normal、loading、error、empty|void setState(int state)|设置状态
|||int getState()|获取状态
custom:sflLoadingDrawable|reference|void setLoadingDrawable(int loading)|设置载入 Drawable
|||void setLoadingDrawable(Drawable loading)|设置载入 Drawable
custom:sflErrorDrawable|reference|void setErrorDrawable(int error)|设置错误 Drawable
|||void setErrorDrawable(Drawable error)|设置错误 Drawable
custom:sflEmptyDrawable|reference|void setEmptyDrawable(int empty)|设置空白 Drawable
|||void setEmptyDrawable(Drawable empty)|设置空白 Drawable
|||void setStateDrawables(int loading, int error, int empty)|设置状态 Drawable
|||void setStateDrawables(Drawable loading, Drawable error, Drawable empty)|设置状态 Drawable
custom:sflLoadingLayout|reference|||设置载入状态下的View布局
|||void setLoadingView(View loadingView)|设置自定义载入View
|||void setLoadingView(View loadingView, LayoutParams layoutParams)|设置自定义载入View
custom:sflErrorLayout|reference|||设置错误状态下的View布局
|||void setErrorView(View errorView)|设置自定义错误View
|||void setErrorView(View errorView, LayoutParams layoutParams)|设置自定义错误View
custom:sflEmptyLayout|reference|||设置空状态下的View布局
|||void setEmptyView(View emptyView)|设置自定义空白View
|||void setEmptyView(View emptyView, LayoutParams layoutParams)|设置自定义空白View
|||void setStateViews(View loading, View error, View empty)|设置状态View
|||void invalidateState()|刷新状态
|||void normal()|修改状态为普通
|||void loading()|修改状态为载入
|||void error()|修改状态为错误
|||void empty()|修改状态为空白
|||void setOnStateClickListener(OnStateClickListener listener)|状态点击监听
custom:sflLayout_state|normal、loading、error、empty|LayoutParams.setState(int state)|为布局内部的子项设置状态


## 注意
- StateFrameLayout继承自FrameLayout，StateLinearLayout继承自LinearLayout，StateRelativeLayout继承自RelativeLayout
- 各种状态下都不拦截子View的点击事件，除Normal状态且isAlwaysDrawChild() == true时，内容子项不会被绘制及显示
- setAlwaysDrawChild(true)强制各种状态下都显示内容子项
- 可通过sflLoadingLayout指定载入状态下的View布局
- 可通过sflErrorLayout指定错误状态下的View布局
- 可通过sflEmptyLayout指定空状态下的View布局
- 布局内部子项可通过添加sflLayout_state属性来定义其为哪种状态下的状态View。（如：custom:sflLayout_state="loading"）
- OnStateClickListener为仅仅监听错误时候的点击，OnAllStateClickListener则为全状态监听。

## 历史
- [**1.0.3**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.3)（[**说明**](https://github.com/AlexMofer/ProjectX/tree/master/stateframelayout/history/1.0.3)）
- [**1.0.2**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.2)
- [**1.0.1**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.1)
- [**1.0.0**](https://bintray.com/alexmofer/maven/StateFrameLayout/1.0.0)
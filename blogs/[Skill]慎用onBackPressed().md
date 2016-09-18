# 慎用onBackPressed()
Android中在按下back键时会调用到onBackPressed()方法，onBackPressed相对于finish方法，还做了一些其他操作，而这些操作涉及到Activity的状态，所以调用还是需要谨慎对待。

##问题描述
最近公司的项目在Bug统计当中，发现了一大堆IllegalStateException的报错：
```java
java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
	at android.support.v4.app.FragmentManagerImpl.checkStateLoss(Unknown Source)
	at android.support.v4.app.FragmentManagerImpl.popBackStackImmediate(Unknown Source)
	at android.support.v4.app.FragmentActivity.onBackPressed(Unknown Source)
	at android.view.View.performClick(View.java:4768)
	at android.view.View$PerformClick.run(View.java:19692)
	at android.os.Handler.handleCallback(Handler.java:739)
	at android.os.Handler.dispatchMessage(Handler.java:95)
	at android.os.Looper.loop(Looper.java:135)
	at android.app.ActivityThread.main(ActivityThread.java:5539)
	at java.lang.reflect.Method.invoke(Native Method)
	at java.lang.reflect.Method.invoke(Method.java:372)
	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:960)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:755)
```

##问题解决
在Activity已经保存了状态以后（onSaveInstanceState）进行了Fragment退栈操作（popBackStackImmediate），触发方法为调用了onBackPressed方法首先我们翻API 24的源码来看看onBackPressed里面到底都干啥了：
```java
/**
 * Called when the activity has detected the user's press of the back
 * key.  The default implementation simply finishes the current activity,
 * but you can override this to do whatever you want.
 */
public void onBackPressed() {
    if (mActionBar != null && mActionBar.collapseActionView()) {
        return;
    }

    if (!mFragments.getFragmentManager().popBackStackImmediate()) {
        finishAfterTransition();
    }
}
```
果然，onBackPressed首先去关闭ActionBar的展开菜单（collapseActionView），其次再对FragmentManager进行退栈操作（popBackStackImmediate），最后才关闭Activity，低版本直接调用finish，高版本调用finishAfterTransition。而当Activity处于onSaveInstanceState状态之后时，调用onBackPressed报错，其实在Activity处于活动状态下时是没有任何问题的。个人习惯通常把Toolbar与onBackPressed方法捆绑起来：
```java
/**
 * 设置Toolbar 为ActionBar
 * @param toolbarId Toolbar资源ID
 */
public void setSupportActionBar(@IdRes int toolbarId) {
    Toolbar mToolbar = (Toolbar) findViewById(toolbarId);
    if (mToolbar != null) {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationClick();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}

/**
 * Toolbar 返回按钮点击
 */
protected void onNavigationClick() {
    onBackPressed();
}
```
这里不存在问题，因为Toolbar的返回按钮必须得在Activity活动状态下才能点击到，而从代码层则需要通过遍历或者id找到到这个ImageButton再对其进行performClick操作（没有id，只能遍历），而真正问题出在于我将其写在网络请求结果处理的回调里面，毫无疑问，这就会出现Activity处于非活动状态了。

##结论
- 调用onBackPressed()方法需要注意Activity状态。
- 调用onBackPressed()方法不一定就能结束Activity。
- 调用onBackPressed()方法结束Activity，其调用的终究还是finish()方法。
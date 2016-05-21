# GradientPagerTabStrip
  继承自View，实现ViewPager.Decor接口（因其为包内私有接口，无法外部继承，故BaseTabStrip类必须存放在android.support.v4.view包中），版本兼容到API4(Android 1.6)
## 预览
-![alt text](https://github.com/AlexMofer/ProjectX/blob/master/Screenshot.gif "Screenshot")
  
## 功能
  为ViewPager添加如PagerTitleStrip一样的Tab，但支持更多自定义功能
  并支持为Tab增加标记点功能，并可以自定义标记点各自的位置及显示状态以及背景等
## 使用
```xml
  <android.support.v4.view.ViewPager xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:tools="http://schemas.android.com/tools"
      android:id="@+id/vp"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context=".MainActivity">
  
      <com.am.gradientpagertabstrip.view.GradientPagerTabStrip
          android:id="@+id/tab"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:textSize="16sp" />
  </android.support.v4.view.ViewPager>
```
```java
  mPagerTab = (GradientPagerTabStrip) findViewById(R.id.tab);
  mPagerTab.setTabsInterval((int) (10 * density));
  mPagerTab.setTabIndicator(0xffffbb33, (int) (2 * density),
  	(int) (10 * density));
  mPagerTab.setUnderline(0xff669900, (int) (2 * density));
  mPagerTab.setTextGradient(0xff8bc34a, 0xff33691e);
  mPagerTab.showTextScale(true);
  mPagerTab.setMagnification(1.25f);
  mPagerTab.showTabGradient(true);
  mPagerTab.setTabGradient(0xff00ddff, 0xff0099cc);

  TabTagAdapter adapter = new TabTagAdapter(context) {

  	@Override
  	public String getTag(int position) {
  		switch (position) {
  		default:
  		case 0:
  			return "000";
  		case 1:
  			return "1";
  		case 2:
  			return "New";
  		}
  	}
			
  	@Override
  	public boolean isEnable(int position) {
  		if (position == 0) {
  			return false;
  		} else {
  			return true;
  		}
  	}
  };
  mPagerTab.setTagAdapter(adapter);
```

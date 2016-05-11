package am.project.x.widgets.utils;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

/**
 * ActionBar 工具
 * 
 * @author Mofer
 * 
 */
public class ActionBarUtils {

	/**
	 * 设置ActionBar居中标题
	 * 
	 * @param activity
	 * @param resource
	 * @return
	 */
	public static void setSupportActionBarFillView(AppCompatActivity activity,
			int resource) {
		setSupportActionBarFillView(activity,
				View.inflate(activity, resource, null));
	}

	/**
	 * 设置ActionBar居中标题
	 * 
	 * @param activity
	 * @param customView
	 * @return
	 */
	public static void setSupportActionBarFillView(AppCompatActivity activity,
			View customView) {
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		activity.getSupportActionBar().setCustomView(customView, params);
		activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
}

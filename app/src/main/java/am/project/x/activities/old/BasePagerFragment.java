package am.project.x.activities.old;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment在ViewPager中的创建与销毁流程不同
 * <p> 不复用RootView会导致出现页面无响应的BUG
 * @author Alex
 *
 */
public abstract class BasePagerFragment extends Fragment {

	private View mRootView;
	private boolean initialized = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(
					setContentLayout(inflater, container, savedInstanceState),
					container, false);
			initResource();
			initialized = true;
		} else {
			reuseView();
		}
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (null != mRootView) {
			((ViewGroup) mRootView.getParent()).removeView(mRootView);
		}
	}

	/**
	 * 设置内容Layout
	 * <p>
	 * 返回的必须是资源 layout ID
	 *
	 * @param inflater 布局容器
	 * @param container 根View
	 * @param savedInstanceState 保存的状态
	 * @return 资源id
	 */
	@LayoutRes
	protected abstract int setContentLayout(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	/**
	 * 初始化
	 */
	protected abstract void initResource();

	/**
	 * 复用View
	 */
	protected void reuseView() {
		
	}
	
	/**
	 * Look for a child view with the given id. If this view has the given id,
	 * return this view.
	 * 
	 * @param id
	 *            The id to search for.
	 * @return The view that has the given id in the hierarchy or null
	 */
	public View findViewById(int id) {
		if (mRootView == null) {
			throw new IllegalStateException("Fragment " + this
					+ " RootView not created");
		}
		return mRootView.findViewById(id);
	}

	/**
	 * 已完成初始化
	 * @return Fragment是否已经完成初始化
	 */
	public boolean isInitialized() {
		return initialized;
	}
}

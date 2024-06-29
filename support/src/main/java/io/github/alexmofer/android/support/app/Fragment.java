package io.github.alexmofer.android.support.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 功能拓展的Fragment
 * Created by Alex on 2024/2/29.
 */
public class Fragment extends androidx.fragment.app.Fragment {

    private View mToolbar;

    public Fragment() {
    }

    public Fragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    @Nullable
    public final <V extends View> V findViewById(int id) {
        final View view = getView();
        return view == null ? null : view.findViewById(id);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    @NonNull
    public final <V extends View> V requireViewById(int id) {
        final View view = getView();
        if (view == null) {
            throw new IllegalArgumentException("Fragment does not has a View");
        }
        final V v = view.findViewById(id);
        if (v == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this Fragment");
        }
        return v;
    }

    /**
     * 获取Toolbar
     *
     * @param <T> androidx.appcompat.widget.Toolbar 或 android.widget.Toolbar
     * @return Toolbar
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends View> T getToolbar() {
        return (T) mToolbar;
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbar Toolbar
     */
    public final void setToolbar(View toolbar) {
        if (toolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar tb =
                    (androidx.appcompat.widget.Toolbar) toolbar;
            tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
            tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
            mToolbar = toolbar;
            invalidateToolbarMenu();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar tb =
                        (android.widget.Toolbar) toolbar;
                tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
                tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
                mToolbar = toolbar;
                invalidateToolbarMenu();
            }
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setToolbar(@IdRes int toolbarId) {
        setToolbar(requireView().findViewById(toolbarId));
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     */
    protected void onToolbarNavigationClick(View v) {
        if (hideOverflowMenu()) {
            return;
        }
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * 更新Toolbar菜单
     *
     * @param menu 菜单
     */
    protected void onToolbarMenuUpdate(@NonNull Menu menu) {
    }

    /**
     * 点击Toolbar的菜单子项
     *
     * @param item 子项
     * @return 是否消耗掉这次点击事件
     */
    protected boolean onToolbarMenuItemClick(@NonNull MenuItem item) {
        return false;
    }

    /**
     * 刷新Toolbar菜单
     */
    public void invalidateToolbarMenu() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) mToolbar;
            onToolbarMenuUpdate(toolbar.getMenu());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar toolbar =
                        (android.widget.Toolbar) mToolbar;
                onToolbarMenuUpdate(toolbar.getMenu());
            }
        }
    }

    /**
     * 隐藏溢出菜单
     *
     * @return 完成隐藏时返回true
     */
    @SuppressLint("RestrictedApi")
    protected boolean hideOverflowMenu() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            return ((androidx.appcompat.widget.Toolbar) mToolbar).hideOverflowMenu();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                return ((android.widget.Toolbar) mToolbar).hideOverflowMenu();
            }
        }
        return false;
    }

    /**
     * 获取回调
     *
     * @return 回调
     */
    @Nullable
    protected <T> T getCallback(@NonNull Class<T> clazz) {
        return FragmentUtils.getCallback(this, clazz);
    }
}

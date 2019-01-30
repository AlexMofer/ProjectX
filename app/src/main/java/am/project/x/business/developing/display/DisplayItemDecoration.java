package am.project.x.business.developing.display;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import am.project.x.utils.ThemeUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * ItemDecoration
 * Created by Xiang Zhicheng on 2017/10/18.
 */
class DisplayItemDecoration extends RecyclerView.ItemDecoration {

    static final int TYPE_FIRST = 0;
    static final int TYPE_NORMAL = 1;
    static final int TYPE_LAST = 2;
    private final int mGap;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mToolbarHeight;
    private int mOrientation = LinearLayoutManager.VERTICAL;

    DisplayItemDecoration(Context context) {
        mGap = 20;
        mToolbarHeight = ThemeUtil.getDimensionPixelSize(context.getTheme(),
                androidx.appcompat.R.attr.actionBarSize, 0);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        @SuppressWarnings("ConstantConditions") final int itemCount = parent.getAdapter().getItemCount();
        final int type = position == 0 ? TYPE_FIRST :
                (position == itemCount - 1 ? TYPE_LAST : TYPE_NORMAL);
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            getHorizontalDecoration(outRect, type);
        } else {
            getVerticalDecoration(outRect, type);
        }
    }

    void getHorizontalDecoration(Rect decoration, int type) {
        decoration.top = mPaddingTop + mToolbarHeight;
        decoration.bottom = mPaddingBottom;
        switch (type) {
            default:
            case TYPE_NORMAL:
                decoration.left = mGap;
                decoration.right = mGap;
                break;
            case TYPE_FIRST:
                decoration.left = mGap * 2;
                decoration.right = mGap;
                break;
            case TYPE_LAST:
                decoration.left = mGap;
                decoration.right = mGap * 2;
                break;
        }
    }

    void getVerticalDecoration(Rect decoration, int type) {
        decoration.left = 0;
        decoration.right = 0;
        switch (type) {
            default:
            case TYPE_NORMAL:
                decoration.top = mGap;
                decoration.bottom = mGap;
                break;
            case TYPE_FIRST:
                decoration.top = mPaddingTop + mToolbarHeight + mGap * 2;
                decoration.bottom = mGap;
                break;
            case TYPE_LAST:
                decoration.top = mGap;
                decoration.bottom = mPaddingBottom + mGap * 2;
                break;
        }
    }

    @SuppressWarnings("unused")
    void fitSystemWindows(int left, int top, int right, int bottom) {
        mPaddingTop = top;
        mPaddingBottom = bottom;
    }

    void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    void updateDecorationMaxWidthOfChildWithMaxSize(DisplayLayoutManager layoutManager) {
        if (layoutManager == null)
            return;
        final int left;
        final int top;
        final int right;
        final int bottom;
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            left = mGap * 2;
            top = mPaddingTop + mToolbarHeight;
            right = mGap * 2;
            bottom = mPaddingBottom;
        } else {
            left = 0;
            top = mPaddingTop + mToolbarHeight + mGap * 2;
            right = 0;
            bottom = mPaddingBottom + mGap * 2;
        }
        layoutManager.setDecorationMaxWidthOfChildWithMaxSize(left, right, top, bottom);
    }

    int getDisplayWidth(RecyclerView view) {
        if (view == null)
            return 0;
        final int width = view.getMeasuredWidth() - view.getPaddingLeft() - view.getPaddingRight();
        return width <= 0 ? 0 : width;
    }

    int getDisplayHeight(RecyclerView view) {
        if (view == null)
            return 0;
        final int height = view.getMeasuredHeight() - mPaddingTop - mPaddingBottom - mToolbarHeight;
        return height <= 0 ? 0 : height;
    }
}
package am.project.x.business.widgets.multifunctionalrecyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * ItemDecoration
 * Created by Alex on 2017/10/18.
 */
class ItemDecoration extends RecyclerView.ItemDecoration {

    private static final int TYPE_FIRST = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_LAST = 2;
    private final int mGap;

    ItemDecoration(int gap) {
        mGap = gap;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        @SuppressWarnings("ConstantConditions") final int itemCount = parent.getAdapter().getItemCount();
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int type = position == 0 ? TYPE_FIRST :
                (position == itemCount - 1 ? TYPE_LAST : TYPE_NORMAL);
        if (manager instanceof LinearLayoutManager &&
                ((LinearLayoutManager) manager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
            outRect.top = mGap * 2;
            outRect.bottom = mGap * 2;
            switch (type) {
                default:
                case TYPE_NORMAL:
                    outRect.left = mGap;
                    outRect.right = mGap;
                    break;
                case TYPE_FIRST:
                    outRect.left = mGap * 2;
                    outRect.right = mGap;
                    break;
                case TYPE_LAST:
                    outRect.left = mGap;
                    outRect.right = mGap * 2;
                    break;
            }
        } else {
            outRect.left = mGap * 2;
            outRect.right = mGap * 2;
            switch (type) {
                default:
                case TYPE_NORMAL:
                    outRect.top = mGap;
                    outRect.bottom = mGap;
                    break;
                case TYPE_FIRST:
                    outRect.top = mGap * 2;
                    outRect.bottom = mGap;
                    break;
                case TYPE_LAST:
                    outRect.top = mGap;
                    outRect.bottom = mGap * 2;
                    break;
            }
        }
    }
}
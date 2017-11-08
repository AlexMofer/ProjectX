package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import am.project.x.R;
import am.project.x.widgets.display.DisplayItemDecoration;
import am.project.x.widgets.display.DisplayLayoutManager;
import am.util.mvp.AMAppCompatActivity;
import am.widget.scrollbarrecyclerview.DefaultScrollbar;
import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;

public class TestActivity extends AMAppCompatActivity {

    private final DisplayAdapter mAdapter = new DisplayAdapter();
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);

        final RecyclerView rvContent = findViewById(R.id.display_rv_content);

        DisplayItemDecoration decoration = new DisplayItemDecoration(this);
        rvContent.addItemDecoration(decoration);
        final DisplayLayoutManager layoutManager = new DisplayLayoutManager(this);
        layoutManager.setChildMaxSize(600 + 20 * 40, 900 + 20 * 40);
        layoutManager.setDecorationMaxWidthOfChildWithMaxSize(decoration.getMargin(),
                decoration.getMargin(), decoration.getMargin(), decoration.getMargin());
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(mAdapter);


        findViewById(R.id.display_btn_temp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rvContent.invalidateItemDecorations();
                ((DefaultScrollbar) ((ScrollbarRecyclerView) rvContent).getScrollbar()).setShowType(ScrollbarRecyclerView.Scrollbar.SHOW_VERTICAL);
//                rvContent.smoothScrollBy(0, 200);
//                mAdapter.notifyItemRangeChanged(0, 5);
            }
        });

        findViewById(R.id.display_btn_temp2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rvContent.invalidateItemDecorations();
                ((DefaultScrollbar) ((ScrollbarRecyclerView) rvContent).getScrollbar()).setShowType(ScrollbarRecyclerView.Scrollbar.SHOW_HORIZONTAL);
//                rvContent.smoothScrollBy(0, 200);
//                mAdapter.notifyItemRangeChanged(0, 5);
            }
        });

        findViewById(R.id.display_btn_temp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.setLayoutInCenter(!layoutManager.isLayoutInCenter());
//                rvContent.smoothScrollBy(0, 200);
//                mAdapter.notifyItemRangeChanged(0, 5);
            }
        });

        findViewById(R.id.display_btn_temp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.setPagingEnable(!layoutManager.isPagingEnable());
//                rvContent.smoothScrollBy(0, 200);
//                mAdapter.notifyItemRangeChanged(0, 5);
            }
        });
    }
}

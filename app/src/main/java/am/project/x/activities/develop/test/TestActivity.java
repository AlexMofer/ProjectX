package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.draglayout.DragLayout;
import am.widget.treelayout.TreeLayout;

public class TestActivity extends BaseActivity {


    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
        findViewById(R.id.drag).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TreeLayout treeLayout = (TreeLayout) ((ViewGroup) view).getChildAt(0);
                if (treeLayout.isExpand()) {
                    treeLayout.setExpand(false);
                    DragLayout.LayoutParams lp = (DragLayout.LayoutParams) treeLayout.getLayoutParams();
                    lp.setDraggable(true);
                    return true;
                }
                return false;
            }
        });
    }

    public void show(View view) {
        Toast.makeText(this, "show", Toast.LENGTH_SHORT).show();
        TreeLayout treeLayout = (TreeLayout) view.getParent();
        DragLayout.LayoutParams lp = (DragLayout.LayoutParams) treeLayout.getLayoutParams();
        treeLayout.setExpand(!treeLayout.isExpand());
        treeLayout.setRight(!lp.isCloseToStart());
        lp.setDraggable(!lp.isDraggable());
    }

    public void message(View view) {
        Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }
}

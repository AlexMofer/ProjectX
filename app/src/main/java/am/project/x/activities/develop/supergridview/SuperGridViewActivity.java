package am.project.x.activities.develop.supergridview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.widgets.supergridview.DragController;
import am.project.x.widgets.supergridview.SuperGridView;

public class SuperGridViewActivity extends BaseActivity {

    private SuperGridViewActivity me = this;
    private List<HashMap<String, Object>> dataSourceList = new ArrayList<>();
    private List<HashMap<String, Object>> dataSourceListNew = new ArrayList<>();
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.dev_activity_supergridview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        final SuperGridView mGridView = (SuperGridView) findViewById(R.id.superGridView);
        for (int i = 0; i < 94; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<>();
            itemHashMap.put("item_text", "Item " + Integer.toString(i));
            dataSourceList.add(itemHashMap);
        }

        for (int i = 0; i < 34; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<>();
            itemHashMap.put("item_text", "New " + Integer.toString(i));
            dataSourceListNew.add(itemHashMap);
        }
        List<HashMap<String, Object>> hAf = new ArrayList<>();
        HashMap<String, Object> h1 = new HashMap<>();
        h1.put("item_text", "Header 1");
        HashMap<String, Object> h2 = new HashMap<>();
        h2.put("item_text", "Header 2");
        HashMap<String, Object> f1 = new HashMap<>();
        f1.put("item_text", "Footer 1");
        HashMap<String, Object> f2 = new HashMap<>();
        f2.put("item_text", "Footer 2");
        hAf.add(h1);
        hAf.add(h2);
        hAf.add(f1);
        hAf.add(f2);
        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(me,
                dataSourceList, R.layout.dev_supergridview_item_griditem,
                new String[] {"item_text" }, new int[] {R.id.item_text });
        final SimpleAdapter mSimpleAdapterNew = new SimpleAdapter(me,
                dataSourceListNew, R.layout.dev_supergridview_item_griditem,
                new String[] {"item_text" }, new int[] {R.id.item_text });

        final SimpleAdapter hAfA = new SimpleAdapter(me, hAf,
                R.layout.dev_supergridview_item_griditem, new String[] {"item_text" },
                new int[] { R.id.item_text });
        final View viewHeaderItem1 = hAfA.getView(0, null, null);
        final View viewHeaderItem2 = hAfA.getView(1, null, null);
        final View header = View.inflate(me, R.layout.dev_supergridview_item_header, null);
        ((Button) header.findViewById(R.id.button1)).setText(R.string.dev_supergridview_add_header_item);
        ((Button) header.findViewById(R.id.button2)).setText(R.string.dev_supergridview_set_adapter);
        viewHeaderItem1.setOnClickListener(new View.OnClickListener() {
            private boolean once = true;

            @Override
            public void onClick(View v) {
                if (once) {
                    once = false;
                    mGridView.removeHeaderView(header);
                } else {
                    once = true;
                    mGridView.addHeaderView(header);
                }
            }
        });
        header.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {
                    private boolean once = false;

                    @Override
                    public void onClick(View v) {
                        if (once) {
                            once = false;
                            mGridView.removeHeaderItem(viewHeaderItem2);
                            ((Button) v).setText(R.string.dev_supergridview_add_header_item);
                        } else {
                            once = true;
                            mGridView
                                    .addHeaderItem(viewHeaderItem2, null, true);
                            ((Button) v).setText(R.string.dev_supergridview_remove_header_item);
                        }

                    }
                });
        header.findViewById(R.id.button2).setOnClickListener(
                new View.OnClickListener() {
                    private boolean once = true;

                    @Override
                    public void onClick(View v) {
                        if (once) {
                            once = false;
                            mGridView.setAdapter(mSimpleAdapterNew);
                            mGridView.setDragable(false);
                        } else {
                            once = true;
                            mGridView.setAdapter(mSimpleAdapter);
                            mGridView.setDragable(true);
                        }
                    }
                });
        final View viewFooterItem1 = hAfA.getView(2, null, null);
        final View viewFooterItem2 = hAfA.getView(3, null, null);
        final View footer = View.inflate(me, R.layout.dev_supergridview_item_header, null);
        ((Button) footer.findViewById(R.id.button1)).setText(R.string.dev_supergridview_add_footer_item);
        ((Button) footer.findViewById(R.id.button2)).setText(R.string.dev_supergridview_change_adapter);
        viewFooterItem1.setOnClickListener(new View.OnClickListener() {
            private boolean once = true;

            @Override
            public void onClick(View v) {
                if (once) {
                    once = false;
                    mGridView.removeFooterView(footer);
                } else {
                    once = true;
                    mGridView.addFooterView(footer);
                }
            }
        });
        footer.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {
                    private boolean once = false;

                    @Override
                    public void onClick(View v) {
                        if (once) {
                            once = false;
                            mGridView.removeFooterItem(viewFooterItem2);
                            ((Button) v).setText(R.string.dev_supergridview_add_footer_item);
                        } else {
                            once = true;
                            mGridView
                                    .addFooterItem(viewFooterItem2, null, true);
                            ((Button) v).setText(R.string.dev_supergridview_remove_footer_item);
                        }

                    }
                });

        footer.findViewById(R.id.button2).setOnClickListener(
                new View.OnClickListener() {
                    private boolean once = true;

                    @Override
                    public void onClick(View v) {
                        if (once) {
                            once = false;
                            dataSourceList.remove(dataSourceList.size() - 1);
                            mSimpleAdapter.notifyDataSetChanged();
                        } else {
                            once = true;
                            HashMap<String, Object> itemHashMap = new HashMap<>();
                            itemHashMap.put(
                                    "item_text",
                                    "Item "
                                            + Integer.toString(dataSourceList
                                            .size()));
                            dataSourceList.add(itemHashMap);
                            mSimpleAdapter.notifyDataSetChanged();
                        }
                    }
                });
        mGridView.addHeaderItem(viewHeaderItem1);
        mGridView.addFooterItem(viewFooterItem1);
        mGridView.addHeaderView(header);
        //mGridView.addFooterView(footer);
        mGridView.setAdapter(mSimpleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(me, "Click" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(me, "LongClick" + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mGridView.setOnDragListener(new SuperGridView.OnDragListener() {

            @Override
            public void onMerge(int position, int toPoaition) {
                Toast.makeText(me, "合并" + position + "到" + toPoaition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(int position,
                                 int fristWrappedAdapterItemPosition,
                                 SuperGridView.DeleteAnimator deleteAnimator) {
                // 当添加HeaderView或HeaderItem后position有偏移
                deleteAnimator.delete();
                //deleteAnimator.deleteWithoutAnimation();
                //deleteAnimator.cancel();
                dataSourceList.remove(position
                        - fristWrappedAdapterItemPosition);
                mSimpleAdapter.notifyDataSetChanged();
            }
        });
        mGridView.setDeleteResource(R.drawable.dev_ic_delete);
        // 设置删除位置
        mGridView.setDeleteDrawableLocation(0);
        mGridView.setStartScale(1.2f);
        mGridView.setDuration(180);
        mGridView.setDragController(new DragController() {
            @Override
            public Bitmap setDragView(View tagView) {
                tagView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(tagView.getDrawingCache());
                tagView.destroyDrawingCache();
                return bitmap;
            }
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SuperGridViewActivity.class));
    }
}

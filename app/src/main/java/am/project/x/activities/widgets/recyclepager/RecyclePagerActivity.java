package am.project.x.activities.widgets.recyclepager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.util.viewpager.adapter.RecyclePagerAdapter;

public class RecyclePagerActivity extends BaseActivity implements View.OnClickListener {

    private MyRecyclePagerAdapter adapter;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_recyclepager;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.rp_toolbar);
        ViewPager vpContent = (ViewPager) findViewById(R.id.rp_vp_content);
        findViewById(R.id.rp_btn_remove).setOnClickListener(this);
        findViewById(R.id.rp_btn_add).setOnClickListener(this);
        adapter = new MyRecyclePagerAdapter();
        vpContent.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rp_btn_remove:
                adapter.remove();
                break;
            case R.id.rp_btn_add:
                adapter.add();
                break;
        }
    }

    public class MyPagerViewHolder extends RecyclePagerAdapter.PagerViewHolder {

        public MyPagerViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recyclepager_page, parent, false));
        }

        public void setData(String data) {
            ((TextView) itemView).setText(data);
        }
    }

    public class MyRecyclePagerAdapter extends RecyclePagerAdapter<MyPagerViewHolder> {

        private int itemCount = 5;
        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public MyPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyPagerViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(MyPagerViewHolder holder, int position) {
            holder.setData(String.format(Locale.getDefault(),
                    getString(R.string.recyclepager_page), position + 1));
        }

        public void add() {
            itemCount++;
            notifyDataSetChanged();
        }

        public void remove() {
            itemCount--;
            itemCount = itemCount < 0 ? 0 : itemCount;
            notifyDataSetChanged();
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, RecyclePagerActivity.class));
    }
}

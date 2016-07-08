package am.project.x.activities.drawable.sharpcornerbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.drawable.SharpCornerBoxDrawable;
import am.project.support.view.CompatPlus;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class SharpCornerBoxActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_sharpcornerbox;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.scb_toolbar);
        final int color = 0xffc8e71c;// 0x14000000
        final int width = 42;
        final int height = 18;
        final int padding = 40;
        final float round = 20;
        CompatPlus.setBackground(findViewById(R.id.scb_top_center),
                new SharpCornerBoxDrawable(color, width, height));
        CompatPlus.setBackground(findViewById(R.id.scb_top_end),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        SharpCornerBoxDrawable.Location.END, padding));
        CompatPlus.setBackground(findViewById(R.id.scb_top_start),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        SharpCornerBoxDrawable.Location.START, padding));

        CompatPlus.setBackground(findViewById(R.id.scb_bottom_center),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.BOTTOM));
        CompatPlus.setBackground(findViewById(R.id.scb_bottom_end),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.BOTTOM, SharpCornerBoxDrawable.Location.END, padding));
        CompatPlus.setBackground(findViewById(R.id.scb_bottom_start),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.BOTTOM, SharpCornerBoxDrawable.Location.START, padding));

        CompatPlus
                .setBackground(findViewById(R.id.scb_left_center),
                        new SharpCornerBoxDrawable(color, width, height,
                                SharpCornerBoxDrawable.Direction.LEFT));
        CompatPlus.setBackground(findViewById(R.id.scb_left_end),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.LEFT, SharpCornerBoxDrawable.Location.END, padding));
        CompatPlus.setBackground(findViewById(R.id.scb_left_start),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.LEFT, SharpCornerBoxDrawable.Location.START, padding));

        CompatPlus.setBackground(findViewById(R.id.scb_right_center),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.RIGHT));
        CompatPlus.setBackground(findViewById(R.id.scb_right_end),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.RIGHT, SharpCornerBoxDrawable.Location.END, padding));
        CompatPlus.setBackground(findViewById(R.id.scb_right_start),
                new SharpCornerBoxDrawable(color, width, height,
                        SharpCornerBoxDrawable.Direction.RIGHT, SharpCornerBoxDrawable.Location.START, padding));

        CompatPlus.setBackground(findViewById(R.id.scb_rr_center),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        round));
        CompatPlus.setBackground(findViewById(R.id.scb_rr_end),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        SharpCornerBoxDrawable.Location.END, round));
        CompatPlus.setBackground(findViewById(R.id.scb_rr_start),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        SharpCornerBoxDrawable.Location.START, round));

        CompatPlus
                .setBackground(findViewById(R.id.scb_null_r),
                        new SharpCornerBoxDrawable(color, width, height,
                                SharpCornerBoxDrawable.Direction.TOP));
        CompatPlus.setBackground(findViewById(R.id.scb_null_rr),
                new SharpCornerBoxDrawable(color, width, height, SharpCornerBoxDrawable.Direction.TOP,
                        round));
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SharpCornerBoxActivity.class));
    }
}

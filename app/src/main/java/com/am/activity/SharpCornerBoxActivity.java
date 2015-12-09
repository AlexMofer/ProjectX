package com.am.activity;

import android.app.Activity;
import android.os.Bundle;

import com.am.widget.R;
import com.am.widget.drawables.SharpCornerBoxDrawable;
import com.am.widget.drawables.SharpCornerBoxDrawable.Direction;
import com.am.widget.drawables.SharpCornerBoxDrawable.Location;
import com.am.widget.utils.VersionCompat;

public class SharpCornerBoxActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int color = 0xffc8e71c;// 0x14000000
		final int width = 42;
		final int height = 18;
		final int padding = 40;
		final float round = 20;
		setContentView(R.layout.activity_sharpcornerbox);
		VersionCompat.setBackground(findViewById(R.id.scb_top_center),
				new SharpCornerBoxDrawable(color, width, height));
		VersionCompat.setBackground(findViewById(R.id.scb_top_end),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						Location.END, padding));
		VersionCompat.setBackground(findViewById(R.id.scb_top_start),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						Location.START, padding));

		VersionCompat.setBackground(findViewById(R.id.scb_bottom_center),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.BOTTOM));
		VersionCompat.setBackground(findViewById(R.id.scb_bottom_end),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.BOTTOM, Location.END, padding));
		VersionCompat.setBackground(findViewById(R.id.scb_bottom_start),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.BOTTOM, Location.START, padding));

		VersionCompat
				.setBackground(findViewById(R.id.scb_left_center),
						new SharpCornerBoxDrawable(color, width, height,
								Direction.LEFT));
		VersionCompat.setBackground(findViewById(R.id.scb_left_end),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.LEFT, Location.END, padding));
		VersionCompat.setBackground(findViewById(R.id.scb_left_start),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.LEFT, Location.START, padding));

		VersionCompat.setBackground(findViewById(R.id.scb_right_center),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.RIGHT));
		VersionCompat.setBackground(findViewById(R.id.scb_right_end),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.RIGHT, Location.END, padding));
		VersionCompat.setBackground(findViewById(R.id.scb_right_start),
				new SharpCornerBoxDrawable(color, width, height,
						Direction.RIGHT, Location.START, padding));

		VersionCompat.setBackground(findViewById(R.id.scb_rr_center),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						round));
		VersionCompat.setBackground(findViewById(R.id.scb_rr_end),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						Location.END, round));
		VersionCompat.setBackground(findViewById(R.id.scb_rr_start),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						Location.START, round));

		VersionCompat
				.setBackground(findViewById(R.id.scb_null_r),
						new SharpCornerBoxDrawable(color, width, height,
								Direction.TOP));
		VersionCompat.setBackground(findViewById(R.id.scb_null_rr),
				new SharpCornerBoxDrawable(color, width, height, Direction.TOP,
						round));
	}
}

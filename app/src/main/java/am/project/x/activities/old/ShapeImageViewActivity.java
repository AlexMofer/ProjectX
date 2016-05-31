package am.project.x.activities.old;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import am.project.x.R;
import am.widget.shapeimageview.CircleImageShape;
import am.widget.shapeimageview.RoundRectImageShape;
import am.widget.shapeimageview.ShapeImageView;


public class ShapeImageViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.old_activity_shapeimage);
		
		final ShapeImageView ivC = (ShapeImageView) findViewById(R.id.image_CR);
		ivC.setImageShape(new CircleImageShape(0xff00ffff, 10, true));
		ivC.setTag(true);
		ivC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((Boolean) v.getTag()) {
					v.setTag(false);
					ivC.setImageResource(R.drawable.ic_widget_gradienttabstrip);
				} else {
					v.setTag(true);
					ivC.setImageResource(R.drawable.old_ic_shapeimage_test);
				}
			}
		});
		
		final ShapeImageView ivRR = (ShapeImageView) findViewById(R.id.image_RR);
		ivRR.setImageShape(new RoundRectImageShape(0xff00ffff, 10, 100));
		ivRR.setTag(true);
		ivRR.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((Boolean) v.getTag()) {
					v.setTag(false);
					ivRR.setImageResource(R.drawable.ic_widget_gradienttabstrip);
				} else {
					v.setTag(true);
					ivRR.setImageResource(R.drawable.old_ic_shapeimage_test);
				}
			}
		});
	}
}

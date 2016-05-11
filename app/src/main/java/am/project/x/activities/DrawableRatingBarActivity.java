package am.project.x.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.widgets.drawableratingbar.DrawableRatingBar;


/**
 * 星标评级
 * Created by Alex on 2015/8/31.
 */
public class DrawableRatingBarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawableratingbar);
        DrawableRatingBar drBar = (DrawableRatingBar) findViewById(R.id.ratingbar);
        drBar.setRating(3);
        drBar.setTouchMin(1);
        drBar.setOnRatingChangeListener(new DrawableRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(int rating, int oldRating) {
                System.out.println("rating = " + rating + "-----------oldRating = " + oldRating);
            }

            @Override
            public void onRatingSelected(int rating) {
                Toast.makeText(DrawableRatingBarActivity.this,
                        "" + rating, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

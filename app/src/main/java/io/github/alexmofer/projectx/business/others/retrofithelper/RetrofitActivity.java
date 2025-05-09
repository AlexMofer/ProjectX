package io.github.alexmofer.projectx.business.others.retrofithelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;

import io.github.alexmofer.projectx.R;

public class RetrofitActivity extends AppCompatActivity {

    public RetrofitActivity() {
        super(R.layout.activity_retrofit);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, RetrofitActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.retrofit_toolbar);
    }
}

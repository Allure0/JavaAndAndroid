package com.allure.study;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.allure.study.utils.Constants;
import com.allure.study.utils.ResourceUtils;

/**
 * Created by Allure on 2017/9/7.
 */

public class DrawableActivity extends AppCompatActivity {
    private ImageView imageView;
    private float xdpi;
    private float ydpi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        xdpi = getResources().getDisplayMetrics().xdpi;
        ydpi = getResources().getDisplayMetrics().ydpi;
        Log.e("密度值", "xdpi: " + xdpi + "--" + "ydpi: " + ydpi + "");
        imageView = (ImageView) findViewById(R.id.image);

//        imageView.setImageResource(R.drawable.biggyy3500);


    }


}

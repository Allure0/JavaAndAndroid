package com.allure.study.quote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.allure.study.R;

import java.lang.ref.SoftReference;

/**
 * Created by Allure on 2017/9/6.
 */

public class SoftReferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView=new ImageView(this);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        options.inPurgeable= true;
        options.inInputShareable= true;
        options.outWidth= 720;
        options.outHeight= 1280;
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round,options);
        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
        SoftReference<Drawable> drawableSoftReference =
                new SoftReference<Drawable>(drawable);
        if(drawableSoftReference != null) {
            //内存不足将不显示
            imageView.setBackground(drawableSoftReference.get());
        }

    }
}

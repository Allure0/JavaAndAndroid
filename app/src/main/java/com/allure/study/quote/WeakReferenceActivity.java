package com.allure.study.quote;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Allure on 2017/9/6.
 */

public class WeakReferenceActivity extends AppCompatActivity {

    private WeakReference<WeakReferenceActivity> reference;
    private MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myHandler.sendEmptyMessage(1);
    }


    public static class MyHandler extends Handler {
        private WeakReference<WeakReferenceActivity> reference;

        public MyHandler(WeakReferenceActivity activity) {
            reference = new WeakReference<WeakReferenceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WeakReferenceActivity weakReferenceActivity = (WeakReferenceActivity) reference.get();
                    if (weakReferenceActivity != null) {
                        System.out.print("WeakReferenceActivity");
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }
}

package com.allure.study.interprocesscommunication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Allure on 2017/9/8.
 */

public class ProcessActivity2 extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getIntent().getExtras();
        String ipc=bundle.getString("ipc_bundle");
    }
}

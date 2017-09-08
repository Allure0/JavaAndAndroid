package com.allure.study.interprocesscommunication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * IPC机制:Bundle
 * Created by Allure on 2017/9/8.
 */

public class ProcessActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString("ipc_bundle", "BundleName");
        Intent intent=new Intent(ProcessActivity.this,ProcessActivity2.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

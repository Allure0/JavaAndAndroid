package com.allure.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.allure.study.interprocesscommunication.ProcessActivity;
import com.allure.study.interprocesscommunication.aidl.AIDLActivity;
import com.allure.study.interprocesscommunication.contentprovider.ContentProviderActivity;
import com.allure.study.interprocesscommunication.messenger.MessengerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBundle;
    private Button btnMessenger;
    private Button btnAIDL;
    private Button btnContentProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBundle = (Button) findViewById(R.id.btn_bundle);
        btnMessenger = (Button) findViewById(R.id.btn_messenger);
        btnAIDL = (Button) findViewById(R.id.btn_aidl);
        btnContentProvider = (Button) findViewById(R.id.btn_content_provider);


        btnBundle.setOnClickListener(this);
        btnMessenger.setOnClickListener(this);
        btnAIDL.setOnClickListener(this);
        btnContentProvider.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bundle:
                startActivity(new Intent(MainActivity.this, ProcessActivity.class));
                break;
            case R.id.btn_messenger:
                startActivity(new Intent(MainActivity.this, MessengerActivity.class));
                break;
            case R.id.btn_aidl:
                startActivity(new Intent(MainActivity.this, AIDLActivity.class));
                break;
            case R.id.btn_content_provider:
                startActivity(new Intent(MainActivity.this, ContentProviderActivity.class));
                break;
        }
    }
}

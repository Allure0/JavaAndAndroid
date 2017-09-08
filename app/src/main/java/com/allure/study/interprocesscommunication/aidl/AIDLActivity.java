package com.allure.study.interprocesscommunication.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.allure.study.R;

/**
 * Created by Allure on 2017/9/8.
 */

public class AIDLActivity extends AppCompatActivity {

    private static final String TAG = AIDLActivity.class.getSimpleName();

    private Button button;

    private TestAIDL testAIDL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        //绑定AIDL服务
        bindService();


        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = testAIDL.reply("你爱我吗");
                    Log.d(TAG,"你爱我吗");
                    Log.d(TAG,s);
                    Toast.makeText(AIDLActivity.this, "你爱我吗", Toast.LENGTH_SHORT).show();
                    Toast.makeText(AIDLActivity.this, s, Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindServcie();

    }

    private void unBindServcie() {
        unbindService(mServiceConnection);
    }

    private void bindService() {
        Intent mIntent = new Intent(this, AIDLService.class);

        bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            testAIDL = TestAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            testAIDL = null;
        }
    };
}

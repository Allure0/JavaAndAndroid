package com.allure.study.interprocesscommunication.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.allure.study.R;
import com.allure.study.interprocesscommunication.Constants;

/**
 * 客户端
 * Created by Allure on 2017/9/8.
 */

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = MessengerActivity.class.getSimpleName();

    private Button button;

    private Messenger messengerService;//服务端Service

    private Messenger messengerClient = new Messenger(new MessageHandler());//客户端Messenger


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        int pid = android.os.Process.myPid();
        Log.d(TAG, "进程Id:" + pid + "");
        bindMessengerService();

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage();

                    }
                }).start();
            }
        });

    }

    //绑定服务
    public void bindMessengerService() {
        Intent mIntent = new Intent(this, MessengerService.class);
        bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }

    //发送消息
    public void sendMessage() {
        Message msgFromClient = Message.obtain(null, Constants.MSG_FROM_CLIENT, 1, 2);
        Bundle data = new Bundle();
        data.putString(Constants.MESSENGER_KEY, "我爱你,你爱我吗？");
        msgFromClient.setData(data);
        msgFromClient.replyTo = messengerClient;
        try {
            messengerService.send(msgFromClient);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }


    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_FROM_SERVICE:
                    Log.d(TAG,"服务端回复消息:"+msg.getData().getString(Constants.MESSENGER_KEY));
                    Toast.makeText(MessengerActivity.this,  msg.getData().getString(Constants.MESSENGER_KEY) , Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        /**
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerService = new Messenger(service);

        }

        /**
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            messengerService = null;
        }
    };
}

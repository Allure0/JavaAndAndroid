package com.allure.study.interprocesscommunication.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.allure.study.interprocesscommunication.Constants;

/**
 * Handler实例一个Messenger对象,在Service的onBinde中返回Binder对象
 * 通过在Handler中接受到消息后通过Messenger.replTo.send进行消息回复
 * Created by Allure on 2017/9/8.
 */

public class MessengerService extends Service {

    public static final String TAG = "MessengerService";

    private HandlerThread handlerThread;
    private Handler handler;
    //Messenger
    private Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();
        int pid = android.os.Process.myPid();
        Log.d(TAG, "进程Id:" + pid + "");

        //HandlerThread,使用Looper处理队列消息
        handlerThread = new HandlerThread("messenger_server");
        handlerThread.start();
        //获取Looper
        Looper looper = handlerThread.getLooper();
        //让Handler 运行在HandlerThread中

        handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                replyToClientMsg(msg);//回复客户端消息
                super.handleMessage(msg);
            }
        };

        messenger = new Messenger(handler);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }


    private void replyToClientMsg(Message msg) {
        switch (msg.what) {
            case Constants.MSG_FROM_CLIENT://接受消息处理
                //模拟器服务器响应过程
                Log.d(TAG, "msg what= [" + msg.what + "" + "]");
                Log.d(TAG, "msg arg1= [" + msg.arg1 + "" + "]");
                Log.d(TAG, "msg arg2= [" + msg.arg2 + "" + "]");
                Log.d(TAG,"客户端发送的消息:"+msg.getData().getString(Constants.MESSENGER_KEY));
                Toast.makeText(MessengerService.this, msg.getData().getString(Constants.MESSENGER_KEY), Toast.LENGTH_SHORT).show();
                try {
                    Messenger msgFromClient = msg.replyTo;//客户端回调
                    Message replyMsgToClient = Message.obtain(null, Constants.MSG_FROM_SERVICE);//回复给客户端的消息
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MESSENGER_KEY, "我也爱你");
                    replyMsgToClient.setData(bundle);

                    msgFromClient.send(replyMsgToClient);//发送Message消息体给客户端
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

}

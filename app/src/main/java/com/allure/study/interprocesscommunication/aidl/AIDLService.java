package com.allure.study.interprocesscommunication.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Allure on 2017/9/8.
 */

public class AIDLService extends Service {

    private static final String TAG = "AIDLService";


    public AIDLService() {
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");

        return super.onUnbind(intent);

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG, "onRebind");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");


        return binder;
    }

    private Binder binder = new TestAIDL.Stub() {


        @Override
        public String reply(String s) throws RemoteException {
            if(s.equals("你爱我吗")){
                return  "我爱你";
            }else {
                return "没有你爱我,我怎么爱你";
            }
        }
    };

}

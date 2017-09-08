package com.allure.study.interprocesscommunication.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.allure.study.R;

/**
 * Created by Allure on 2017/9/8.
 */

public class ContentProviderActivity extends AppCompatActivity {

    private static final String TAG = ContentProviderActivity.class.getSimpleName();
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        Log.v(TAG, "Activity进程Id " + android.os.Process.myPid());
        uri = Uri.parse(TestContentProvider.CONTACTS_URI);
    }

    public void insert(View v) {
        ContentValues values = new ContentValues();
        values.put("phoneNumber", "13333333333");
        values.put("name", "Jacye");
        getContentResolver().insert(uri, values);
        Log.v(TAG, "插入了 " + "Jacye " + "电话号码: 13333333333");
    }

    public void delete(View v) {
        getContentResolver().delete(uri, "name=?", new String[]{"Jacye"});
    }

    public void update(View v) {
    }

    public void query(View v) {
        String[] colum = new String[]{"phoneNumber", "name"};
        Cursor cursor = getContentResolver().query(uri, colum, null, null, null);
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(0);
            String name = cursor.getString(1);
            Log.v(TAG, "获取到联系人 " + phoneNumber + "  " + name);
        }
        cursor.close();
    }
}

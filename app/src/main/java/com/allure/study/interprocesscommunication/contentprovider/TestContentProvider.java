package com.allure.study.interprocesscommunication.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Allure on 2017/9/8.
 */

public class TestContentProvider extends ContentProvider {
    private static final String TAG = TestContentProvider.class.getSimpleName();

    private static final String AUTHORITY = "com.allure.study.interprocesscommunication.contentprovider";
    public static final String CONTACTS_URI = "content://" + AUTHORITY + "/" + DBOpenHelper.CONTACTS_TABLE_NAME;

    private static final int CONTACTS_TABLE_CODE = 0;

    private UriMatcher uriMatcher;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {

        Log.v(TAG, "ContactsProvider进程Id " + android.os.Process.myPid());

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.CONTACTS_TABLE_NAME, CONTACTS_TABLE_CODE);
        sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        String tableName = getTableName(uri);
        if(tableName == null) {
            throw new IllegalArgumentException("Uri错误: " + uri);
        }
        Cursor cursor = sqLiteDatabase.query(tableName, strings, s, strings1, s1, null, null);
        return cursor;
    }

    /**
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String tableName = getTableName(uri);
        if(tableName == null) {
            throw new IllegalArgumentException("Erro Uri: " + uri);
        }
        sqLiteDatabase.insert(tableName, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String tableName = getTableName(uri);
        if(tableName == null) {
            throw new IllegalArgumentException("Erro Uri: " + uri);
        }
        int count = sqLiteDatabase.delete(tableName, s, strings);
        if(count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case CONTACTS_TABLE_CODE:
                tableName = DBOpenHelper.CONTACTS_TABLE_NAME;
                break;
        }
        return tableName;
    }
}


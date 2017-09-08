package com.allure.study.interprocesscommunication.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Allure on 2017/9/8.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";

    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + CONTACTS_TABLE_NAME + " (phonenumber CHAR(11) PRIMARY KEY, name TEXT)";

    public static final int DB_VESION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VESION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}


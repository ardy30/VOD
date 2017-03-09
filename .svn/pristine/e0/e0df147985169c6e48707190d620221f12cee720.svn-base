package com.ppfuns.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppfuns.util.LogUtils;

/**
 * Created by zpf on 2017/1/9.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context, String dbName, int version) {
        this(context, dbName, null, version);
    }

    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        LogUtils.i(TAG, "db_name: " + dbName + ", db_version: " + version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

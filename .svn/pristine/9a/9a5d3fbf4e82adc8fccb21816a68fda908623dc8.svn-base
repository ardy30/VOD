package com.ppfuns.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppfuns.util.LogUtils;

/**
 * Created by zpf on 2017/1/9.
 */

public class ColumnDbHelper extends DBHelper {

    private static final String TAG = ColumnDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "column.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "column";

    public ColumnDbHelper(Context context) {
        this(context, DATABASE_NAME, DATABASE_VERSION);
    }

    public ColumnDbHelper(Context context, String dbName, int version) {
        super(context, dbName, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.i(TAG, "onCreate");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, cpId int, name VARCHAR, logo VARCHAR, logoDetail VARCHAR, cpType int, isPay int, pricePic VARCHAR, seq int, riseTime long, columnBg VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.i(TAG, "onUpgrade");
    }
}

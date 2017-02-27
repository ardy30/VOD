package com.ppfuns.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ppfuns.model.entity.Columns;
import com.ppfuns.util.LogUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zpf on 2017/1/9.
 */

public class ColumnDBManager {

    private static final String TAG = ColumnDBManager.class.getSimpleName();

    public static final String COLUMN_ID = "cpId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOGO = "logo";
    public static final String COLUMN_LOGO_DETAIL = "logoDetail";
    public static final String COLUMN_TYPE = "cpType";
    public static final String COLUMN_IS_PAY = "isPay";
    public static final String COLUMN_PRICE_PICTURE = "pricePic";
    public static final String COLUMN_SEQ = "seq";
    public static final String COLUMN_RISE_TIME = "riseTime";

    private static ColumnDBManager mManager;
    private static DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private ColumnDBManager() {

    }

    public static synchronized ColumnDBManager getInstance(Context context) {
        if (mManager == null) {
            if (mManager == null) {
                mDBHelper = new ColumnDbHelper(context);
                mManager = new ColumnDBManager();
            }
        }
        return mManager;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        LogUtils.i(TAG, "openDatabase count: " + mOpenCounter.get());
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
        LogUtils.i(TAG, "closeDatabase count: " + mOpenCounter.get());
    }

    public void addColumn(Columns columns) {
        if(columns == null) {
            return;
        }
        int cpId = columns.cpId;
        Cursor c = queryColumn(cpId);
        if (c != null && c.getCount() > 0) {
            updateColumn(columns);
            c.close();
        } else {
            insert(columns);
        }
    }

    public void insert(Columns columns) {
        if(columns == null) {
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, columns.cpId);
        cv.put(COLUMN_NAME, columns.name);
        cv.put(COLUMN_LOGO, columns.logo);
        cv.put(COLUMN_LOGO_DETAIL, columns.logoDetail);
        cv.put(COLUMN_TYPE, columns.cpType);
        cv.put(COLUMN_IS_PAY, columns.isPay);
        cv.put(COLUMN_PRICE_PICTURE, columns.pricePic);
        cv.put(COLUMN_SEQ, columns.seq);
        cv.put(COLUMN_RISE_TIME, System.currentTimeMillis());
        openDatabase().insert(ColumnDbHelper.TABLE_NAME, null, cv);
        closeDatabase();
    }

    public void addColumns(List<Columns> columnsList) {
        if (columnsList == null || columnsList.size() <= 0) {
            return;
        }
        SQLiteDatabase database = openDatabase();
        database.beginTransaction();
        try {
            for(Columns columns: columnsList) {
                if (columns != null) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_ID, columns.cpId);
                    cv.put(COLUMN_NAME, columns.name);
                    cv.put(COLUMN_LOGO, columns.logo);
                    cv.put(COLUMN_LOGO_DETAIL, columns.logoDetail);
                    cv.put(COLUMN_TYPE, columns.cpType);
                    cv.put(COLUMN_IS_PAY, columns.isPay);
                    cv.put(COLUMN_PRICE_PICTURE, columns.pricePic);
                    cv.put(COLUMN_SEQ, columns.seq);
                    cv.put(COLUMN_RISE_TIME, System.currentTimeMillis());
                    database.insert(ColumnDbHelper.TABLE_NAME, null, cv);
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        }finally {
            database.endTransaction();
        }
        closeDatabase();
    }

    public Cursor queryColumn(int cpId) {
        Cursor c = openDatabase().rawQuery("SELECT * FROM column WHERE cpId == ?", new String[] {String.valueOf(cpId)});
        return c;
    }

    public Columns getColumn(int cpId) {
        Columns columns = null;
        Cursor c = queryColumn(cpId);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            columns = new Columns();
            columns.cpId = c.getInt(c.getColumnIndex(COLUMN_ID));
            columns.name = c.getString(c.getColumnIndex(COLUMN_NAME));
            columns.logo  = c.getString(c.getColumnIndex(COLUMN_LOGO));
            columns.logoDetail = c.getString(c.getColumnIndex(COLUMN_LOGO_DETAIL));
            columns.cpType = c.getInt(c.getColumnIndex(COLUMN_TYPE));
            columns.pricePic=c.getString(c.getColumnIndex(COLUMN_PRICE_PICTURE));
            columns.seq=c.getInt(c.getColumnIndex(COLUMN_SEQ));
            columns.isPay = c.getInt(c.getColumnIndex(COLUMN_IS_PAY));
            columns.riseTime = c.getLong(c.getColumnIndex(COLUMN_RISE_TIME));
        }
        c.close();
        closeDatabase();
        return columns;
    }

    public void deleteTable() {
        openDatabase().delete(ColumnDbHelper.TABLE_NAME, null, null);
        closeDatabase();
    }

    public void updateColumn(Columns columns) {
        if(columns == null) {
            return;
        }
        int cpId = columns.cpId;
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, cpId);
        cv.put(COLUMN_NAME, columns.name);
        cv.put(COLUMN_LOGO, columns.logo);
        cv.put(COLUMN_LOGO_DETAIL, columns.logoDetail);
        cv.put(COLUMN_TYPE, columns.cpType);
        cv.put(COLUMN_PRICE_PICTURE, columns.pricePic);
        cv.put(COLUMN_SEQ, columns.seq);
        cv.put(COLUMN_IS_PAY, columns.isPay);
        cv.put(COLUMN_RISE_TIME, System.currentTimeMillis());
        String where = "cpId = ?";
        String[] args = new String[]{String.valueOf(cpId)};
        openDatabase().update(ColumnDbHelper.TABLE_NAME, cv, where, args);
        closeDatabase();
    }
}

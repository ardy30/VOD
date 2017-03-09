package com.ppfuns.model.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zpf on 2017/1/9.
 */

public class DBManager {

    private static final String TAG = DBManager.class.getSimpleName();

    private static DBManager mManager;
    private static DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private DBManager() {

    }

    public static synchronized void initializeInstance(DBHelper helper) {
        if (mManager == null) {
            mManager = new DBManager();
            mDBHelper = helper;
        }
    }

    public static synchronized DBManager getInstance() {
        if (mManager == null) {
            throw new IllegalStateException(TAG + " is not initialized ");
        }
        return mManager;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDBHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}

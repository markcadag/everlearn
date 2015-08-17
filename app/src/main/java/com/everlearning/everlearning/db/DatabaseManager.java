package com.everlearning.everlearning.db;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artifex.mupdfdemo.AsyncTask;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private final String TAG = DatabaseManager.class.getSimpleName();
    private static DatabaseManager instance;
    private SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public interface OnTaskFinished {
       public void onTaskFinished(SQLiteDatabase database);
    }

    private DatabaseManager(SQLiteOpenHelper helper) {
        mDatabaseHelper = helper;
    }

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager(helper);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    private synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        Log.d(TAG,"Database open counter: " + mOpenCounter.get());
        return mDatabase;
    }

    private synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
        Log.d(TAG,"Database open counter: " + mOpenCounter.get());
    }

    public void executeQuery(QueryExecutor executor) {
        SQLiteDatabase database = openDatabase();
        executor.run(database);
        closeDatabase();
    }


    public void executeQueryTask(final QueryExecutor executor, final OnTaskFinished onTaskFinished) {
        //TODO change to async task for UI Thread
        final SQLiteDatabase database = openDatabase();
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params) {
                executor.run(database);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onTaskFinished.onTaskFinished(database);
                closeDatabase();
            }
        }.execute();
    }

    public void executeQueryTask(final QueryExecutor executor) {
        //TODO change to async task for UI Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = openDatabase();
                executor.run(database);
                closeDatabase();
            }
        }).start();
    }
}

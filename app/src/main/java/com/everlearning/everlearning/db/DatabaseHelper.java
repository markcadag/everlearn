package com.everlearning.everlearning.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.everlearning.everlearning.db.dao.HandoutsDAO;
import com.everlearning.everlearning.db.dao.SubjectDAO;
import com.everlearning.everlearning.db.dao.UserDAO;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "everlearndb";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_SUBJECT = "subjects";
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create all tables
        sqLiteDatabase.execSQL(SubjectDAO.getCreateTable(mContext));
        sqLiteDatabase.execSQL(HandoutsDAO.getCreateTable(mContext));
        sqLiteDatabase.execSQL(UserDAO.getCreateTable(mContext));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // drop all tables
            sqLiteDatabase.execSQL(SubjectDAO.getDropTable(mContext));
            sqLiteDatabase.execSQL(HandoutsDAO.getDropTable(mContext));
            sqLiteDatabase.execSQL(UserDAO.getDropTable(mContext));
            //re-create all tables
            onCreate(sqLiteDatabase);
        }
    }
}

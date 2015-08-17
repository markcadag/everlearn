package com.everlearning.everlearning.db.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.everlearning.everlearning.R;
import com.everlearning.everlearning.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    //TODO fix query from queries.xml
    interface Table {
        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
    }

    private SQLiteDatabase mDatabase;
    private Context mContext;
    public static String TABLE_NAME  = "subjects";

    public SubjectDAO(SQLiteDatabase database, Context context) {
        mDatabase = database;
        mContext = context;
    }

    public static String getCreateTable(Context context) {
        return context.getString(R.string.create_table_subject);
    }

    public static String getDropTable(Context context) {
        return context.getString(R.string.drop_table_subject);
    }


    //TODO bind arguments for faster insertion
    public void insert(List<Subject> subjectList) {

        for (Subject subject : subjectList) {
            String[] bindArgs = {
                    String.valueOf(subject.getId()),
                    subject.getName()
            };
            mDatabase.execSQL(mContext.getString(R.string.insert_subject), bindArgs);
        }
    }

    public void insert(Subject subject) {
        String[] bindArgs = {
                String.valueOf(subject.getId()),
                subject.getName()
        };
        mDatabase.execSQL(mContext.getString(R.string.insert_subject), bindArgs);
    }

    public void insertOrUpdate(Subject subject){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", subject.getId());
        contentValues.put("name", subject.getName());
        long id = mDatabase.replace(SubjectDAO.TABLE_NAME, null, contentValues);
        Log.i(TABLE_NAME, id+"");
       // mDatabase.execS
       // QL(mContext.getString(R.string.insert_subject), bindArgs);
    }

    public List<Subject> searchSubject(String searchString){
        String selectQuery = " SELECT * from " + TABLE_NAME + " WHERE name LIKE '%" + searchString + "%'";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Subject> dataList = manageCursor(cursor);
        closeCursor(cursor);
        Log.i(TABLE_NAME,dataList.size()+ " "+ searchString +" result");
        return  dataList;
    }

    public void insertOrUpdate(List<Subject> subjects){
        for (Subject subject: subjects) {
            insertOrUpdate(subject);
        }
    }


//    public void updateNameByAge(String name, int age) {
//        String[] bindArgs = {
//                name,
//                String.valueOf(age)
//        };
//        mDatabase.execSQL(mContext.getString(R.string.update_user_name_by_age), bindArgs);
//    }

//    public List<Subject> selectByAge(int age) {
//        String[] selectionArgs = {
//                String.valueOf(age)
//        };
//        String query = mContext.getString(R.string.select_users_by_age);
//        Cursor cursor = mDatabase.rawQuery(query, selectionArgs);
//
//        List<User> dataList = manageCursor(cursor);
//
//        closeCursor(cursor);
//
//        return dataList;
//    }

    public List<Subject> selectAll() {
        Cursor cursor = mDatabase.rawQuery(mContext.getString(R.string.select_all_subject), null);

        List<Subject> dataList = manageCursor(cursor);

        closeCursor(cursor);

        return dataList;
    }

    protected Subject cursorToData(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(Table.COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(Table.COLUMN_NAME);

        Subject subject = new Subject();
        subject.setId(cursor.getLong(idIndex));
        subject.setName(cursor.getString(nameIndex));

        return subject;
    }

    protected void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    protected List<Subject> manageCursor(Cursor cursor) {
        List<Subject> dataList = new ArrayList<Subject>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Subject subject = cursorToData(cursor);
                dataList.add(subject);
                cursor.moveToNext();
            }
        }
        return dataList;
    }
}

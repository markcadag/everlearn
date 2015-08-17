package com.everlearning.everlearning.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.everlearning.everlearning.R;
import com.everlearning.everlearning.model.Handout;

import java.util.ArrayList;
import java.util.List;

public class HandoutsDAO {

    interface Table {
        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_SUBID = "subject_id";
        String COLUMN_URLPATH = "url_path";
        String COLUMN_UPDATDEAT = "updated_at";
        String COLUMN_ENCYPTEDPATH= "encrypted_path";
        String COLUMN_DL_STATE = "state";
    }

    private SQLiteDatabase mDatabase;
    private Context mContext;
    public static String TABLE_NAME  = "handouts";

    public HandoutsDAO(SQLiteDatabase database, Context context) {
        mDatabase = database;
        mContext = context;
    }

    public static String getCreateTable(Context context) {
        return context.getString(R.string.create_table_handout);
    }

    public static String getDropTable(Context context) {
        return context.getString(R.string.drop_table_handout);
    }

    public void insertOrUpdate(Handout handout){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Table.COLUMN_ID, handout.getId());
        contentValues.put(Table.COLUMN_NAME, handout.getName());
        contentValues.put(Table.COLUMN_SUBID, handout.getSubject_id());
        contentValues.put(Table.COLUMN_UPDATDEAT,handout.getUpdatedAt());
        contentValues.put(Table.COLUMN_URLPATH,handout.getUrlPath());
        contentValues.put(Table.COLUMN_ENCYPTEDPATH,handout.getEncryptedPath());
        contentValues.put(Table.COLUMN_DL_STATE,handout.getDlState());
        long id = mDatabase.replace(HandoutsDAO.TABLE_NAME, null, contentValues);
        Log.i(TABLE_NAME, id+"");
    }

    public void insertOrUpdate(List<Handout> handouts){
        for (Handout handout: handouts) {
            insertOrUpdate(handout);
        }
    }

    public List<Handout> selectAll() {
        Cursor cursor = mDatabase.rawQuery(mContext.getString(R.string.select_all_handouts), null);
        List<Handout> dataList = manageCursor(cursor);
        closeCursor(cursor);
        return dataList;
    }

    public List<Handout> selectBySubjectId(long id) {
        String[] selectionArgs = {
                String.valueOf(id)
        };
        String query = mContext.getString(R.string.select_handout_by_subject_id);
        Cursor cursor = mDatabase.rawQuery(query, selectionArgs);

        List<Handout> dataList = manageCursor(cursor);

        closeCursor(cursor);

        return dataList;
    }


    protected Handout cursorToData(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(Table.COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(Table.COLUMN_NAME);
        int subIndex = cursor.getColumnIndex(Table.COLUMN_SUBID);
        int urlIndex = cursor.getColumnIndex(Table.COLUMN_URLPATH);
        int updateIndex = cursor.getColumnIndex(Table.COLUMN_UPDATDEAT);
        int encryptedIndex = cursor.getColumnIndex(Table.COLUMN_ENCYPTEDPATH);
        int dlState = cursor.getColumnIndex(Table.COLUMN_DL_STATE);

        Handout handout = new Handout();
        handout.setId(cursor.getLong(idIndex));
        handout.setName(cursor.getString(nameIndex));
        handout.setSubject_id(cursor.getLong(subIndex));
        handout.setUrlPath(cursor.getString(urlIndex));
        handout.setUpdatedAt(cursor.getString(updateIndex));
        handout.setEncryptedPath(cursor.getString(encryptedIndex));
        handout.setDlState(cursor.getLong(dlState));

        return handout;
    }

    protected void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    protected List<Handout> manageCursor(Cursor cursor) {
        List<Handout> dataList = new ArrayList<Handout>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Handout handout = cursorToData(cursor);
                dataList.add(handout);
                cursor.moveToNext();
            }
        }
        return dataList;
    }
}

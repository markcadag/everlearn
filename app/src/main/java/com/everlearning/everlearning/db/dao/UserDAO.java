package com.everlearning.everlearning.db.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.everlearning.everlearning.R;
import com.everlearning.everlearning.model.User;

public class UserDAO {

    interface Table {
        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_AUTH = "auth_key";
        String COLUMN_IMG_URL = "img_url";
        String COLUMN_EMAIL = "email";
        String COLUMN_IS_CURRENT = "is_current";
    }

    private SQLiteDatabase mDatabase;
    private Context mContext;
    public static String TABLE_NAME  = "users";

    public UserDAO(SQLiteDatabase database, Context context) {
        mDatabase = database;
        mContext = context;
    }

    public static String getCreateTable(Context context) {
        return context.getString(R.string.create_table_user);
    }

    public static String getDropTable(Context context) {
        return context.getString(R.string.drop_table_user);
    }

    public void insertOrUpdate(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Table.COLUMN_ID, user.getId());
        contentValues.put(Table.COLUMN_NAME, user.getName());
        contentValues.put(Table.COLUMN_AUTH, user.getAuth_key());
        contentValues.put(Table.COLUMN_IMG_URL, user.getImg_url());
        contentValues.put(Table.COLUMN_EMAIL, user.getEmail());
        int current = user.is_current() ? 1 : 0;
        contentValues.put(Table.COLUMN_IS_CURRENT, current);
        long id = mDatabase.replace(UserDAO.TABLE_NAME, null, contentValues);
        Log.i(TABLE_NAME, "data inserted"+ user.getName() + ":"+ id);
    }

    private boolean deleteUser(User user) {
        return mDatabase.delete(TABLE_NAME, Table.COLUMN_ID + "=" + user.getId(),null) > 0;
    }

    public User getCurrentUser() {
        String selectQuery = "SELECT * from " + TABLE_NAME + " WHERE " + Table.COLUMN_IS_CURRENT +"=1 LIMIT 1";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        User user = manageCursor(cursor);
        closeCursor(cursor);
        return user;
    }

    protected User cursorToData(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(Table.COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(Table.COLUMN_NAME);
        int authIndex = cursor.getColumnIndex(Table.COLUMN_AUTH);
        int urlIndex = cursor.getColumnIndex(Table.COLUMN_IMG_URL);
        int emailIndex = cursor.getColumnIndex(Table.COLUMN_EMAIL);
        int currentIndex = cursor.getColumnIndex(Table.COLUMN_IS_CURRENT);

        User user = new User();
        user.setId(cursor.getLong(idIndex));
        user.setName(cursor.getString(nameIndex));
        user.setAuth_key(cursor.getString(authIndex));
        user.setImg_url(cursor.getString(urlIndex));
        user.setEmail(cursor.getString(emailIndex));
        boolean isCurrent = cursor.getInt(currentIndex) == 1;
        user.setIs_current(isCurrent);

        return user;
    }

    protected void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    protected User manageCursor(Cursor cursor) {
        Log.i(TABLE_NAME, "colum count" + cursor.getCount());
        User user = null;
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                user = cursorToData(cursor);
                cursor.moveToNext();
            }
        }
        return user;
    }
}

package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public final class AuthenticationLocal {
    private UserSaveDbHelper dbMan;
    private SQLiteDatabase db;

    public AuthenticationLocal(Context context) {
        dbMan = new AuthenticationLocal.UserSaveDbHelper(context);
        db = dbMan.getWritableDatabase();
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserSave.TABLE_NAME + " (" +
                    UserSave._ID + " INTEGER PRIMARY KEY," +
                    UserSave.COLUMN_NAME_USER_ID + " TEXT," +
                    UserSave.COLUMN_NAME_TOKEN + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserSave.TABLE_NAME;

    public static class UserSave implements BaseColumns {
        public static final String TABLE_NAME = "session";
        public static final String COLUMN_NAME_TOKEN = "token";
        public static final String COLUMN_NAME_USER_ID = "userId";
    }

    public static class UserSaveDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "UserSave.db";

        public UserSaveDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public boolean check_saved() {
        String[] projection = {
                BaseColumns._ID,
                UserSave.COLUMN_NAME_TOKEN,
                UserSave.COLUMN_NAME_USER_ID
        };

        Cursor cursor = db.query(
                UserSave.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(UserSave._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        if(!itemIds.isEmpty()){
            return true;
        }
        return false;
    }

    public void save_session(String token, String userId){
        ContentValues values = new ContentValues();
        values.put(UserSave.COLUMN_NAME_TOKEN, token);
        values.put(UserSave.COLUMN_NAME_USER_ID, userId);
        db.insert(UserSave.TABLE_NAME, null, values);
    }

    public void delete_session(){
        //delete and create again table
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
}

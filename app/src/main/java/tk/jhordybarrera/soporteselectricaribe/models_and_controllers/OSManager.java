package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class OSManager {
    private OSManager.OSDbHelper dbMan;
    private SQLiteDatabase db;
    public OSManager(Context context) {
        dbMan = new OSManager.OSDbHelper(context);
        db = dbMan.getWritableDatabase();
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + OS.TABLE_NAME + " (" +
                    OS._ID + " INTEGER PRIMARY KEY," +
                    OS.COLUMN_NAME_OS + " TEXT," +
                    OS.COLUMN_NAME_USER_ID + " TEXT," +
                    OS.COLUMN_NAME_NIC + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OS.TABLE_NAME;

    public static class OS implements BaseColumns {
        public static final String TABLE_NAME = "order_service";
        public static final String COLUMN_NAME_OS = "os";
        public static final String COLUMN_NAME_NIC = "nic";
        public static final String COLUMN_NAME_USER_ID = "user_id";
    }
    public static class OSDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "OS.db";

        public OSDbHelper(Context context) {
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

    public void save_os(String id,String os, String nic){
        ContentValues values = new ContentValues();
        values.put(OS.COLUMN_NAME_OS, os);
        values.put(OS.COLUMN_NAME_NIC, nic);
        values.put(OS.COLUMN_NAME_USER_ID, id);
        db.insert(OS.TABLE_NAME, null, values);
    }
    public void delete_nic(String nic){
        String selection = OS.COLUMN_NAME_NIC + " LIKE ?";
        String[] selectionArgs = { nic };
        db.delete(OS.TABLE_NAME, selection, selectionArgs);
    }

    public void update_os(String id,String os, String nic,String old_nic){
        delete_nic(old_nic);
        save_os(id,os,nic);
    }

    public ArrayList list_os() {
        String[] projection = {
                BaseColumns._ID,
                OS.COLUMN_NAME_OS,
                OS.COLUMN_NAME_NIC,
                OS.COLUMN_NAME_USER_ID
        };

        Cursor cursor = db.query(
                OS.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<OSModel> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            list.add(
                    new OSModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(OS.COLUMN_NAME_OS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(OS.COLUMN_NAME_NIC)),
                            cursor.getString(cursor.getColumnIndexOrThrow(OS.COLUMN_NAME_USER_ID))
                    )
            );
        }
        cursor.close();

        return list;
    }

}

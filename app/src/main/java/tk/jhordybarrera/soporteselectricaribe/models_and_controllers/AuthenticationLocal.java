package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class AuthenticationLocal {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserSave.TABLE_NAME + " (" +
                    UserSave._ID + " INTEGER PRIMARY KEY," +
                    UserSave.COLUMN_NAME_TOKEN + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserSave.TABLE_NAME;

    public static class UserSave implements BaseColumns {
        public static final String TABLE_NAME = "session";
        public static final String COLUMN_NAME_TOKEN = "token";
    }

    public static class UserSaveDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "UserSave.db";

        public UserSaveDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}

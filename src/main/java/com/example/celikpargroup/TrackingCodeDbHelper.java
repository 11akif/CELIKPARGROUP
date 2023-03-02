package com.example.celikpargroup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TrackingCodeDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tracking_codes.db";

    public TrackingCodeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRACKING_CODES_TABLE = "CREATE TABLE " +
                TrackingCodeContract.TrackingCodeEntry.TABLE_NAME + " (" +
                TrackingCodeContract.TrackingCodeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrackingCodeContract.TrackingCodeEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                TrackingCodeContract.TrackingCodeEntry.COLUMN_TRACKING_CODE + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_TRACKING_CODES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement your database upgrade logic here
    }

    public static final class TrackingCodeContract {
        private TrackingCodeContract() {}

        public static final class TrackingCodeEntry implements BaseColumns {
            public static final String TABLE_NAME = "tracking_codes";
            public static final String COLUMN_PHONE_NUMBER = "phone_number";
            public static final String COLUMN_TRACKING_CODE = "tracking_code";
        }
    }
}


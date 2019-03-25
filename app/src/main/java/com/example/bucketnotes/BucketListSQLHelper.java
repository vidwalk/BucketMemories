package com.example.bucketnotes;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class BucketListSQLHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "bucket.list.db";
    public static final String TABLE_NAME = "LIST";
    public static final String COL1_TASK = "item";
    public static final String _ID = BaseColumns._ID;

    public BucketListSQLHelper(Context context) {
        //1 is bucket list database version
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String createTodoListTable = "CREATE TABLE " + TABLE_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1_TASK + " TEXT)";
        sqlDB.execSQL(createTodoListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqlDB);
    }
}

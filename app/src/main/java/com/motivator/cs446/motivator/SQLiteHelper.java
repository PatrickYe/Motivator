package com.motivator.cs446.motivator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jacobsimon on 3/1/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_COMPLETEDON = "completedOn";

    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_CREATE = "create table "
            + TABLE_TASKS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_DEADLINE + " bigint not null, "
            + COLUMN_COMPLETEDON + " bigint not null, "
            + COLUMN_STATE + " text not null, "
            + COLUMN_REPEAT + " text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
}

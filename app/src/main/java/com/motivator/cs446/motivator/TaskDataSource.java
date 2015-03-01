package com.motivator.cs446.motivator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobsimon on 2/27/15.
 */
public class TaskDataSource {

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_DEADLINE, SQLiteHelper.COLUMN_STATE};

    public TaskDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task createTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, task.title);
        values.put(SQLiteHelper.COLUMN_DEADLINE, task.deadline.getTime());
        values.put(SQLiteHelper.COLUMN_STATE, task.state.toString());
        long insertId = db.insert(SQLiteHelper.TABLE_TASKS, null, values);
        Cursor cursor = db.query(SQLiteHelper.TABLE_TASKS,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
        return task;
    }

    public void deleteTask(Task task) {
        long id = task.id;
        System.out.println("Task deleted with id: " + id);
        db.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Task> getAllTasks() {

        List<Task> comments = new ArrayList<Task>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task comment = cursorToTask(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task(cursor.getString(1), new Date(cursor.getInt(2)),
                Task.State.valueOf(cursor.getString(3)));
        task.id = cursor.getLong(0);
        return task;
    }
}

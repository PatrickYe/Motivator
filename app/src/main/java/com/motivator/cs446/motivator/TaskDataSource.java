package com.motivator.cs446.motivator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobsimon on 2/27/15.
 */
public class TaskDataSource {

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_DEADLINE, SQLiteHelper.COLUMN_STATE, SQLiteHelper.COLUMN_REPEAT,
            SQLiteHelper.COLUMN_COMPLETEDON};

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

        String repeat = "";
        for(int i = 0; i < task.repeat.size() - 1; i++) {
            repeat = repeat + task.repeat.get(i) + ",";
        }
        repeat = repeat + task.repeat.get(task.repeat.size() - 1);
        values.put(SQLiteHelper.COLUMN_REPEAT, repeat);

        Log.d("mehdi", "Inserting repeat:" + repeat);

        values.put(SQLiteHelper.COLUMN_COMPLETEDON, task.completedOn.getTime());

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

        List<Task> tasks = new ArrayList<Task>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_TASKS,
                allColumns, null, null, null, SQLiteHelper.COLUMN_DEADLINE, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tasks;
    }

    public List<Task> getInProgressTasks() {
        List<Task> tasks = new ArrayList<Task>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_TASKS, allColumns, SQLiteHelper.COLUMN_STATE
                + " = '" + Task.State.IN_PROGRESS.toString() + "'", null, null, null,
                SQLiteHelper.COLUMN_DEADLINE);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<Task>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_TASKS, allColumns, SQLiteHelper.COLUMN_STATE
                + " = '" + Task.State.COMPLETED.toString() + "'", null, null, null,
                SQLiteHelper.COLUMN_COMPLETEDON);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }


    public Task updateTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, task.title);
        values.put(SQLiteHelper.COLUMN_DEADLINE, task.deadline.getTime());
        values.put(SQLiteHelper.COLUMN_STATE, task.state.toString());

        values.put(SQLiteHelper.COLUMN_REPEAT, task.repeat.toString());

        values.put(SQLiteHelper.COLUMN_COMPLETEDON, task.deadline.getTime());

        db.update(SQLiteHelper.TABLE_TASKS, values, SQLiteHelper.COLUMN_ID + " = " + task.id,null);
        return task;
    }

    private Task cursorToTask(Cursor cursor) {
        String repeat = cursor.getString(4);
        List<Integer> listOfInteger = null;
        if (repeat != null) {
            List<String> listOfString = Arrays.asList(cursor.getString(4).split("\\s*,\\s*"));

            listOfInteger = new ArrayList<Integer>();
            for(String s : listOfString) {
                try {
                    listOfInteger.add(Integer.valueOf(s));
                } catch (Exception e) {
                    listOfInteger.add(0);
                }

            }
        }
        Log.d("mehdi", "Retrieving repeat:" + listOfInteger.toString());
        Task task = new Task(cursor.getString(1), new Date(cursor.getLong(2)),
                Task.State.valueOf(cursor.getString(3)), listOfInteger, new Date(cursor.getLong(5)));

        task.id = cursor.getLong(0);
        return task;
    }
}

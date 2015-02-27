package com.motivator.cs446.motivator;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobsimon on 2/27/15.
 */
public class TaskDataSource {

    public List<Task> tasks;
    private static TaskDataSource sharedInstance;

    public static TaskDataSource getInstance() {
        if(sharedInstance == null) {
            sharedInstance = new TaskDataSource();
        }
        return sharedInstance;
    }

    private TaskDataSource() {

    }

    public void addTask(Task task, Context context) {
        tasks.add(task);
        save(context);
    }

    public void updateState(Task task, Task.State state, Context context) {
        task.state = state;
        save(context);
    }

    public List<Task> getInProgressTasks() {
        List<Task> inProgress = new ArrayList<Task>() {};
        for(Task task : tasks) {
            if(task.state == Task.State.IN_PROGRESS) {
                inProgress.add(task);
            }
        }
        return inProgress;
    }

    private void save(Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(MainActivity.fileName, Context.MODE_PRIVATE);
            for(Task task : tasks) {
                String taskLine = task.title + ";" + task.deadline.toString() + ";" + task.state + "|";
                outputStream.write(taskLine.getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(Context context) {
        try {
            InputStream inputStream = context.openFileInput(MainActivity.fileName);
            Log.d("JACOB", "reading tasks **************");
            if(inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                while(line != null) {
                    Log.d("JACOB", line + "&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    String[] tasksData = line.split("|");
                    for(String task : tasksData) {
                        String[] taskData = task.split(";");
                        Date dueDate = new Date(taskData[1]);
                        Task.State state = Task.State.valueOf(taskData[2]);
                        tasks.add(new Task(taskData[0], dueDate, state));
                    }
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

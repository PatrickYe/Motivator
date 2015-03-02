package com.motivator.cs446.motivator;

import android.content.Context;
import android.content.Intent;
import android.gesture.Prediction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private TaskDataSource dataSource;

    final ArrayList<Task> list = new ArrayList<Task>();
    public final static String fileName = "taskData";
    StableArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            dataSource = new TaskDataSource(this);
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final ListView listview = (ListView) findViewById(R.id.listview);

        Predicate<Task> isInProgress = new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return task.state == Task.State.IN_PROGRESS;
            }
        };
        adapter = new StableArrayAdapter(this,
                R.layout.task_cell, dataSource.getInProgressTasks());



        listview.setAdapter(adapter);

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                final Task item = (Task) parent.getItemAtPosition(position);
//                view.animate().setDuration(1000).alpha(0)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                Task
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                            }
//                        });
//            }
//
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.add_task) {

            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);

//            list.add(new Task("Homework", "9:00"));

//            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Jacob", "RESUMING %%%%%%%%%%%%%%%%%%%%%%%%%%%");
        adapter.clear();
        adapter.addAll(dataSource.getInProgressTasks());
        adapter.notifyDataSetChanged();
    }

    private class StableArrayAdapter extends ArrayAdapter<Task> {

//        HashMap<Task, Integer> mIdMap = new HashMap<Task, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<Task> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Task task = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_cell, parent, false);
            }
            // Lookup view for data population
            TextView taskName = (TextView) convertView.findViewById(R.id.firstLine);
            TextView deadline = (TextView) convertView.findViewById(R.id.secondLine);
            ImageButton doneButton = (ImageButton) convertView.findViewById(R.id.doneButton);
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
            // Populate the data into the template view using the data object
            taskName.setText(task.title);
            deadline.setText(task.deadline.toString());
            // Set click listeners for the buttons
            final View viewHolder = convertView;
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.state = Task.State.COMPLETED;
                    dataSource.updateTask(task);
                    viewHolder.animate().setDuration(1000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clear();
                                    adapter.addAll(dataSource.getInProgressTasks());
                                    adapter.notifyDataSetChanged();
                                    viewHolder.setAlpha(1);
                                }
                            });
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.state =  Task.State.DELETED;
                    dataSource.updateTask(task);
                    viewHolder.animate().setDuration(1000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clear();
                                    adapter.addAll(dataSource.getInProgressTasks());
                                    adapter.notifyDataSetChanged();
                                    viewHolder.setAlpha(1);
                                }
                            });
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            Task item = getItem(position);
//            return mIdMap.get(item);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}

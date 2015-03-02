package com.motivator.cs446.motivator;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jacobsimon on 2/27/15.
 */
public class AddTaskActivity extends ActionBarActivity {
    private TaskDataSource dataSource;
    private Date deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTitle("New Task");
        dataSource = new TaskDataSource(this);
        deadline = new Date();
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final Button addDate = (Button) findViewById(R.id.addDate);
        final Button addTime = (Button) findViewById(R.id.addTime);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("EEEE, LLL d, y");
        String formattedDate = df.format(c.getTime());

        df = new SimpleDateFormat("H:m");
        String formattedTime = df.format(c.getTime());

        addDate.setText(formattedDate);
        addTime.setText(formattedTime);

        addDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        addTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        newFragment.show(ft,"datePicker");
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        newFragment.show(ft, "timePicker");
    }

    public void updateDate(int year, int month, int day) {
        final Button addDate = (Button) findViewById(R.id.addDate);

        deadline = new Date(year-1900,month,day);
        SimpleDateFormat df = new SimpleDateFormat("EEEE, LLL d, y");
        String formattedDate = df.format(deadline.getTime());
        addDate.setText(formattedDate);
    }

    public void updateTime(int hourOfDay, int minute) {
        final Button addTime = (Button) findViewById(R.id.addTime);
        if (minute < 10) {
            addTime.setText(hourOfDay + ":0" + minute);
        } else {
            addTime.setText(hourOfDay + ":" + minute);
        }
        deadline.setHours(hourOfDay);
        deadline.setMinutes(minute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id  = menuItem.getItemId();
        if(id  == R.id.add_task) {
            EditText taskName = (EditText) findViewById(R.id.taskName);
            try {
                dataSource.createTask(new Task(taskName.getText().toString(), deadline, Task.State.COMPLETED));
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }
}

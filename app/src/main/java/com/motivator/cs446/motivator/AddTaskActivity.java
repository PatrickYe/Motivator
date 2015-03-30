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
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobsimon on 2/27/15.
 */
public class AddTaskActivity extends ActionBarActivity {
    private TaskDataSource dataSource;
    private Date deadline;
    private List<Integer> reccurence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTitle("New Task");
        dataSource = new TaskDataSource(this);
        deadline = new Date();

        reccurence = new ArrayList<Integer>();
        for (int i =0; i< 7; i++) {
            reccurence.add(i, 0);
        }

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final Button addDate = (Button) findViewById(R.id.addDate);
        final Button addTime = (Button) findViewById(R.id.addTime);

        final CheckBox monday = (CheckBox) findViewById(R.id.monday);
        final CheckBox tuesday = (CheckBox) findViewById(R.id.tuesday);
        final CheckBox wednesday = (CheckBox) findViewById(R.id.wednesday);
        final CheckBox thursday = (CheckBox) findViewById(R.id.thursday);
        final CheckBox friday = (CheckBox) findViewById(R.id.friday);
        final CheckBox saturday = (CheckBox) findViewById(R.id.saturday);
        final CheckBox sunday = (CheckBox) findViewById(R.id.sunday);



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

        monday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monday.isChecked()) {
                    reccurence.set(0, 1);
                } else {
                    reccurence.set(0, 0);
                }

            }
        });
        tuesday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tuesday.isChecked()) {
                    reccurence.set(1, 1);
                } else {
                    reccurence.set(1, 0);
                }
            }
        });
        wednesday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wednesday.isChecked()) {
                    reccurence.set(2, 1);
                } else {
                    reccurence.set(2, 0);
                }
            }
        });
        thursday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thursday.isChecked()) {
                    reccurence.set(3, 1);
                } else {
                    reccurence.set(3, 0);
                }
            }
        });
        friday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friday.isChecked()) {
                    reccurence.set(4, 1);
                } else {
                    reccurence.set(4, 0);
                }
            }
        });
        saturday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saturday.isChecked()) {
                    reccurence.set(5, 1);
                } else {
                    reccurence.set(5, 0);
                }
            }
        });
        sunday.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sunday.isChecked()) {
                    reccurence.set(6, 1);
                } else {
                    reccurence.set(6, 0);
                }
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
                Task task = new Task(taskName.getText().toString(), deadline, Task.State.IN_PROGRESS, reccurence);
                if (task.isRecurring()) {
                    task.deadline = task.getNextDueDate();
                }
                dataSource.createTask(task);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }
}

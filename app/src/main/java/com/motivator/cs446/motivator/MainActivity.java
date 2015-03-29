package com.motivator.cs446.motivator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Prediction;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import java.io.File;
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
    FragmentPagerAdapter adapterViewPager;

    final ArrayList<Task> list = new ArrayList<Task>();

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
        final ViewPager pager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapterViewPager);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyPagerAdapter adapter = (MyPagerAdapter) pager.getAdapter();
                if (position < 2) {
                    PendingTaskFragment fragment = (PendingTaskFragment) adapter.getRegisteredFragment(position);
                    fragment.tabChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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

        if (id == R.id.add_picture) {
            Intent intent = new Intent(this, AddPictureActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Jacob", "RESUMING %%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return 3;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    PendingTaskFragment fragment = new PendingTaskFragment().setCompleted(false);
                    return fragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    PendingTaskFragment fragment1  = new PendingTaskFragment().setCompleted(true);
                    return fragment1;
                case 2:
                    GalleryFragment fragment2 = new GalleryFragment();
                    return fragment2;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "In Progress";
                case 1:
                    return "Completed";
                case 2:
                    return "Gallery";
            }
            return "Unknown";

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }
}

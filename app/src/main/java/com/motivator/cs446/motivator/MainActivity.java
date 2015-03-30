package com.motivator.cs446.motivator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private TaskDataSource dataSource;
    FragmentPagerAdapter adapterViewPager;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private UiLifecycleHelper uiHelper;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
    String fbPhotoAddress;
    String description;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
        if (state.isOpened()) {
//            shareButton.setVisibility(View.VISIBLE);
        } else if (state.isClosed()) {
//            shareButton.setVisibility(View.INVISIBLE);
        }
//        shareButton.setVisibility(View.VISIBLE);
        if (pendingPublishReauthorization &&
                state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            pendingPublishReauthorization = false;
        }

    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {

//                            Uri myImageContentUri = new Uri("http://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Wappen_Uri_matt.svg/2000px-Wappen_Uri_matt.svg.png"); // A content Uri to the image you would like to share.
//                            String myAppId = "865505920179290";
//
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.setType("image/*");
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, myImageContentUri);
//
//// Include your Facebook App Id for attribution
//                            shareIntent.putExtra("com.facebook.platform.extra.APPLICATION_ID", myAppId);
//
//                            startActivityForResult(Intent.createChooser(shareIntent, "Share"), myRequestId);

                            if (user != null) {
                                Log.i("u",user.getId());
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                // Set the Textview's text to the user's name.
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    final ArrayList<Task> list = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiHelper = new UiLifecycleHelper(this , callback);
        uiHelper.onCreate(savedInstanceState);
        // Check for an open session
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
        if (savedInstanceState != null) {
            pendingPublishReauthorization =
                    savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }

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
    startService(new Intent(this, MyService.class));
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        Log.i("d","destoryed");
        alarm.setRepeating(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis(), 10000,
                PendingIntent.getService(this, 0, new Intent(this, MyService.class), 0)
        );

    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    private void uploadImage(String des, String path){
        Session session = Session.getActiveSession();
        description = des;

        // Check for publish permissions
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            pendingPublishReauthorization = true;
            Session.NewPermissionsRequest newPermissionsRequest = new Session
                    .NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
            return;
        }

        // Part 1: create callback to get URL of uploaded photo
        Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                // safety check
                if (isFinishing()) {
                    Log.i("1","");
                    return;
                }
                if (response.getError() != null) {  // [IF Failed Posting]
                    Log.d("D", "photo upload problem. Error="+response.getError() );
                    Log.i("2","");
                    return;
                }  //  [ENDIF Failed Posting]

                Object graphResponse = response.getGraphObject().getProperty("id");
                if (graphResponse == null || !(graphResponse instanceof String) ||
                        TextUtils.isEmpty((String) graphResponse)) { // [IF Failed upload/no results]
                    Log.d("D", "failed photo upload/no response");
                    Log.i("3","");
                } else {  // [ELSEIF successful upload]
                    fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" +graphResponse;
                    Log.i("4","");
                    publishStory();
                }  // [ENDIF successful posting or not]
            }  // [END onCompleted]
        };

        //Part 2: upload the photo

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap imageSelected = BitmapFactory.decodeFile(path, options);
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            imageSelected = Bitmap.createBitmap(imageSelected, 0, 0, imageSelected.getWidth(), imageSelected.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
        Request request = Request.newUploadPhotoRequest(session, imageSelected, uploadPhotoRequestCallback);

        request.executeAsync();
    }

    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null){

            Bundle postParams = new Bundle();
            postParams.putString("name", "Motivator");
//            postParams.putString("caption", "Testing1");
            if (description != null){
                postParams.putString("description", description);
            }
//            postParams.putString("link", "https://developers.facebook.com/android");
            postParams.putString("picture", fbPhotoAddress);

            Request.Callback callback= new Request.Callback() {
                public void onCompleted(Response response) {
                    if (response.getGraphObject() != null) {
                        JSONObject graphResponse = response
                                .getGraphObject()
                                .getInnerJSONObject();
                        String postId = null;
                        try {
                            postId = graphResponse.getString("id");
                        } catch (JSONException e) {
                            Log.i("T",
                                    "JSON error " + e.getMessage());
                        }
                        FacebookRequestError error = response.getError();
//                    if (error != null) {
//                        Toast.makeText(getActivity()
//                                        .getApplicationContext(),
//                                error.getErrorMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity()
//                                        .getApplicationContext(),
//                                postId,
//                                Toast.LENGTH_LONG).show();
//                    }
                    }
                    else {
                        Log.i("E","graphobject is null");
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }

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
        uiHelper.onResume();
        Log.d("Jacob", "RESUMING %%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
//        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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

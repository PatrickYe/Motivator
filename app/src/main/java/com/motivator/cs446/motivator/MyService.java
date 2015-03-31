package com.motivator.cs446.motivator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by patrick on 2015-03-30.
 */
public class MyService extends Service{
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private UiLifecycleHelper uiHelper;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
    String fbPhotoAddress = "http://www.online-image-editor.com//styles/2014/images/example_image.png";
    String description;
    private TaskDataSource dataSource;

    @Override
    public void onCreate(){
        Log.i("s", "create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies
        Log.i("s", "time:"+System.currentTimeMillis());

        try {
            dataSource = new TaskDataSource(this);
            dataSource.open();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (dataSource != null) {
            List<Task> tasks = dataSource.getInProgressTasks();
            if (tasks.size() > 1) {
                for (int i = 0; i < tasks.size(); i++) {
                    Calendar c = Calendar.getInstance();
                    Task curtask = tasks.get(i);
                    if (curtask.deadline.after((Date) c.getTime())) {
                        curtask.state = Task.State.FAILED;
                        dataSource.updateTask(curtask);
                        publishStory();
                    }
                }
            }
        }
        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        return START_NOT_STICKY;
    }

    private void uploadImage(String des, String path){
        Session session = Session.getActiveSession();
        description = des;

        // Part 1: create callback to get URL of uploaded photo
        Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
//        I want to restart this service again in one hour
    }


}

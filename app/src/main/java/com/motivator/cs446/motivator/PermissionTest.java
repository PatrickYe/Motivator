package com.motivator.cs446.motivator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class PermissionTest extends ActionBarActivity {
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private UiLifecycleHelper uiHelper;
    private Button shareButton;
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

    public void onShareClick(View v) {
        uploadImage("description","");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout layout = (RelativeLayout) ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)) .inflate(R.layout.activity_permission_test, null);
        profilePictureView = (ProfilePictureView)layout.findViewById(R.id.selection_profile_pic);
        if (profilePictureView == null){
            Log.i("P","empty");
        }
        shareButton = (Button) layout.findViewById(R.id.shareButton);
//        profilePictureView.setCropped(true);
        userNameView = (TextView) layout.findViewById(R.id.selection_user_name);
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
        setContentView(R.layout.activity_permission_test);
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

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
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
                                if (profilePictureView != null) {
                                    profilePictureView.setProfileId(user.getId());
                                }
                                // Set the Textview's text to the user's name.
                                userNameView.setText(user.getName());
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
        if (state.isOpened()) {
            shareButton.setVisibility(View.VISIBLE);
        } else if (state.isClosed()) {
            shareButton.setVisibility(View.INVISIBLE);
        }
        shareButton.setVisibility(View.VISIBLE);
        if (pendingPublishReauthorization &&
                state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            pendingPublishReauthorization = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_permission_test, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
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
}

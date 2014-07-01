package com.inappstudios.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "FACEBOOK -- ";
    private LoginButton loginBtn;
    private Button postImageBtn;
    private Button updateStatusBtn;
    private Button userInfoBtn;

    private TextView userName;

    private UiLifecycleHelper uiHelper;

    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    private static String message = "Sample status posted from android app, working on Facebook SDK for Android";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        userName = (TextView) findViewById(R.id.user_name);

        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    userName.setText("Hello, " + user.getName());
                } else {
                    userName.setText("You are not logged");
                }
            }
        });

        postImageBtn = (Button) findViewById(R.id.post_image);
        postImageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                postImage();
            }
        });

        updateStatusBtn = (Button) findViewById(R.id.update_status);
        updateStatusBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                postStatusMessage();
            }
        });

        userInfoBtn = (Button) findViewById(R.id.getUserInfo);
        userInfoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInfo();
            }
        });

        buttonsEnabled(false);

    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                buttonsEnabled(true);
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                buttonsEnabled(false);
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    public void buttonsEnabled(boolean isEnabled) {
        postImageBtn.setEnabled(isEnabled);
        updateStatusBtn.setEnabled(isEnabled);
        userInfoBtn.setEnabled(isEnabled);
    }

    public void postImage() {
        if (checkPermissions()) {
            Bitmap img = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

            Request uploadRequest = Request.newUploadPhotoRequest(
                Session.getActiveSession(), img, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        Toast.makeText(MainActivity.this,
                                "Photo uploaded successfully",
                                Toast.LENGTH_LONG).show();
                    }
                });
            uploadRequest.executeAsync();
        } else {
            requestPermissions();
        }
    }

    public void postStatusMessage() {
        if (checkPermissions()) {
            Request request = Request.newStatusUpdateRequest(
                Session.getActiveSession(), message,
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if (response.getError() == null)
                            Toast.makeText(MainActivity.this,
                                    "Status updated successfully",
                                    Toast.LENGTH_LONG).show();
                    }
                });
            request.executeAsync();
        } else {
            requestPermissions();
        }
    }

    public void getUserInfo() {

        final Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {

            @Override
            public void onCompleted(GraphUser user,Response response) {

                Log.d(TAG, "USER == null: " + (user == null) + "");

                if (user != null) {

                    String first_name = user.getFirstName();
                    String last_name = user.getLastName();
                    String gender = user.getProperty("gender").toString();
                    String email = user.asMap().get("email").toString();
                    String fb_id = user.getId();
                    String birthday = user.getBirthday();

                    Log.d(TAG, "first_name "+ first_name);
                    Log.d(TAG, "last_name "+ last_name);
                    Log.d(TAG, "gender "+ gender);
                    Log.d(TAG, "email "+ email);
                    Log.d(TAG, "fb_id "+ fb_id);
                    Log.d(TAG, "birthday "+ birthday);


                    Log.d(TAG, "TOKEN 2"+ Session.getActiveSession().getAccessToken());
                    //new facebookRegister().execute();
                }
            }
        });

        request.executeAsync();
        String token = request.getSession().getAccessToken();

        Log.d(TAG, "TOKEN 1"+ token);

        if(token.equals(Session.getActiveSession().getAccessToken())) {
            Log.d(TAG, "Both tokens are equal");
        }
    }


    public boolean checkPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            return s.getPermissions().contains("publish_actions");
        } else
            return false;
    }

    public void requestPermissions() {
        Session s = Session.getActiveSession();
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                    this, PERMISSIONS));
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        buttonsEnabled(Session.getActiveSession().isOpened());
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
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

}

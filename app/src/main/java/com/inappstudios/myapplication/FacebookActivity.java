package com.inappstudios.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;

import java.util.Arrays;

public class FacebookActivity extends Activity {

    private String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setOnErrorListener(new OnErrorListener() {

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG + "Faceebook", "Error " + error.getMessage());
            }
        });

        // set permission list to read user details
        authButton.setReadPermissions(Arrays.asList("basic_info","email","user_birthday"));

        // session state call back event
        authButton.setSessionStatusCallback(new Session.StatusCallback() {


            @Override
            public void call(Session session, SessionState state, Exception exception) {

                if (session.isOpened()) {

                    Log.i(TAG, "Access Token" + session.getAccessToken());
                    Log.i(TAG + " Facebook Session", "Session opened");

                    Request.newMeRequest(session, new Request.GraphUserCallback() {

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

                                //new facebookRegister().execute();
                            }
                        }
                    });
                } else {
                    Log.i(TAG + " Facebook Session", "Session closed");
                }




                if (session.isOpened()) {

                    Log.i(TAG,"Access Token"+ session.getAccessToken());
                    Log.i(TAG + " Facebook Session", "Session opened");


                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser user,Response response) {
                            if (user != null) {

                                String first_name = user.getFirstName();
                                String last_name = user.getLastName();
                                String gender = user.getProperty("gender").toString();
                                String email = user.asMap().get("email").toString();
                                String fb_id = user.getId();
                                String birthday = user.getBirthday();

                                Log.i(TAG, "first_name "+ first_name);
                                Log.i(TAG, "last_name "+ last_name);
                                Log.i(TAG, "gender "+ gender);
                                Log.i(TAG, "email "+ email);
                                Log.i(TAG, "fb_id "+ fb_id);
                                Log.i(TAG, "birthday "+ birthday);

                                //new facebookRegister().execute();

                            }
                        }
                    });
                }
                else {
                    Log.i(TAG + " Facebook Session", "Session closed");
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    private class facebookRegister {
    }
}

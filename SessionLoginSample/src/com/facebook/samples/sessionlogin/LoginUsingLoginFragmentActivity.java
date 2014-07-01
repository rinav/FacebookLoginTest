/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.sessionlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.widget.UserSettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class LoginUsingLoginFragmentActivity extends FragmentActivity {
    private UserSettingsFragment userSettingsFragment;

    String url = "https://www.facebook.com/dialog/oauth?client_id=750363385009098&redirect_uri=http%3A%2F%2F188.226.224.99%2Findex.php&state=59ad2cbb09f82eab623c8243d1d02626&sdk=php-sdk-3.2.3&scope=public_profile%2C+email%2C+publish_stream%2C+user_birthday%2C+user_location%2C+user_hometown%2C+user_about_me%2C+user_photos%2C+user_interests%2C+user_friends%2C+read_fr0iendlists%2C+user_education_history%2C+friends_online_presence%2C+user_actions.video%2C+user_actions.music%2C+user_actions.books%2C+user_likes";

    private List<String> permissions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_fragment_activity);

        permissions = new ArrayList<String>();

        permissions.add("public_profile");
        permissions.add("email");
        //permissions.add("publish_stream");
        permissions.add("user_birthday");
        permissions.add("user_location");
        permissions.add("user_hometown");
        permissions.add("user_about_me");
        permissions.add("user_photos");
        permissions.add("user_interests");
        permissions.add("user_friends");
        permissions.add("read_friendlists");
        permissions.add("user_education_history");
        permissions.add("friends_online_presence");
        permissions.add("user_actions.video");
        permissions.add("user_actions.music");
        permissions.add("user_actions.books");
        permissions.add("user_likes");

        FragmentManager fragmentManager = getSupportFragmentManager();
        userSettingsFragment = (UserSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);

        userSettingsFragment.setReadPermissions( permissions); //  Arrays.asList("user_likes", "user_status"));

        userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d("LoginUsingLoginFragmentActivity", String.format("New session state: %s", state.toString()));

                List<String> permissions = session.getPermissions();

                for (String perm : permissions) {
                    Log.d("xxxxx: ", "Permissions: " + perm);
                }

                String accessToken = session.getAccessToken();
                String applicationId = session.getApplicationId();
                Bundle authorizationBundle = session.getAuthorizationBundle();

                Log.d("xxxxx: ", "accessToken: " + accessToken);
                Log.d("xxxxx: ", "applicationId: " + applicationId);

                Log.d("xxxxx: ", "authorizationBundle: " + (authorizationBundle != null ? authorizationBundle.toString() : "NULL "));

                if(state.isOpened()) {

                    new Request(
                            session,
                            "/me",
                            null,
                            HttpMethod.GET,
                            new Request.Callback() {
                                public void onCompleted(Response response) {

                                    Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:", response.toString());
                                    GraphObject graphObject = response.getGraphObject();

                                    Object id = graphObject.getProperty("id");
                                    Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:", "ID: " + id);
                                    Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:", graphObject.asMap().entrySet().toString());
                                }
                            }
                    ).executeAsync();
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        userSettingsFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}

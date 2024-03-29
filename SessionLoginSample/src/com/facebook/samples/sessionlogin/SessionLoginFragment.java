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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;

import java.util.List;

public class SessionLoginFragment extends Fragment {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);

        buttonLoginLogout = (Button) view.findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView) view.findViewById(R.id.instructionsOrLink);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();

        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);

            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        updateView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();

        if (session.isOpened()) {

            List<String> perms = session.getPermissions();

            for(String perm : perms) {
                Log.d("xxxxxxxxxxxxxxxxxxxx ", perm);
            }

            textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
            buttonLoginLogout.setText(R.string.logout);
            buttonLoginLogout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
        } else {
            textInstructionsOrLink.setText(R.string.instructions);
            buttonLoginLogout.setText(R.string.login);
            buttonLoginLogout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }


    }


    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }

        if(session.getState().isOpened()) {

            new Request(
                    session,
                    "/me",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {

                            Log.d("11111111111:", response.toString());
                            GraphObject graphObject = response.getGraphObject();

                            //Object id = graphObject.getProperty("id");

                            // Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:", graphObject.asMap().entrySet().toString());
                        }
                    }
            ).executeAsync();
        }


    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            Log.d("222222222", "222222222");

            if(session.getState().isOpened()) {

                new Request(
                        session,
                        "/me",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {

                                Log.d("222222222:", response.toString());
                                GraphObject graphObject = response.getGraphObject();

                                //Object id = graphObject.getProperty("id");

                                // Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:", graphObject.asMap().entrySet().toString());
                            }
                        }
                ).executeAsync();
            }




            updateView();
        }
    }

    public static void requestPublishPermissions(Activity activity, Session session, List<String> permissions,
                                                 int requestCode) {
        if (session != null) {
            Session.NewPermissionsRequest reauthRequest = new Session.NewPermissionsRequest(activity, permissions)
                    .setRequestCode(requestCode);
            session.requestNewPublishPermissions(reauthRequest);
        }
    }

    public static void requestReadPermissions(Activity activity, Session session, List<String> permissions,
                                              int requestCode) {
        if (session != null) {
            Session.NewPermissionsRequest reauthRequest = new Session.NewPermissionsRequest(activity, permissions)
                    .setRequestCode(requestCode);
            session.requestNewReadPermissions(reauthRequest);
        }
    }
}

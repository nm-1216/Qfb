package com.sy.qfb.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sy.qfb.R;
import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.User;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.util.QfbFileHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by shenyin on 2017/6/8.
 */

public class DownloadController {
    private static final String TAG = "DownloadController";

//    private static final String url_user = "http://192.168.3.3/user.json";
//    private static final String url_project = "http://192.168.3.3/project.json";

    private static final String url_user = "http://10.90.75.149/user.json";
    private static final String url_project = "http://10.90.75.149/project.json";

    public interface NetworkCallback_Users {
        void networkCallback_Users(List<User> users);
    }

    public interface NetworkCallback_Projects {
        void networkCallback_Projects(List<Project> projects);
    }

    public void downloadUsers(final NetworkCallback_Users callback) {
        Request request = new StringRequest(Request.Method.GET, url_user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success" );

                if (!TextUtils.isEmpty(response)) {
                    new QfbFileHelper().saveFile_User(response);
                }

                Gson gson = new Gson();
                User[] users = gson.fromJson(response, User[].class);
                List<User> lstUser = new ArrayList<User>();
                for (int i = 0; i < users.length; ++i) {
                    lstUser.add(users[i]);
                }
                if (callback != null) {
                    callback.networkCallback_Users(lstUser);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "fail");
            }
        });
        VolleyHelper.getInstance().makeRequest(request);
    }

    public void downloadProjects(final NetworkCallback_Projects callback) {
        Request request = new StringRequest(Request.Method.GET, url_project, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");

                if (!TextUtils.isEmpty(response)) {
                    new QfbFileHelper().saveFile_Project(response);
                }

                Gson gson = new Gson();
                Project[] projects = gson.fromJson(response, Project[].class);
                List<Project> lstProjects = new ArrayList<Project>();
                for (int i = 0; i < projects.length; ++i) {
                    lstProjects.add(projects[i]);
                }
                if (callback != null) {
                    callback.networkCallback_Projects(lstProjects);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "fail");
            }
        });
        VolleyHelper.getInstance().makeRequest(request);
    }

}
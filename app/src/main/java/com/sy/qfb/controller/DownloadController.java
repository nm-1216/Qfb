package com.sy.qfb.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.model.User;
import com.sy.qfb.net.FileRequest;
import com.sy.qfb.net.MyStringRequest;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.util.Global;
import com.sy.qfb.util.QfbFileHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by shenyin on 2017/6/8.
 */

public class DownloadController {
    private static final String TAG = "DownloadController";

//    public static final String SERVER = "http://192.168.3.3/res/";
//    private static final String url_user = "http://192.168.3.3/res/user.json";
//    private static final String url_project = "http://192.168.3.3/res/project.json";
//    private static final String url_manual = "http://192.168.3.3/res/manual.pdf";

//    public static final String SERVER = "http://10.90.75.149:51956/res/";
//    private static final String url_user = "http://10.90.75.149:51956/res/user.json";
//    private static final String url_project = "http://10.90.75.149:51956/res/project.json";
//    private static final String url_manual = "http://10.90.75.149:51956/res/manual.pdf";


//    public static final String SERVER = "http://106.15.231.194/res/";
//    private static final String url_user = "http://106.15.231.194/res/user.json";
//    private static final String url_project = "http://106.15.231.194/res/project.json";
//    private static final String url_manual = "http://106.15.231.194/res/manual.pdf";


//    public static final String SERVER = "http://114.55.105.88:8088/res/";
//    private static final String url_user = "http://114.55.105.88:8088/res/user.json";
//    private static final String url_project = "http://114.55.105.88:8088/res/project.json";
//    private static final String url_manual = "http://114.55.105.88:8088/res/manual.pdf";


    public static final String SERVER = "http://" + Global.SERVER_ADDRESS + "/res/";
    private static final String url_user = "http://" + Global.SERVER_ADDRESS + "/res/user.json";
    private static final String url_project = "http://" + Global.SERVER_ADDRESS + "/res/project.json";
    private static final String url_manual = "http://" + Global.SERVER_ADDRESS + "/res/manual.pdf";


    public interface NetworkCallback_Users {
        void networkCallback_Users(boolean success, List<User> users);
    }

    public interface NetworkCallback_Projects {
        void networkCallback_Projects(boolean success, List<Project> projects);
    }

    public interface NetworkCallback_Manual {
        void networkCallback_Manual(boolean success);
    }

    public interface NetworkCallback_Image {
        void networkCallback_Image(boolean success);
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
                    callback.networkCallback_Users(true, lstUser);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "fail");
                if (callback != null) {
                    callback.networkCallback_Users(false, null);
                }
            }
        });
        VolleyHelper.getInstance().makeRequest(request);
    }

    public void downloadProjects(final NetworkCallback_Projects callback) {
        Request request = new MyStringRequest(Request.Method.GET, url_project, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");
                Logger.d(response);

//                try {
//                    response = new String(response.getBytes(), "UTF-8");

                    if (!TextUtils.isEmpty(response)) {
                        new QfbFileHelper().saveFile_Project(response);
                    }

                    Gson gson = new Gson();
                    Project[] projects = gson.fromJson(response, Project[].class);
                    List<Project> lstProjects = new ArrayList<Project>();
                    for (int i = 0; i < projects.length; ++i) {
                        lstProjects.add(projects[i]);
                    }

                    InitController initController = new InitController();
                    initController.clearAllProjectTables();
                    initController.readProjectsFromJsonString(response);

                    if (callback != null) {
                        callback.networkCallback_Projects(true, lstProjects);
                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (callback != null) {
//                        callback.networkCallback_Projects(false, null);
//                    }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "fail");
                if (callback != null) {
                    callback.networkCallback_Projects(false, null);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> result = super.getHeaders();
//                result.put("Accept-Charset", "UTF-8");
                return result;
            }
        };
        VolleyHelper.getInstance().makeRequest(request);
    }

    public void downloadManual(final NetworkCallback_Manual callback) {

        Request request = new FileRequest(Request.Method.GET, url_manual, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                Log.d(TAG, "success");

                new QfbFileHelper().saveFile_Binary("manual.pdf", response);
                if (callback != null) {
                    callback.networkCallback_Manual(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "fail");
                if (callback != null) {
                    callback.networkCallback_Manual(false);
                }
            }
        });
        VolleyHelper.getInstance().makeRequest(request);

    }


    public void downloadImage(final String imgName, final NetworkCallback_Image callback) {
        String url = DownloadController.SERVER + imgName;

        Request request = new FileRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                QfbFileHelper qfbFileHelper = new QfbFileHelper();
                qfbFileHelper.saveFile_Binary(imgName, response);

                callback.networkCallback_Image(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.networkCallback_Image(false);
            }
        });

        VolleyHelper.getInstance().makeRequest(request);
    }


}

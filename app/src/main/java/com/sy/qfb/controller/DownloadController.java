package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.User;
import com.sy.qfb.model.QfbVersion;
import com.sy.qfb.net.FileRequest;
import com.sy.qfb.net.MyStringRequest;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.service.UserService;
import com.sy.qfb.util.Global;
import com.sy.qfb.util.QfbFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by shenyin on 2017/6/8.
 */

public class DownloadController {
    private static final String TAG = "DownloadController";


    public interface NetworkCallback_Projects {
        void networkCallback_Projects(boolean success, List<Project> projects);
    }

    public interface NetworkCallback_Manual {
        void networkCallback_Manual(boolean success);
    }

    public interface NetworkCallback_Image {
        void networkCallback_Image(boolean success);
    }

    public void downloadUsers(final UserService.NetworkCallback_Users callback) {
        new UserService().downloadUsers(callback);
    }

    public void downloadProjects(final NetworkCallback_Projects callback, String userName) {
        String prj_url = Global.getUrl_Base() + userName + ".json";
        Request request = new MyStringRequest(Request.Method.GET, prj_url, new Response.Listener<String>() {
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
        String url_manual = Global.getUrl_Manual();
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
        String url = Global.getUrl_Base() + imgName;
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

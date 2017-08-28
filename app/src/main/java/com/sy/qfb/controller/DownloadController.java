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

    public interface VersionCallback {
        void versionCallback(boolean success, boolean hasNewVersion, QfbVersion qfbVersion);
    }

    public interface DownloadNewVersionCallback {
        void downloaded(boolean succes, String filePath);
    }

    public void downloadUsers(final NetworkCallback_Users callback) {
        String url_user = Global.getUrl_User();
        Logger.d("url_user = " + url_user);
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
                SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();
                db.execSQL("DELETE FROM " + QfbContract.UserEntry.TABLE_NAME);
                for (int i = 0; i < users.length; ++i) {
                    lstUser.add(users[i]);

                    ContentValues cv = new ContentValues();
                    cv.put(QfbContract.UserEntry.COLUMN_NAME_USERNAME, users[i].username);
                    cv.put(QfbContract.UserEntry.COLUMN_NAME_PASSWORD, users[i].password);
                    db.insert(QfbContract.UserEntry.TABLE_NAME, null, cv);
                }
                db.close();

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

    public void hasNewVersion(final VersionCallback versionCallback, final double currentVersion) {
        String url = Global.getUrl_Version();
        Request request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean hasNewVersion = false;

                Gson gson = new Gson();
                QfbVersion qfbVersion = gson.fromJson(response, QfbVersion.class);
                hasNewVersion = qfbVersion.latest_version > currentVersion;

                versionCallback.versionCallback(true, hasNewVersion, qfbVersion);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                versionCallback.versionCallback(false, false, null);
            }
        });

        VolleyHelper.getInstance().makeRequest(request);
    }

    public void downloadNewVersion(final QfbVersion qfbVersion, final DownloadNewVersionCallback downloadCallback) {
        String url = Global.getUrl_Base() + qfbVersion.file_name;
        Request request = new FileRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                Logger.d("download apk success");
                String fileName = qfbVersion.file_name;
                QfbFileHelper qfbFileHelper = new QfbFileHelper();
                String filePath = qfbFileHelper.saveFile_Download(fileName, response);
                Logger.d("filePath = " + filePath);

                downloadCallback.downloaded(true, filePath);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d("download apk fail");
                downloadCallback.downloaded(false, "");
            }
        });

        VolleyHelper.getInstance().makeRequest(request);
    }

}

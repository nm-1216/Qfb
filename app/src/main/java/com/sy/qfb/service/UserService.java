package com.sy.qfb.service;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.User;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.util.Global;
import com.sy.qfb.util.QfbFileHelper;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final String TAG = "UserService";

    public interface NetworkCallback_Users {
        void networkCallback_Users(boolean success, List<User> users);
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

}
package com.sy.qfb.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sy.qfb.ble.MyApplication;

/**
 * Created by jshenf on 2017/6/8.
 */

public class VolleyHelper {
    private static VolleyHelper instance;

    private VolleyHelper() {
        initQueue(MyApplication.APP_CONTEXT);
    }

    public static VolleyHelper getInstance() {
        if (instance == null) {
            instance = new VolleyHelper();
        }
        return instance;
    }

    RequestQueue mQueue;

    private void initQueue(Context context) {
        mQueue = Volley.newRequestQueue(context);
        mQueue.start();
    }

    public void stopQueue() {
        if (mQueue != null) {
            mQueue.stop();
            mQueue = null;
        }
    }

    public void makeRequest(Request request) {
        mQueue.add(request);
    }
}

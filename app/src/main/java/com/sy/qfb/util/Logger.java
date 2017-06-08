package com.sy.qfb.util;

import android.util.Log;

/**
 * Created by shenyin on 2017/6/8.
 */

public class Logger {
    public static String TAG = "qfb";
    public static boolean ENABLED = true;

    public static void d(String msg) {
        if (ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("[Debug]");
            sb.append(msg);
            Log.d(TAG, sb.toString());
        }
    }

    public static void e(String msg) {
        if (ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("[Debug]");
            sb.append(msg);
            Log.e(TAG, sb.toString());
        }
    }
}

package com.sy.qfb.util;

import android.widget.Toast;

import com.sy.qfb.ble.MyApplication;

/**
 * Created by shenyin on 2017/6/11.
 */

public class ToastHelper {
    static Toast toast;

    public static void showShort(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.APP_CONTEXT, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLong(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.APP_CONTEXT, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}

package com.sy.qfb.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sy.qfb.ble.MyApplication;

/**
 * Created by shenyin on 2017/7/20.
 */

public class Global {
    public static String DEFAULT_SERVER_ADDRESS = "114.55.105.88:8088";

    public static String getServerAddress() {
        Context context = MyApplication.APP_CONTEXT;
        SharedPreferences sharedPreferences = context.getSharedPreferences("qfb", Context.MODE_PRIVATE);
        String serverDomain = sharedPreferences.getString("server_domain", DEFAULT_SERVER_ADDRESS);
        return serverDomain;
    }

    public static String getUrl_Base() {
        String serverAddr = getServerAddress();
        return "http://" + serverAddr + "/res/";
    }

    public static String getUrl_User() {
        String serverAddr = getServerAddress();
        return "http://" + serverAddr + "/res/user.json";
    }

    public static String getUrl_Project() {
        String serverAddr = getServerAddress();
//        return "http://" + serverAddr + "/res/user.json";
        return "http://" + serverAddr + "/res/";
    }

    public static String getUrl_Manual() {
        String serverAddr = getServerAddress();
        return "http://" + serverAddr + "/res/manual.pdf";
    }

    public static String getUrl_Version() {
        String serverAddr = getServerAddress();
        return "http://" + serverAddr + "/res/version.json";
    }

    public static String getUrl_Upload() {
        String serverAddr = getServerAddress();
        return "http://" + serverAddr + "/api/MeasureDatas";
    }

    public static boolean isNetworkOnline(Context context) {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }


//    public static final String SERVER = "http://" + Global.SERVER_ADDRESS + "/res/";
//    public static final String url_user = "http://" + Global.SERVER_ADDRESS + "/res/user.json";
//    public static final String url_project = "http://" + Global.SERVER_ADDRESS + "/res/";
//    public static final String url_manual = "http://" + Global.SERVER_ADDRESS + "/res/manual.pdf";
//    public static final String URL_VERSION = "http://" + Global.SERVER_ADDRESS + "/res/version.json";
//
//    public static String URL_UPLOAD = "http://" + Global.SERVER_ADDRESS + "/api/MeasureDatas";




//    private static String URL_UPLOAD = "http://114.55.105.88:8088/api/MeasureDatas";
//    private static String URL_UPLOAD = "http://" + Global.SERVER_ADDRESS + "/api/MeasureDatas";
//    private static String URL_UPLOAD = "http://10.90.75.149:51956/api/MeasureDatas";




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


//    public static final String SERVER = "http://" + Global.SERVER_ADDRESS + "/res/";
//    private static final String url_user = "http://" + Global.SERVER_ADDRESS + "/res/user.json";
//    private static final String url_project = "http://" + Global.SERVER_ADDRESS + "/res/";
//    private static final String url_manual = "http://" + Global.SERVER_ADDRESS + "/res/manual.pdf";
//    public static final String URL_VERSION = "http://" + Global.SERVER_ADDRESS + "/res/version.json";
}

package com.sy.qfb.ble;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orm.SugarContext;
import com.sy.qfb.controller.InitController;
import com.sy.qfb.net.VolleyHelper;

@SuppressLint("SdCardPath")
public class MyApplication extends Application {
    public static Context APP_CONTEXT;
    public static String filePath = "/sdcard/data/qfb/";

    public String FILENAME = "";

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        filePath = "/sdcard/data/qfb/";

        APP_CONTEXT = this.getApplicationContext();

        SugarContext.init(APP_CONTEXT);

//        deleteDatabase("Qfb.db");

        Logger.addLogAdapter(new AndroidLogAdapter());

        new InitController().init();
    }

    public void setFileName(String filename) {
        this.FILENAME = filename;
    }

    public String getFileName() {

        return FILENAME;
    }

    public String getFilePath() {

        return filePath;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VolleyHelper.getInstance().stopQueue();
    }
}

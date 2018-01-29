package com.sy.qfb.ble;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orm.SugarContext;
import com.sy.qfb.controller.InitController;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.net.VolleyHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

@SuppressLint("SdCardPath")
public class MyApplication extends Application {
    public static Context APP_CONTEXT;
    public static String filePath = "/sdcard/data/qfb/";

    public String FILENAME = "";

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


        UMConfigure.init(this, "5a6f3450f29d9824d60001b0", "field",
                UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
        UMConfigure.setLogEnabled(true);



        filePath = "/sdcard/data/qfb/";

        APP_CONTEXT = this.getApplicationContext();

        SugarContext.init(APP_CONTEXT);

//        deleteDatabase("Qfb.db");

//        emptyDataDB();

//        Logger.addLogAdapter(new AndroidLogAdapter());

        Logger.addLogAdapter(new LogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }

            @Override
            public void log(int priority, String tag, String message) {
                Log.d("Qfb", message);
            }
        });

        new InitController().init();
    }

    private void emptyDataDB() {
        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();
        db.execSQL("DELETE FROM " + QfbContract.DataEntry.TABLE_NAME);
        db.close();
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

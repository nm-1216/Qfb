package com.sy.qfb.ble;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.sy.qfb.controller.InitController;
import com.sy.qfb.net.VolleyHelper;

@SuppressLint("SdCardPath") public class MyApplication extends Application{
	public static Context APP_CONTEXT;
	public static String filePath = "/sdcard/data/qfb/";
	
	public String FILENAME = "";
	
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		filePath = "/sdcard/data/qfb/";

		APP_CONTEXT = this.getApplicationContext();

//		deleteDatabase("Qfb.db");

		new InitController().init();
	}
	public void setFileName(String filename){
		this.FILENAME = filename;
	}
	public String getFileName(){
		
		return FILENAME;
	} 
	public String getFilePath(){
		
		return filePath;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		VolleyHelper.getInstance().stopQueue();
	}
}

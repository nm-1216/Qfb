package com.sy.qfb.util;

import android.content.Context;

import com.sy.qfb.R;
import com.sy.qfb.ble.MyApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jshenf on 2017/6/9.
 */

public class QfbFileHelper {
    public static final String FILENAME_USER = "user.json";
    public static final String FILENAME_PROJECT = "project.json";

    public String readFile_User() {
        return readFile(FILENAME_USER);
    }

    public String readFile_Project() {
        return readFile(FILENAME_PROJECT);
    }

    private String readFile(String fileName) {
        Context context = MyApplication.APP_CONTEXT;
        File fileDir = context.getFilesDir();
        File file = new File(fileDir, fileName);
        try {
            if (!file.exists()) {
                return "";
            }
            FileReader fileReader = new FileReader(file);

            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int len = -1;
            while ((len = fileReader.read(buffer, 0, 1024)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            return sb.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "";
    }

    public boolean isFileExist_User() {
        return existFile(FILENAME_USER);
    }

    public boolean isFileExist_Project() {
        return existFile(FILENAME_PROJECT);
    }

    private boolean existFile(String fileName) {
        Context context = MyApplication.APP_CONTEXT;
        File fileDir = context.getFilesDir();
        File file = new File(fileDir, fileName);
        return file.exists();
    }

    public void saveFile_User(String content) {
        saveToFile(FILENAME_USER, content);
    }

    public void saveFile_Project(String content) {
        saveToFile(FILENAME_PROJECT, content);
    }

    private void saveToFile(String fileName, String content) {
        Context context = MyApplication.APP_CONTEXT;
        File fileDir = context.getFilesDir();
        File file = new File(fileDir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String readInitialFile_User() {
        return readRaw(R.raw.user);
    }

    public String readInitialFile_Project() {
        return readRaw(R.raw.project);
    }

    private String readRaw(int id) {
        InputStream inputStream = MyApplication.APP_CONTEXT.getResources().openRawResource(id);
        StringBuffer sb = new StringBuffer();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

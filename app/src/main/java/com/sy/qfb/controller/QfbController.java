package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenyin on 2017/6/9.
 */

public class QfbController {

    public List<Project> getProjects() {
        List<Project> result = new ArrayList<Project>();

        QfbDbHelper dbHelper = new QfbDbHelper(MyApplication.APP_CONTEXT);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c_prj = db.query(QfbContract.ProjectEntry.TABLE_NAME, null,
                null, null, null, null, null);
        if (c_prj.getCount() <= 0) return result;


        return result;
    }
}

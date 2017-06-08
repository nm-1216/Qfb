package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;

/**
 * Created by shenyin on 2017/6/9.
 */

public class UserController {

    public boolean login(String username, String password) {
        QfbDbHelper dbHelper = new QfbDbHelper(MyApplication.APP_CONTEXT);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(QfbContract.UserEntry.TABLE_NAME, null,
                null, null, null, null, null);
        if (c.getCount() <= 0) return true;
        c.moveToFirst();
        while (true) {
            String u = c.getString(c.getColumnIndex(QfbContract.UserEntry.COLUMN_NAME_USERNAME));
            String p = c.getString(c.getColumnIndex(QfbContract.UserEntry.COLUMN_NAME_PASSWORD));
            if (username.equals(u) && password.equals(password)) {
                c.close();
                return true;
            }
            if (!c.moveToNext()) {
                break;
            }
        }
        c.close();
        return false;
    }
}

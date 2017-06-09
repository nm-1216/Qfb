package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jshenf on 2017/6/9.
 */

public class LoginController {

    public List<User> getUsers() {
        List<User> result = new ArrayList<User>();

        QfbDbHelper dbHelper = QfbDbHelper.getInstance();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(QfbContract.UserEntry.TABLE_NAME, null, null, null, null, null, null);

        if (c.getCount() == 0) {
            db.close();
            return result;
        }

        c.moveToFirst();
        while (true) {
            int userId = c.getInt(c.getColumnIndex(QfbContract.UserEntry.COLUMN_NAME_USERID));
            String username = c.getString(c.getColumnIndex(QfbContract.UserEntry.COLUMN_NAME_USERNAME));
            String password = c.getString(c.getColumnIndex(QfbContract.UserEntry.COLUMN_NAME_PASSWORD));

            User user = new User();
            user.user_id = userId;
            user.username = username;
            user.password = password;
            result.add(user);
            if (!c.moveToNext()) break;
        }

        c.close();
        db.close();
        return result;
    }
}

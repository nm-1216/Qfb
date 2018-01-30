package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.exception.IException;
import com.sy.qfb.model.User;
import com.sy.qfb.service.UserService;
import com.sy.qfb.util.MD5;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jshenf on 2017/6/9.
 */

public class LoginController extends BaseController {
    public static final int SYNC_USERS_KEY = 311;
    public static final int LOGIN_KEY = 312;

    public void syncUsers(UpdateViewAsyncCallback<Boolean> viewCallback) {
        doAsyncTask(SYNC_USERS_KEY, viewCallback,
                new DoAsyncTaskCallback<Void, Boolean>() {
                    @Override
                    public Boolean doAsyncTask(Void... voids) throws IException {
                        new UserService().downloadUsers(null);
                        return true;
                    }
                });

    }

    public void login(UpdateViewAsyncCallback<User> viewCallback, String userName, String password) {
        doAsyncTask(LOGIN_KEY, viewCallback,
                new DoAsyncTaskCallback<String, User>() {
                    @Override
                    public User doAsyncTask(String... strings) throws IException {
                        String userName = strings[0];
                        String password = strings[1];
                        return getUser(userName, password);
                    }
                }, userName, password);
    }

    private User getUser(String userName, String password) {
        List<User> users = getUsers();
        try {
            String pwd = MD5.md5(password);
            Logger.d("pwd = " + pwd);
            for (User user : users) {
                if (user.username.equalsIgnoreCase(userName) && user.password.equalsIgnoreCase(pwd)) {
//                    authed = true;
                    return user;
                }
            }
        } catch (Exception e) {
//            authed = false;
            return null;
        }
        return null;
    }

    private List<User> getUsers() {
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

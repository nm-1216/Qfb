package com.sy.qfb.controller;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.exception.IException;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shenyin on 2018/1/5.
 */

public class DeleteOldDataController extends BaseController {
    public static final int DELETE_KEY = 1011;

    public void deleteOldData(final UpdateViewAsyncCallback<Boolean> viewAsyncCallback) {
        doAsyncTask(DELETE_KEY, viewAsyncCallback,
                new DoAsyncTaskCallback<Void, Boolean>() {
                    @Override
                    public Boolean doAsyncTask(Void... voids) throws IException {
                        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();

                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        long min = calendar.getTimeInMillis();
                        long cacheOldestTime = min - 1000 * 60 * 60 * 24 * 50;
                        db.execSQL("DELETE FROM " + QfbContract.DataEntry.TABLE_NAME + " WHERE " +
                                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < " + cacheOldestTime);

                        return true;
                    }
                });

    }

}

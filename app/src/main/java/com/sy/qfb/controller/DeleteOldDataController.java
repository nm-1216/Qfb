package com.sy.qfb.controller;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shenyin on 2018/1/5.
 */

public class DeleteOldDataController {
    public interface DeletedOldDataCallback {
        public void deletedOldData(boolean success);
    }

    public void deleteOldData(DeletedOldDataCallback cb) {
        new DeleteOldDataTask(cb).execute();
    }

    public class DeleteOldDataTask extends AsyncTask<Void, Integer, Void> {
        private DeletedOldDataCallback callback;

        public DeleteOldDataTask(DeletedOldDataCallback cb) {
            this.callback = cb;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.deletedOldData(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
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

//            db.close();

            return null;
        }
    }
}

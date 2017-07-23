package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.MeasureData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jshenf on 2017/6/9.
 */

public class SaveController {

    public void saveData(List<MeasureData> lstData) {
        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();

        Cursor c1 = db.query(QfbContract.DataEntry.TABLE_NAME, null, null, null, null, null, null);
        Logger.d("c1.getCount() = " + c1.getCount());
        c1.close();

        db.beginTransaction();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long min = calendar.getTimeInMillis();
        long max = min + 1000 * 60 * 60 * 24;

        Logger.d("min = " + min);
        Logger.d("max = " + max);

        if (lstData.size() > 0) {
            MeasureData data = lstData.get(0);
            db.execSQL("DELETE FROM " + QfbContract.DataEntry.TABLE_NAME + " WHERE " +
                            QfbContract.DataEntry.COLUMN_NAME_PROJID + "=" + data.projectId + " AND " +
                            QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME + "='" + data.projectName + "' AND " +
                            QfbContract.DataEntry.COLUMN_NAME_PRDID + "=" + data.productId + " AND " +
                            QfbContract.DataEntry.COLUMN_NAME_PRD_NAME + "='" + data.productName + "' AND " +
                            QfbContract.DataEntry.COLUMN_NAME_TID + "=" + data.targetId + " AND " +
                            QfbContract.DataEntry.COLUMN_NAME_T_NAME + "='" + data.targetName + "' AND " +
                            QfbContract.DataEntry.COLUMN_NAME_PGID + "=" + data.pageId + " AND " +
                            QfbContract.DataEntry.COLUMN_NAME_USERNAME + "='" + data.username + "' AND " +
                            QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">=" + min + " AND " +
                            QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + "<" + max
                    );
        }


        for (MeasureData data : lstData) {
            ContentValues content = new ContentValues();
//            content.put(QfbContract.DataEntry.COLUMN_NAME_DATAID, data.dataId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PROJID, data.projectId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME, data.projectName);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PRDID, data.productId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME, data.productName);
            content.put(QfbContract.DataEntry.COLUMN_NAME_TID, data.targetId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_T_NAME, data.targetName);
            content.put(QfbContract.DataEntry.COLUMN_NAME_T_TYPE, data.targetType);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PGID, data.pageId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_MPID, data.pointId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_MPOINT, data.measure_point);
            content.put(QfbContract.DataEntry.COLUMN_NAME_DIRECTION, data.direction);
            content.put(QfbContract.DataEntry.COLUMN_NAME_UPPER_TOLERANCE, data.upperTolerance);
            content.put(QfbContract.DataEntry.COLUMN_NAME_LOWER_TOLERANCE, data.lowerTolerance);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_1, data.value1);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_2, data.value2);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_3, data.value3);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_4, data.value4);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_5, data.value5);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_6, data.value6);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_7, data.value7);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_8, data.value8);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_9, data.value9);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_10, data.value10);
            content.put(QfbContract.DataEntry.COLUMN_NAME_USERNAME, data.username);
            content.put(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP, data.timestamp);
            content.put(QfbContract.DataEntry.COLUMN_NAME_UPLOADED, 0);

            long rowId = db.insert(QfbContract.DataEntry.TABLE_NAME, null, content);
            Logger.d("rowId = " + rowId);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();

        dumpData();
    }

    private void dumpData() {
        SQLiteDatabase db = QfbDbHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.query(QfbContract.DataEntry.TABLE_NAME, null, null, null, null, null, null);
        Logger.d("cursor.getCount() = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(true) {
                int dataId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DATAID));
                int prjId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
                String prjName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));
                int prdId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
                String prdName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));
                int target_Id = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TID));
                String target_Name = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_NAME));
                String targetType = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_TYPE));
                int page_Id = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PGID));
                int pointId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_MPID));
                String measurePoint = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_MPOINT));
                String direction = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DIRECTION));
                String upperTolerance = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_UPPER_TOLERANCE));
                String lowerTolerance = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_LOWER_TOLERANCE));
                String value1 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_1));
                String value2 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_2));
                String value3 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_3));
                String value4 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_4));
                String value5 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_5));
                String value6 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_6));
                String value7 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_7));
                String value8 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_8));
                String value9 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_9));
                String value10 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_10));
                String username = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_USERNAME));
                long timestamp = cursor.getLong(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP));
                int uploaded = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_UPLOADED));
                Logger.d("dataId = " + dataId + ", prjId = " + prjId + ", prjName = " + prjName +
                        ", prdId = " + prdId + ", prdName = " + prdName + ", target_Id = " + target_Id +
                        ", target_Name = " + target_Name + ", targetType = " + targetType + ", page_Id" + page_Id +
                        ", measurePoint = " + measurePoint + ", value1 = " + value1 + ", value2 = " + value2 +
                        ", value3 = " + value3 + ", value4 = " + value4 + ", username = " + username +
                        ", timestamp = " + timestamp + ", uploaded = " + uploaded);
                if (!cursor.moveToNext()) break;
            }
        }
        cursor.close();
        db.close();
    }

}

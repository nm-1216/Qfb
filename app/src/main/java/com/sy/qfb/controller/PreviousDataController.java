package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;
import com.sy.qfb.controller.model.QueryDataOfSpecificDay;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.exception.IException;
import com.sy.qfb.model.MeasureData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PreviousDataController extends BaseController {

    public static final int GET_DATA_BY_DATE = 3001;

    public void getDataByDate(BaseController.UpdateViewAsyncCallback<List<MeasureData>> viewCallback,
                              QueryDataOfSpecificDay queryData) {
        doAsyncTask(GET_DATA_BY_DATE, viewCallback,
                new DoAsyncTaskCallback<QueryDataOfSpecificDay, List<MeasureData>>() {
                    @Override
                    public List<MeasureData> doAsyncTask(QueryDataOfSpecificDay... queryDataOfSpecificDays) throws IException {
                        return getDataByDate_Impl(queryDataOfSpecificDays[0]);
                    }
                }, queryData);
    }


    private List<MeasureData> getDataByDate_Impl(QueryDataOfSpecificDay queryData) {
        Logger.d(queryData.toString());

        int projectId = queryData.projectId;
        String projectName = queryData.projectName;
        int productId = queryData.productId;
        String productName = queryData.productName;
        int targetId = queryData.targetId;
        String targetName = queryData.targetName;
        int pageId = queryData.pageId;
        String username = queryData.userName;
        Date date = queryData.date;

        List<MeasureData> result = new ArrayList<MeasureData>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long min = calendar.getTimeInMillis();
        long max = min + 1000 * 60 * 60 * 24;

        Logger.d("min = " + min);
        Logger.d("max = " + max);

        SQLiteDatabase db = QfbDbHelper.getInstance().getReadableDatabase();
//        Cursor cursor = db.query(false, QfbContract.DataEntry.TABLE_NAME, null,
//                QfbContract.DataEntry.COLUMN_NAME_PROJID + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_PRDID + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_PRD_NAME + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_TID + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_T_NAME + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_PGID + "=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">=? and " +
//                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + "<?",
//                new String[] {"" + projectId, projectName, "" + productId, productName, "" + targetId,
//                targetName, "" + pageId, "" + min, "" + max},
//                null, null, null, null, null
//        );

        Cursor cursor = db.rawQuery("SELECT * FROM " + QfbContract.DataEntry.TABLE_NAME + " WHERE " +
                        QfbContract.DataEntry.COLUMN_NAME_PROJID + "=" + projectId + " AND " +
                        QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME + "='" + projectName + "' AND " +
                        QfbContract.DataEntry.COLUMN_NAME_PRDID + "=" + productId + " AND " +
                        QfbContract.DataEntry.COLUMN_NAME_PRD_NAME + "='" + productName + "' AND " +
                        QfbContract.DataEntry.COLUMN_NAME_TID + "=" + targetId + " AND " +
                        QfbContract.DataEntry.COLUMN_NAME_T_NAME + "='" + targetName + "' AND " +
                        QfbContract.DataEntry.COLUMN_NAME_PGID + "=" + pageId + " AND " +
                        QfbContract.DataEntry.COLUMN_NAME_USERNAME + "='" + username + "' AND " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">=" + min + " AND " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + "<" + max
                , null);

        Logger.d("today data getCount() = " + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (true) {

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
                String user_name = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_USERNAME));
                long timestamp = cursor.getLong(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP));
                int uploaded = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_UPLOADED));

                MeasureData data = new MeasureData();
                data.dataId = dataId;
                data.projectId = prjId;
                data.projectName = prjName;
                data.productId = prdId;
                data.productName = prdName;
                data.targetId = target_Id;
                data.targetName = target_Name;
                data.targetType = targetType;
                data.pageId = page_Id;
                data.pointId = pointId;
                data.measure_point = measurePoint;
                data.direction = direction;
                data.upperTolerance = upperTolerance;
                data.lowerTolerance = lowerTolerance;
                data.value1 = value1;
                data.value2 = value2;
                data.value3 = value3;
                data.value4 = value4;
                data.value5 = value5;
                data.value6 = value6;
                data.value7 = value7;
                data.value8 = value8;
                data.value9 = value9;
                data.value10 = value10;
                data.username = user_name;
                data.timestamp = timestamp;
                data.uploaded = uploaded;

                result.add(data);

                if (!cursor.moveToNext()) break;
            }
        }

        Logger.d("result.size() = " + result.size());
        return result;
    }
}
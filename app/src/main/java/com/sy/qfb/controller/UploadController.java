package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.util.Global;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenyin on 2017/6/8.
 */

public class UploadController {
    public interface UploadFinishCallback {
        void finish(int successCount, int failCount, HashMap<MeasureData, Integer> uploadRecoder);
    }

//    private static String URL_UPLOAD = "http://114.55.105.88:8088/api/MeasureDatas";
    private static String URL_UPLOAD = "http://" + Global.SERVER_ADDRESS + "/api/MeasureDatas";

//    private static String URL_UPLOAD = "http://10.90.75.149:51956/api/MeasureDatas";

    SQLiteDatabase db_shared;
    Cursor cursor;

    private HashMap<MeasureData, Integer> uploadRecoder = new HashMap<MeasureData, Integer>();

    public int getDataSize() {
        db_shared = QfbDbHelper.getInstance().getReadableDatabase();

        cursor = db_shared.query(QfbContract.DataEntry.TABLE_NAME, null,
                QfbContract.DataEntry.COLUMN_NAME_UPLOADED + "=?",
                new String[] {"0"}, null, null, null);

        Logger.d("cursor.getCount() = " + cursor.getCount());

        if (cursor.getCount() <= 0) {
            cursor.close();
            db_shared.close();
            return 0;
        }
        return cursor.getCount();
    }

    public void uploadData(UploadFinishCallback callback) {
        cursor.moveToFirst();
        while(true) {
            int did = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DATAID));
            int projectId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
            String projectName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));
            int productId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
            String productName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));
            int targetId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TID));
            String targetName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_NAME));
            String targetType = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_TYPE));
            int pageId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PGID));
            int pointId = cursor.getInt(cursor.getColumnIndex((QfbContract.DataEntry.COLUMN_NAME_MPID)));
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

            MeasureData measureData = new MeasureData();
            measureData.dataId = did;
            measureData.projectId = projectId;
            measureData.projectName = projectName;
            measureData.productId = productId;
            measureData.productName = productName;
            measureData.targetId = targetId;
            measureData.targetName = targetName;
            measureData.targetType = targetType;
            measureData.pageId = pageId;
            measureData.pointId = pointId;
            measureData.measure_point = measurePoint;
            measureData.direction = direction;
            measureData.upperTolerance = upperTolerance;
            measureData.lowerTolerance = lowerTolerance;
            measureData.value1 = value1;
            measureData.value2 = value2;
            measureData.value3 = value3;
            measureData.value4 = value4;
            measureData.value5 = value5;
            measureData.value6 = value6;
            measureData.value7 = value7;
            measureData.value8 = value8;
            measureData.value9 = value9;
            measureData.value10 = value10;
            measureData.username = username;
            measureData.timestamp = timestamp;

            uploadRecoder.put(measureData, 0);

            if (!cursor.moveToNext()) {
                break;
            }
        }
        cursor.close();
        db_shared.close();

        for (MeasureData measureData : uploadRecoder.keySet()) {
            VolleyHelper volleyHelper = VolleyHelper.getInstance();
            volleyHelper.makeRequest(new UploadDataRequest(
                    StringRequest.Method.POST, URL_UPLOAD, new UploadSuccess(measureData, callback),
                    new UploadFail(measureData, callback), measureData));
        }
    }


    private class UploadSuccess implements Response.Listener<String> {
        private MeasureData measureData;
        private UploadFinishCallback finishCallback;

        public UploadSuccess(MeasureData data, UploadFinishCallback callback) {
            this.measureData = data;
            this.finishCallback = callback;
        }

        @Override
        public void onResponse(String response) {
            if (uploadRecoder.containsKey(measureData)) {
                uploadRecoder.put(measureData, 1);
            }
            checkFinish(finishCallback);
        }
    }

    private class UploadFail implements Response.ErrorListener {
        private MeasureData measureData;
        private UploadFinishCallback finishCallback;

        public UploadFail(MeasureData data, UploadFinishCallback callback) {
            this.measureData = data;
            this.finishCallback = callback;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            if (uploadRecoder.containsKey(measureData)) {
                uploadRecoder.put(measureData, 2);
            }
            checkFinish(finishCallback);
        }
    }

    private void checkFinish(UploadFinishCallback callback) {
        boolean finished = true;
        for (MeasureData data : uploadRecoder.keySet()) {
            if (uploadRecoder.get(data) == 0) {
                finished = false;
            }
        }

        if (finished) {
            SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();
            for (MeasureData data : uploadRecoder.keySet()) {
                if (uploadRecoder.get(data) == 1) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(QfbContract.DataEntry.COLUMN_NAME_UPLOADED, 1);
                    db.update(QfbContract.DataEntry.TABLE_NAME, contentValues,
                            QfbContract.DataEntry.COLUMN_NAME_DATAID + "=?", new String[]{"" + data.dataId});
                }
            }
            db.close();
        }

        if (finished) {
            int success_count = 0;
            int fail_count = 0;
            for(MeasureData data : uploadRecoder.keySet()) {
                if (uploadRecoder.get(data) == 1) {
                    success_count++;
                } else if (uploadRecoder.get(data) == 2) {
                    fail_count++;
                }
            }

            callback.finish(success_count, fail_count, uploadRecoder);
        }
    }

    private class UploadDataRequest extends StringRequest {
        private MeasureData measureData;

        public UploadDataRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, MeasureData data) {
            super(method, url, listener, errorListener);
            this.measureData = data;
        }


        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
//            params.put("data_id", "" + measureData.dataId);

            Logger.d("projectId = " + measureData.projectId + ", projectName = " + measureData.projectName +
            ", productId = " + measureData.productId + ", productName = " + measureData.productName +
            ", targetId = " + measureData.targetName + ", targetName = " + measureData.targetName +
            ", targetType = " + measureData.targetType + ", pageId = " + measureData.pageId +
            ", measurePoint = " +measureData.measure_point + ", direction = " + measureData.direction +
            ", value1 = " + measureData.value1 + ", value2 = " + measureData.value2 +
            ", value3 = " + measureData.value3 + ", value4 = " + measureData.value4 +
            ", username = " + measureData.username + ", timestamp = " + measureData.timestamp);

            params.put("ProjectId", "" + measureData.projectId);
            params.put("ProjectName", measureData.projectName == null ? "" : measureData.projectName);
            params.put("ProductId", "" + measureData.productId);
            params.put("ProductName", measureData.productName == null ? "" : measureData.productName);
            params.put("TargetId", "" + measureData.targetId);
            params.put("TargetName", measureData.targetName == null ? "" : measureData.targetName);
            params.put("TargetType", measureData.targetType == null ? "" : measureData.targetType);
            params.put("PageId", "" + measureData.pageId);
            params.put("pointId", "" + measureData.pointId);
            params.put("MeasurePoint", measureData.measure_point == null ? "" : measureData.measure_point);
            params.put("Direction", measureData.direction == null ? "" : measureData.direction);
            params.put("UpperTolerance", measureData.upperTolerance == null ? "" : measureData.upperTolerance);
            params.put("LowerTolerance", measureData.lowerTolerance == null ? "" : measureData.lowerTolerance);
            params.put("Value1", measureData.value1 == null ? "" : measureData.value1);
            params.put("Value2", measureData.value2 == null ? "" : measureData.value2);
            params.put("Value3", measureData.value3 == null ? "" : measureData.value3);
            params.put("Value4", measureData.value4 == null ? "" : measureData.value4);
            params.put("Value5", measureData.value5 == null ? "" : measureData.value5);
            params.put("Value6", measureData.value6 == null ? "" : measureData.value6);
            params.put("Value7", measureData.value7 == null ? "" : measureData.value7);
            params.put("Value8", measureData.value8 == null ? "" : measureData.value8);
            params.put("Value9", measureData.value9 == null ? "" : measureData.value9);
            params.put("Value10", measureData.value10 == null ? "" : measureData.value10);
            params.put("Username", measureData.username == null ? "" : measureData.username);
            params.put("Timestamp", "" + measureData.timestamp);


//            params.put("ProjectId", "" + 1);
//            params.put("ProjectName", "Car");
//            params.put("ProductId", "" + 1);
//            params.put("ProductName", "Door");
//            params.put("TargetId", "" + 1);
//            params.put("TargetName", "Hole");
//            params.put("TargetType", "NG,OK");
//            params.put("PageId", "" + 1);
//            params.put("MeasurePoint", "sdf23");
//            params.put("Direction", "CC");
//            params.put("Value1", "OK");
//            params.put("Value2", "OK");
//            params.put("Value3", "NG");
//            params.put("Value4", "NG");
//            params.put("Username", "shenyin");
//            params.put("Timestamp", "" + 112234235);

            return params;
        }

        @Override
        protected String getParamsEncoding() {
            return "UTF-8";
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> result = super.getHeaders();
//            result.put("Content-Type", "application/x-www-form-urlencoded");
            return result;
        }
    }

}

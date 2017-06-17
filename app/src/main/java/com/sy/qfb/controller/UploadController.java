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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenyin on 2017/6/8.
 */

public class UploadController {
    public interface UploadFinishCallback {
        void finish(int successCount, int failCount, HashMap<MeasureData, Integer> uploadRecoder);
    }

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
            String measurePoint = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_MPOINT));
            String value1 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_1));
            String value2 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_2));
            String value3 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_3));
            String value4 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_4));
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
            measureData.measure_point = measurePoint;
            measureData.value1 = value1;
            measureData.value2 = value2;
            measureData.value3 = value3;
            measureData.value4 = value4;
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
            String url = "";
            VolleyHelper volleyHelper = VolleyHelper.getInstance();
            volleyHelper.makeRequest(new UploadDataRequest(
                    StringRequest.Method.POST, url, new UploadSuccess(measureData, callback),
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
            params.put("data_id", "" + measureData.dataId);
            params.put("project_id", "" + measureData.projectId);
            params.put("project_name", measureData.projectName);
            params.put("product_id", "" + measureData.productId);
            params.put("product_name", measureData.productName);
            params.put("target_id", "" + measureData.targetId);
            params.put("target_name", measureData.targetName);
            params.put("target_type", measureData.targetType);
            params.put("page_id", "" + measureData.pageId);
            params.put("measure_point", measureData.measure_point);
            params.put("value1", measureData.value1);
            params.put("value2", measureData.value2);
            params.put("value3", measureData.value3);
            params.put("value4", measureData.value4);
            params.put("username", measureData.username);
            params.put("timestamp", "" + measureData.timestamp);
            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> result = super.getHeaders();
            result.put("Content-Type", "application/x-www-form-urlencoded");
            return result;
        }
    }

}

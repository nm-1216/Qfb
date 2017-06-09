package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.util.Logger;

import java.util.List;

/**
 * Created by jshenf on 2017/6/9.
 */

public class SaveController {

    public void saveData(List<MeasureData> lstData) {
        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();

        db.beginTransaction();

        for (MeasureData data : lstData) {
            ContentValues content = new ContentValues();
//            content.put(QfbContract.DataEntry.COLUMN_NAME_DATAID, data.dataId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PROJID, data.projectId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PRDID, data.productId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_TID, data.targetId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_PGID, data.pageId);
            content.put(QfbContract.DataEntry.COLUMN_NAME_MPOINT, data.measure_point);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_1, data.value1);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_2, data.value2);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_3, data.value3);
            content.put(QfbContract.DataEntry.COLUMN_NAME_VALUE_4, data.value4);
            content.put(QfbContract.DataEntry.COLUMN_NAME_USERNAME, data.username);
            content.put(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP, data.timestamp);
            content.put(QfbContract.DataEntry.COLUMN_NAME_UPLOADED, 0);

            long rowId = db.insert(QfbContract.DataEntry.TABLE_NAME, null, content);
            Logger.d("rowId = " + rowId);
        }

        db.endTransaction();

        db.close();
    }

}

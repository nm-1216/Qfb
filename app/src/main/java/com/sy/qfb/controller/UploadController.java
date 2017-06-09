package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;

/**
 * Created by shenyin on 2017/6/8.
 */

public class UploadController {
    SQLiteDatabase db;
    Cursor cursor;

    public int getDataSize() {
        db = QfbDbHelper.getInstance().getWritableDatabase();

        cursor = db.query(QfbContract.DataEntry.TABLE_NAME, null,
                QfbContract.DataEntry.COLUMN_NAME_UPLOADED + "=?",
                new String[] {"0"}, null, null, null);

        if (cursor.getCount() <= 0) {
            return 0;
        }
        return cursor.getCount();
    }

    public void uploadData() {
        cursor.moveToFirst();
        while(true) {

            int did = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DATAID));
            ContentValues contentValues = new ContentValues();
            contentValues.put(QfbContract.DataEntry.COLUMN_NAME_UPLOADED, 1);
            db.update(QfbContract.DataEntry.TABLE_NAME, contentValues,
                    QfbContract.DataEntry.COLUMN_NAME_DATAID + "=?", new String[] {"" + did});

            if (!cursor.moveToNext()) {
                break;
            }
        }

        db.close();
    }
}

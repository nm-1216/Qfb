package com.sy.qfb.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sy.qfb.ble.MyApplication;

/**
 * Created by shenyin on 2017/6/9.
 */

public class QfbDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "Qfb.db";

    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + QfbContract.UserEntry.TABLE_NAME + " (" +
                    QfbContract.UserEntry.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY," +
                    QfbContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    QfbContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + QfbContract.UserEntry.TABLE_NAME;


    private static final String SQL_CREATE_PROJ =
            "CREATE TABLE " + QfbContract.ProjectEntry.TABLE_NAME + " (" +
                    QfbContract.ProjectEntry.COLUMN_NAME_PROJID + " INTEGER PRIMARY KEY," +
                    QfbContract.ProjectEntry.COLUMN_NAME_PROJNAME + " TEXT)";

    private static final String SQL_DELETE_PROJ =
            "DROP TABLE IF EXISTS " + QfbContract.ProjectEntry.TABLE_NAME;


    private static final String SQL_CREATE_PRD =
            "CREATE TABLE " + QfbContract.ProductEntry.TABLE_NAME + " (" +
                    QfbContract.ProductEntry.COLUMN_NAME_PRDID + " INTEGER PRIMARY KEY," +
                    QfbContract.ProductEntry.COLUMN_NAME_PROJID + " INTEGER," +
                    QfbContract.ProductEntry.COLUMN_NAME_PRDNAME + " TEXT)";

    private static final String SQL_DELETE_PRD =
            "DROP TABLE IF EXISTS " + QfbContract.ProductEntry.TABLE_NAME;


    private static final String SQL_CREATE_TARGET =
            "CREATE TABLE " + QfbContract.TargetEntry.TABLE_NAME + " (" +
                    QfbContract.TargetEntry.COLUMN_NAME_TID + " INTEGER PRIMARY KEY," +
                    QfbContract.TargetEntry.COLUMN_NAME_PRDID + " INTEGER," +
                    QfbContract.TargetEntry.COLUMN_NAME_TVT + " TEXT," +
                    QfbContract.TargetEntry.COLUMN_NAME_TNAME + " TEXT)";

    private static final String SQL_DELETE_TARGET =
            "DROP TABLE IF EXISTS " + QfbContract.TargetEntry.TABLE_NAME;


    private static final String SQL_CREATE_PAGE =
            "CREATE TABLE " + QfbContract.PageEntry.TABLE_NAME + " (" +
                    QfbContract.PageEntry.COLUMN_NAME_PGID + " INTEGER PRIMARY KEY," +
                    QfbContract.PageEntry.COLUMN_NAME_PGNAME + " TEXT," +
                    QfbContract.PageEntry.COLUMN_NAME_PICS + " TEXT," +
                    QfbContract.PageEntry.COLUMN_NAME_TID + " INTEGER)";

    private static final String SQL_DELETE_PAGE =
            "DROP TABLE IF EXISTS " + QfbContract.PageEntry.TABLE_NAME;

    private static final String SQL_CREATE_MEASURE_POINT =
            "CREATE TABLE " + QfbContract.MeasurePointEntry.TABLE_NAME + " (" +
                    QfbContract.MeasurePointEntry.COLUMN_NAME_MPID + " INTEGER PRIMARY KEY," +
                    QfbContract.MeasurePointEntry.COLUMN_NAME_PGID + " INTEGER," +
                    QfbContract.MeasurePointEntry.COLUMN_NAME_POINT + " TEXT," +
                    QfbContract.MeasurePointEntry.COLUMN_NAME_DIRECTION + " TEXT)";

    private static final String SQL_DELETE_MEASURE_POINT =
            "DROP TABLE IF EXISTS " + QfbContract.MeasurePointEntry.TABLE_NAME;

    private static final String SQL_CREATE_DATA =
            "CREATE TABLE " + QfbContract.DataEntry.TABLE_NAME + " (" +
                    QfbContract.DataEntry.COLUMN_NAME_DATAID + " INTEGER PRIMARY KEY," +
                    QfbContract.DataEntry.COLUMN_NAME_PROJID + " INTEGER," +
                    QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_PRDID + " INTEGER," +
                    QfbContract.DataEntry.COLUMN_NAME_PRD_NAME + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_TID + " INTEGER," +
                    QfbContract.DataEntry.COLUMN_NAME_T_NAME + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_T_TYPE + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_PGID + " INTEGER," +
                    QfbContract.DataEntry.COLUMN_NAME_MPOINT + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_DIRECTION + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_VALUE_1 + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_VALUE_2 + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_VALUE_3 + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_VALUE_4 + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    QfbContract.DataEntry.COLUMN_NAME_UPLOADED + " INTEGER," +
                    QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " NUMBER)";

    private static final String SQL_DELETE_DATA =
            "DROP TABLE IF EXISTS " + QfbContract.DataEntry.TABLE_NAME;

    private static QfbDbHelper instance;

    public static QfbDbHelper getInstance() {
        if (instance == null) {
            instance = new QfbDbHelper();
        }
        return instance;
    }

    private QfbDbHelper() {
        super(MyApplication.APP_CONTEXT, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_PROJ);
        db.execSQL(SQL_CREATE_PRD);
        db.execSQL(SQL_CREATE_TARGET);
        db.execSQL(SQL_CREATE_PAGE);
        db.execSQL(SQL_CREATE_MEASURE_POINT);
        db.execSQL(SQL_CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER);
        db.execSQL(SQL_DELETE_PROJ);
        db.execSQL(SQL_DELETE_PRD);
        db.execSQL(SQL_DELETE_TARGET);
        db.execSQL(SQL_DELETE_PAGE);
        db.execSQL(SQL_DELETE_MEASURE_POINT);
        db.execSQL(SQL_DELETE_DATA);
        onCreate(db);
    }
}

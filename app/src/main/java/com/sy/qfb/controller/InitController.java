package com.sy.qfb.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.model.User;
import com.sy.qfb.util.QfbFileHelper;

/**
 * Created by jshenf on 2017/6/9.
 */

public class InitController {
    public void init() {

        QfbFileHelper qfbFileHelper = new QfbFileHelper();

        QfbDbHelper qfbDbHelper = QfbDbHelper.getInstance();
        SQLiteDatabase db_user = qfbDbHelper.getWritableDatabase();

        Cursor c_user = db_user.query(QfbContract.UserEntry.TABLE_NAME, null, null, null, null, null, null);
        Logger.d("c_user.getCount() = " + c_user.getCount());
        if (c_user.getCount() == 0) {
            String strUsers = "";
            if (qfbFileHelper.isFileExist_User()) {
                strUsers = qfbFileHelper.readFile_User();
            } else {
                strUsers = qfbFileHelper.readInitialFile_User();
            }

            if (!TextUtils.isEmpty(strUsers)) {
                Gson gson = new Gson();
                User[] users = gson.fromJson(strUsers, User[].class);
                for (int i = 0; i < users.length; ++i) {
                    User user = users[i];

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(QfbContract.UserEntry.COLUMN_NAME_USERNAME, user.username);
                    contentValues.put(QfbContract.UserEntry.COLUMN_NAME_PASSWORD, user.password);
                    db_user.insert(QfbContract.UserEntry.TABLE_NAME, null, contentValues);
                }
            }
        }
        c_user.close();
        db_user.close();


        SQLiteDatabase db_project = QfbDbHelper.getInstance().getReadableDatabase();
        Cursor c_proj = db_project.query(QfbContract.ProjectEntry.TABLE_NAME, null, null, null, null, null, null);
        Logger.d("c_proj.getCount() = " + c_proj.getCount());
        if (c_proj.getCount() == 0) {
//        if (true) {
            String strProj = "";
            if (qfbFileHelper.isFileExist_Project()) {
                strProj = qfbFileHelper.readFile_Project();
            } else {
                strProj = qfbFileHelper.readInitialFile_Project();
            }
            readProjectsFromJsonString(strProj);
        }
        c_proj.close();
        db_project.close();

    }

    public void clearAllProjectTables() {
        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();
        db.execSQL("DELETE FROM " + QfbContract.ProjectEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QfbContract.ProductEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QfbContract.TargetEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QfbContract.PageEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + QfbContract.MeasurePointEntry.TABLE_NAME);
        db.close();
    }


    public void readProjectsFromJsonString(String strProj) {
        SQLiteDatabase db = QfbDbHelper.getInstance().getWritableDatabase();

        if (!TextUtils.isEmpty(strProj)) {
            Gson gson = new Gson();
            Project[] projects = gson.fromJson(strProj, Project[].class);
            for (int i = 0; i < projects.length; ++i) {
                Project project = projects[i];

                ContentValues contentValues = new ContentValues();
                contentValues.put(QfbContract.ProjectEntry.COLUMN_NAME_PROJID, project.project_id);
                contentValues.put(QfbContract.ProjectEntry.COLUMN_NAME_PROJNAME, project.project_name);
                db.insert(QfbContract.ProjectEntry.TABLE_NAME, null, contentValues);

                if (project.products != null && project.products.length > 0) {
                    for (Product product : project.products) {
                        ContentValues cv_prd = new ContentValues();
                        cv_prd.put(QfbContract.ProductEntry.COLUMN_NAME_PROJID, project.project_id);
                        cv_prd.put(QfbContract.ProductEntry.COLUMN_NAME_PRDID, product.product_id);
                        cv_prd.put(QfbContract.ProductEntry.COLUMN_NAME_PRDNAME, product.product_name);
                        db.insert(QfbContract.ProductEntry.TABLE_NAME, null, cv_prd);

                        if (product.targets != null && product.targets.length > 0) {
                            for(Target target : product.targets) {
                                ContentValues cv_target = new ContentValues();
                                cv_target.put(QfbContract.TargetEntry.COLUMN_NAME_PRDID, product.product_id);
                                cv_target.put(QfbContract.TargetEntry.COLUMN_NAME_TID, target.target_id);
                                cv_target.put(QfbContract.TargetEntry.COLUMN_NAME_TNAME, target.target_name);
                                cv_target.put(QfbContract.TargetEntry.COLUMN_NAME_TVT, target.value_type);
                                db.insert(QfbContract.TargetEntry.TABLE_NAME, null, cv_target);

                                if (target.pages != null && target.pages.length > 0) {
                                    for(Page page : target.pages) {
                                        ContentValues cv_page = new ContentValues();
                                        cv_page.put(QfbContract.PageEntry.COLUMN_NAME_TID, target.target_id);
                                        cv_page.put(QfbContract.PageEntry.COLUMN_NAME_PGID, page.page_id);
                                        cv_page.put(QfbContract.PageEntry.COLUMN_NAME_PGNAME, page.page_name);

                                        StringBuilder sb = new StringBuilder();
                                        if (page.pictures != null) {
                                            for (int k = 0; k < page.pictures.length; ++k) {
                                                if (k != 0) sb.append(",");
                                                sb.append(page.pictures[k]);
                                            }
                                        }
                                        cv_page.put(QfbContract.PageEntry.COLUMN_NAME_PICS, sb.toString());

                                        db.insert(QfbContract.PageEntry.TABLE_NAME, null, cv_page);

                                        if (page.measure_points != null && page.measure_points.length > 0) {

                                            String previousMeasurePointName = "";
                                            for (int j = 0; j < page.measure_points.length; ++j) {
                                                ContentValues cv_mp = new ContentValues();
                                                cv_mp.put(QfbContract.MeasurePointEntry.COLUMN_NAME_PGID, page.page_id);
                                                if (!TextUtils.isEmpty(page.measure_points[j].point)) {
                                                    previousMeasurePointName = page.measure_points[j].point;
                                                }
                                                cv_mp.put(QfbContract.MeasurePointEntry.COLUMN_NAME_POINT, previousMeasurePointName);
                                                cv_mp.put(QfbContract.MeasurePointEntry.COLUMN_NAME_DIRECTION, page.measure_points[j].direction);
                                                db.insert(QfbContract.MeasurePointEntry.TABLE_NAME, null, cv_mp);
                                            }

                                        }

                                    }
                                }

                            }
                        }

                    }

                }
            }
        }
        db.close();

    }
}

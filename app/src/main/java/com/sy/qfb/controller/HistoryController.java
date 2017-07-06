package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.viewmodel.ProjectHistoryItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jshenf on 2017/6/15.
 */

public class HistoryController {

    public List<ProjectHistoryItem> getProjectHistoryItems() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        long max = calendar.getTimeInMillis();
        long min = max - 1000 * 60 * 60 * 24 * 7;


        SQLiteDatabase db_entries = QfbDbHelper.getInstance().getReadableDatabase();
        Cursor cursor_entries = db_entries.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[]{QfbContract.DataEntry.COLUMN_NAME_PROJID, QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME,
                        QfbContract.DataEntry.COLUMN_NAME_PRDID, QfbContract.DataEntry.COLUMN_NAME_PRD_NAME,
                        QfbContract.DataEntry.COLUMN_NAME_TID, QfbContract.DataEntry.COLUMN_NAME_T_NAME,
                        QfbContract.DataEntry.COLUMN_NAME_T_TYPE, QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP},
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">= ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ?",
                new String[] {"" + min, "" + max}, null, null, QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " DESC", null);

        List<ProjectHistoryItem> result = new ArrayList<ProjectHistoryItem>();
        if (cursor_entries.getCount() > 0) {
            cursor_entries.moveToFirst();
            while(true) {
                ProjectHistoryItem item = new ProjectHistoryItem();

                item.projectId = cursor_entries.getInt(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
                item.projectName = cursor_entries.getString(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));
                item.productId = cursor_entries.getInt(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
                item.productName = cursor_entries.getString(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));
                item.targetId = cursor_entries.getInt(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TID));
                item.targetName = cursor_entries.getString(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_NAME));
                item.targetType = cursor_entries.getString(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_TYPE));;
                item.timeStamp = cursor_entries.getLong(cursor_entries.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP));

//                Cursor c_time = db_entries.rawQuery("SELECT " + QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " FROM " +
//                                QfbContract.DataEntry.TABLE_NAME + " WHERE " +
//                                QfbContract.DataEntry.COLUMN_NAME_PROJID + "=" + item.projectId + " AND " +
//                                QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME + "='" + item.projectName + "' AND " +
//                                QfbContract.DataEntry.COLUMN_NAME_PRDID + "=" + item.productId + " AND " +
//                                QfbContract.DataEntry.COLUMN_NAME_PRD_NAME + "='" + item.productName + "' AND " +
//                                QfbContract.DataEntry.COLUMN_NAME_TID + "=" + item.targetId + " AND " +
//                                QfbContract.DataEntry.COLUMN_NAME_T_NAME + "='" + item.targetName + "'"
//                        , null);
//                if (c_time.getCount() > 0) {
//                    c_time.moveToFirst();
//                    item.timeStamp = c_time.getLong(c_time.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP));
//                }
//                c_time.close();

                result.add(item);

                if (!cursor_entries.moveToNext()) break;
            }
        }

        cursor_entries.close();
        db_entries.close();
        return result;
    }

    public List<MeasureData> getData(ProjectHistoryItem item) {
        SQLiteDatabase db_data = QfbDbHelper.getInstance().getReadableDatabase();

        Cursor cursor = db_data.query(QfbContract.DataEntry.TABLE_NAME,null,
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + "=?",
                new String[] {"" + item.timeStamp}, null, null, null, null);

        List<MeasureData> result = new ArrayList<MeasureData>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(true) {

                int dataId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DATAID));
                int prjId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
                String prjName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));
                int prdId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
                String prdName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));
                int targetId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TID));
                String targetName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_NAME));
                String targetType = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_TYPE));
                int pageId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PGID));
                String measurePoint = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_MPOINT));
                String direction = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DIRECTION));
                String value1 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_1));
                String value2 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_2));
                String value3 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_3));
                String value4 = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_VALUE_4));
                String username = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_USERNAME));
                long timestamp = cursor.getLong(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP));
                int uploaded = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_UPLOADED));

                MeasureData data = new MeasureData();
                data.dataId = dataId;
                data.projectId = prjId;
                data.projectName = prjName;
                data.productId = prdId;
                data.productName = prdName;
                data.targetId = targetId;
                data.targetName = targetName;
                data.targetType = targetType;
                data.pageId = pageId;
                data.measure_point = measurePoint;
                data.direction = direction;
                data.value1 = value1;
                data.value2 = value2;
                data.value3 = value3;
                data.value4 = value4;
                data.username = username;
                data.timestamp = timestamp;
                data.uploaded = uploaded;

                result.add(data);

                if (!cursor.moveToNext()) break;
            }
        }
        cursor.close();
        db_data.close();
        return result;
    }

    SQLiteDatabase db_shared = null;

    public List<Project> getProjects(long min, long max) {
        List<Project> result = new ArrayList<Project>();


        Logger.d("min = " + min + ", max = " + max);
        db_shared = QfbDbHelper.getInstance().getReadableDatabase();

        Cursor cTemp = db_shared.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[] {QfbContract.DataEntry.COLUMN_NAME_PROJID, QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME},
                null, null, null, null, null, null);

        Logger.d("cTemp.getCount() = " + cTemp.getCount());

        Cursor cTemp2 = db_shared.query(QfbContract.DataEntry.TABLE_NAME,
                null, null, null, null, null, null, null);
        Logger.d("cTemp2.getCount() = " + cTemp2.getCount());


        Cursor c = db_shared.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[] {QfbContract.DataEntry.COLUMN_NAME_PROJID, QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME},
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">= ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ?",
                new String[] {"" + min, "" + max}, null, null, null, null);

        Logger.d("c.getCount() = " + c.getCount());
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (true) {

                int id = c.getInt(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
                String name = c.getString(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));

                Project project = new Project();
                project.project_id = id;
                project.project_name = name;

                result.add(project);

                if (!c.moveToNext()) break;
            }
        }

        c.close();
        return result;
    }

    public List<Product> getProducts(long min, long max, int id_prj) {
        List<Product> result = new ArrayList<Product>();

        Cursor c = db_shared.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[] {QfbContract.DataEntry.COLUMN_NAME_PRDID, QfbContract.DataEntry.COLUMN_NAME_PRD_NAME},
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">= ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_PROJID + " = ?",
                new String[] {"" + min, "" + max, "" + id_prj}, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            while (true) {

                int id = c.getInt(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
                String name = c.getString(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));

                Product product = new Product();
                product.product_id = id;
                product.product_name = name;

                result.add(product);

                if (!c.moveToNext()) break;
            }
        }

        c.close();
        return result;
    }

    public List<Target> getTargets(long min, long max, int id_product) {
        List<Target> result = new ArrayList<Target>();

        Cursor c = db_shared.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[] {QfbContract.DataEntry.COLUMN_NAME_TID, QfbContract.DataEntry.COLUMN_NAME_T_NAME,
                        QfbContract.DataEntry.COLUMN_NAME_T_TYPE},
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">= ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_PRDID + " = ?",
                new String[] {"" + min, "" + max, "" + id_product}, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            while (true) {

                int id = c.getInt(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_TID));
                String name = c.getString(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_NAME));
                String type = c.getString(c.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_T_TYPE));

                Target target = new Target();
                target.target_id = id;
                target.target_name = name;
                target.value_type = type;

                result.add(target);

                if (!c.moveToNext()) break;
            }
        }

        c.close();
        db_shared.close();
        return result;
    }

    public ArrayMap<Page, List<MeasureData>> getPages(long min, long max, int t_id) {
        ArrayMap<Page, List<MeasureData>> result = new ArrayMap<Page, List<MeasureData>>();
        List<Page> pages = new ArrayList<Page>();

        SQLiteDatabase db = QfbDbHelper.getInstance().getReadableDatabase();
        Cursor c_p = db.query(true, QfbContract.DataEntry.TABLE_NAME,
                new String[] {QfbContract.DataEntry.COLUMN_NAME_PGID},
                QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + ">= ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TIMESTAMP + " < ? and " +
                        QfbContract.DataEntry.COLUMN_NAME_TID + " = ?",
                new String[] {"" + min, "" + max, "" + t_id},
                null, null, null, null);

        if (c_p.getCount() > 0) {
            c_p.moveToFirst();
            while(true) {
                int id = c_p.getInt(c_p.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PGID));

                Page page = new Page();
                page.page_id = id;

                pages.add(page);

                if (!c_p.moveToNext()) break;
            }
        }

        c_p.close();

        for (int i = 0; i < pages.size(); ++i) {
            Page page = pages.get(i);

            Cursor cursor = db.query(QfbContract.DataEntry.TABLE_NAME, null,
                    QfbContract.DataEntry.COLUMN_NAME_PGID + " = ?",
                    new String[] {"" + page.page_id}, null, null, null, null);

            if (cursor.getCount() > 0) {

                List<MeasureData> lstData = new ArrayList<MeasureData>();
                cursor.moveToFirst();
                while(true) {
                    int dataId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_DATAID));
                    int prjId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJID));
                    String prjName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PROJ_NAME));
                    int prdId = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRDID));
                    String prdName = cursor.getString(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_PRD_NAME));
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
                    int uploaded = cursor.getInt(cursor.getColumnIndex(QfbContract.DataEntry.COLUMN_NAME_UPLOADED));

                    MeasureData data = new MeasureData();
                    data.dataId = dataId;
                    data.projectId = prjId;
                    data.projectName = prjName;
                    data.productId = prdId;
                    data.productName = prdName;
                    data.targetId = targetId;
                    data.targetName = targetName;
                    data.targetType = targetType;
                    data.pageId = pageId;
                    data.measure_point = measurePoint;
                    data.value1 = value1;
                    data.value2 = value2;
                    data.value3 = value3;
                    data.value4 = value4;
                    data.username = username;
                    data.timestamp = timestamp;
                    data.uploaded = uploaded;

                    lstData.add(data);

                    if (!cursor.moveToNext()) break;
                }

                result.put(page, lstData);
            }

            cursor.close();
        }

        db.close();

        return result;
    }


}

package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.model.MeasurePoint;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shenyin on 2017/6/9.
 */

public class QfbController {
    private SQLiteDatabase db;

    public List<Project> getProjects() {
        List<Project> result = new ArrayList<Project>();

        QfbDbHelper dbHelper = QfbDbHelper.getInstance();
        db = dbHelper.getReadableDatabase();
        Cursor c_prj = db.query(QfbContract.ProjectEntry.TABLE_NAME, null,
                null, null, null, null, null);
        Logger.d("c_prj.getCount() = " + c_prj.getCount());
        if (c_prj.getCount() <= 0) {
            c_prj.close();
            db.close();
            return result;
        }

        c_prj.moveToFirst();
        while(true) {

            int prj_id = c_prj.getInt(c_prj.getColumnIndex(QfbContract.ProjectEntry.COLUMN_NAME_PROJID));
            String prj_name = c_prj.getString(c_prj.getColumnIndex(QfbContract.ProjectEntry.COLUMN_NAME_PROJNAME));

            Project project = new Project();
            project.project_id = prj_id;
            project.project_name = prj_name;

            List<Product> lstProduct = getProdcuts(prj_id);
            Product[] products = new Product[lstProduct.size()];
            for(int i = 0; i < lstProduct.size(); ++i) {
                products[i] = lstProduct.get(i);
            }
            project.products = products;

            result.add(project);

            if (!c_prj.moveToNext()) break;
        }

        c_prj.close();
        db.close();
        return result;
    }

    private List<Product> getProdcuts(int prj_id) {
        List<Product> result = new ArrayList<Product>();

        Cursor c_prd = db.query(QfbContract.ProductEntry.TABLE_NAME, null,
                QfbContract.ProductEntry.COLUMN_NAME_PROJID + "=?", new String[] {"" + prj_id}, null, null, null);

        Logger.d("c_prd.getCount() = " + c_prd.getCount());

        if (c_prd.getCount() <= 0) {
            c_prd.close();
            return result;
        }

        c_prd.moveToFirst();
        while(true) {

            int prd_id = c_prd.getInt(c_prd.getColumnIndex(QfbContract.ProductEntry.COLUMN_NAME_PRDID));
            String prd_name = c_prd.getString(c_prd.getColumnIndex(QfbContract.ProductEntry.COLUMN_NAME_PRDNAME));

            Product product = new Product();
            product.product_id = prd_id;
            product.product_name = prd_name;

            List<Target> lstTargets = getTargets(prd_id);
            Target[] targets = new Target[lstTargets.size()];
            for (int i = 0; i < lstTargets.size(); ++i) {
                targets[i] = lstTargets.get(i);
            }
            product.targets = targets;

            result.add(product);

            if (!c_prd.moveToNext()) break;
        }

        c_prd.close();
        return result;
    }

    private List<Target> getTargets(int prd_id) {
        List<Target> result = new ArrayList<Target>();

        Cursor cursor = db.query(QfbContract.TargetEntry.TABLE_NAME, null,
                QfbContract.TargetEntry.COLUMN_NAME_PRDID + "=?", new String[] {"" + prd_id}, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return result;
        }

        cursor.moveToFirst();
        while(true) {
            int tid = cursor.getInt(cursor.getColumnIndex(QfbContract.TargetEntry.COLUMN_NAME_TID));
            String tvt = cursor.getString(cursor.getColumnIndex(QfbContract.TargetEntry.COLUMN_NAME_TVT));
            String tname = cursor.getString(cursor.getColumnIndex(QfbContract.TargetEntry.COLUMN_NAME_TNAME));

            Target target = new Target();
            target.target_id = tid;
            target.target_name = tname;
            target.value_type = tvt;

            List<Page> lstPages = getPages(tid);
            Page[] pages = new Page[lstPages.size()];
            for (int i = 0; i < lstPages.size(); ++i) {
                pages[i] = lstPages.get(i);
            }
            target.pages = pages;

            result.add(target);

            if (!cursor.moveToNext()) break;
        }
        cursor.close();
        return result;
    }

    private List<Page> getPages(int tid) {
        List<Page> result = new ArrayList<Page>();

        Cursor cursor = db.query(QfbContract.PageEntry.TABLE_NAME, null,
                QfbContract.PageEntry.COLUMN_NAME_TID + "=?", new String[] {"" + tid}, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return result;
        }

        cursor.moveToFirst();
        while(true) {
            int pgId = cursor.getInt(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_PGID));
            String pgname = cursor.getString(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_PGNAME));
            String strPics = cursor.getString(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_PICS));

            Page page = new Page();
            page.page_id = pgId;
            page.page_name = pgname;

            if (!TextUtils.isEmpty(strPics)) {
                page.pictures = strPics.split(",");
            }

            Cursor c_m = db.query(QfbContract.MeasurePointEntry.TABLE_NAME, null,
                    QfbContract.MeasurePointEntry.COLUMN_NAME_PGID + "=?",
                    new String[]{"" + pgId}, null, null, null);
            if (c_m.getCount() <= 0) {
                page.measure_points = new MeasurePoint[0];
            } else {
                List<MeasurePoint> measurePoints = new ArrayList<MeasurePoint>();
                c_m.moveToFirst();
                while(true) {
                    String point = c_m.getString(c_m.getColumnIndex(QfbContract.MeasurePointEntry.COLUMN_NAME_POINT));
                    String direction = c_m.getString(c_m.getColumnIndex(QfbContract.MeasurePointEntry.COLUMN_NAME_DIRECTION));
                    String upperTolerance = c_m.getString(c_m.getColumnIndex(QfbContract.MeasurePointEntry.COLUMN_NAME_UPPER_TOLERANCE));
                    String lowerTolerance = c_m.getString(c_m.getColumnIndex(QfbContract.MeasurePointEntry.COLUMN_NAME_LOWER_TOLERANCE));

                    MeasurePoint measurePoint = new MeasurePoint();
                    measurePoint.point = point;
                    measurePoint.direction = direction;
                    measurePoint.upperTolerance = upperTolerance;
                    measurePoint.lowerTolerance = lowerTolerance;
                    measurePoint.page_id = pgId;

                    measurePoints.add(measurePoint);

                    if (!c_m.moveToNext()) break;
                }
                c_m.close();

                MeasurePoint[] ma = new MeasurePoint[measurePoints.size()];
                for (int i = 0 ;i < measurePoints.size(); ++i) {
                    ma[i] = measurePoints.get(i);
                }

                page.measure_points = ma;
            }

            result.add(page);

            if (!cursor.moveToNext()) break;
        }
        cursor.close();
        return result;
    }

    public List<MeasureData> GetDataByDate(int projectId, String projectName,
                                          int productId, String productName,
                                          int targetId, String targetName,
                                          int pageId, String username, Date date) {

        Logger.d("projectId = " + projectId + ", projectName = " + projectName +
        ", productId = " + productId + ", productName = " + productName +
        ", targetId = " + targetId + ", targetName = " + targetName +
        ", pageId = " + pageId + ", username = " + username);

        List<MeasureData> result = new ArrayList<MeasureData>();

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

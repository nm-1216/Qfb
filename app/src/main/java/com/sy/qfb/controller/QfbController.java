package com.sy.qfb.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sy.qfb.ble.MyApplication;
import com.sy.qfb.db.QfbContract;
import com.sy.qfb.db.QfbDbHelper;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.util.Logger;
import com.sy.qfb.util.QfbFileHelper;

import java.util.ArrayList;
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
        if (c_prj.getCount() <= 0) return result;

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

        return result;
    }

    private List<Product> getProdcuts(int prj_id) {
        List<Product> result = new ArrayList<Product>();

        Cursor c_prd = db.query(QfbContract.ProductEntry.TABLE_NAME, null,
                QfbContract.ProductEntry.COLUMN_NAME_PROJID + "=?", new String[] {"" + prj_id}, null, null, null);

        if (c_prd.getCount() <= 0) return result;

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

        return result;
    }

    private List<Target> getTargets(int prd_id) {
        List<Target> result = new ArrayList<Target>();

        Cursor cursor = db.query(QfbContract.TargetEntry.TABLE_NAME, null,
                QfbContract.TargetEntry.COLUMN_NAME_PRDID + "=?", new String[] {"" + prd_id}, null, null, null);

        if (cursor.getCount() <= 0) return result;

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
        return result;
    }

    private List<Page> getPages(int tid) {
        List<Page> result = new ArrayList<Page>();

        Cursor cursor = db.query(QfbContract.PageEntry.TABLE_NAME, null,
                QfbContract.PageEntry.COLUMN_NAME_TID + "=?", new String[] {"" + tid}, null, null, null);

        if (cursor.getCount() <= 0) return result;

        cursor.moveToFirst();
        while(true) {
            int pgId = cursor.getInt(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_PGID));
            String pgname = cursor.getString(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_PGNAME));
            String mpoints = cursor.getString(cursor.getColumnIndex(QfbContract.PageEntry.COLUMN_NAME_MPOINTS));

            Page page = new Page();
            page.page_id = pgId;
            page.page_name = pgname;
            if (TextUtils.isEmpty(mpoints)) {
                page.measure_points = new String[0];
            } else {
                page.measure_points = mpoints.split(",");
            }

            result.add(page);

            if (!cursor.moveToNext()) break;
        }
        return result;
    }

}

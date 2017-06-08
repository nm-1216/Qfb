package com.sy.qfb.db;

import android.provider.BaseColumns;

/**
 * Created by shenyin on 2017/6/9.
 */

public final class QfbContract {
    private QfbContract(){}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERID = "userid";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    public static class ProjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "project";
        public static final String COLUMN_NAME_PROJID = "projid";
        public static final String COLUMN_NAME_PROJNAME = "projname";
    }

    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME_PROJID = "projid";
        public static final String COLUMN_NAME_PRDID = "prdid";
        public static final String COLUMN_NAME_PRDNAME = "prdname";
    }

    public static class TargetEntry implements BaseColumns {
        public static final String TABLE_NAME = "target";
        public static final String COLUMN_NAME_PRDID = "prdid";
        public static final String COLUMN_NAME_TID = "tid";
        public static final String COLUMN_NAME_TNAME = "tname";
        public static final String COLUMN_NAME_TVT = "tvt";
    }

    public static class PageEntry implements BaseColumns {
        public static final String TABLE_NAME = "page";
        public static final String COLUMN_NAME_TID = "tid";
        public static final String COLUMN_NAME_PGID = "pgid";
        public static final String COLUMN_NAME_PGNAME = "pgname";
        public static final String COLUMN_NAME_MPOINTS = "mpoints";
    }

    public static class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_DATAID = "dataid";
        public static final String COLUMN_NAME_PROJID = "projid";
        public static final String COLUMN_NAME_PRDID = "prdid";
        public static final String COLUMN_NAME_TID = "tid";
        public static final String COLUMN_NAME_PGID = "pgid";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}

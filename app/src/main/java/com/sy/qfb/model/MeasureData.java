package com.sy.qfb.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * Created by jshenf on 2017/6/9.
 */
public class MeasureData extends SugarRecord {
    public int dataId;
    public int projectId;
    public String projectName;
    public int productId;
    public String productName;
    public int targetId;
    public String targetName;
    public String targetType;
    public int pageId;
    public String measure_point;
    public String direction;
    public String value1;
    public String value2;
    public String value3;
    public String value4;
    public String username;
    public long timestamp;
    public int uploaded;
}

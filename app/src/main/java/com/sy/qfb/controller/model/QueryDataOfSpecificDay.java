package com.sy.qfb.controller.model;

import java.util.Date;

public class QueryDataOfSpecificDay {
    public int projectId;
    public String projectName;
    public int productId;
    public String productName;
    public int targetId;
    public String targetName;
    public int pageId;
    public String userName;
    public Date date;

    @Override
    public String toString() {
        return "QueryDataOfSpecificDay{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", targetId=" + targetId +
                ", targetName='" + targetName + '\'' +
                ", pageId=" + pageId +
                ", userName='" + userName + '\'' +
                ", date=" + date +
                '}';
    }
}
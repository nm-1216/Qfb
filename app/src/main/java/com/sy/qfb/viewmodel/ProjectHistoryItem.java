package com.sy.qfb.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shenyin on 2017/6/19.
 */

public class ProjectHistoryItem implements Parcelable {
    public int projectId;
    public String projectName;
    public int productId;
    public String productName;
    public int targetId;
    public String targetName;
    public String targetType;
    public long timeStamp;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(projectId);
        dest.writeString(projectName);
        dest.writeInt(productId);
        dest.writeString(productName);
        dest.writeInt(targetId);
        dest.writeString(targetName);
        dest.writeString(targetType);
        dest.writeLong(timeStamp);
    }

    public static final Parcelable.Creator<ProjectHistoryItem> CREATOR =
            new Parcelable.Creator<ProjectHistoryItem>() {
                @Override
                public ProjectHistoryItem createFromParcel(Parcel source) {
                    ProjectHistoryItem result = new ProjectHistoryItem();
                    result.projectId = source.readInt();
                    result.projectName = source.readString();
                    result.projectId = source.readInt();
                    result.productName = source.readString();
                    result.targetId = source.readInt();
                    result.targetName = source.readString();
                    result.targetType = source.readString();
                    result.timeStamp = source.readLong();
                    return result;
                }

                @Override
                public ProjectHistoryItem[] newArray(int size) {
                    return new ProjectHistoryItem[size];
                }
            };
}

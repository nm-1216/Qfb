package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/14.
 */

public class HistoryActivity extends Activity {
    @BindView(R.id.lv_dates)
    ListView lvDates;

    List<Date> dates = null;
    List<Project> projects = new ArrayList<Project>();
    List<Product> products = new ArrayList<Product>();
    List<Target> targets = new ArrayList<Target>();

    DateAdapter dateAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        generateDates();
    }

    private void generateDates() {
        dates = new ArrayList<Date>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0 ;i < 7; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dates.add(calendar.getTime());
        }

        dateAdapter = new DateAdapter();
        lvDates.setAdapter(dateAdapter);
    }

    private void showData(Date date) {
        LayoutInflater layoutInflater = getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.item_measure, null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);

    }


    private class DateAdapter extends ExpandableListAdapter {
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return 4;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return dates.size();
            } else if (groupPosition == 1) {
                return projects.size();
            } else if (groupPosition == 2) {
                return products.size();
            } else if (groupPosition == 3) {
                return targets.size();

            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case 0:
                    return dates;
                case 1:
                    return projects;
                case 2:
                    return products;
                case 3:
                    return targets;
                default:
                    return null;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case 0:
                    return dates.get(childPosition);
                case 1:
                    return projects.get(childPosition);
                case 2:
                    return products.get(childPosition);
                case 3:
                    return targets.get(childPosition);
                default:
                    return null;
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 1000 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();



            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }

    }

    private class DateOnClickListener implements View.OnClickListener {
        private Date date;

        public DateOnClickListener(Date d) {
            this.date = d;
        }

        @Override
        public void onClick(View v) {
            showData(date);
        }
    }

}

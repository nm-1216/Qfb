package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.controller.HistoryController;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/14.
 */

public class HistoryActivity extends Activity {
    @BindView(R.id.lv_dates)
    ExpandableListView lvDates;

    @BindView(R.id.tl_measure)
    TableLayout tlMeasure;

    @BindView(R.id.btn_next_page)
    Button btnNextPage;

    @BindView(R.id.btn_previous_page)
    Button btnPreviousPage;

    List<Date> dates = new ArrayList<Date>();
    ArrayMap<Date, List<HistoryPoint>> historyPoints = new ArrayMap<Date, List<HistoryPoint>>();

    ArrayMap<Page, List<MeasureData>> currentPageAndData = null;
    List<MeasureData> currentData = null;
    int currentPageIndex;

    DateAdapter dateAdapter;

    HistoryController historyController = new HistoryController();

    List<View> currentRows = new ArrayList<View>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageAndData != null) {
                    Page page = currentPageAndData.keyAt(currentPageIndex + 1);
                    if (page != null) {
                        currentPageIndex++;
                        showData();
                    }
                }
            }
        });

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageAndData != null) {
                    Page page = currentPageAndData.keyAt(currentPageIndex - 1);
                    if (page != null) {
                        currentPageIndex--;
                        showData();
                    }
                }
            }
        });
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

        // 生成日期
        for (int i = 0; i < 7; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dates.add(calendar.getTime());
        }

        // 找出每天的数据
        for (int i = 0; i < dates.size(); ++i) {
            Date d = dates.get(i);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            long timestamp_min = c.getTimeInMillis();
            long timestamp_max = timestamp_min + 1000 * 60 * 60 * 24;

            List<HistoryPoint> lstHistoryPoint = new ArrayList<HistoryPoint>();

            List<Project> projects = historyController.getProjects(timestamp_min, timestamp_max);

            for (Project project : projects) {
                List<Product> products = historyController.getProducts(timestamp_min, timestamp_max, project.project_id);

                for (Product product : products) {
                    List<Target> targets = historyController.getTargets(timestamp_min, timestamp_max, product.product_id);

                    for (Target target : targets) {
                        ArrayMap<Page, List<MeasureData>> data = historyController.getPages(timestamp_min, timestamp_max, target.target_id);

                        HistoryPoint historyPoint = new HistoryPoint();
                        historyPoint.date = d;
                        historyPoint.project = project;
                        historyPoint.product = product;
                        historyPoint.target = target;
                        historyPoint.pageAndData = data;

                        lstHistoryPoint.add(historyPoint);
                    }
                }
            }

            historyPoints.put(d, lstHistoryPoint);
        }

        Logger.d("finished initialize");

        dateAdapter = new DateAdapter();
        lvDates.setAdapter(dateAdapter);
    }

    private void showData() {
        LayoutInflater layoutInflater = getLayoutInflater();
        currentData = currentPageAndData.get(currentPageAndData.keyAt(currentPageIndex));

        for (View v : currentRows) {
            currentRows.remove(v);
        }

        for (MeasureData data : currentData) {
            View view = layoutInflater.inflate(R.layout.item_measure, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
            tvName.setText(data.measure_point);

            TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
            TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
            TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
            TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

            tvData1.setText(data.value1);
            tvData2.setText(data.value2);
            tvData3.setText(data.value3);
            tvData4.setText(data.value4);

            currentRows.add(view);
            tlMeasure.addView(view);
        }
    }


    private class DateAdapter implements ExpandableListAdapter {
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return dates.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Date date = dates.get(groupPosition);
            if (historyPoints.get(date) !=  null) {
                List<HistoryPoint> lstHp = historyPoints.get(date);
                return lstHp == null ? 0 : lstHp.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return dates.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            Date date = dates.get(groupPosition);
            if (historyPoints.get(date) !=  null) {
                List<HistoryPoint> lstHp = historyPoints.get(date);
                if (lstHp != null && childPosition < lstHp.size() && childPosition >= 0) {
                    return lstHp.get(childPosition);
                }
            }
            return null;
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
            View view = layoutInflater.inflate(R.layout.item_history_group, null);

            TextView tvGroup = (TextView) view.findViewById(R.id.tv_group);
            Date date = dates.get(groupPosition);
            if (date != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                tvGroup.setText(simpleDateFormat.format(date));
            }

            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_history_child, null);

            TextView tvChild = (TextView) view.findViewById(R.id.tv_child);
            Date date = dates.get(groupPosition);
            if (historyPoints.get(date) !=  null) {

                List<HistoryPoint> lstHp = historyPoints.get(date);

                HistoryPoint hp = lstHp.get(childPosition);
                String strChild = hp.product.product_name + " - " + hp.target.target_name;
                tvChild.setText(strChild);

                view.setOnClickListener(new OnClickListener_History(hp));
            }

            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
            return groupId * 1000 + childId;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return groupId;
        }

    }

    private class OnClickListener_History implements View.OnClickListener {
        private HistoryPoint historyPoint;

        public OnClickListener_History(HistoryPoint p) {
            this.historyPoint = p;
        }

        @Override
        public void onClick(View v) {
            ArrayMap<Page, List<MeasureData>> pageAndData = historyPoint.pageAndData;
            currentPageAndData = pageAndData;
            currentPageIndex = 0;
            showData();
        }
    }

    private class HistoryPoint {
        public Date date;
        public Project project;
        public Product product;
        public Target target;
        public ArrayMap<Page, List<MeasureData>> pageAndData;
    }

}

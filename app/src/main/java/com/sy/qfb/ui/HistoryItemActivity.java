package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.HistoryController;
import com.sy.qfb.viewmodel.ProjectHistoryItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/19.
 */

public class HistoryItemActivity extends Activity {
    @BindView(R.id.tl_history_entry)
    TableLayout tlHistoryEntry;

    HistoryController historyController = new HistoryController();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_entry);

        ButterKnife.bind(this);

        List<ProjectHistoryItem> items = historyController.getProjectHistoryItems();

        for (ProjectHistoryItem item : items) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_history_entry, null);
            TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);
            TextView tvProductId = (TextView) view.findViewById(R.id.tv_product_id);
            TextView tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
            TextView tvMeasureTarget = (TextView) view.findViewById(R.id.tv_measure_target);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_date);

            tvProjectName.setText(item.projectName);
            tvProductId.setText("" + item.productId);
            tvProductName.setText(item.productName);
            tvMeasureTarget.setText(item.targetName);

            Date date = new Date(item.timeStamp);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            tvDate.setText(simpleDateFormat.format(date));

            view.setOnClickListener(new HistoryItemOnClickListener(item));

            tlHistoryEntry.addView(view);
        }


    }

    private class HistoryItemOnClickListener implements View.OnClickListener {
        private ProjectHistoryItem item;

        public HistoryItemOnClickListener(ProjectHistoryItem i) {
            this.item = i;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HistoryItemActivity.this, HistoryActivity.class);
            intent.putExtra("history_item", item);
            startActivity(intent);
        }
    }
}

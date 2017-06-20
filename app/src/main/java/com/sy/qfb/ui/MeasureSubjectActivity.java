package com.sy.qfb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.ble.activity.DeviceScanActivity;
import com.sy.qfb.model.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/4.
 */

public class MeasureSubjectActivity extends BaseActivity {
    @BindView(R.id.lv_subject)
    ListView lvSubject;

    private MeasureSubjectAdapter measureSubjectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_measure_subject);

        ButterKnife.bind(this);

        measureSubjectAdapter = new MeasureSubjectAdapter();
        lvSubject.setAdapter(measureSubjectAdapter);
    }

    private class MeasureSubjectAdapter extends BaseAdapter {
        List<Target> targets;

        public MeasureSubjectAdapter() {
            targets = new ArrayList<Target>();
            for (Target t : MainActivity.CURRENT_PRODUCT.targets) {
                targets.add(t);
            }
        }

        @Override
        public int getCount() {
            return targets == null ? 0 : targets.size();
        }

        @Override
        public Object getItem(int position) {
            return targets == null || position >= targets.size() ? 0 : targets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.measure_subject_item, null);

            TextView tvSubject = (TextView) view.findViewById(R.id.tv_subject_name);

            Target target = targets.get(position);
            Logger.d("target.target_name = " + target.target_name);
            tvSubject.setText(target.target_name);

            view.setOnClickListener(new ClickListener_Target(target));

            return view;
        }

        private class ClickListener_Target implements View.OnClickListener {
            private Target target;

            public ClickListener_Target(Target t) {
                this.target = t;
            }

            @Override
            public void onClick(View v) {
                MainActivity.CURRENT_TARGET = target;
                if ("data".equals(target.value_type)) {
                    Intent intent = new Intent(MeasureSubjectActivity.this, DeviceScanActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MeasureSubjectActivity.this, MeasureActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
}

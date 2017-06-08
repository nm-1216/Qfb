package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.sy.qfb.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/4.
 */

public class MeasureSubjectActivity extends Activity {
    @BindView(R.id.lv_subject)
    ListView lvSubject;

    private MeasureSubjectAdapter measureSubjectAdapter;
    private ArrayList<String> subjects;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_measure_subject);

        subjects = new ArrayList<String>();
        subjects.add("Door");
        subjects.add("Base");
        subjects.add("Window");

        ButterKnife.bind(this);
        measureSubjectAdapter = new MeasureSubjectAdapter();
        lvSubject.setAdapter(measureSubjectAdapter);
    }

    private class MeasureSubjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return subjects == null ? 0 : subjects.size();
        }

        @Override
        public Object getItem(int position) {
            return subjects == null || position >= subjects.size() ? 0 : subjects.get(position);
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
            tvSubject.setText(subjects.get(position));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeasureSubjectActivity.this, MeasureActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}

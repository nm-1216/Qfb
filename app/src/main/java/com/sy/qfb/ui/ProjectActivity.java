package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.sy.qfb.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/4.
 */

public class ProjectActivity extends BaseActivity {
    @BindView(R.id.lv_project)
    ListView lvProject;

    private ProjectAdapter projectAdapter;
    private ArrayList<String> projectNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        ButterKnife.bind(this);

        projectNames = new ArrayList<String>();
        projectNames.add("Door");
        projectNames.add("project 2");
        projectNames.add("project 3");

        projectAdapter = new ProjectAdapter();
        lvProject.setAdapter(projectAdapter);
    }

    private class ProjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return projectNames == null ? 0 : projectNames.size();
        }

        @Override
        public Object getItem(int position) {
            return (projectNames == null || position >= projectNames.size()) ? null : projectNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.project_item, null);

            TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);
            tvProjectName.setText(projectNames.get(position));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProjectActivity.this, MeasureSubjectActivity.class);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}

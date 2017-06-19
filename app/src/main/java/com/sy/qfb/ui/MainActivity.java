package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.QfbController;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class MainActivity extends BaseActivity {
    public static List<Project> PROJECTS = new ArrayList<Project>();
    public static Project CURRENT_PROJECT = null;
    public static Product CURRENT_PRODUCT = null;
    public static Target CURRENT_TARGET = null;
    public static Page CURRENT_PAGE = null;

    @BindView(R.id.btn_upload)
    Button btnUpload;

    @BindView(R.id.btn_download)
    Button btnDownload;

    @BindView(R.id.lv_project)
    ListView lvProject;

    @BindView(R.id.btn_history)
    Button btnHistory;

    @BindView(R.id.btn_manual_pdf)
    Button btnPdf;

    private QfbController qfbController;
    private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        qfbController = new QfbController();
        PROJECTS = qfbController.getProjects();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryItemActivity.class);
                startActivity(intent);
            }
        });

        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManualActivity.class);
                startActivity(intent);
            }
        });

        projectAdapter = new ProjectAdapter();
        lvProject.setAdapter(projectAdapter);
    }


    private class ProjectAdapter extends BaseAdapter {
        List<Object> items;

        public ProjectAdapter() {
            items = new ArrayList<Object>();
            for (Project project : PROJECTS) {
                items.add(project);
                if (project.products != null) {
                    for (Product product : project.products) {
                        items.add(product);
                    }
                }
            }
        }


        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public Object getItem(int position) {
            return (items == null || position >= items.size()) ? null : items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) instanceof Project) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.project_item, null);

            TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);

            Object o = items.get(position);
            if (o instanceof Project) {
                Project project = (Project) o;
                tvProjectName.setText(project.project_name);
            } else {
                Product product = (Product) o;
                Project project = null;
                for (Project prj : PROJECTS) {
                    if (prj.products != null) {
                        for (Product prd : prj.products) {
                            if (prd == product) {
                                project = prj;
                                break;
                            }
                        }
                    }
                }

                tvProjectName.setText("       " + product.product_name + " - " + product.product_id);
                view.setOnClickListener(new ClickListener_Product(project, product));
            }

            return view;
        }

        private class ClickListener_Product implements View.OnClickListener {
            private Project project;
            private Product product;

            public ClickListener_Product(Project prj, Product prd) {
                this.project = prj;
                this.product = prd;
            }

            @Override
            public void onClick(View v) {
                MainActivity.CURRENT_PROJECT = project;
                MainActivity.CURRENT_PRODUCT = product;
                Intent intent = new Intent(MainActivity.this, MeasureSubjectActivity.class);
                startActivity(intent);
            }
        }
    }

}

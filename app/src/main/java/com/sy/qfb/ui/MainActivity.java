package com.sy.qfb.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.controller.QfbController;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.QfbVersion;
import com.sy.qfb.model.Target;
import com.sy.qfb.view.CommonHeader;

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

    public static void setCurrentProject(int projectId, String projectName) {
        if (PROJECTS != null) {
            for (Project project : PROJECTS) {
                if (project.project_id == projectId && project.project_name != null &&
                        project.project_name.equals(projectName)) {
                    CURRENT_PROJECT = project;
                    break;
                }
            }
        }
    }

    public static void setCurrentProduct(int productId, String productName) {
        if (CURRENT_PROJECT != null && CURRENT_PROJECT.products != null) {
            for (Product product : CURRENT_PROJECT.products) {
                if (product.product_id == productId && product.product_name != null &&
                        product.product_name.equals(productName)) {
                    CURRENT_PRODUCT = product;
                    break;
                }
            }
        }
    }

    public static void setCurrentTarget(int targetId, String targetName) {
        if (CURRENT_PRODUCT != null && CURRENT_PRODUCT.targets != null) {
            for (Target target : CURRENT_PRODUCT.targets) {
                if (target.target_id == targetId && target.target_name != null &&
                        target.target_name.equals(targetName)) {
                    CURRENT_TARGET = target;
                    break;
                }
            }
        }
    }

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

    @BindView(R.id.common_header)
    CommonHeader header;

    private QfbController qfbController;
    private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

        ImageView imgRight = (ImageView) header.findViewById(R.id.img_right);
        imgRight.setVisibility(View.VISIBLE);
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ServerSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qfbController = new QfbController();
        PROJECTS = qfbController.getProjects();

//        projectAdapter.notifyDataSetChanged();
        projectAdapter = new ProjectAdapter();
        lvProject.setAdapter(projectAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (isNetworkConnected()) {
//            try {
//                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
//                String version = pInfo.versionName;
//                final DownloadController downloadController = new DownloadController();
//                showProgressDialog(true);
//                downloadController.hasNewVersion(new DownloadController.VersionCallback() {
//                    @Override
//                    public void versionCallback(boolean success, boolean hasNewVersion, final QfbVersion qfbVersion) {
//                        showProgressDialog(false);
//                        if (success) {
//                            if (hasNewVersion) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                                builder.setMessage("有最新版本：" + qfbVersion.latest_version + "，是否要下载？");
//                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        showProgressDialog(true);
//                                        downloadController.downloadNewVersion(qfbVersion, new DownloadController.DownloadNewVersionCallback() {
//                                            @Override
//                                            public void downloaded(boolean succes, String filePath) {
//                                                showProgressDialog(false);
//
//                                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                                intent.setDataAndType(Uri.parse("file://" + filePath),"application/vnd.android.package-archive");
//                                                startActivity(intent);
//                                            }
//                                        });
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setCancelable(false);
//                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.show();
//                            }
//                        }
//                    }
//                }, Double.parseDouble(version));
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

//                tvProjectName.setText("       " + product.product_name + " - " + product.product_id);
                tvProjectName.setText("       " + product.product_name);
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

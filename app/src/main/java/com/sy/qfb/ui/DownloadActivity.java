package com.sy.qfb.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sy.qfb.R;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Product;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.model.User;
import com.sy.qfb.net.FileRequest;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.service.UserService;
import com.sy.qfb.util.QfbFileHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class DownloadActivity extends BaseActivity {
    @BindView(R.id.btn_download_project)
    Button btnDownloadProject;

    @BindView(R.id.btn_download_user)
    Button btnDownloadUser;

    @BindView(R.id.btn_download_manual)
    Button btnDownloadManual;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.sv_state)
    ScrollView svState;



    private DownloadController downloadController;
    private boolean[] imgFinishedFlags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);

        downloadController = new DownloadController();

        btnDownloadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            appendStatus("下载超时，请检查网络!");
                            showProgressDialog(false);
                        }
                    }
                }, 10000);

                appendStatus("正在下载  user.json....");
                downloadController.downloadUsers(new UserService.NetworkCallback_Users() {
                    @Override
                    public void networkCallback_Users(boolean success, List<User> users) {
                        if (success) {
                            appendStatus("user.json下载成功！");
                        } else {
                            appendStatus("user.json下载失败！");
                        }
                        showProgressDialog(false);
                    }
                });
            }
        });

        btnDownloadProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            appendStatus("下载超时，请检查网络!");
                            showProgressDialog(false);
                        }
                    }
                }, 10000);

                SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
                final String userName = sharedPreferences.getString("username", "project");
                appendStatus("正在下载  " + userName + ".json....");
                downloadController.downloadProjects(new DownloadController.NetworkCallback_Projects() {
                    @Override
                    public void networkCallback_Projects(boolean success, List<Project> projects) {
                        if (success) {
                            appendStatus(userName + ".json下载成功！");
                            MainActivity.PROJECTS = projects;
                            downloadImages(MainActivity.PROJECTS);
                        } else {
                            appendStatus(userName + ".json下载失败！");
                        }
                        showProgressDialog(false);
                    }
                }, userName);
            }
        });

        btnDownloadManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            appendStatus("下载超时，请检查网络!");
                            showProgressDialog(false);
                        }
                    }
                }, 10000);

                appendStatus("正在下载  manual.pdf....");
                downloadController.downloadManual(new DownloadController.NetworkCallback_Manual() {
                    @Override
                    public void networkCallback_Manual(boolean success) {
                        if (success) {
                            appendStatus("manual.pdf下载成功！");
                        } else {
                            appendStatus("manual.pdf下载失败！");
                        }

                        showProgressDialog(false);
                    }
                });

            }
        });
    }

    private void appendStatus(String status) {
        tvStatus.append("\n" + status);
        svState.fullScroll(View.FOCUS_DOWN);
    }

    private void downloadImages(List<Project> projects) {
        progressDialog.show();

        List<String> picNames = new ArrayList<String>();
        for (Project project : projects) {
            for (Product product : project.products) {
                for (Target target : product.targets) {
                    for (Page page : target.pages) {
                        for (String picName : page.pictures) {
                            if (!picNames.contains(picName)) {
                                picNames.add(picName);
                            }
                        }
                    }
                }
            }
        }

        imgFinishedFlags = new boolean[picNames.size()];

        for (int i = 0; i < picNames.size(); ++i) {
            appendStatus("正在下载图片  " + picNames.get(i) );
            downloadController.downloadImage(picNames.get(i), new ImageCallback(i, picNames.get(i)));
        }
    }

    private class ImageCallback implements DownloadController.NetworkCallback_Image {
        private String picName;
        private int index;

        public ImageCallback(int i, String picName) {
            this.index = i;
            this.picName = picName;
        }

        @Override
        public void networkCallback_Image(boolean success) {
            if (success) {
                appendStatus("图片  " + picName + "  下载成功" );
            } else {
                appendStatus("图片  " + picName + "  下载失败" );
            }

            imgFinishedFlags[index] = true;

            boolean allFinished = true;
            for (boolean b : imgFinishedFlags) {
                if (!b) {
                    allFinished = false;
                    break;
                }
            }

            if (allFinished) {
                progressDialog.dismiss();
                appendStatus("所有图片下载完毕！" );
            }
        }
    }

}


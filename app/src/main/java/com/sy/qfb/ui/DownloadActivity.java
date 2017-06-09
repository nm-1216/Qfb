package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class DownloadActivity extends BaseActivity {
    @BindView(R.id.btn_download)
    Button btnDownload;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    private DownloadController downloadController;

    private boolean finishedDownloadUser;
    private boolean isFinishedDownloadProject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);

        downloadController = new DownloadController();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(true);
                finishedDownloadUser = false;
                isFinishedDownloadProject = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText(tvStatus.getText() + "\n下载超时，请检查网络!");
                        showProgressDialog(false);
                    }
                }, 10000);

                tvStatus.setText(tvStatus.getText() + "\n正在下载  user.json....");
                downloadController.downloadUsers(new DownloadController.NetworkCallback_Users() {
                    @Override
                    public void networkCallback_Users(List<User> users) {
                        tvStatus.setText(tvStatus.getText() + "\nuser.json下载成功！");
                        finishedDownloadUser = true;

                        if (finishedDownloadUser && isFinishedDownloadProject) {
                            showProgressDialog(false);
                        }
                    }
                });


                tvStatus.setText(tvStatus.getText() + "\n正在下载  product.json....");
                downloadController.downloadProjects(new DownloadController.NetworkCallback_Projects() {
                    @Override
                    public void networkCallback_Projects(List<Project> projects) {
                        tvStatus.setText(tvStatus.getText() + "\nproduct.json下载成功！");
                        MainActivity.PROJECTS = projects;
                        isFinishedDownloadProject = true;

                        if (finishedDownloadUser && isFinishedDownloadProject) {
                            showProgressDialog(false);
                        }
                    }
                });
            }
        });

    }


}


package com.sy.qfb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.sy.qfb.R;
import com.sy.qfb.model.Project;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shenyin on 2017/6/4.
 */

public class MainActivity extends BaseActivity {
    public static List<Project> PROJECTS = new ArrayList<Project>();


    private Button btnProject;
    private Button btnUpload;
    private Button btnDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnProject = (Button) findViewById(R.id.btn_project);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnDownload = (Button) findViewById(R.id.btn_download);

        btnProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                startActivity(intent);
            }
        });
    }
}

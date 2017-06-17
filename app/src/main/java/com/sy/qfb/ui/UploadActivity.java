package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.UploadController;
import com.sy.qfb.model.MeasureData;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class UploadActivity extends BaseActivity {
    @BindView(R.id.tv_upload_status)
    TextView tvUploadStatus;

    @BindView(R.id.btn_upload)
    Button btnUpload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        ButterKnife.bind(this);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvUploadStatus.setText(tvUploadStatus.getText() + "\n下载超时，请检查网络！");
                        showProgressDialog(false);
                    }
                }, 10000);

                UploadController uploadController = new UploadController();

                int count = uploadController.getDataSize();
                if (count <= 0) {
                    tvUploadStatus.setText(tvUploadStatus.getText() + "\n没有数据要上传！");
                } else {
                    tvUploadStatus.setText(tvUploadStatus.getText() + "\n正在上传 " + count + " 条数据！");
                    uploadController.uploadData(new UploadController.UploadFinishCallback() {
                        @Override
                        public void finish(int successCount, int failCount,
                                           HashMap<MeasureData, Integer> uploadRecoder) {
                            tvUploadStatus.setText(tvUploadStatus.getText() + "\n数据上传完毕！"
                                    + successCount + "条成功，" + failCount + "条失败！");
                            showProgressDialog(false);
                        }
                    });
                }
            }
        });

    }
}

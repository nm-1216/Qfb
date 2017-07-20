package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.sy.qfb.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/15.
 */

public class ManualActivity extends BaseActivity {

    @BindView(R.id.pdfview)
    PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ButterKnife.bind(this);

        String pdfName = "manual.pdf";

        File dir = getFilesDir();
        File pdfFile = new File(dir, pdfName);

        if (!pdfFile.exists()) {
            showAlertDialog("没有下载用户手册，请到下载页进行下载！");

        } else {

            pdfView.fromFile(pdfFile)
//        pdfView.fromAsset(pdfName)
//                .pages(0, 2, 1, 3, 3, 3)
                    .defaultPage(1)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .swipeVertical(true)
//                .onDraw(onDrawListener)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {

                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                    })
                    .load();
        }
    }
}

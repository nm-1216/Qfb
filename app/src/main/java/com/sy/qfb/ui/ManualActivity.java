package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.sy.qfb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/15.
 */

public class ManualActivity extends Activity {

    @BindView(R.id.pdfview)
    PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ButterKnife.bind(this);

        String pdfName = "manual.pdf";

        pdfView.fromAsset(pdfName)
//                .pages(0, 2, 1, 3, 3, 3)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
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

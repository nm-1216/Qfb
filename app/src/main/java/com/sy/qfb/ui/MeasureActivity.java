package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Project;
import com.sy.qfb.model.Target;
import com.sy.qfb.model.User;
import com.sy.qfb.util.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/8.
 */

public class MeasureActivity extends BaseActivity {
    @BindView(R.id.tl_measure)
    TableLayout tlTableMeasure;

    @BindView(R.id.sc_scroll)
    ScrollView scScroll;

    @BindView(R.id.btn_previous_page)
    Button btnPreviousPage;

    @BindView(R.id.btn_next_page)
    Button btnNextPage;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.tv_page_indicator)
    TextView tvPageIndicator;

    ClickLisenter_Okng clickLisenter_okng = new ClickLisenter_Okng();

    private int currentPageIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        ButterKnife.bind(this);

        currentPageIndex = 0;
        loadTable();

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loadTable() {
        Target target = MainActivity.CURRENT_TARGET;
        Page[] pages = target.pages;

        if (pages.length > currentPageIndex) {
            for (int i = 0;  i < tlTableMeasure.getChildCount(); ++i) {
                View child = tlTableMeasure.getChildAt(i);
                if (child.getId() != R.id.row1 && child.getId() != R.id.row2) {
                    tlTableMeasure.removeView(child);
                    --i;
                }
            }


            Page p = pages[currentPageIndex];
            String[] mpoints = p.measure_points;

            LayoutInflater layoutInflater = getLayoutInflater();
            for (int i = 0; i < mpoints.length; ++i) {
                View view = layoutInflater.inflate(R.layout.item_measure, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
                tvName.setText(mpoints[i]);

                TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
                TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
                TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
                TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

                tvData1.setOnClickListener(clickLisenter_okng);
                tvData2.setOnClickListener(clickLisenter_okng);
                tvData3.setOnClickListener(clickLisenter_okng);
                tvData4.setOnClickListener(clickLisenter_okng);

                tlTableMeasure.addView(view);
            }

            MainActivity.CURRENT_PAGE = p;
        }
    }

    private class ClickLisenter_Okng implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView tvText = (TextView) v;
            String strTag = (String) tvText.getTag();
            if (TextUtils.isEmpty(strTag)) {
                strTag = "NG";
            } else if (strTag.equals("NG")) {
                strTag = "OK";
            } else if (strTag.equals("OK")) {
                strTag = "NG";
            }
            tvText.setTag(strTag);
            tvText.setText(strTag);
        }
    }

}

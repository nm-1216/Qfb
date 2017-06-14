package com.sy.qfb.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.ble.activity.DeviceScanActivity;
import com.sy.qfb.controller.SaveController;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Target;
import com.sy.qfb.util.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @BindView(R.id.tv_part_pn)
    TextView tvPartPn;

    @BindView(R.id.tv_part_name)
    TextView tvPartName;

    @BindView(R.id.tv_measure_target)
    TextView tvMeasureTarget;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.btn_ng)
    Button btnNg;

    private int currentPageIndex = 0;

    private List<View> addedRows = new ArrayList<View>();

    private SaveController saveController = new SaveController();

    private TextView[][] tvArray;
    private int currentRow_TvArray = 0;
    private int currentCol_TvArray = 0;
    private TextView currentTextView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        ButterKnife.bind(this);

        tvPartPn.setText("" + MainActivity.CURRENT_PRODUCT.product_id);

        tvPartName.setText(MainActivity.CURRENT_PRODUCT.product_name);
        tvMeasureTarget.setText(MainActivity.CURRENT_TARGET.target_name);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf.format(new Date()));

        if (MainActivity.CURRENT_TARGET.value_type.equals("data")) {
            Intent intent = new Intent(MeasureActivity.this, DeviceScanActivity.class);
            startActivity(intent);
        }


        currentPageIndex = 0;
        loadTable();
        setPageIndicator();

        btnPreviousPage.setEnabled(false);
        btnNextPage.setEnabled(false);
        btnPreviousPage.setVisibility(View.INVISIBLE);
        btnNextPage.setVisibility(View.INVISIBLE);

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPageIndex--;
                if (currentPageIndex < 0) {
                    currentPageIndex = 0;
                    ToastHelper.showShort("已在第一页！");
                    return;
                }
                loadTable();
                setPageIndicator();
                btnPreviousPage.setEnabled(false);
                btnNextPage.setEnabled(false);
                btnPreviousPage.setVisibility(View.INVISIBLE);
                btnNextPage.setVisibility(View.INVISIBLE);
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPageIndex++;
                if (currentPageIndex > MainActivity.CURRENT_TARGET.pages.length - 1) {
                    currentPageIndex = MainActivity.CURRENT_TARGET.pages.length - 1;
                    ToastHelper.showShort("已在最后一页！");
                    return;
                }
                loadTable();
                setPageIndicator();
                btnPreviousPage.setEnabled(false);
                btnNextPage.setEnabled(false);
                btnPreviousPage.setVisibility(View.INVISIBLE);
                btnNextPage.setVisibility(View.INVISIBLE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

                btnPreviousPage.setEnabled(true);
                btnNextPage.setEnabled(true);
                btnPreviousPage.setVisibility(View.VISIBLE);
                btnNextPage.setVisibility(View.VISIBLE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = tvArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = tvArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("OK");
                    }
                }
                goNext();
            }
        });

        btnNg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = tvArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = tvArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("NG");
                    }
                }
                goNext();
            }
        });
    }

    private void goNext() {
        int c_rows = tvArray.length;
        int c_cols = 4;
        if (currentRow_TvArray < c_rows - 1) {
            currentRow_TvArray++;
        } else {
            if (currentCol_TvArray == 3) {
                currentCol_TvArray = 0;
            } else {
                currentCol_TvArray++;
            }
            currentRow_TvArray = 0;
        }

        if (currentTextView != null) {
            hilightTextView(currentTextView, false);
        }
        currentTextView = tvArray[currentRow_TvArray][currentCol_TvArray];
//        if (currentTextView != null) {
            hilightTextView(currentTextView, true);
//        }

        View vRow = addedRows.get(currentRow_TvArray);
//        double width = vRow.getX();
//        double height = vRow.getY();
//        Logger.d("width = " + width + ", height = " + height);
        int height = scScroll.getMeasuredHeight();
        double scrollY = scScroll.getScrollY();
        Logger.d("vRow.getY() = " + vRow.getY() + ", scScroll.getScrollY() = " + scScroll.getScrollY()  + ", height = " + height);
        if (vRow.getY() + 70 > scScroll.getScrollY() + height) {
            scScroll.scrollTo(0, (int) vRow.getY());
        }
        else if (vRow.getY() < scScroll.getScrollY()) {
            int target_y = (int) (scScroll.getScrollY() - height);
            if (target_y < 0) target_y = 0;
            scScroll.scrollTo(0, target_y);
        }
    }

    private void hilightTextView(TextView tv, boolean hilight) {
        if (hilight) {
//            TableRow.LayoutParams lp = new TableRow.LayoutParams();
//            lp.setMargins(1, 1, 1, 1);
//            currentTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.BLUE);
            tv.setTextColor(Color.WHITE);
        } else {
//                TableRow.LayoutParams lp = new TableRow.LayoutParams();
//                lp.setMargins(0, 0, 0, 0);
//                currentTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextColor(Color.BLACK);
        }
    }

    private void setPageIndicator() {
        int totalPage = MainActivity.CURRENT_TARGET.pages.length;
        String indication = String.format("页码 Page No. " + (currentPageIndex + 1) + " of " + totalPage);
        tvPageIndicator.setText(indication);
    }

    private void loadTable() {
        Target target = MainActivity.CURRENT_TARGET;
        Page[] pages = target.pages;

        if (pages.length > currentPageIndex) {
            for (int i = 0; i < tlTableMeasure.getChildCount(); ++i) {
                View child = tlTableMeasure.getChildAt(i);
                if (child.getId() != R.id.row1 && child.getId() != R.id.row2) {
                    tlTableMeasure.removeView(child);
                    --i;
                }
            }

            addedRows.clear();

            Page p = pages[currentPageIndex];
            String[] mpoints = p.measure_points;

            tvArray = new TextView[mpoints.length][4];

            LayoutInflater layoutInflater = getLayoutInflater();
            for (int i = 0; i < mpoints.length; ++i) {
                View view = layoutInflater.inflate(R.layout.item_measure, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
                tvName.setText(mpoints[i]);

                TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
                TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
                TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
                TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

                tvData1.setOnClickListener(new ClickLisenter_Okng(i, 0));
                tvData2.setOnClickListener(new ClickLisenter_Okng(i, 1));
                tvData3.setOnClickListener(new ClickLisenter_Okng(i, 2));
                tvData4.setOnClickListener(new ClickLisenter_Okng(i, 3));

                tvArray[i][0] = tvData1;
                tvArray[i][1] = tvData2;
                tvArray[i][2] = tvData3;
                tvArray[i][3] = tvData4;

                tlTableMeasure.addView(view);

                addedRows.add(view);
            }

            MainActivity.CURRENT_PAGE = p;

            currentRow_TvArray = 0;
            currentCol_TvArray = 0;
            currentTextView = tvArray[currentRow_TvArray][currentCol_TvArray];
            hilightTextView(currentTextView, true);
        }
    }


    private class ClickLisenter_Okng implements View.OnClickListener {
        private int index_row = 0;
        private int index_col = 0;

        public ClickLisenter_Okng(int r, int c) {
            this.index_row = r;
            this.index_col = c;
        }

        @Override
        public void onClick(View v) {
            MeasureActivity.this.currentRow_TvArray = index_row;
            MeasureActivity.this.currentCol_TvArray = index_col;

            currentTextView = tvArray[currentRow_TvArray][currentCol_TvArray];

        }
    }


    private void saveData() {
        List<MeasureData> lstMeasureData = new ArrayList<MeasureData>();
        for (View view : addedRows) {
            MeasureData data = new MeasureData();

            TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
            TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
            TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
            TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
            TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

            data.measure_point = tvName.getText().toString();
            data.value1 = tvData1.getText().toString();
            data.value2 = tvData2.getText().toString();
            data.value3 = tvData3.getText().toString();
            data.value4 = tvData4.getText().toString();

            data.username = LoginActivity.CURRENT_USER.username;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            data.timestamp = calendar.getTimeInMillis();

            data.projectId = MainActivity.CURRENT_PROJECT.project_id;
            data.productId = MainActivity.CURRENT_PRODUCT.product_id;
            data.targetId = MainActivity.CURRENT_TARGET.target_id;
            data.pageId = MainActivity.CURRENT_PAGE.page_id;

//            data.save();

            lstMeasureData.add(data);
        }

        saveController.saveData(lstMeasureData);

        ToastHelper.showShort("保存成功！");
    }

}

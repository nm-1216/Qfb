package com.sy.qfb.ui;

import android.app.AlertDialog;
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
import java.util.HashMap;
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

    private int currentPage_Index = 0;
    private List<View> currentPage_Rows = new ArrayList<View>();
    private TextView[][] currentPaten_TextViewArray;
    private int currentRow_TvArray = 0;
    private int currentCol_TvArray = 0;
    private TextView currentPage_ActiveTextView = null;

    private SaveController saveController = new SaveController();
    private boolean currentPageSaved = false;

    private HashMap<Integer, List<MeasureData>> page_datas = new HashMap<Integer, List<MeasureData>>();

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


        currentPage_Index = 0;
        loadTable();
        setPageIndicator();
        currentPageSaved = false;

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                gotoPreviousPage();
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                gotoNextPage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = currentPaten_TextViewArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("OK");
                    }
                }
                goNextCell();
            }
        });

        btnNg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = currentPaten_TextViewArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("NG");
                    }
                }
                goNextCell();
            }
        });
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("本页数据还没保存，请先保存再翻页，不然会丢失数据");

    }

    private void gotoNextPage() {
        currentPage_Index++;
        if (currentPage_Index > MainActivity.CURRENT_TARGET.pages.length - 1) {
            currentPage_Index = MainActivity.CURRENT_TARGET.pages.length - 1;
            ToastHelper.showShort("已在最后一页！");
            return;
        }
        loadTable();
        setPageIndicator();
        currentPageSaved = false;
    }

    private void gotoPreviousPage() {
        currentPage_Index--;
        if (currentPage_Index < 0) {
            currentPage_Index = 0;
            ToastHelper.showShort("已在第一页！");
            return;
        }
        loadTable();
        setPageIndicator();
        currentPageSaved = false;
    }

    private void goNextCell() {
        int c_rows = currentPaten_TextViewArray.length;
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

        if (currentPage_ActiveTextView != null) {
            hilightTextView(currentPage_ActiveTextView, false);
        }
        currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
//        if (currentPage_ActiveTextView != null) {
            hilightTextView(currentPage_ActiveTextView, true);
//        }

        View vRow = currentPage_Rows.get(currentRow_TvArray);
        int height = scScroll.getMeasuredHeight();
        double scrollY = scScroll.getScrollY();
        Logger.d("vRow.getY() = " + vRow.getY() + ", scScroll.getScrollY() = " + scScroll.getScrollY()  + ", height = " + height);
        if (vRow.getY() + 70 > scrollY + height) {
            scScroll.scrollTo(0, (int) vRow.getY());
        }
        else if (vRow.getY() < scrollY) {
            int target_y = (int) (scrollY - height);
            if (target_y < 0) target_y = 0;
            scScroll.scrollTo(0, target_y);
        }
    }

    private void hilightTextView(TextView tv, boolean hilight) {
        if (hilight) {
//            TableRow.LayoutParams lp = new TableRow.LayoutParams();
//            lp.setMargins(1, 1, 1, 1);
//            currentPage_ActiveTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.BLUE);
            tv.setTextColor(Color.WHITE);
        } else {
//                TableRow.LayoutParams lp = new TableRow.LayoutParams();
//                lp.setMargins(0, 0, 0, 0);
//                currentPage_ActiveTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextColor(Color.BLACK);
        }
    }

    private void setPageIndicator() {
        int totalPage = MainActivity.CURRENT_TARGET.pages.length;
        String indication = String.format("页码 Page No. " + (currentPage_Index + 1) + " of " + totalPage);
        tvPageIndicator.setText(indication);
    }

    private void loadTable() {
        Target target = MainActivity.CURRENT_TARGET;
        Page[] pages = target.pages;

        if (currentPage_Index >= 0 && currentPage_Index < pages.length) {
            // 在 UI 中去掉上一页的行
            for (int i = 0; i < tlTableMeasure.getChildCount(); ++i) {
                View child = tlTableMeasure.getChildAt(i);
                if (child.getId() != R.id.row1 && child.getId() != R.id.row2) {
                    tlTableMeasure.removeView(child);
                    --i;
                }
            }

            currentPage_Rows.clear();

            Page p = pages[currentPage_Index];
            String[] mpoints = p.measure_points;

            currentPaten_TextViewArray = new TextView[mpoints.length][4];

            boolean hasPreviousData = page_datas.containsKey(currentPage_Index);

            LayoutInflater layoutInflater = getLayoutInflater();
            for (int i = 0; i < mpoints.length; ++i) {
                View view = layoutInflater.inflate(R.layout.item_measure, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
                tvName.setText(mpoints[i]);

                TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
                TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
                TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
                TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

                if (target.value_type.equals("OK,NG")) {
                    tvData1.setOnClickListener(new ClickLisenter_Okng(i, 0));
                    tvData2.setOnClickListener(new ClickLisenter_Okng(i, 1));
                    tvData3.setOnClickListener(new ClickLisenter_Okng(i, 2));
                    tvData4.setOnClickListener(new ClickLisenter_Okng(i, 3));
                }

                if (hasPreviousData) {
                    List<MeasureData> datas = page_datas.get(currentPage_Index);
                    loadPreviousData(datas, mpoints[i], tvData1, tvData2, tvData3, tvData4);
                }

                currentPaten_TextViewArray[i][0] = tvData1;
                currentPaten_TextViewArray[i][1] = tvData2;
                currentPaten_TextViewArray[i][2] = tvData3;
                currentPaten_TextViewArray[i][3] = tvData4;

                tlTableMeasure.addView(view);

                currentPage_Rows.add(view);
            }

            MainActivity.CURRENT_PAGE = p;

            currentRow_TvArray = 0;
            currentCol_TvArray = 0;
            currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
            hilightTextView(currentPage_ActiveTextView, true);
        }
    }

    private void loadPreviousData(List<MeasureData> datas, String mpoint, TextView tvData1, TextView tvData2, TextView tvData3, TextView tvData4) {
        for (MeasureData md : datas) {
            if (mpoint.equals(md.measure_point)) {
                tvData1.setText(md.value1);
                tvData2.setText(md.value2);
                tvData3.setText(md.value3);
                tvData4.setText(md.value4);
                break;
            }
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

            currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
        }
    }


    private void saveData() {
        List<MeasureData> lstMeasureData = new ArrayList<MeasureData>();
        for (View view : currentPage_Rows) {
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

            lstMeasureData.add(data);
        }

        saveController.saveData(lstMeasureData);
        page_datas.put(currentPage_Index, lstMeasureData);

        currentPageSaved = true;
        ToastHelper.showShort("保存成功！");
    }

}

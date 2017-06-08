package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sy.qfb.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/8.
 */

public class MeasureActivity extends Activity {
    @BindView(R.id.tl_measure)
    TableLayout tlTableMeasure;

    @BindView(R.id.sc_scroll)
    ScrollView scScroll;


    ArrayList<String> measurePoints;

    ClickLisenter_Okng clickLisenter_okng = new ClickLisenter_Okng();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        measurePoints = new ArrayList<String>();
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");
        measurePoints.add("sdfs");

        ButterKnife.bind(this);

        loadTable();
    }

    private void loadTable() {
        LayoutInflater layoutInflater = getLayoutInflater();
        for (int i = 0;i < measurePoints.size(); ++i)
        {
            View view = layoutInflater.inflate(R.layout.item_measure, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
            tvName.setText(measurePoints.get(i));

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

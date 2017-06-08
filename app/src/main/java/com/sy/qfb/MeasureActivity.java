package com.sy.qfb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
            tlTableMeasure.addView(view);
        }
    }

    private class ClickLisenter_Okng implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView tv
        }
    }

}

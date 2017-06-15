package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sy.qfb.R;

import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/15.
 */

public class ManualActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ButterKnife.bind(this);
    }
}

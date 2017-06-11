package com.sy.qfb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sy.qfb.R;

import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/11.
 */

public class UserManualActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        ButterKnife.bind(this);
    }
}

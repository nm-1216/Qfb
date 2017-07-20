package com.sy.qfb.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.util.Global;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/7/20.
 */

public class ServerSettingActivity extends BaseActivity {
    @BindView(R.id.tv_server_address)
    TextView tvServerAddress;

    @BindView(R.id.et_server_address)
    EditText etServerAddress;

    @BindView(R.id.btn_ok)
    Button btnOK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);

        ButterKnife.bind(this);

        etServerAddress.setText(Global.SERVER_ADDRESS);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverAddress = etServerAddress.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("server_domain", serverAddress);
                editor.commit();

                Global.SERVER_ADDRESS = serverAddress;

                ServerSettingActivity.this.finish();
            }
        });
    }
}

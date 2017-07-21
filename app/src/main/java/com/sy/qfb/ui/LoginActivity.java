package com.sy.qfb.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.controller.LoginController;
import com.sy.qfb.model.User;
import com.sy.qfb.util.Global;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class LoginActivity extends BaseActivity {
    public static List<User> USERS = new ArrayList<User>();
    public static User CURRENT_USER = null;

    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.et_username)
    EditText etUserName;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.chk_remember_pass)
    CheckBox chkRememberPass;

    @BindView(R.id.tv_setting)
    TextView tvSetting;

    private LoginController loginController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Logger.d(new String[] {"LoginActivity onCreate()"});

        ButterKnife.bind(this);

        loginController = new LoginController();
        USERS = loginController.getUsers();

        SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "user1");
        etUserName.setText(username);
        if (sharedPreferences.getBoolean("remember_password", false)) {
            chkRememberPass.setChecked(true);
            etPassword.setText(sharedPreferences.getString("password", ""));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean authed = false;
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();
                for (User user : USERS) {
                    if (user.username.equals(u) && user.password.equals(p)) {
                        CURRENT_USER = user;
                        authed = true;
                        break;
                    }
                }

                if (authed) {
                    SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("remember_password", chkRememberPass.isChecked());
                    editor.putString("username", etUserName.getText().toString());
                    editor.putString("password", etPassword.getText().toString());
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    showAlertDialog("用户名或密码不对");
                }
            }
        });


        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ServerSettingActivity.class);
                startActivity(intent);
            }
        });


    }
}

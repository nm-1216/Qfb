package com.sy.qfb.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sy.qfb.R;
import com.sy.qfb.controller.LoginController;
import com.sy.qfb.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by shenyin on 2017/6/4.
 */

public class LoginActivity extends BaseActivity {
    public static List<User> USERS = new ArrayList<User>();

    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.et_username)
    EditText etUserName;

    @BindView(R.id.et_password)
    EditText etPassword;

    private LoginController loginController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        loginController = new LoginController();
        USERS = loginController.getUsers();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean authed = false;
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();
                for (User user : USERS) {
                    if (user.username.equals(u) && user.password.equals(p)) {
                        authed = true;
                        break;
                    }
                }

                if (authed) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    showAlertDialog("用户名或密码不对");
                }
            }
        });
    }
}

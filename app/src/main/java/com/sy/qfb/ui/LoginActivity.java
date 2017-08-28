package com.sy.qfb.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.controller.LoginController;
import com.sy.qfb.model.QfbVersion;
import com.sy.qfb.model.User;
import com.sy.qfb.util.Global;
import com.sy.qfb.util.MD5;

import java.io.File;
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


        if (Global.isNetworkOnline(this)) {
            showProgressDialog(true);
            DownloadController downloadController = new DownloadController();
            downloadController.downloadUsers(new DownloadController.NetworkCallback_Users() {
                @Override
                public void networkCallback_Users(boolean success, List<User> users) {
                    if (success) {
//                    appendStatus("user.json下载成功！");
                    } else {
//                    appendStatus("user.json下载失败！");
                    }
                    showProgressDialog(false);
                }
            });
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean authed = false;
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();
                try {
                    String pwd = MD5.md5(p);
                    Logger.d("pwd = " + pwd);
                    for (User user : USERS) {
                        if (user.username.equalsIgnoreCase(u) && user.password.equalsIgnoreCase(pwd)) {
                            CURRENT_USER = user;
                            authed = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    authed = false;
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


    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isNetworkConnected()) {
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                final DownloadController downloadController = new DownloadController();
                showProgressDialog(true);
                downloadController.hasNewVersion(new DownloadController.VersionCallback() {
                    @Override
                    public void versionCallback(boolean success, boolean hasNewVersion, final QfbVersion qfbVersion) {
                        showProgressDialog(false);
                        if (success) {
                            if (hasNewVersion) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("有最新版本：" + qfbVersion.latest_version + "，是否要下载？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog(true, "正在下载安装文件，请稍等！");
                                        downloadController.downloadNewVersion(qfbVersion, new DownloadController.DownloadNewVersionCallback() {
                                            @Override
                                            public void downloaded(boolean success, String filePath) {
                                                showProgressDialog(false);
                                                Logger.d("filePath = " + filePath);

                                                if (success && !TextUtils.isEmpty(filePath)) {
                                                    File apkFile = new File(filePath);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                                                    startActivity(intent);
                                                } else {
                                                    showAlertDialog("下载新版本失败。请以后再试。");
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                                builder.setCancelable(false);
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                }, Double.parseDouble(version));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

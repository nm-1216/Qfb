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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.controller.BaseController;
import com.sy.qfb.controller.DownloadController;
import com.sy.qfb.controller.LoginController;
import com.sy.qfb.controller.UpgradeController;
import com.sy.qfb.exception.IException;
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

    private LoginController loginController = new LoginController();
    private UpgradeController upgradeController = new UpgradeController();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Logger.d(new String[] {"LoginActivity onCreate()"});

        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "user1");
        etUserName.setText(username);
        if (sharedPreferences.getBoolean("remember_password", false)) {
            chkRememberPass.setChecked(true);
            etPassword.setText(sharedPreferences.getString("password", ""));
        }


        if (Global.isNetworkOnline(this)) {
            loginController.syncUsers(new BaseController.UpdateViewAsyncCallback<Boolean>() {
                @Override
                public void onPreExecute() {
                    showProgressDialog(true);
                }

                @Override
                public void onPostExecute(Boolean success) {
                    showProgressDialog(false);
                    if (success) {
//                    appendStatus("user.json下载成功！");
                    } else {
//                    appendStatus("user.json下载失败！");
                    }
                }

                @Override
                public void onCancelled() {
                    showProgressDialog(false);
                }

                @Override
                public void onException(IException ie) {
                    showProgressDialog(false);
                }
            });
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();

                loginController.login(new BaseController.UpdateViewAsyncCallback<User>() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog(true);
                    }

                    @Override
                    public void onPostExecute(User user) {
                        showProgressDialog(false);
                        CURRENT_USER = user;
                        if (user != null) {
                            saveUserAndGotoMainPage();
                        } else {
                            showAlertDialog("用户名或密码不对");
                        }
                    }

                    @Override
                    public void onCancelled() {
                        showProgressDialog(false);

                    }

                    @Override
                    public void onException(IException ie) {
                        showProgressDialog(false);

                    }
                }, u, p);
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

    private void saveUserAndGotoMainPage() {
        SharedPreferences sharedPreferences = getSharedPreferences("qfb", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_password", chkRememberPass.isChecked());
        editor.putString("username", etUserName.getText().toString());
        editor.putString("password", etPassword.getText().toString());
        editor.commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
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
                showProgressDialog(true);
                upgradeController.hasNewVersion(new UpgradeController.VersionCallback() {
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
                                        upgradeController.downloadNewVersion(qfbVersion, new UpgradeController.DownloadNewVersionCallback() {
                                            @Override
                                            public void downloaded(boolean success, String filePath) {
                                                showProgressDialog(false);
                                                Logger.d("filePath = " + filePath);

                                                if (success && !TextUtils.isEmpty(filePath)) {
                                                    File apkFile = new File(filePath);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        Uri contentUri = FileProvider.getUriForFile(LoginActivity.this, "com.sy.qfb.fileprovider", new File(filePath));
                                                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                                    } else {
                                                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    }
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

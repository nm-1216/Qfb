package com.sy.qfb.controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sy.qfb.model.QfbVersion;
import com.sy.qfb.net.FileRequest;
import com.sy.qfb.net.VolleyHelper;
import com.sy.qfb.util.Global;
import com.sy.qfb.util.QfbFileHelper;

public class UpgradeController extends BaseController {

    public interface VersionCallback {
        void versionCallback(boolean success, boolean hasNewVersion, QfbVersion qfbVersion);
    }

    public interface DownloadNewVersionCallback {
        void downloaded(boolean succes, String filePath);
    }

    public void hasNewVersion(final VersionCallback versionCallback, final double currentVersion) {
        String url = Global.getUrl_Version();
        Request request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean hasNewVersion = false;

                Gson gson = new Gson();
                QfbVersion qfbVersion = gson.fromJson(response, QfbVersion.class);
                hasNewVersion = qfbVersion.latest_version > currentVersion;

                versionCallback.versionCallback(true, hasNewVersion, qfbVersion);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                versionCallback.versionCallback(false, false, null);
            }
        });

        VolleyHelper.getInstance().makeRequest(request);
    }

    public void downloadNewVersion(final QfbVersion qfbVersion, final DownloadNewVersionCallback downloadCallback) {
        String url = Global.getUrl_Base() + qfbVersion.file_name;
        Request request = new FileRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                Logger.d("download apk success");
                String fileName = qfbVersion.file_name;
                QfbFileHelper qfbFileHelper = new QfbFileHelper();
                String filePath = qfbFileHelper.saveFile_Download(fileName, response);
                Logger.d("filePath = " + filePath);

                downloadCallback.downloaded(true, filePath);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d("download apk fail");
                downloadCallback.downloaded(false, "");
            }
        });

        VolleyHelper.getInstance().makeRequest(request);
    }
}
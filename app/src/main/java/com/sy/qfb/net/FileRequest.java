package com.sy.qfb.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.nio.ByteBuffer;

/**
 * Created by shenyin on 2017/6/17.
 */

public class FileRequest extends Request<byte[]>
{
    private Response.Listener<byte[]> mListener;

    public FileRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        if (response.data == null || response.data.length < 0) {
            return Response.error(new ParseError());
        }else {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }
}

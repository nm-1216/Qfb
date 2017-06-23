package com.sy.qfb.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sy.qfb.R;
import com.sy.qfb.view.PinchImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shenyin on 2017/6/23.
 */

public class ImageViewActivity extends Activity {
    @BindView(R.id.pic)
    PinchImageView pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

        ButterKnife.bind(this);

        String picName = getIntent().getStringExtra("PIC_NAME");

        File dir = getFilesDir();
        File picFile = new File(dir, picName);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("file");
        builder.path(picFile.getAbsolutePath());

        pic.setImageURI(builder.build());
    }
}

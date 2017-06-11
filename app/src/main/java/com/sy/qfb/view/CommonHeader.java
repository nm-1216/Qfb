package com.sy.qfb.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.qfb.R;

/**
 * Created by shenyin on 2017/6/11.
 */

public class CommonHeader extends FrameLayout {
    TextView tvTitle;

    ImageView imgBack;

    ImageView imgRight;


    public CommonHeader(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CommonHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CommonHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public CommonHeader(@NonNull Context context, @Nullable AttributeSet attrs,
                        @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_common_header, this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgRight = (ImageView) findViewById(R.id.img_right);

//        if (attrs == null) return;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CommonHeader, defStyleAttr, defStyleRes);

        try {
            String strTitle = typedArray.getString(R.styleable.CommonHeader_title_text);
            tvTitle.setText(strTitle);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }


    }

}

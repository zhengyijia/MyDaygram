package com.oneplus.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Oneplus on 2016/9/16.
 */
public class MyTextView1 extends TextView{
    public MyTextView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode()) {
            init();
        }
    }

    public MyTextView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init();
        }
    }

    public MyTextView1(Context context) {
        super(context);
        if(!isInEditMode()) {
            init();
        }
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"OPTIAgency-Gothic.otf");
        setTypeface(tf);
    }
}

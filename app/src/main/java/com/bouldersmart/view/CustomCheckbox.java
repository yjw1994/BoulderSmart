package com.bouldersmart.view;

/**
 * Created by COMP on 29-08-2018.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.bouldersmart.R;


public class CustomCheckbox extends CheckBox {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCheckbox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public CustomCheckbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomCheckbox(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCheckbox);
            String fontName = a.getString(R.styleable.CustomCheckbox_font_cb);

            try {
                if (fontName != null) {

                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/" + fontName);
                    setTypeface(myTypeface);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            a.recycle();
        } else {

        }
    }
}
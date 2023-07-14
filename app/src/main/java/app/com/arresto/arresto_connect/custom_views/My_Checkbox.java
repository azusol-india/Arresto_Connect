/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.custom_views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;

public class My_Checkbox extends AppCompatCheckBox {

    public My_Checkbox(Context context) {
        super(context);
    }

    public My_Checkbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextColor(Dynamic_Var.getInstance().getApp_text());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setButtonTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_text()));
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Dynamic);
        if (a.getString(R.styleable.Dynamic_lbl_text) != null && !a.getString(R.styleable.Dynamic_lbl_text).isEmpty()) {
            setText(AppUtils.getResString(a.getString(R.styleable.Dynamic_lbl_text)));
        }
        setTypeface(Font_type.getCustomFont());
    }

    public My_Checkbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setTypeface(Font_type.setCustomFont(context, attrs));
    }
}

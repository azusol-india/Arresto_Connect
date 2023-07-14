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
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;

public class My_AutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public My_AutoCompleteTextView(Context context) {
        super(context);
    }

    public My_AutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Dynamic);
        if (a.getString(R.styleable.Dynamic_lbl_text) != null && !a.getString(R.styleable.Dynamic_lbl_text).isEmpty()) {
            setHint(AppUtils.getResString(a.getString(R.styleable.Dynamic_lbl_text)));
        }
    }

    public My_AutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Dynamic);
        if (a.getString(R.styleable.Dynamic_lbl_text) != null && !a.getString(R.styleable.Dynamic_lbl_text).isEmpty()) {
            setHint(AppUtils.getResString(a.getString(R.styleable.Dynamic_lbl_text)));
        }
    }
}

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
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


public class My_TextView extends AppCompatTextView {

    public My_TextView(Context context) {
        super(context);
    }

    public My_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        new Font_type().setTextView_Color(context, this, attrs);
    }

    public My_TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}




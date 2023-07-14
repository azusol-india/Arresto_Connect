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

import androidx.appcompat.widget.AppCompatEditText;

public class My_EditText extends AppCompatEditText {

    public My_EditText(Context context) {
        super(context);
    }

    public My_EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setTypeface(Font_type.setCustomFont(context, attrs));
        new Font_type().setEdit_Color(context, this, attrs);
    }

    public My_EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setTypeface(Font_type.setCustomFont(context, attrs));
    }

}

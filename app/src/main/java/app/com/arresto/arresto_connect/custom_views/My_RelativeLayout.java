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
import android.widget.RelativeLayout;

public class My_RelativeLayout extends RelativeLayout {

    public My_RelativeLayout(Context context) {
        super(context);
    }

    public My_RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        new Font_type().setLayout_view(context, this, attrs);
    }

    public My_RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

}

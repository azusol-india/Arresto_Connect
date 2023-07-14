package app.com.arresto.arresto_connect.custom_views;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

public class MyButton extends MaterialButton {
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        new Font_type().setMaterial_button(context, this, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new Font_type().setMaterial_button(context, this, attrs);
    }
}

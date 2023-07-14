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
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class Font_type {
    public static Typeface getCustomFont() {
        Typeface tf;
        try {
//            tf = Typeface.createFromAsset(AppController.getInstance().getAssets(), "fonts/roboto.ttf");
            tf = ResourcesCompat.getFont(AppController.getInstance(),R.font.helvetica_font);
        } catch (Exception e) {
            return null;
        }
        return tf;
    }

    public void setTextView_Color(Context ctx, TextView textView, AttributeSet attrs) {
        int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent()
                , Dynamic_Var.getInstance().getApp_background()
                , Dynamic_Var.getInstance().getApp_text()
                , Dynamic_Var.getInstance().getBtn_txt_clr()
                , Dynamic_Var.getInstance().getBtn_bg_clr()};
        get_TypeArray(ctx, attrs);
        if (bg_color > -1 && !bg_type.isEmpty()) {
            set_background(textView, color_arry[bg_color], bg_type);
            if (text_color > -1)
                textView.setTextColor(color_arry[text_color]);
        }
        if (lbl_text != null && !lbl_text.isEmpty()) {
            if (isMandatory)
                textView.setText("*" + getResString(lbl_text));
            else textView.setText(getResString(lbl_text));
        }
    }

    public void setEdit_Color(Context ctx, EditText editText, AttributeSet attrs) {
        int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent()
                , Dynamic_Var.getInstance().getApp_background()
                , Dynamic_Var.getInstance().getApp_text()
                , Dynamic_Var.getInstance().getBtn_txt_clr()
                , Dynamic_Var.getInstance().getBtn_bg_clr()};
        get_TypeArray(ctx, attrs);
        if (bg_color > -1 && !bg_type.isEmpty()) {
            set_background(editText, color_arry[bg_color], bg_type);
            editText.setTextColor(color_arry[text_color]);
            editText.setHintTextColor(color_arry[text_color]);
//            if (bg_type.equals("bottom_line_bg")) {
//                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean hasFocus) {
//                        if (hasFocus) {
//                            set_background(editText, color_arry[bg_color], bg_type);
//                        } else {
//                            set_background(editText, getResColor(R.color.button_bg), bg_type);
//                        }
////                        setHintColor(editText, color_arry[bg_color]);
//                    }
//
//                });
        }
        if (lbl_text != null && !lbl_text.isEmpty()) {
            if (isMandatory)
                editText.setHint("*" + getResString(lbl_text));
            else
                editText.setHint(getResString(lbl_text));
        }


    }

    public void setLayout_view(Context ctx, View view, AttributeSet attrs) {

        int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent()
                , Dynamic_Var.getInstance().getApp_background()
                , Dynamic_Var.getInstance().getApp_text()
                , Dynamic_Var.getInstance().getBtn_txt_clr()
                , Dynamic_Var.getInstance().getBtn_bg_clr()};
        get_TypeArray(ctx, attrs);
        if (bg_color > -1 && !bg_type.isEmpty())
            set_background(view, color_arry[bg_color], bg_type);
    }

    public void setMaterial_button(Context ctx, MaterialButton view, AttributeSet attrs) {
        int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent()
                , Dynamic_Var.getInstance().getApp_background()
                , Dynamic_Var.getInstance().getApp_text()
                , Dynamic_Var.getInstance().getBtn_txt_clr()
                , Dynamic_Var.getInstance().getBtn_bg_clr()};
        get_TypeArray(ctx, attrs);
        if (text_color != -1)
            view.setTextColor(color_arry[text_color]);
        if (lbl_text != null && !lbl_text.isEmpty()) {
            view.setText(getResString(lbl_text));
        }
        if (bg_color > -1)
            ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color_arry[bg_color]));

        view.setTypeface(getCustomFont());
    }

    void setRadio_button(Context ctx, RadioButton view, AttributeSet attrs) {
        int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent()
                , Dynamic_Var.getInstance().getApp_background()
                , Dynamic_Var.getInstance().getApp_text()
                , Dynamic_Var.getInstance().getBtn_txt_clr()
                , Dynamic_Var.getInstance().getBtn_bg_clr()};
        get_TypeArray(ctx, attrs);
        if (text_color != -1)
            view.setTextColor(color_arry[text_color]);

        LayerDrawable selected = (LayerDrawable) ctx.getResources().getDrawable(R.drawable.toggle_button_selected_drawable);
        ((GradientDrawable) selected.getDrawable(0)).setStroke(2, color_arry[4]);
        ((GradientDrawable) selected.getDrawable(1)).setColor(color_arry[4]);

        GradientDrawable unselected = (GradientDrawable) ctx.getResources().getDrawable(R.drawable.toggle_button_unselected_drawable);
        unselected.setStroke(2, color_arry[2]);
        unselected.setColor(color_arry[1]);

        StateListDrawable st2 = new StateListDrawable();
        st2.addState(new int[]{android.R.attr.state_checked}, selected);
        st2.addState(new int[]{-android.R.attr.state_checked}, unselected);
        InsetDrawable insetDrawable = new InsetDrawable(st2, 10, 5, 0, 5);
        view.setButtonDrawable(insetDrawable);
        if (lbl_text != null && !lbl_text.isEmpty()) {
            view.setText(getResString(lbl_text));
        }
        view.setTypeface(getCustomFont());
    }

    public static void set_background(View view, int color, String bg_type) {
        switch (bg_type) {
            case "trans_bg":
            case "color_bg":
                view.setBackgroundColor(color);
                break;
            case "round_bg":
                GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
                gradientDrawable.setColor(color);
                view.setBackground(gradientDrawable);

//        ViewCompat.setBackgroundTintList(textView, ColorStateList.valueOf(getResColor(R.color.app_text)));
                break;
            case "bottom_line_bg":
                LayerDrawable drawable1 = (LayerDrawable) view.getBackground();
                drawable1.setLayerInset(0, -2, -2, -2, 1);
                ((GradientDrawable) drawable1.getDrawable(0)).setStroke(1, color);
                view.setBackground(drawable1);
                break;
            case "border_bg":
                GradientDrawable gradientDrawable1 = (GradientDrawable) view.getBackground();
                gradientDrawable1.setStroke(1, color);
                gradientDrawable1.setColor(Dynamic_Var.getInstance().getApp_background());
                view.setBackground(gradientDrawable1);
                break;
            case "border_stroke":
                GradientDrawable gradientDrawable2 = (GradientDrawable) view.getBackground();
                gradientDrawable2.setStroke(3, color);
                gradientDrawable2.setColor(Dynamic_Var.getInstance().getApp_background());
                view.setBackground(gradientDrawable2);
                break;
        }
    }

    private String bg_type, lbl_text, default_txt;
    private int bg_color, text_color;
    private boolean isMandatory;

    public void get_TypeArray(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Dynamic);
        bg_type = a.getString(R.styleable.Dynamic_bg_type);
        bg_color = a.getInt(R.styleable.Dynamic_bg_color, -1);
        text_color = a.getInt(R.styleable.Dynamic_text_color, -1);
        lbl_text = a.getString(R.styleable.Dynamic_lbl_text);
        isMandatory = a.getBoolean(R.styleable.Dynamic_mandatory, false);
    }

    public void setHintColor(EditText etText, int color) {
        if (etText.getParent().getParent() instanceof TextInputLayout) {
            setInputTextLayoutColor((TextInputLayout) etText.getParent().getParent(), color);
        } else
            etText.setHintTextColor(color);
    }

    public static void setInputTextLayoutColor(TextInputLayout til, @ColorInt int color) {
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{color}));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{color}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

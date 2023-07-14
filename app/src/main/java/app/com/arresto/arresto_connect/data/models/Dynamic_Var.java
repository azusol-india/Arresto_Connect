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

package app.com.arresto.arresto_connect.data.models;

import android.graphics.Color;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;

public class Dynamic_Var {
    private String Dynamic_error;
    private String Dynamic_background;
    private String Dynamic_text;
    private String btn_bg_clr;
    private String btn_txt_clr;


    public int getApp_error() {
        if (Dynamic_error != null && Dynamic_error.length()>3)
            return Color.parseColor(Dynamic_error);
        else
            return AppUtils.getResColor(R.color.app_error);
    }

    public int getApp_transparent() {
        return AppUtils.getResColor(R.color.transparent);
    }

    public void setApp_error(String app_error) {
        this.Dynamic_error = app_error;
    }

    public int getApp_background() {
        if (Dynamic_background != null && Dynamic_background.length()>3)
            return Color.parseColor(Dynamic_background);
        else
            return AppUtils.getResColor(R.color.app_background);
    }

    public void setApp_background(String app_background) {
        this.Dynamic_background = app_background;
    }

    public int getApp_text() {
        if (Dynamic_text != null && Dynamic_text.length()>3)
            return Color.parseColor(Dynamic_text);
        else
            return AppUtils.getResColor(R.color.app_text);
    }

    public void setApp_text(String app_text) {
        this.Dynamic_text = app_text;
    }

    public int getBtn_bg_clr() {
        if (btn_bg_clr != null && btn_bg_clr.length()>3)
            return Color.parseColor(btn_bg_clr);
        else
            return AppUtils.getResColor(R.color.button_bg);
    }

    public void setBtn_bg_clr(String button_txt) {
        this.btn_bg_clr = button_txt;
    }

    public int getBtn_txt_clr() {
        if (btn_txt_clr != null && btn_txt_clr.length()>3)
            return Color.parseColor(btn_txt_clr);
        else
            return AppUtils.getResColor(R.color.button_txt);
    }

    public void setBtn_txt_clr(String button_bg) {
        this.btn_txt_clr = button_bg;
    }
    // object instance

    private static final Dynamic_Var holder = new Dynamic_Var();

    public static Dynamic_Var getInstance() {
        return holder;
    }

}

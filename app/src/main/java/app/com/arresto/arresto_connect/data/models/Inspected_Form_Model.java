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

import java.text.ParseException;
import java.util.Date;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class Inspected_Form_Model {
    public String getCf_id() {
        return cf_id;
    }

    public void setCf_id(String cf_id) {
        this.cf_id = cf_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public Date get_date() {
        try {
            return BaseActivity.server_date_time.parse(created_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public String getCreated_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_time.parse(created_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return created_date;
        }
//        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    String cf_id, project_id, form_id, created_date, form_name, project_name;
}

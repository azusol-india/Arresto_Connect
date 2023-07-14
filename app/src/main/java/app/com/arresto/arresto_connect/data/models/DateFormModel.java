package app.com.arresto.arresto_connect.data.models;

import java.text.ParseException;
import java.util.ArrayList;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class DateFormModel {
    String date;
    ArrayList<CustomForm_Model> forms;

    public String getDate() {
        return date;
    }
    public String getFormatedDate() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<CustomForm_Model> getForms() {
        return forms;
    }

    public void setForms(ArrayList<CustomForm_Model> forms) {
        this.forms = forms;
    }
}

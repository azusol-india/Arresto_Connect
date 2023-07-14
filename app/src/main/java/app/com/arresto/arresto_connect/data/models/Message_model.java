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

import java.text.SimpleDateFormat;
import java.util.Date;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class Message_model {
    private String title;
    private String msg_status;
    private String time;
    private String date;
    private String message;
    private String type;
    private String timestamp;

    public Message_model(String msg, String stats, String timestamp) {
        this.message = msg;
        this.msg_status = stats;
        this.timestamp = timestamp;
        Date date = new Date();
        date.setTime(Long.parseLong(timestamp) * 1000L);
        this.date = BaseActivity.Date_Format().format(date);
        SimpleDateFormat spf = new SimpleDateFormat("hh:mm a");
        this.time = spf.format(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getTime() {
        return time;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(String msg_status) {
        this.msg_status = msg_status;
    }

}

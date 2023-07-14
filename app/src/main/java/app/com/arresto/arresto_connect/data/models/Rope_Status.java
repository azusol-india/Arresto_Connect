package app.com.arresto.arresto_connect.data.models;

import com.google.gson.annotations.SerializedName;

public class Rope_Status {
    String rt_id;
    String rt_rope_duration_manual;
    @SerializedName(value = "total_rope_duraion",alternate = {"rt_rope_duration"})
    String total_rope_duraion;
    String rt_report_no;
    String rt_rope_status;
    String rt_rope_uptime;
    String rt_rope_downtime;

    public String getRt_report_no() {
        return rt_report_no;
    }

    public void setRt_report_no(String rt_report_no) {
        this.rt_report_no = rt_report_no;
    }


    public String getTotal_rope_duraion() {
        return total_rope_duraion;
    }

    public void setTotal_rope_duraion(String total_rope_duraion) {
        this.total_rope_duraion = total_rope_duraion;
    }

    public String getRt_rope_status() {
        return rt_rope_status;
    }

    public void setRt_rope_status(String rt_rope_status) {
        this.rt_rope_status = rt_rope_status;
    }

    public String getRt_rope_uptime() {
        return rt_rope_uptime;
    }

    public void setRt_rope_uptime(String rt_rope_uptime) {
        this.rt_rope_uptime = rt_rope_uptime;
    }

    public String getRt_rope_downtime() {
        return rt_rope_downtime;
    }

    public void setRt_rope_downtime(String rt_rope_downtime) {
        this.rt_rope_downtime = rt_rope_downtime;
    }

    public String getRt_id() {
        return rt_id;
    }

    public void setRt_id(String rt_id) {
        this.rt_id = rt_id;
    }

    public String getRt_rope_duration_manual() {
        return rt_rope_duration_manual;
    }

    public void setRt_rope_duration_manual(String rt_rope_duration_manual) {
        this.rt_rope_duration_manual = rt_rope_duration_manual;
    }
}

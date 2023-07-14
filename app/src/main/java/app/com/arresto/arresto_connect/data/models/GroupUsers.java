package app.com.arresto.arresto_connect.data.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class GroupUsers {
    @SerializedName(value = "fullname", alternate = {"name","uacc_username", "username"})
    String fullname;
    String uacc_client_fk;
    String uacc_group_fk;
    String group_id;
    String cgrp_id;
    String uacc_cgroup_fk;
    String uacc_id, uacc_email, upro_phone, upro_image;
    Rope_Status rope_status;
    String attendance;
    String cgrp_type;
    String group_name;
    String group_desc;
    String asset_count;
    boolean is_Attendance = false;

    boolean already_up = false;
    boolean already_down = false;
    long timeWhenStopped = 0;

    public String getUacc_id() {
        return uacc_id;
    }

    public void setUacc_id(String uacc_id) {
        this.uacc_id = uacc_id;
    }

    public String getUacc_email() {
        return uacc_email;
    }

    public void setUacc_email(String uacc_email) {
        this.uacc_email = uacc_email;
    }

    public String getUacc_client_fk() {
        return uacc_client_fk;
    }

    public void setUacc_client_fk(String uacc_client_fk) {
        this.uacc_client_fk = uacc_client_fk;
    }

    public String getUacc_group_fk() {
        return uacc_group_fk;
    }

    public void setUacc_group_fk(String uacc_group_fk) {
        this.uacc_group_fk = uacc_group_fk;
    }

    public String getUacc_cgroup_fk() {
        return uacc_cgroup_fk;
    }

    public void setUacc_cgroup_fk(String uacc_cgroup_fk) {
        this.uacc_cgroup_fk = uacc_cgroup_fk;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getCgrp_id() {
        return cgrp_id;
    }

    public void setCgrp_id(String cgrp_id) {
        this.cgrp_id = cgrp_id;
    }

    public String getUacc_username() {
        return fullname;
    }

    public void setUacc_username(String uacc_username) {
        this.fullname = uacc_username;
    }

    public String getUpro_phone() {
        return upro_phone;
    }

    public void setUpro_phone(String upro_phone) {
        this.upro_phone = upro_phone;
    }

    public String getUpro_image() {
        return upro_image;
    }

    public void setUpro_image(String upro_image) {
        this.upro_image = upro_image;
    }

    public boolean is_Attendance() {
        return is_Attendance;
    }

    public void setIs_Attendance(boolean is_Attendance) {
        this.is_Attendance = is_Attendance;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public boolean isAlready_up() {
        return already_up;
    }

    public void setAlready_up(boolean already_up) {
        this.already_up = already_up;
    }

    public boolean isAlready_down() {
        return already_down;
    }

    public void setAlready_down(boolean already_down) {
        this.already_down = already_down;
    }

    public long getTimeWhenStopped() {
        return timeWhenStopped;
    }

    public void setTimeWhenStopped(long timeWhenStopped) {
        this.timeWhenStopped = timeWhenStopped;
    }

    public String getCgrp_type() {
        return cgrp_type;
    }

    public void setCgrp_type(String cgrp_type) {
        this.cgrp_type = cgrp_type;
    }

    public Rope_Status getRope_status() {
        return rope_status;
    }

    public void setRope_status(Rope_Status rope_status) {
        this.rope_status = rope_status;
    }

    public String getAsset_count() {
        return asset_count;
    }

    public void setAsset_count(String asset_count) {
        this.asset_count = asset_count;
    }

    @NonNull
    @Override
    public String toString() {
        if (fullname != null && !fullname.equals(""))
            return fullname;
        return uacc_email;
    }
}

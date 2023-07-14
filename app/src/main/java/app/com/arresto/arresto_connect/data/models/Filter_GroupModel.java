/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

public class Filter_GroupModel {

    public String getCgrp_id() {
        return cgrp_id;
    }

    public void setCgrp_id(String cgrp_id) {
        this.cgrp_id = cgrp_id;
    }

    public String getCgrp_pid() {
        return cgrp_pid;
    }

    public void setCgrp_pid(String cgrp_pid) {
        this.cgrp_pid = cgrp_pid;
    }

    public String getCgrp_user_id() {
        return cgrp_user_id;
    }

    public void setCgrp_user_id(String cgrp_user_id) {
        this.cgrp_user_id = cgrp_user_id;
    }

    public String getCgrp_desc() {
        return cgrp_desc;
    }

    public void setCgrp_desc(String cgrp_desc) {
        this.cgrp_desc = cgrp_desc;
    }

    public String getCgrp_type() {
        return cgrp_type;
    }

    public void setCgrp_type(String cgrp_type) {
        this.cgrp_type = cgrp_type;
    }

    public String getCgrp_admin() {
        return cgrp_admin;
    }

    public void setCgrp_admin(String cgrp_admin) {
        this.cgrp_admin = cgrp_admin;
    }

    String cgrp_id;
    String cgrp_pid;
    String cgrp_user_id;
    String cgrp_name;

    public String getCgrp_name() {
        return cgrp_name;
    }

    public void setCgrp_name(String cgrp_name) {
        this.cgrp_name = cgrp_name;
    }

    public String getCgrp_slug() {
        return cgrp_slug;
    }

    public void setCgrp_slug(String cgrp_slug) {
        this.cgrp_slug = cgrp_slug;
    }

    String cgrp_slug;
    String cgrp_desc;
    String cgrp_type;
    String cgrp_admin;

    public String getAsset_count_flag() {
        return asset_count_flag;
    }

    public void setAsset_count_flag(String asset_count_flag) {
        this.asset_count_flag = asset_count_flag;
    }

    String asset_count_flag;

}

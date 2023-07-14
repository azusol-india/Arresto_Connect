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

import java.util.ArrayList;

public class Component_history_Model {

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CheckStatus> getData() {
        return data;
    }

    public void setData(ArrayList<CheckStatus> data) {
        this.data = data;
    }

    private String user_id, name;
    private ArrayList<CheckStatus> data;

    public class CheckStatus {
        public String getPu_checkin() {
            return pu_checkin;
        }

        public void setPu_checkin(String pu_checkin) {
            this.pu_checkin = pu_checkin;
        }

        public String getPu_checkout() {
            return pu_checkout;
        }

        public void setPu_checkout(String pu_checkout) {
            this.pu_checkout = pu_checkout;
        }

        public String getPu_id() {
            return pu_id;
        }

        public void setPu_id(String pu_id) {
            this.pu_id = pu_id;
        }

        public String getPu_product_id() {
            return pu_product_id;
        }

        public void setPu_product_id(String pu_product_id) {
            this.pu_product_id = pu_product_id;
        }

        public String getPu_logtime() {
            return pu_logtime;
        }

        public void setPu_logtime(String pu_logtime) {
            this.pu_logtime = pu_logtime;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUin() {
            return uin;
        }

        public void setUin(String uin) {
            this.uin = uin;
        }

        String pu_id,pu_product_id,pu_logtime,date,pu_checkin, pu_checkout,uin;
    }
}

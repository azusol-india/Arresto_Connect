package app.com.arresto.arresto_connect.data.models;

public class SmsModel {
    String id, client_fk, jc_number, sms_number, series, item_code, item_quantity, no_of_lines, status, created_date, created_date1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient_fk() {
        return client_fk;
    }

    public void setClient_fk(String client_fk) {
        this.client_fk = client_fk;
    }

    public String getJc_number() {
        return jc_number;
    }

    public void setJc_number(String jc_number) {
        this.jc_number = jc_number;
    }

    public String getSms_number() {
        return sms_number;
    }

    public void setSms_number(String sms_number) {
        this.sms_number = sms_number;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(String item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getNo_of_lines() {
        return no_of_lines;
    }

    public void setNo_of_lines(String no_of_lines) {
        this.no_of_lines = no_of_lines;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCreated_date1() {
        return created_date1;
    }

    public void setCreated_date1(String created_date1) {
        this.created_date1 = created_date1;
    }
}

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


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Dashbrd_Search{
    @SerializedName("sno")
    private String sno;
    @SerializedName("client_name")
    private String client_name;
    @SerializedName("asset")
    private String asset;
    @SerializedName("asset_series")
    private String asset_series;
    @SerializedName("report_no")
    private String report_no;
    @SerializedName("site_id")
    private String site_id;
    @SerializedName("job_card")
    private String job_card;
    @SerializedName("sms")
    private String sms;
    @SerializedName("status")
    private String status;
    @SerializedName("time")
    private String time;


//    public Dashbrd_Search(String sno, String client_name, String asset, String asset_series, String report_no, String site_id,
//                          String job_card, String sms, String status, String time) {
//        this.sno = sno;
//        this.client_name =client_name ;
//        this.asset = asset;
//        this.asset_series = asset_series;
//        this.report_no =report_no ;
//        this.site_id =site_id ;
//        this.job_card =job_card ;
//        this.sms = sms;
//        this.status=status;
//        this.time=time;
//    }

    public String getSno(){
        return sno;
    }

    public String getClient_name(){
        return client_name;
    }

    public String getAsset(){
        return asset;
    }

    public String getAsset_series(){
        return asset_series;
    }

    public String getReport_no(){
        return report_no;
    }

    public String getSite_id(){
        return site_id;
    }

    public String getJob_card(){
        return job_card;
    }

    public String getSms(){
        return sms;
    }

    public String getStatus(){
        return status;
    }

    public String getTime(){
        return time;
    }

    public ArrayList<String> getALL(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(getClient_name());
        strings.add(getAsset());
        strings.add(getAsset_series());
        strings.add(getReport_no());
        strings.add(getSite_id());
        strings.add(getJob_card());
        strings.add(getSms());
        strings.add(getStatus());
        strings.add(getTime());
        return strings;
    }

}
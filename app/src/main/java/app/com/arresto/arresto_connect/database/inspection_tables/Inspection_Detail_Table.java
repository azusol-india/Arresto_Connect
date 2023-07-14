package app.com.arresto.arresto_connect.database.inspection_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Entity(indices = {@Index(value = "unique_id", unique = true)})
public class Inspection_Detail_Table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "reportNo")
    private String reportNo;
    @ColumnInfo(name = "siteID")
    private String siteID;
    @ColumnInfo(name = "siteName")
    private String siteName;
    @ColumnInfo(name = "jobCard")
    private String jobCard;
    @ColumnInfo(name = "sms")
    private String sms;
    @ColumnInfo(name = "todayDate")
    private String todayDate;
    @ColumnInfo(name = "assetSeries")
    private String assetSeries;
    @ColumnInfo(name = "poNumber")
    private String poNumber;
    @ColumnInfo(name = "batchNumber")
    private String batchNumber;
    @ColumnInfo(name = "serialNumber")
    private String serialNumber;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "lattitude")
    private String lattitude;
    @ColumnInfo(name = "longitude")
    private String longitude;
    @ColumnInfo(name = "userID")
    private String userID;
    @ColumnInfo(name = "inputType")
    private String inputType = "";
    @ColumnInfo(name = "inputValue")
    private String inputValue;
    @ColumnInfo(name = "master_id")
    private String master_id;

    public void set_inspection_details(String user_id, String unique_id, String s, String s1, String s2, String s3, String s4, String s5,
                                       String s6, String s7, String s8, String s9,String address, String s10, String s11, String s12, String s13,
                                       String s14, String s15) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.reportNo = s;
        this.siteID = s1;
        this.siteName = s2;
        this.jobCard = s3;
        this.sms = s4;
        this.todayDate = s5;
        this.assetSeries = s6;
        this.poNumber = s7;
        this.batchNumber = s8;
        this.serialNumber = s9;
        this.address = address;
        this.lattitude = s10;
        this.longitude = s11;
        this.userID = s12;
        this.inputType = s13;
        this.inputValue = s14;
        this.master_id = s15;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getJobCard() {
        return jobCard;
    }

    public void setJobCard(String jobCard) {
        this.jobCard = jobCard;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public String getAssetSeries() {
        return assetSeries;
    }

    public void setAssetSeries(String assetSeries) {
        this.assetSeries = assetSeries;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public void setAsset_series(String asset) {
        this.assetSeries = asset;
    }
    @Dao
    public interface Inspection_Detail_Dao {
        @Query("SELECT * FROM inspection_detail_table")
        List<Inspection_Detail_Table> getAll();

        @Query("SELECT * FROM inspection_detail_table WHERE user_id =:user_id  AND unique_id =:unique_id")
        Inspection_Detail_Table getInspection_Detail(String user_id, String unique_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(Inspection_Detail_Table detail_table);

        @Update
        void updateInspection_Detail(Inspection_Detail_Table detail_table);

        @Query("DELETE FROM inspection_detail_table WHERE user_id =:user AND unique_id =:unique_id")
        void deleteInspection_Detail(String user, String unique_id);

    }

}

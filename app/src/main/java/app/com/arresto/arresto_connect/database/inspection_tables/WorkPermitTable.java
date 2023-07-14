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
public class WorkPermitTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "client_id")
    private String client_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "client_name")
    private String client_name;
    @ColumnInfo(name = "today_date")
    private String today_date;
    @ColumnInfo(name = "workPermit_number")
    private String workPermit_number;
    @ColumnInfo(name = "client_workpermit")
    private String client_workpermit;
    @ColumnInfo(name = "siteID_name")
    private String siteID_name;
    @ColumnInfo(name = "permitDate_from")
    private String permitDate_from;
    @ColumnInfo(name = "permitValid_upto")
    private String permitValid_upto;
    @ColumnInfo(name = "siteID_address")
    private String siteID_address;
    @ColumnInfo(name = "workPermit_lattitude")
    private String workPermit_lattitude;
    @ColumnInfo(name = "workPermit_longitude")
    private String workPermit_longitude;
    @ColumnInfo(name = "workPermit_asset_series")
    private String workPermit_asset_series;
    @ColumnInfo(name = "workPermit_batch_no")
    private String workPermit_batch_no;
    @ColumnInfo(name = "workPermit_serial_no")
    private String workPermit_serial_no;
    @ColumnInfo(name = "harness")
    private String harness;
    @ColumnInfo(name = "worker_height")
    private String worker_height;
    @ColumnInfo(name = "weather_good")
    private String weather_good;
    @ColumnInfo(name = "equipment_calibrated")
    private String equipment_calibrated;
    @ColumnInfo(name = "physically_fitness")
    private String physically_fitness;
    @ColumnInfo(name = "work_at_height")
    private String work_at_height;
    @ColumnInfo(name = "alcohol_influence")
    private String alcohol_influence = "";
    @ColumnInfo(name = "client_approval")
    private String client_approval;
    @ColumnInfo(name = "image_path")
    private String image_path;
    @ColumnInfo(name = "inspectionDate")
    private String inspectionDate;

    public void set_workPermit(String user_id, String client_id, String unique_id, String client_name, String curr_date, String workpermit_number,String client_workPermit,
                               String sub_site_id, String s, String toString, String site, String lat, String longi, String asset, String string,
                               String s1, String slctd_harnss, String slctd_traning1, String slctd_situation1, String slctd_equipment1,
                               String slctd_medical1, String slctd_medical2, String slctd_misclncs1, String image_path, String inspectionDate) {
        this.user_id = user_id;
        this.client_id = client_id;
        this.unique_id = unique_id;
        this.client_name = client_name;
        this.today_date = curr_date;
        this.workPermit_number = workpermit_number;
        this.client_workpermit = client_workPermit;
        this.siteID_name = sub_site_id;
        this.permitDate_from = s;
        this.permitValid_upto = toString;
        this.siteID_address = site;
        this.workPermit_lattitude = lat;
        this.workPermit_longitude = longi;
        this.workPermit_asset_series = asset;
        this.workPermit_batch_no = string;
        this.workPermit_serial_no = s1;
        this.harness = slctd_harnss;
        this.worker_height = slctd_traning1;
        this.weather_good = slctd_situation1;
        this.equipment_calibrated = slctd_equipment1;
        this.physically_fitness = slctd_medical1;
        this.alcohol_influence = slctd_medical2;
        this.client_approval = slctd_misclncs1;
        this.image_path = image_path;
        this.inspectionDate = inspectionDate;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setAsset_series(String asset) {
        this.workPermit_asset_series = asset;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getToday_date() {
        return today_date;
    }

    public void setToday_date(String today_date) {
        this.today_date = today_date;
    }

    public String getWorkPermit_number() {
        return workPermit_number;
    }

    public void setWorkPermit_number(String workPermit_number) {
        this.workPermit_number = workPermit_number;
    }

    public String getClient_workpermit() {
        return client_workpermit;
    }

    public void setClient_workpermit(String client_workpermit) {
        this.client_workpermit = client_workpermit;
    }

    public String getSiteID_name() {
        return siteID_name;
    }

    public void setSiteID_name(String siteID_name) {
        this.siteID_name = siteID_name;
    }

    public String getPermitDate_from() {
        return permitDate_from;
    }

    public void setPermitDate_from(String permitDate_from) {
        this.permitDate_from = permitDate_from;
    }

    public String getPermitValid_upto() {
        return permitValid_upto;
    }

    public void setPermitValid_upto(String permitValid_upto) {
        this.permitValid_upto = permitValid_upto;
    }

    public String getSiteID_address() {
        return siteID_address;
    }

    public void setSiteID_address(String siteID_address) {
        this.siteID_address = siteID_address;
    }

    public String getWorkPermit_lattitude() {
        return workPermit_lattitude;
    }

    public void setWorkPermit_lattitude(String workPermit_lattitude) {
        this.workPermit_lattitude = workPermit_lattitude;
    }

    public String getWorkPermit_longitude() {
        return workPermit_longitude;
    }

    public void setWorkPermit_longitude(String workPermit_longitude) {
        this.workPermit_longitude = workPermit_longitude;
    }

    public String getWorkPermit_asset_series() {
        return workPermit_asset_series;
    }

    public void setWorkPermit_asset_series(String workPermit_asset_series) {
        this.workPermit_asset_series = workPermit_asset_series;
    }

    public String getWorkPermit_batch_no() {
        return workPermit_batch_no;
    }

    public void setWorkPermit_batch_no(String workPermit_batch_no) {
        this.workPermit_batch_no = workPermit_batch_no;
    }

    public String getWorkPermit_serial_no() {
        return workPermit_serial_no;
    }

    public void setWorkPermit_serial_no(String workPermit_serial_no) {
        this.workPermit_serial_no = workPermit_serial_no;
    }

    public String getHarness() {
        return harness;
    }

    public void setHarness(String harness) {
        this.harness = harness;
    }

    public String getWorker_height() {
        return worker_height;
    }

    public void setWorker_height(String worker_height) {
        this.worker_height = worker_height;
    }

    public String getWeather_good() {
        return weather_good;
    }

    public void setWeather_good(String weather_good) {
        this.weather_good = weather_good;
    }

    public String getEquipment_calibrated() {
        return equipment_calibrated;
    }

    public void setEquipment_calibrated(String equipment_calibrated) {
        this.equipment_calibrated = equipment_calibrated;
    }

    public String getPhysically_fitness() {
        return physically_fitness;
    }

    public void setPhysically_fitness(String physically_fitness) {
        this.physically_fitness = physically_fitness;
    }

    public String getWork_at_height() {
        return work_at_height;
    }

    public void setWork_at_height(String work_at_height) {
        this.work_at_height = work_at_height;
    }

    public String getAlcohol_influence() {
        return alcohol_influence;
    }

    public void setAlcohol_influence(String alcohol_influence) {
        this.alcohol_influence = alcohol_influence;
    }

    public String getClient_approval() {
        return client_approval;
    }

    public void setClient_approval(String client_approval) {
        this.client_approval = client_approval;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    @Dao
    public interface WorkPermit_Dao {
        @Query("SELECT * FROM workpermittable")
        List<WorkPermitTable> getAll();

        @Query("SELECT * FROM workpermittable WHERE user_id =:user_id AND client_id =:client AND unique_id =:unique_id")
        WorkPermitTable getWorkPermit_data(String user_id, String client, String unique_id);

//        @Query("SELECT unique_id FROM workpermittable WHERE client_id =:client_id")
//        List<WorkPermitTable> getAllWorkPermit(String client_id);

        @Update
        void updateWorkPermit(WorkPermitTable permitTable);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(WorkPermitTable workPermitTable);

        @Query("DELETE FROM workpermittable WHERE user_id =:user_id AND  client_id =:client AND unique_id =:unique_id")
        void deleteWorkPermit_data(String user_id, String client, String unique_id);

    }

}

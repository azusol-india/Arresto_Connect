/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

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

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/29/2019.
 */

@Entity(indices = {@Index(value = "unique_id", unique = true)})
public class Sites_data_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unique_id")
    private String unique_id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "site_data")
    private String site_data;

    @ColumnInfo(name = "uploaded")
    private String isUploaded;

    @ColumnInfo(name = "inspectionTime")
    private long inspectionTime;

    @ColumnInfo(name = "isInspection")
    private int isInspection;

    public void setIsInspection(int isInspection) {
        this.isInspection = isInspection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //For PDM
    public void setData(String user_id, String client_id, String unique_id, String site_data) {
        this.user_id = user_id;
        this.client_id = client_id;
        this.unique_id = unique_id;
        this.site_data = site_data;
        this.isInspection = 0;
    }

    //For Inspection
    public void setInspectionSiteData(String user_id, String client_id, String unique_id, String site_data, long inspectionTime) {
        this.user_id = user_id;
        this.client_id = client_id;
        this.unique_id = unique_id;
        this.site_data = site_data;
        this.isInspection = 1;
        this.inspectionTime = inspectionTime;
    }


    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSite_data() {
        return site_data;
    }

    public void setSite_data(String site_data) {
        this.site_data = site_data;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        this.isUploaded = isUploaded;
    }

    public int getIsInspection() {
        return isInspection;
    }

    public long getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(long inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    @Dao
    public interface Sites_data_Dao {
        @Query("SELECT site_data FROM sites_data_table WHERE  user_id=:user_id AND isInspection =1")
        List<String> getInspectedSites_data(String user_id);

        @Query("SELECT site_data FROM sites_data_table WHERE  user_id=:user_id AND isInspection =0")
        List<String> getPdmSites_data(String user_id);

        @Query("SELECT site_data FROM sites_data_table WHERE client_id =:client")
        List<String> getSites_data(String client);

        @Query("SELECT * FROM sites_data_table WHERE user_id =:user_id AND client_id =:client AND isInspection =1")
        List<Sites_data_table> getAllInspectedSites(String user_id, String client);

        @Query("SELECT * FROM sites_data_table WHERE user_id =:user_id AND unique_id =:unique_id")
        Sites_data_table getSingleSites(String user_id, String unique_id);

        @Query("SELECT unique_id FROM sites_data_table WHERE user_id =:user_id AND client_id =:client")
        List<String> getAllSitesId(String user_id, String client);

        @Update
        void updateSite(Sites_data_table sitesDataTable);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Sites_data_table sites_data_table);

        @Query("DELETE FROM sites_data_table WHERE unique_id = :unique_id AND user_id=:user_id")
        void deleteSites_data(String unique_id, String user_id);

    }
}

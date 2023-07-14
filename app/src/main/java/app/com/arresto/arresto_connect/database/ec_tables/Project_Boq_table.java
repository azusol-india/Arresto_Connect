package app.com.arresto.arresto_connect.database.ec_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 9/13/2019.
 */
@Entity(tableName = "project_BOQ_table")
public class Project_Boq_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "project_id")
    private String project_id;

    @ColumnInfo(name = "boq_id")
    private String boq_id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "cat_id")
    private String cat_id;

    @ColumnInfo(name = "remark")
    private String remark;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "subsite_image")
    private String subsite_image;

    @ColumnInfo(name = "site_id")
    private String site_id;

    @ColumnInfo(name = "revision")
    private int revision=0;

    @ColumnInfo(name = "geolocation")
    private String geolocation;

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getSubsite_image() {
        return subsite_image;
    }

    public void setSubsite_image(String subsite_image) {
        this.subsite_image = subsite_image;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getBoq_id() {
        return boq_id;
    }

    public void setBoq_id(String boq_id) {
        this.boq_id = boq_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    @Dao
    public interface Project_Boq_Dao {
        @Query("SELECT * FROM project_BOQ_table")
        List<Project_Boq_table> getAll();

        @Query("SELECT * FROM project_BOQ_table WHERE user_id =:key AND project_id=:project_id AND site_id=:site_id")
        List<Project_Boq_table> getBOQ(String key, String project_id, String site_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Project_Boq_table ec_projectTable);

        @Update
        void updateBOQ(Project_Boq_table ec_projectTable);

        @Query("SELECT * FROM project_BOQ_table WHERE user_id = :key AND project_id=:project_id AND site_id=:site_id AND boq_id =:boq_id LIMIT 1")
        Project_Boq_table getSingle_boq(String key, String project_id, String site_id, String boq_id);

        @Query("DELETE FROM project_BOQ_table WHERE user_id = :user_id AND project_id=:project_id AND site_id=:site_id AND boq_id =:boq_id")
        void deleteBOQ(String user_id, String project_id, String site_id, String boq_id);

        @Query("SELECT boq_id FROM project_BOQ_table WHERE user_id = :key AND project_id=:project_id AND site_id=:site_id AND boq_id =:boq_id LIMIT 1")
        boolean isBOQExist(String key, String project_id, String site_id, String boq_id);
    }
}

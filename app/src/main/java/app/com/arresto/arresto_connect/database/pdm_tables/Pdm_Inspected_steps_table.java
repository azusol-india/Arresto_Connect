/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.database.pdm_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/29/2019.
 */
@Entity(tableName = "pdm_inspected_steps_table")
public class Pdm_Inspected_steps_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unique_id")
    private String unique_id;

    @ColumnInfo(name = "asset_id")
    private String asset_id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "step_id")
    private String step_id;

    @ColumnInfo(name = "step_position")
    private int step_position;

    @ColumnInfo(name = "observation")
    private String observation;

    @ColumnInfo(name = "action_proposed")
    private String action_proposed;

    @ColumnInfo(name = "action_taken")
    private String action_taken;

    @ColumnInfo(name = "before_images")
    private String before_images;

    @ColumnInfo(name = "after_images")
    private String after_images;

    @ColumnInfo(name = "skip_flag")
    private String skip_flag;

    @ColumnInfo(name = "skip_remark")
    private String skip_remark;

    @ColumnInfo(name = "master_id")
    private String master_id;

    @ColumnInfo(name = "start_time")
    private long start_time;


    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public int getStep_position() {
        return step_position;
    }

    public void setStep_position(int step_position) {
        this.step_position = step_position;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getAction_proposed() {
        return action_proposed;
    }

    public void setAction_proposed(String action_proposed) {
        this.action_proposed = action_proposed;
    }

    public String getAction_taken() {
        return action_taken;
    }

    public void setAction_taken(String action_taken) {
        this.action_taken = action_taken;
    }


    public String getBefore_images() {
        return before_images;
    }

    public void setBefore_images(String before_images) {
        this.before_images = before_images;
    }

    public String getAfter_images() {
        return after_images;
    }

    public void setAfter_images(String after_images) {
        this.after_images = after_images;
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

    public String getStep_id() {
        return step_id;
    }

    public void setStep_id(String step_id) {
        this.step_id = step_id;
    }

    public String getSkip_flag() {
        return skip_flag;
    }

    public void setSkip_flag(String skip_flag) {
        this.skip_flag = skip_flag;
    }

    public String getSkip_remark() {
        return skip_remark;
    }

    public void setSkip_remark(String skip_remark) {
        this.skip_remark = skip_remark;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    @Dao
    public interface Pdm_stepsDao {
        @Query("SELECT * FROM pdm_inspected_steps_table")
        List<Pdm_Inspected_steps_table> getAll();

        @Query("SELECT * FROM pdm_inspected_steps_table WHERE unique_id =:key")
        List<Pdm_Inspected_steps_table> getInspectedSteps(String key);

        @Query("SELECT step_position FROM pdm_inspected_steps_table WHERE unique_id =:key")
        List<Integer> getInspectedPos(String key);

        @Insert
        long insert(Pdm_Inspected_steps_table pdm_steps_table);

        @Query("DELETE FROM pdm_inspected_steps_table WHERE unique_id = :key")
        void deletePdm_step(String key);

    }
}

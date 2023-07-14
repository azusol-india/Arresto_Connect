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
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/29/2019.
 */

@Entity(indices = {@Index(value = "asset_id", unique = true)})
public class Asset_steps_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "asset_id")
    private String asset_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "steps_data")
    private String steps_data;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSteps_data() {
        return steps_data;
    }

    public void setSteps_data(String steps_data) {
        this.steps_data = steps_data;
    }

    @Dao
    public interface Asset_stepsDao {
        @Query("SELECT * FROM asset_steps_table")
        List<Asset_steps_table> getAll();

        @Query("SELECT steps_data FROM asset_steps_table WHERE asset_id =:key AND client_id =:client")
        String getAsset_steps(String key, String client);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Asset_steps_table pdm_steps_table);

        @Query("DELETE FROM asset_steps_table WHERE asset_id = :key AND client_id =:client")
        void deleteAsset_steps(String key, String client);

    }
}

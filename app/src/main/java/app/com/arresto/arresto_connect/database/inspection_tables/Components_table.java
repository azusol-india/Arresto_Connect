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

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/29/2019.
 */

@Entity(indices = {@Index(value = "asset_id", unique = true)})
public class Components_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "asset_id")
    private String asset_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "component_data")
    private String component_data;

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getComponent_data() {
        return component_data;
    }

    public void setComponent_data(String component_data) {
        this.component_data = component_data;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setData(String client_id, String asset_id, String component_data) {
        this.client_id = client_id;
        this.asset_id = asset_id;
        this.component_data = component_data;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }


    @Dao
    public interface Components_Dao {
        @Query("SELECT * FROM components_table")
        List<Components_table> getAll();

        @Query("SELECT component_data FROM components_table WHERE asset_id = :key AND client_id =:client")
        String getComponents(String key, String client);

        @Query("SELECT asset_id FROM components_table WHERE client_id =:client")
        List<String> getAllComponents(String client);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Components_table sites_data_table);

        @Query("DELETE FROM components_table WHERE asset_id = :key AND client_id =:client")
        void deleteComponents(String key, String client);

    }
}

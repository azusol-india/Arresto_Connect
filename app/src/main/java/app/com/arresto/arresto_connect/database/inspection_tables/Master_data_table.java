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

@Entity(indices={@Index(value="master_id", unique=true)})
public class Master_data_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "master_id")
    private String master_id;

    @ColumnInfo(name = "client_id")
    private String client_id;


    @ColumnInfo(name = "master_data")
    private String master_data;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setData(String client_id,String master_id,String master_data) {
        this.client_id = client_id;
        this.master_id = master_id;
        this.master_data = master_data;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getMaster_data() {
        return master_data;
    }

    public void setMaster_data(String master_data) {
        this.master_data = master_data;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }


    @Dao
    public interface Master_data_Dao {
        @Query("SELECT * FROM master_data_table")
        List<Master_data_table> getAll();

        @Query("SELECT master_data FROM master_data_table WHERE master_id =:key AND client_id =:client")
        String getMaster_data(String key, String client);

        @Query("SELECT master_id FROM master_data_table WHERE master_id =:key AND client_id =:client")
        String checkEntry(String key, String client);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(Master_data_table master_data_table);

        @Query("DELETE FROM master_data_table WHERE master_id = :key AND client_id =:client")
        void deleteMaster_data(String key, String client);

    }
}

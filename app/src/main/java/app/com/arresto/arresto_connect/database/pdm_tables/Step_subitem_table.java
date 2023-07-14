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

@Entity(indices = {@Index(value = "unique_id", unique = true)})
public class Step_subitem_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unique_id")
    private String unique_id;

    @ColumnInfo(name = "client_id")
    private String client_id;


    @ColumnInfo(name = "subitem_data")
    private String subitem_data;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setData(String client_id, String unique_id, String subitem_data) {
        this.client_id = client_id;
        this.unique_id = unique_id;
        this.subitem_data = subitem_data;
    }


    public String getSubitem_data() {
        return subitem_data;
    }

    public void setSubitem_data(String subitem_data) {
        this.subitem_data = subitem_data;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }


    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }


    @Dao
    public interface Step_subitem_Dao {
        @Query("SELECT * FROM step_subitem_table")
        List<Step_subitem_table> getAll();

        @Query("SELECT subitem_data FROM step_subitem_table WHERE unique_id = :key AND client_id =:client")
        String getStep_subitemData(String key, String client);

        @Query("SELECT unique_id FROM step_subitem_table WHERE client_id =:client")
        List<String> getAllStep_subitemData(String client);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Step_subitem_table stepSubitemTable);

        @Query("DELETE FROM step_subitem_table WHERE unique_id = :key AND client_id =:client")
        void deleteStep_subitem(String key, String client);

    }
}

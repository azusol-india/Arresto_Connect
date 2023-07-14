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
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 5/10/2019.
 */


@Entity(tableName = "signature_table")
public class Signature_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unique_id")
    private String unique_id;

    @ColumnInfo(name = "signature_type") // may be inspection or pdm
    private String signature_type;

    @ColumnInfo(name = "client_id")
    private String client_id;


    @ColumnInfo(name = "signature_of")
    private String signature_of;

    @ColumnInfo(name = "signature_data")
    private String signature_data;

    @ColumnInfo(name = "end_time")
    private long end_time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setData(String client_id, String unique_id, String signature_type, String signature_of, String signature_data) {
        this.client_id = client_id;
        this.unique_id = unique_id;
        this.signature_type = signature_type;
        this.signature_of = signature_of;
        this.signature_data = signature_data;
    }


    public String getSignature_of() {
        return signature_of;
    }

    public void setSignature_of(String signature_of) {
        this.signature_of = signature_of;
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

    public String getSignature_type() {
        return signature_type;
    }

    public void setSignature_type(String signature_type) {
        this.signature_type = signature_type;
    }

    public String getSignature_data() {
        return signature_data;
    }

    public void setSignature_data(String signature_data) {
        this.signature_data = signature_data;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    @Dao
    public interface Signature_Dao {
        @Query("SELECT * FROM signature_table")
        List<Signature_table> getAll();

        @Query("SELECT * FROM signature_table WHERE client_id =:client AND unique_id =:unique_id AND signature_type =:signature_type AND signature_of =:signature_of")
        Signature_table getSignature(String client, String unique_id, String signature_type, String signature_of);

        @Query("SELECT signature_data FROM signature_table WHERE client_id =:client AND unique_id =:unique_id AND signature_type =:signature_type " +
                "AND signature_of =:signature_of")
        String getSignature_data(String client, String unique_id, String signature_type, String signature_of);

        @Query("SELECT unique_id FROM signature_table WHERE client_id =:client")
        List<String> getAllSignature(String client);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Signature_table signature_table);

        @Query("DELETE FROM signature_table WHERE client_id =:client AND unique_id =:unique_id")
        void deleteSignature_data(String client, String unique_id);

    }
}
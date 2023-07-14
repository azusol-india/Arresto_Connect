package app.com.arresto.arresto_connect.database.factory_tables;

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


@Entity(indices = {@Index(value = {"mdata_id"}, unique = true)})
public class FactoryMasterTable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "batch_fk")
    private long batch_fk;
    @ColumnInfo(name = "mdata_id")
    private String mdata_id;
    @ColumnInfo(name = "mdata_jobcard")
    private String mdata_jobcard;
    @ColumnInfo(name = "mdata_batch")
    private String mdata_batch;
    @ColumnInfo(name = "mdata_serial")
    private String mdata_serial;
    @ColumnInfo(name = "mdata_rfid")
    private String mdata_rfid;
    @ColumnInfo(name = "mdata_barcode")
    private String mdata_barcode;
    @ColumnInfo(name = "mdata_uin")
    private String mdata_uin;
    @ColumnInfo(name = "mdata_item_series")
    private String mdata_item_series;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBatch_fk() {
        return batch_fk;
    }

    public void setBatch_fk(long batch_fk) {
        this.batch_fk = batch_fk;
    }

    public String getMdata_id() {
        return mdata_id;
    }

    public void setMdata_id(String mdata_id) {
        this.mdata_id = mdata_id;
    }

    public String getMdata_jobcard() {
        return mdata_jobcard;
    }

    public void setMdata_jobcard(String mdata_jobcard) {
        this.mdata_jobcard = mdata_jobcard;
    }

    public String getMdata_batch() {
        return mdata_batch;
    }

    public void setMdata_batch(String mdata_batch) {
        this.mdata_batch = mdata_batch;
    }

    public String getMdata_serial() {
        return mdata_serial;
    }

    public void setMdata_serial(String mdata_serial) {
        this.mdata_serial = mdata_serial;
    }

    public String getMdata_rfid() {
        return mdata_rfid;
    }

    public void setMdata_rfid(String mdata_rfid) {
        this.mdata_rfid = mdata_rfid;
    }

    public String getMdata_barcode() {
        return mdata_barcode;
    }

    public void setMdata_barcode(String mdata_barcode) {
        this.mdata_barcode = mdata_barcode;
    }

    public String getMdata_uin() {
        return mdata_uin;
    }

    public void setMdata_uin(String mdata_uin) {
        this.mdata_uin = mdata_uin;
    }

    public String getMdata_item_series() {
        return mdata_item_series;
    }

    public void setMdata_item_series(String mdata_item_series) {
        this.mdata_item_series = mdata_item_series;
    }

    @Dao
    public interface FactoryMasterTable_Dao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(FactoryMasterTable factoryMasterTable);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<FactoryMasterTable> factoryMasterTable);

        @Update
        void updateMaster(FactoryMasterTable factoryMasterTable);

        @Query("SELECT * FROM factoryMasterTable")
        List<FactoryMasterTable> getAll();

        @Query("SELECT * FROM factoryMasterTable WHERE batch_fk = :key")
        List<FactoryMasterTable> getMaster(long key);

        @Query("SELECT * FROM factoryMasterTable WHERE batch_fk = :key AND mdata_id=:mdata_id")
        FactoryMasterTable getMasterRow(long key, String mdata_id);

        @Query("DELETE FROM factoryMasterTable WHERE batch_fk = :key")
        void deleteMaster(long key);

        @Query("SELECT mdata_id FROM factoryMasterTable WHERE batch_fk = :key AND mdata_id=:mdata_id LIMIT 1")
        boolean isMasterExist(long key, String mdata_id);
    }
}

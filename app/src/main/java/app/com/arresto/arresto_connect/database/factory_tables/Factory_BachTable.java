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

@Entity(indices = {@Index(value = {"batch_no", "s_from", "s_to"}, unique = true)})
public class Factory_BachTable {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "batch_no")
    private String batch_no;

    @ColumnInfo(name = "s_from")
    private String serial_from;

    @ColumnInfo(name = "s_to")
    private String serial_to;

    public long getId() {
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

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getSerial_from() {
        return serial_from;
    }

    public void setSerial_from(String serial_from) {
        this.serial_from = serial_from;
    }

    public String getSerial_to() {
        return serial_to;
    }

    public void setSerial_to(String serial_to) {
        this.serial_to = serial_to;
    }

    @Dao
    public interface Factory_BachTable_Dao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(Factory_BachTable factory_bachTable);

        @Update
        void updateBatch(Factory_BachTable factory_bachTable);

        @Query("SELECT * FROM factory_bachtable WHERE user_id = :key ")
        List<Factory_BachTable> getAll(String key);

        @Query("SELECT * FROM factory_bachtable WHERE user_id = :key AND batch_no=:batch_no AND s_from=:serial_from AND s_to =:serial_to")
        Factory_BachTable getBatch(String key, String batch_no, String serial_from, String serial_to);

        @Query("DELETE FROM factory_bachtable WHERE user_id = :user_id AND batch_no=:batch_no AND s_from=:serial_from AND s_to =:serial_to")
        void deleteBatch(String user_id, String batch_no, String serial_from, String serial_to);

        @Query("DELETE FROM factory_bachtable WHERE id = :id")
        void deleteBatch(long id);

        @Query("SELECT batch_no FROM factory_bachtable WHERE user_id = :key AND batch_no=:batch_no AND s_from=:serial_from AND s_to =:serial_to LIMIT 1")
        boolean isBatchExist(String key, String batch_no, String serial_from, String serial_to);
    }
}

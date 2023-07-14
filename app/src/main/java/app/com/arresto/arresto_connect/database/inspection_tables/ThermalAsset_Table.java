package app.com.arresto.arresto_connect.database.inspection_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "thermalasset_table")
public class ThermalAsset_Table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "asset")
    private String asset;
    @ColumnInfo(name = "data")
    private String jsonData;

    public int getId() {
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

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }


    @Dao
    public interface ThermalAsset_Dao {
        @Query("SELECT * FROM thermalasset_table")
        List<ThermalAsset_Table> getAll();

        @Query("SELECT * FROM thermalasset_table WHERE user_id =:user_id AND unique_id =:unique_id")
        ThermalAsset_Table getThermal_Asset(String user_id, String unique_id);

        @Query("SELECT * FROM thermalasset_table WHERE user_id =:user AND unique_id =:unique_id")
        List<ThermalAsset_Table> getAllThermal_Asset(String user, String unique_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(ThermalAsset_Table detail_table);

        @Query("DELETE FROM thermalasset_table WHERE user_id =:user AND unique_id =:unique_id")
        void deleteThermal_Asset(String user, String unique_id);

    }
}

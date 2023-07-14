package app.com.arresto.arresto_connect.database.inspection_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "asset_positions_table")
public class Asset_Positions_Table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "comp_position")
    private int comp_position;

    public Asset_Positions_Table(String user_id, String unique_id, int comp_position) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.comp_position = comp_position;
    }

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

    public int getComp_position() {
        return comp_position;
    }

    public void setComp_position(int comp_position) {
        this.comp_position = comp_position;
    }

    @Dao
    public interface Asset_Position_Dao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Asset_Positions_Table inspectionPositions);

        @Query("SELECT comp_position FROM asset_positions_table WHERE user_id =:user_id AND unique_id =:unique_id")
        List<Integer> getInspected_Asset_Positions(String user_id, String unique_id);

        @Query("DELETE FROM asset_positions_table WHERE user_id =:user AND unique_id =:unique_id")
        void deleteInspected_AssetPos(String user, String unique_id);

    }

}

package app.com.arresto.arresto_connect.database.inspection_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "inspection_table")
public class Inspection_Table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "asset")
    private String asset;
    @ColumnInfo(name = "subAsset")
    private String subAsset;
    @ColumnInfo(name = "expected_result")
    private String expected_result;
    @ColumnInfo(name = "observation")
    private String observation;
    @ColumnInfo(name = "actionProposed")
    private String actionProposed;
    @ColumnInfo(name = "result")
    private String result;
    @ColumnInfo(name = "before_images")
    private String before_images = "";
    @ColumnInfo(name = "after_images")
    private String after_images = "";
    @ColumnInfo(name = "skip_result")
    private String skip_result;
    @ColumnInfo(name = "skip_flag")
    private String skip_flag;
    @ColumnInfo(name = "skip_remark")
    private String skip_remark;
    @ColumnInfo(name = "comp_position")
    private int comp_position;
    @ColumnInfo(name = "ins_subast_pos")
    private int ins_subast_pos;

    public void set_inspection_Asset(String user_id, String unique_id, String asset, String subAsset, String observation,
                                     String actionProposed, String result, String skip_status,
                                     String skip_remark, int comp_position, int ins_subast_pos) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.asset = asset;
        this.subAsset = subAsset;
        this.observation = observation;
        this.actionProposed = actionProposed;
        if (skip_status != null && !skip_status.equals("")) {
            this.skip_flag = "1";
            this.skip_result = skip_status;
            this.skip_remark = skip_remark;
        } else {
            this.skip_flag = "0";
        }
        this.result = result;
        this.skip_remark = skip_remark;
        this.comp_position = comp_position;
        this.ins_subast_pos = ins_subast_pos;
    }

    public void set_inspection_Asset(String user_id, String unique_id, String asset, String subAsset, String expected_result,String observation,
                                     String actionProposed, String result, String skip_status,
                                     String skip_remark, int comp_position, int ins_subast_pos) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.asset = asset;
        this.subAsset = subAsset;
        this.expected_result = expected_result;
        this.observation = observation;
        this.actionProposed = actionProposed;
        if (skip_status != null && !skip_status.equals("")) {
            this.skip_flag = "1";
            this.skip_result = skip_status;
            this.skip_remark = skip_remark;
        } else {
            this.skip_flag = "0";
        }
        this.result = result;
        this.skip_remark = skip_remark;
        this.comp_position = comp_position;
        this.ins_subast_pos = ins_subast_pos;
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

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSubAsset() {
        return subAsset;
    }

    public void setSubAsset(String subAsset) {
        this.subAsset = subAsset;
    }

    public String getExpected_result() {
        return expected_result;
    }

    public void setExpected_result(String expected_result) {
        this.expected_result = expected_result;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getActionProposed() {
        return actionProposed;
    }

    public void setActionProposed(String actionProposed) {
        this.actionProposed = actionProposed;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getSkip_result() {
        return skip_result;
    }

    public void setSkip_result(String skip_result) {
        this.skip_result = skip_result;
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

    public int getComp_position() {
        return comp_position;
    }

    public void setComp_position(int comp_position) {
        this.comp_position = comp_position;
    }

    public int getIns_subast_pos() {
        return ins_subast_pos;
    }

    public void setIns_subast_pos(int ins_subast_pos) {
        this.ins_subast_pos = ins_subast_pos;
    }

    @Dao
    public interface Inspection_Asset_Dao {
        @Query("SELECT * FROM inspection_table")
        List<Inspection_Table> getAll();

        @Query("SELECT * FROM inspection_table WHERE user_id =:user_id AND unique_id =:unique_id")
        Inspection_Table getInspected_Asset(String user_id, String unique_id);

        @Query("SELECT * FROM inspection_table WHERE user_id =:user AND unique_id =:unique_id")
        List<Inspection_Table> getAllInspected_Asset(String user, String unique_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(Inspection_Table detail_table);

        @Query("SELECT ins_subast_pos FROM inspection_table WHERE user_id =:user_id AND unique_id =:unique_id AND asset=:asset_code AND comp_position=:asset_pos")
        List<Integer> getInspected_subAsset_Positions(String user_id, String unique_id, String asset_code, int asset_pos);

        @Query("DELETE FROM inspection_table WHERE user_id =:user AND unique_id =:unique_id")
        void deleteInspected_Asset(String user, String unique_id);

    }

}

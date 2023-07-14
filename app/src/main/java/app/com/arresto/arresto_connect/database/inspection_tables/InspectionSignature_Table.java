package app.com.arresto.arresto_connect.database.inspection_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "inspectionSignature_table")
public class InspectionSignature_Table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String user_id;
    @ColumnInfo(name = "client_id")
    private String client_id;
    @ColumnInfo(name = "unique_id")
    private String unique_id;
    @ColumnInfo(name = "clientName")
    private String clientName;
    @ColumnInfo(name = "designation")
    private String designation;
    @ColumnInfo(name = "remark")
    private String remark;
    @ColumnInfo(name = "spares")
    private String spares;
    @ColumnInfo(name = "signImage")
    private String signImage;
    @ColumnInfo(name = "isConfirm")
    private String isConfirm;
    @ColumnInfo(name = "isInspector")
    private int isInspector;

    public void setInspector_Sign(String user_id, String client_id, String unique_id, String remark, String spares,
                                  String signImage, boolean checked) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.client_id = client_id;
        this.remark = remark;
        this.spares = spares;
        this.signImage = signImage;
        this.isInspector = 1;
        if (checked)
            this.isConfirm = "1";
        else
            this.isConfirm = "0";
    }

    public void setClient_Sign(String user_id, String client_id, String unique_id, String clientName, String designation,
                               String remark, String signImage) {
        this.user_id = user_id;
        this.unique_id = unique_id;
        this.client_id = client_id;
        this.clientName = clientName;
        this.designation = designation;
        this.remark = remark;
        this.signImage = signImage;
        this.isInspector = 0;
    }
    public String getImagePath(){
        return signImage;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSpares() {
        return spares;
    }

    public void setSpares(String spares) {
        this.spares = spares;
    }

    public String getSignImage() {
        return signImage;
    }

    public void setSignImage(String signImage) {
        this.signImage = signImage;
    }

    public String getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm;
    }

    public int getIsInspector() {
        return isInspector;
    }

    public void setIsInspector(int isInspector) {
        this.isInspector = isInspector;
    }

    @Dao
    public interface InspectionSignature_Dao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(InspectionSignature_Table inspectionSignature_table);

        @Query("SELECT * FROM inspectionSignature_table WHERE user_id =:user_id AND unique_id =:unique_id AND isInspector =:isInspector")
        InspectionSignature_Table getInspectionSignature(String user_id, String unique_id, int isInspector);

        @Query("DELETE FROM inspectionSignature_table WHERE user_id =:user AND unique_id =:unique_id AND isInspector =:isInspector")
        void deleteSignature(String user, String unique_id, int isInspector);

        @Query("DELETE FROM inspectionSignature_table WHERE user_id =:user AND unique_id =:unique_id")
        void deleteSignature(String user, String unique_id);

    }
}

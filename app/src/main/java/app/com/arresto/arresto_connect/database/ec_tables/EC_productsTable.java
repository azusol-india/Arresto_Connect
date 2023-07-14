/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.database.ec_tables;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 5/27/2019.
 */
@Entity(indices = {@Index(value = {"cat_id", "name", "BOQ_id"}, unique = true)})
public class EC_productsTable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "cat_id")
    private String cat_id;

    @ColumnInfo(name = "cat_name")
    private String cat_name;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "price")
    String price;
    @ColumnInfo(name = "hsn_code")
    String hsn_code;
    @ColumnInfo(name = "tax")
    String tax;
    @ColumnInfo(name = "group")
    String group;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "BOQ_id")
    private String BOQ_id;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getBOQ_id() {
        return BOQ_id;
    }

    public void setBOQ_id(String BOQ_id) {
        this.BOQ_id = BOQ_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHsn_code() {
        return hsn_code;
    }

    public void setHsn_code(String hsn_code) {
        this.hsn_code = hsn_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
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

    public String getName() {
        return name;
    }

    public void setName(String product_data) {
        this.name = product_data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Dao
    public interface EC_productsTable_Dao {

//        @Query("SELECT * FROM ec_productstable WHERE user_id =:key AND client_id =:client")
//        List<EC_productsTable> getAll(String key, String client);

        @Query("SELECT * FROM ec_productstable WHERE boq_id =:boq_id")
        List<EC_productsTable> getBoqProducts(String boq_id);

        @Query("SELECT * FROM ec_productstable WHERE user_id =:key AND client_id =:client AND boq_id =:boq_id")
        List<EC_productsTable> getProducts(String key, String client, String boq_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<EC_productsTable> ec_productsTable);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(EC_productsTable ec_productsTable);

        @Query("DELETE FROM ec_productstable WHERE user_id = :key AND client_id =:client AND name =:name AND cat_id =:cat_id AND boq_id =:boq_id")
        void deleteProduct(String key, String client, String name, String cat_id, String boq_id);

        @Query("DELETE FROM ec_productstable WHERE user_id = :key AND client_id =:client AND boq_id =:boq_id")
        void deleteProduct(String key, String client, String boq_id);

        @Query("SELECT id FROM ec_productstable WHERE user_id = :user AND cat_id =:cat_id AND boq_id =:boq_id AND name =:name LIMIT 1")
        boolean isItemExist(String user,String cat_id, String boq_id, String name);
    }
}

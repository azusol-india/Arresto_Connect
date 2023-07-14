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

@Entity(indices = {@Index(value = {"cat_id", "BOQ_id"}, unique = true)})
public class Category_Table {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "client_id")
    private String client_id;

    @ColumnInfo(name = "cat_id")
    private String cat_id;

    @ColumnInfo(name = "parent_cat_id")
    private String parent_cat_id;

    @ColumnInfo(name = "BOQ_id")
    private String BOQ_id;

    public String getBOQ_id() {
        return BOQ_id;
    }

    public void setBOQ_id(String BOQ_id) {
        this.BOQ_id = BOQ_id;
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

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getParent_cat_id() {
        return parent_cat_id;
    }

    public void setParent_cat_id(String parent_cat_id) {
        this.parent_cat_id = parent_cat_id;
    }


    @Dao
    public interface Category_Table_Dao {
        @Query("SELECT * FROM category_table")
        List<Category_Table> getAll();

        @Query("SELECT * FROM category_table WHERE boq_id =:boq_id")
        List<Category_Table> getBoqCategories(String boq_id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insert(Category_Table category_table);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<Category_Table> category_tables);

        @Query("DELETE FROM category_table WHERE user_id = :key AND client_id =:client AND cat_id =:cat_id AND boq_id =:boq_id")
        void deleteCategory(String key, String client, String cat_id, String boq_id);

        @Query("DELETE FROM category_table WHERE user_id = :key AND client_id =:client AND boq_id =:boq_id")
        void deleteCategory(String key, String client, String boq_id);

        @Query("SELECT * FROM category_table WHERE user_id = :key AND cat_id = :cat_id ORDER BY id DESC LIMIT 1")
        Category_Table getLastEntry(String key, String cat_id);

        @Query("SELECT id FROM category_table WHERE user_id = :key AND cat_id = :cat_id AND boq_id =:boq_id LIMIT 1")
        boolean isItemExist(String key, String cat_id, String boq_id);

        @Query("SELECT id FROM category_table WHERE user_id = :key AND boq_id =:boq_id LIMIT 1")
        boolean isBoqExist(String key, String boq_id);
    }


}

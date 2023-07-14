/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.database;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/23/2019.
 */
//@Entity(tableName = "language_table")

@Entity(indices={@Index(value="lang_key", unique=true)})
public class Language_table implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "data")
    private String language_data;

    @ColumnInfo(name = "date")
    private String modified_date;


    @ColumnInfo(name = "lang_key")
    private String lang_key;



    public String getLang_key() {
        return lang_key;
    }

    public void setLang_key(String lang_key) {
        this.lang_key = lang_key;
//        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage_data() {
        return language_data;
    }

    public void setLanguage_data(String language_data) {
        this.language_data = language_data;
//        return this;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }


    @Dao
    public interface LanguageDao {
        @Query("SELECT * FROM language_table")
        List<Language_table> getAll();

        @Query("SELECT * FROM language_table WHERE lang_key =:key")
        Language_table getLanguage(String key);


        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Language_table language_table);

        @Delete
        void delete(Language_table language_table);

        @Update
        void update(Language_table language_table);

    }
}

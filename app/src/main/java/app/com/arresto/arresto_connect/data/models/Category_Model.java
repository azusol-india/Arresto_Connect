/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

import androidx.annotation.NonNull;

/**
 * Created by AZUSOL-PC-02 on 5/21/2019.
 */
public class Category_Model {

    private String id;
    private String cat_parentid;
    private String cat_name;
    private String category;
    private String cat_uniqueName;
    private String cat_image;
    private String cat_status;
    private String cat_parentname;
    private String min_qty;
    private String max_qty;
    private boolean completed;
    private int missing_count;

    public boolean is_completed() {
        return completed;
    }

    public void setIs_completed(boolean completed) {
        this.completed = completed;
    }

    public int getMissing() {
        return missing_count;
    }

    public void setMissing(int missing_count) {
        this.missing_count = missing_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_parentid() {
        return cat_parentid;
    }

    public void setCat_parentid(String cat_parentid) {
        this.cat_parentid = cat_parentid;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCat_uniqueName() {
        return cat_uniqueName;
    }

    public void setCat_uniqueName(String cat_uniqueName) {
        this.cat_uniqueName = cat_uniqueName;
    }

    public String getCat_image() {
        return cat_image;
    }

    public void setCat_image(String cat_image) {
        this.cat_image = cat_image;
    }

    public String getCat_status() {
        return cat_status;
    }

    public void setCat_status(String cat_status) {
        this.cat_status = cat_status;
    }

    public String getCat_parentname() {
        return cat_parentname;
    }

    public void setCat_parentname(String cat_parentname) {
        this.cat_parentname = cat_parentname;
    }

    public int getMin_qty() {
        if (!min_qty.equals(""))
            return Integer.parseInt(min_qty);
        else return 0;
    }

    public void setMin_qty(String min_qty) {
        this.min_qty = min_qty;
    }

    public int getMax_qty() {
        if (!max_qty.equals(""))
            return Integer.parseInt(max_qty);
        else return 0;
    }

    public void setMax_qty(String max_qty) {
        this.max_qty = max_qty;
    }

    @NonNull
    @Override
    public String toString() {
        return cat_name;
    }
    //    boolean leaf;
//    public void setIsLeaf(boolean leaf) {
//        this.leaf=leaf;
//    }
//    public boolean isLeaf() {
//        return leaf;
//    }
}

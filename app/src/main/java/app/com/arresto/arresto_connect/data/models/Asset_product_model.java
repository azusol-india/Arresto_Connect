/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

public class Asset_product_model {
    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getStore_stock() {
        return store_stock;
    }

    public void setStore_stock(String store_stock) {
        this.store_stock = store_stock;
    }

    public String getStore_mol() {
        return store_mol;
    }

    public void setStore_mol(String store_mol) {
        this.store_mol = store_mol;
    }

    public String getStore_eoq() {
        return store_eoq;
    }

    public void setStore_eoq(String store_eoq) {
        this.store_eoq = store_eoq;
    }

    public String getEnd_of_life() {
        return end_of_life;
    }

    public void setEnd_of_life(String end_of_life) {
        this.end_of_life = end_of_life;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    String product_id, product_code, product_description, manufacture, store_stock, store_mol, store_eoq, end_of_life, user_id, user_name;

}
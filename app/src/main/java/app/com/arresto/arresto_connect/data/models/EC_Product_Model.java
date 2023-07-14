/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;


import java.util.ArrayList;

public class EC_Product_Model {

    public ArrayList<Data> getAssets() {
        return assets;
    }

    public void setAssets(ArrayList<Data> assets) {
        this.assets = assets;
    }

    public ArrayList<Data> getSub_assets() {
        return sub_assets;
    }

    public void setSub_assets(ArrayList<Data> sub_assets) {
        this.sub_assets = sub_assets;
    }

    public ArrayList<Data> getAssets_series() {
        return assets_series;
    }

    public void setAssets_series(ArrayList<Data> assets_series) {
        this.assets_series = assets_series;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCatParent_id() {
        return catParent_id;
    }

    public void setCatParent_id(String catParent_id) {
        this.catParent_id = catParent_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    private ArrayList<Data> assets, sub_assets, assets_series;
    private String category_id,catParent_id,category_name, parent_name;

   public static class Data {
       String name;
       String url;
       String type;
       String price;
       String tax;
       String group;
       String hsn_code;
       String description;

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

       public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

    }

}

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

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;

public class DataHolder_Model {

    private ArrayList<HomeModel> homeModels;
    private ArrayList<CustomForm_Model> customViews_models;
    private ArrayList<CustomForm_Model> customViewsData_models;
    private Advt_Model advt_model;
    private List<Dashbrd_Search> search_data;
    private ArrayList<Component_model> component_models;
    private ArrayList<Product_model> product_models;
    private ArrayList<Project_Model> project_models;
    private ArrayList<EC_Project> ec_project;
    private ArrayList<Category_Model> category_models;
    private Periodic_model slctd_periodic_model;

    private EC_Project slctd_ec_project;

    private ECSites slctd_site;
    public ArrayList knowledge_treeProducts;

    List<EC_productsTable> Boq_products;

    public ArrayList<HomeModel> getHomeModels() {
        return homeModels;
    }

    public void setHomeModels(ArrayList<HomeModel> homeModels) {
        this.homeModels = homeModels;
    }

    public List<EC_productsTable> getBoq_products() {
        return Boq_products;
    }

    public void setBoq_products(List<EC_productsTable> boq_products) {
        Boq_products = boq_products;
    }

    public Advt_Model getAdvt_model() {
        return advt_model;
    }

    public void setAdvt_model(Advt_Model advt_model) {
        this.advt_model = advt_model;
    }


    public ArrayList<Category_Model> getCategory_models() {
        return category_models;
    }

    public void setCategory_models(ArrayList<Category_Model> category_models) {
        this.category_models = category_models;
    }

    public void set_Search_data(List<Dashbrd_Search> search_data) {
        this.search_data = search_data;
    }

    public List<Dashbrd_Search> get_Search_data() {
        return search_data;
    }

    public ArrayList<CustomForm_Model> getCustomViews_models() {
        return customViews_models;
    }

    public void setCustomViews_models(ArrayList<CustomForm_Model> customViews_models) {
        this.customViews_models = customViews_models;
    }


    public ArrayList<CustomForm_Model> getCustomViewsData_models() {
        return customViewsData_models;
    }

    public void setCustomViewsData_models(ArrayList<CustomForm_Model> customViewsData_models) {
        this.customViewsData_models = customViewsData_models;
    }


    public void setComponent_models(ArrayList<Component_model> component_models) {
        this.component_models = component_models;
    }

    public ArrayList<Component_model> getComponent_models() {
        return component_models;
    }

    public ArrayList<Product_model> getProduct_models() {
        return product_models;
    }

    public void setProduct_models(ArrayList<Product_model> product_models) {
        this.product_models = product_models;
    }

    public ArrayList<Project_Model> getProject_models() {
        return project_models;
    }

    public void setProject_models(ArrayList<Project_Model> project_models) {
        this.project_models = project_models;
    }

    private ArrayList<Component_history_Model> component_history_models;

    public ArrayList<Component_history_Model> getComponent_history_models() {
        return component_history_models;
    }

    public void setComponent_history_models(ArrayList<Component_history_Model> coponent_history_models) {
        this.component_history_models = coponent_history_models;
    }

    public Periodic_model getSlctd_periodic_model() {
        return slctd_periodic_model;
    }

    public void setSlctd_periodic_model(Periodic_model slctd_periodic_model) {
        this.slctd_periodic_model = slctd_periodic_model;
    }

    public ArrayList<EC_Project> getEc_project() {
        return ec_project;
    }

    public void setEc_project(ArrayList<EC_Project> ec_project) {
        this.ec_project = ec_project;
    }


    public EC_Project getSlctd_ec_project() {
        return slctd_ec_project;
    }

    public void setSlctd_ec_project(EC_Project slctd_ec_project) {
        this.slctd_ec_project = slctd_ec_project;
    }

    public ECSites getSlctd_site() {
        return slctd_site;
    }

    public void setSlctd_site(ECSites slctd_site) {
        this.slctd_site = slctd_site;
    }

    public ArrayList getKnowledge_treeProducts() {
        if (knowledge_treeProducts == null)
            return new ArrayList();
        return knowledge_treeProducts;
    }

    public void setKnowledge_treeProducts(ArrayList knowledge_treeProducts) {
        this.knowledge_treeProducts = knowledge_treeProducts;
    }

    // object instance
    private static DataHolder_Model data_holder = new DataHolder_Model();

    public static DataHolder_Model getInstance() {
        return data_holder;
    }


}

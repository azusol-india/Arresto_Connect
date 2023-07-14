package app.com.arresto.arresto_connect.data.models;

import java.util.List;

public class AddSubAssetModel {
    private String client_id;
    private String subasset_code;
    private String description;
    private String uom;
    private String inspection_type;
    private List<String> sub_assets;
    private List<String> results;
    private List<String> observations;
    private String repair;
    private String image;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSubasset_code() {
        return subasset_code;
    }

    public void setSubasset_code(String subasset_code) {
        this.subasset_code = subasset_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public List<String> getSub_assets() {
        return sub_assets;
    }

    public void setSub_assets(List<String> sub_assets) {
        this.sub_assets = sub_assets;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<String> getObservations() {
        return observations;
    }

    public void setObservations(List<String> observations) {
        this.observations = observations;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    static AddSubAssetModel addSAssetModel;

    public static AddSubAssetModel getInstance() {
        if (addSAssetModel == null)
            addSAssetModel = new AddSubAssetModel();
        return addSAssetModel;
    }
}

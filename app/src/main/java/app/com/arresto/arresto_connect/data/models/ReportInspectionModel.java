package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class ReportInspectionModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("client_id_fk")
    @Expose
    private String clientIdFk;
    @SerializedName("inspection_list_id")
    @Expose
    private String inspectionListId;
    @SerializedName("asset_name")
    @Expose
    private String assetName;
    @SerializedName("subAsset_name")
    @Expose
    private String subAssetName;
    @SerializedName("skip_remarks")
    @Expose
    private String skipRemarks;
    @SerializedName("skip_result")
    @Expose
    private String skipResult;
    @SerializedName("skip_flag")
    @Expose
    private String skipFlag;
    @SerializedName("before_repair_image")
    @Expose
    private Object beforeRepairImage;
    @SerializedName("after_repair_image")
    @Expose
    private Object afterRepairImage;
    @SerializedName("observation_type")
    @Expose
    private List<ObservationType> observationType = null;
    @SerializedName("action_proposed")
    @Expose
    private String actionProposed;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("rams_inspection")
    @Expose
    private String ramsInspection;
    @SerializedName("image_zip")
    @Expose
    private String imageZip;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("observation_name")
    @Expose
    private List<String> observationName = null;
    @SerializedName("action_proposed_name")
    @Expose
    private List<String> actionProposedName = null;
    @SerializedName("result_array")
    @Expose
    private List<String> resultArray = null;
    @SerializedName("expected_result")
    @Expose
    private List<String> expectedResult = null;
    @SerializedName("asset_image")
    @Expose
    private String assetImage;
    @SerializedName("inspection_type_name")
    @Expose
    private String inspectionTypeName;
    @SerializedName("asset_qty")
    @Expose
    private String assetQty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientIdFk() {
        return clientIdFk;
    }

    public void setClientIdFk(String clientIdFk) {
        this.clientIdFk = clientIdFk;
    }

    public String getInspectionListId() {
        return inspectionListId;
    }

    public void setInspectionListId(String inspectionListId) {
        this.inspectionListId = inspectionListId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSubAssetName() {
        return subAssetName;
    }

    public void setSubAssetName(String subAssetName) {
        this.subAssetName = subAssetName;
    }

    public String getSkipRemarks() {
        return skipRemarks;
    }

    public void setSkipRemarks(String skipRemarks) {
        this.skipRemarks = skipRemarks;
    }

    public String getSkipResult() {
        return skipResult;
    }

    public void setSkipResult(String skipResult) {
        this.skipResult = skipResult;
    }

    public String getSkipFlag() {
        return skipFlag;
    }

    public void setSkipFlag(String skipFlag) {
        this.skipFlag = skipFlag;
    }

    public ArrayList<String> getBeforeRepairImage() {
        if(!beforeRepairImage.equals("")) {
            return (ArrayList<String>) beforeRepairImage;
        } return new ArrayList<>();
    }

    public void setBeforeRepairImage(String beforeRepairImage) {
        this.beforeRepairImage = beforeRepairImage;
    }

    public ArrayList<String> getAfterRepairImage() {
        if(!afterRepairImage.equals("")) {
            return (ArrayList<String>) afterRepairImage;
        } return new ArrayList<>();
    }

    public void setAfterRepairImage(String afterRepairImage) {
        this.afterRepairImage = afterRepairImage;
    }

    public List<ObservationType> getObservationType() {
        return observationType;
    }

    public void setObservationType(List<ObservationType> observationType) {
        this.observationType = observationType;
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

    public String getRamsInspection() {
        return ramsInspection;
    }

    public void setRamsInspection(String ramsInspection) {
        this.ramsInspection = ramsInspection;
    }

    public String getImageZip() {
        return imageZip;
    }

    public void setImageZip(String imageZip) {
        this.imageZip = imageZip;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<String> getObservationName() {
        return observationName;
    }

    public void setObservationName(List<String> observationName) {
        this.observationName = observationName;
    }

    public List<String> getActionProposedName() {
        return actionProposedName;
    }

    public void setActionProposedName(List<String> actionProposedName) {
        this.actionProposedName = actionProposedName;
    }

    public List<String> getResultArray() {
        return resultArray;
    }

    public void setResultArray(List<String> resultArray) {
        this.resultArray = resultArray;
    }

    public List<String> getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(List<String> expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getAssetImage() {
        return assetImage;
    }

    public void setAssetImage(String assetImage) {
        this.assetImage = assetImage;
    }

    public String getInspectionTypeName() {
        return inspectionTypeName;
    }

    public void setInspectionTypeName(String inspectionTypeName) {
        this.inspectionTypeName = inspectionTypeName;
    }

    public String getAssetQty() {
        return assetQty;
    }

    public void setAssetQty(String assetQty) {
        this.assetQty = assetQty;
    }

    public class ObservationType {

        @SerializedName("observation_type")
        @Expose
        private String observationType;
        @SerializedName("action_proposed")
        @Expose
        private String actionProposed;
        @SerializedName("result")
        @Expose
        private String result;
        @SerializedName("expected_result")
        @Expose
        private String expected_result;

        public String getObservationType() {
            return observationType;
        }

        public void setObservationType(String observationType) {
            this.observationType = observationType;
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

        public String getExpected_result() {
            return expected_result;
        }

        public void setExpected_result(String expected_result) {
            this.expected_result = expected_result;
        }
    }
}
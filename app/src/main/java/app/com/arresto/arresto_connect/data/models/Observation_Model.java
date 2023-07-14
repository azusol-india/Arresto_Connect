package app.com.arresto.arresto_connect.data.models;

import android.widget.ImageView;

public class Observation_Model {
    String expected_result,observation, action_proposed, result="";
    String observation_id="", action_proposed_id="";
    ImageView delete_btn;
    boolean checkingOld;
    public String getExpected_result() {
        return expected_result;
    }

    public void setExpected_result(String expected_result) {
        this.expected_result = expected_result;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getAction_proposed() {
        return action_proposed;
    }

    public void setAction_proposed(String action_proposed) {
        this.action_proposed = action_proposed;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(String observation_id) {
        this.observation_id = observation_id;
    }

    public String getAction_proposed_id() {
        return action_proposed_id;
    }

    public void setAction_proposed_id(String action_proposed_id) {
        this.action_proposed_id = action_proposed_id;
    }

    public ImageView getDelete_btn() {
        return delete_btn;
    }

    public void setDelete_btn(ImageView delete_btn) {
        this.delete_btn = delete_btn;
    }

    public boolean isCheckingOld() {
        return checkingOld;
    }

    public void setCheckingOld(boolean checkingOld) {
        this.checkingOld = checkingOld;
    }
}

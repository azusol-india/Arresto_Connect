package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;

public class RopePreviewModel {

    String project_id,user_id, username, upro_image,total_manual_time;
    ArrayList<Rope_Status> rope_history;
    boolean edited;
    String total_time="";
    String edited_time;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getUserImage() {
        return upro_image;
    }

    public void setUserImage(String userImage) {
        this.upro_image = userImage;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getEdited_time() {
        return edited_time;
    }

    public void setEdited_time(String edited_time) {
        this.edited_time = edited_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getTotal_manual_time() {
        return total_manual_time;
    }

    public void setTotal_manual_time(String total_manual_time) {
        this.total_manual_time = total_manual_time;
    }

    public ArrayList<Rope_Status> getRope_history() {
        return rope_history;
    }

    public void setRope_history(ArrayList<Rope_Status> rope_history) {
        this.rope_history = rope_history;
    }
}

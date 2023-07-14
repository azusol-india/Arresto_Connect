package app.com.arresto.arresto_connect.data.models;

import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by AZUSOL-PC-02 on 7/25/2019.
 */
public class Repeaters_Model {
    ArrayList<String> childs;
    ArrayList<CustomForm_Model.FieldData> childs_data;
    LinearLayout parent_layer;
    String lastHeading="";


    int child_count = 0;

    public ArrayList<String> getChilds() {
        return childs;
    }

    public void addChilds(String child) {
        if (this.childs == null) {
            this.childs = new ArrayList<>();
        }
        this.childs.add(child);
    }

    public int getChild_count() {
        return child_count;
    }

    public void addChild_count() {
        this.child_count++;
    }

    public ArrayList<CustomForm_Model.FieldData> getChilds_data() {
        return childs_data;
    }

    public void addChilds_data(CustomForm_Model.FieldData child_data) {
        if (this.childs_data == null) {
            this.childs_data = new ArrayList<>();
        }
        this.childs_data.add(child_data);
    }

    public String getLastHeading() {
        return lastHeading;
    }

    public void setLastHeading(String lastHeading) {
        this.lastHeading = lastHeading;
    }

    public LinearLayout getParent_layer() {
        return parent_layer;
    }

    public void setParent_layer(LinearLayout parent_layer) {
        this.parent_layer = parent_layer;
    }
}

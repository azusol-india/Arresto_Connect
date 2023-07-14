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

package app.com.arresto.arresto_connect.constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.Preferences;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;

public class Static_values {

    //  Static variables
//    public static String directory = Environment.getExternalStorageDirectory() + "/Arresto/";
    public static String directory = "";
    public static String user_email, user_id, group_id, user_typ;
    //    public static String page_type="";
    public static String logo_url = "";
    public static String client_id = "";
    public static String role_id = "";
    public static String client_group_id = "";
    public static String slctd_product_name = "";
    public static String slctd_product_image = "";
    public static String device_id = "";

    public static ArrayList<Site_Model> today_Pm_data = new ArrayList<>();
    public static String report_no = "";

    public static Site_Model selected_Site_model = new Site_Model();
    public static MasterData_model selectedMasterData_model = new MasterData_model();
    public static int indicatorDrawable[] = {R.drawable.light_red, R.drawable.light_yellow, R.drawable.light_green, R.drawable.light_orange, R.drawable.breakdown_light};

    public static String unique_id = "";

    public static Preferences mPrefrence;

    public static String current_language = "English";
    public static JSONObject language_data = new JSONObject();

    public static List<String> downloaded_sites = new ArrayList<>();

    public static Map<String, TreeNode> treeNodes = new HashMap<>();

    public static ArrayList<Category_Model> nested_catgrs = new ArrayList<>();

    public static String horizontal = "582";
    public static String vertical = "653";
    public static String vertical_alurail = "658";
    public static String vertical_wire = "656";
    public static int revision = 0;
    public static boolean isProfile = false;
    public static boolean currently_inspected = false;
    public static String docsStatus = "";
    public static String docsName = "";

}

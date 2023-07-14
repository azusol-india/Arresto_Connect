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

package app.com.arresto.arresto_connect.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import app.com.arresto.Flavor_Id.FlavourInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface All_Api {
    //    data get api's1
    //    String Host = "http://192.168.1.3/Mysites/karam/kare/";
//     String Host = "http://www.arresto.in/kare/";
//    String Host = "https://arresto.in/connect/";
    String Host = FlavourInfo.HOST;

    String login_url = Host + "api_controller/authenticate?email=";

    String register_URL = Host + "api_controller/register";
    String forget_URL = Host + "api_controller/forgotten_password?email=";
    String home_data = Host + "api_controller/welcomepage?client_id=";

    // profile Api's

    String checkServer_api = "https://arresto.in/connectkare/api_controller/is_shutdown";
    String getAdvt = Host + "api_controller/adScreen?client_id=";
    String getcurrency = Host + "api_controller/currency?client_id=";

    String getProfile = Host + "api_controller/user_profile?user_id=";
    String getNotification = Host + "api_controller/get_notifications?device_type=ios&client_id=";
    String update_profile = Host + "api_controller/update_profile";
    String reset_pass = Host + "api_controller/set_forgotten_password";
    String social_login = Host + "api_controller/register_socialaccounts";
    String client_info = Host + "api_controller/client_info?client_id=";
    String chage_password = Host + "api_controller/reset_password";
    String feedBackUrl = Host + "api_controller/user_feedbacks";

    String connection_MSG = "Something is wrong Not connected";

    String product_service = Host + "api_controller/products/";
    String subasset_service = Host + "api_controller/subassets/";
    //     String components_service = Host + "api_controller/components/";
    String components_service = Host + "api_controller/components_new/";
    String ins_parameter_api = Host + "api_controller/asset_assestseries/";
    String preuse_reportList = Host + "api_controller/preuse_reports?client_id=";
    String preuse_report = Host + "api_controller/preuse_webview?client_id=";
    String post_confirmation = Host + "api_controller/preuse_confirmation";
    String inspection_service = Host + "api_controller/mdata?";
    String assigned_sites = Host + "api_controller/siteid?";
    String search_Data = Host + "api_controller/searchSiteidBarcode?";
    String actionProposed_service = Host + "api_controller/actionProposed?actionProposed=";
    String all_actionPropose = Host + "api_controller/actionProposedAll";
    String all_clientinfo = Host + "api_controller/clientdata";
    String all_search_data = Host + "api_controller/search_data";
    String certificateView = Host + "api_controller/reportCertificateView?ref_id=";
    String getLanguage = Host + "asm_api/select_language?language=";
    String getAllAssets = Host + "api_controller/get_assets?client_id=";
    String getAllSeries = Host + "api_controller/get_series?client_id=";
    String getJobCard = Host + "api_controller/get_jobcards?client_id=";
    String getAllSubassets = Host + "api_controller/subassets_list/";
    String getResults = Host + "api_controller/results/";
    String getObservations = Host + "api_controller/observations/";
    String getStandards = Host + "api_controller/standards_list/";

    String getInfection = Host + "api_controller/covid19?client_id=";
    String postInfection = Host + "api_controller/covid19";

    String post_sms_components = Host + "api_controller/sms_components";

//    Tracker api's

    String save_location_api = Host + "api_controller/user_tracks";
    String get_DateTimeLine_api = Host + "api_controller/timeline_dates?user_id=";
    String get_Datelocation_api = Host + "api_controller/user_tracks?user_id=";

//    data upload api's

    String worrkprmit_service = Host + "api_controller/workPermit?";
    String inspection_frag_send = Host + "api_controller/inspection";
    String abinspectionForm_service = Host + "api_controller/inspectionForm?";
    String abinspectionForm_service1 = Host + "api_controller/inspectionForm1?";
    String abinspectionForm_service_v2 = Host + "api_controller/inspectionForm_v2?"; // for ASPL
    String postThermal_service = Host + "api_controller/thermal_inspection";
    String inspectorInformation_send = Host + "api_controller/inspectorInformation?";
    String clientInformation_send = Host + "api_controller/clientInformation?";

    String updateThermal_data = Host + "api_controller/update_thermal_inspection";
    String getThermal_Api = Host + "api_controller/thermal_data?client_id=";

//    Address api's

    String contry_base = Host + "api_controller/country";
    String state_base = Host + "api_controller/state?country_id=";
    String city_base = Host + "api_controller/city?state_id=";

//    webview api's

    String inspector_mngsite = Host + "api_controller/manage_inspector?userID=";
    String inspectorInspectionList = Host + "api_controller/inspectorInspectionList?userID=";
    String productInspection_history = Host + "api_controller/productInspection_history?code=";
    String inspectorInspectionListbyID = Host + "api_controller/inspectorInspectionListbyID?id=";

//    manual api's

    String add_subasset = Host + "api_controller/add_subassets";
    String add_asset = Host + "api_controller/add_assets";
    String add_asset_series = Host + "api_controller/add_series";
    String add_Master = Host + "api_controller/add_mdata";
    String add_Site = Host + "api_controller/add_siteid";
    String add_AssetMaster = Host + "api_controller/register_mdata";
    String search_by_batch = Host + "msearch_api/databybatch?client_id=";
    String karam_manual_insdata = Host + "api_controller/manual_insdata";
    String karam_factory_insdata = Host + "api_controller/update_factory";
    String update_rfid = Host + "api_controller/update_rfid";
    String karam_client_insdata = Host + "api_controller/update_dealer";
    String search_product = Host + "api_controller/search_product_barcode?code=";
    String getMasterSmsSite = Host + "api_controller/series_masterdata?client_id=";

    String replace_api = Host + "api_controller/replace_inspection";
    String approve_api = Host + "inspection_mobile/inspection";
    String pdm_approve_api = Host + "pdm_api/inspection";
    String safety_approve_api = Host + "/asm_api/safety_report_action";
    String breakdown_machine_api = Host + "api_controller/breakdown_machine";

//    history api's

    String history_base = Host + "api_controller/searchHistory/";
    String logs_api = Host + "api_controller/logs_event?userID=";
    String inspection_data_api = Host + "api_controller/get_inspection_data/";

//    KnowledgeTree api's

    String karam_infonet = Host + "api_controller/kdCategory?client_id=";
    String karam_product = Host + "api_controller/kt_search_product?client_id=";
    String itemsInCategory = Host + "api_controller/itemsInCategory?catID=";
    String assetValues = Host + "api_controller/assetValues?type=";
    String connected_clients = Host + "api_controller/connected_clients?client_id=";
    String search_asset = Host + "api_controller/searchProduct?item=";

//   EC api's

    String ec_add_project = Host + "asm_api/ec_projects";
    String ec_update_project = Host + "asm_api/ec_projects_update";
    String Ec_project_list = Host + "asm_api/ec_projects?user_id=";
    String EC_delete_project = Host + "asm_api/ec_projects_del";
    String ec_category = Host + "asm_api/ecCategory";
    String itemsInECCategory = Host + "asm_api/itemsInEcCategory?catID=";
    String Ec_data_post = Host + "asm_api/ec_specification";
    String Ec_get = Host + "asm_api/ec_specification?client_id=";
    String Ec_report = Host + "asm_api/ec_reportlist?client_id=";
    String Ec_quote = Host + "asm_api/quotation_report";
//    String assetValues = Host + "api_controller/assetValues?type=";

//    String search_asset = Host + "api_controller/searchProduct?item=";

    //Scheduler Api

    String add_assets = Host + "webview_api/assets_list?client_id=";
    String add_master = Host + "webview_api/mdata_inspection?client_id=";
    String dealerinfo = Host + "api_controller/dealers?client_id=";
    String register_site = Host + "api_controller/registerSite";
    String get_due_sites = Host + "api_controller/searchRegisterDataCountWithUserId?user_id=";
    String get_childgroups = Host + "api_controller/childgroups?client_id=";
    String get_childgroups_v1 = Host + "api_controller/childgroups_v1?client_id=";//
    String get_child_users = Host + "api_controller/child_users?client_id=";
    String get_group_assets = Host + "api_controller/group_assets?client_id=";
    String deleteRegisteredSite = Host + "api_controller/deleteRegisteredSite?user_id=";
    String insert_schedule = Host + "api_controller/add_scheduler";
    String delete_schedule = Host + "api_controller/delete_schedule?user_id=";
    //     String myasset_api = Host + "api_controller/searchRegisterDataUserId?user_id=";
    String myasset_api = Host + "api_controller/registered_assets?user_id=";
    String get_schedule = Host + "api_controller/get_scheduler_sites?user_id=";
    String assign_user = Host + "api_controller/label_register_assets";
    String search_register_assets = Host + "api_controller/search_register_assets?client_id=";

//  Assets Management API's

    String get_assetInputType = Host + "api_controller/searchCompWithUidBCodeRid?";
    String add_store_api = Host + "asm_api/assign_to_store_v1";
    String return_to_store = Host + "asm_api/return_to_store";
    String add_to_project = Host + "asm_api/assign_to_project";
    String get_store_count = Host + "asm_api/store_count?store_id=";
    String filtrd_storeData = Host + "asm_api/store_list?store_id=";
    String add_project = Host + "asm_api/user_project";
    String edit_project = Host + "asm_api/user_project_edit";
    String ec_sites = Host + "asm_api/ec_sites";
    String asm_delete_project = Host + "asm_api/user_project_delete";
    String project_list = Host + "asm_api/user_projects?user_id=";
    String project_data = Host + "asm_api/project_products?user_id=";
    String checkin_out_api = Host + "asm_api/change_product_status";
    String asst_histry_api = Host + "asm_api/product_history?product_id=";
    String aprove_status_api = Host + "asm_api/u2u_transfer_status";
    String getGroup_Users = Host + "asm_api/group_users?cgrp_id=";
    String getGroup_clients = Host + "asm_api/group_clients?client_id=";
    String getAll_Users = Host + "asm_api/all_users?client_id=";
    String post_attendance = Host + "asm_api/attendance";
    String training_status = Host + "asm_api/training_status";
    String getTodayRopeHistory = Host + "asm_api/rope_history?client_id=";
    String getCheckInOut = Host + "asm_api/checkinout_report?client_id=";

    String datecustom_forms = Host + "asm_api/custom_forms?client_id=";
    String custom_forms = Host + "asm_api/hira_form?client_id=";
    String custom_forms_subItem = Host + "asm_api/get_items";
    String get_custom_forms_data = Host + "asm_api/rope_status?user_id=";
    String post_custom_form = Host + "pdm_api/post_custom_form";
    String update_rope_status = Host + "asm_api/rope_status";
    String asm_signature_upload = Host + "pdm_api/asm_signature_upload";
    String asm_ins_list = Host + "asm_api/asm_training_reports?client_id=";
    String asm_custom_formReportView = Host + "asm_api/custom_form_report?client_id=";
    String asm_custom_formLogView = Host + "asm_api/product_historylist?client_id=";
    String asm_rope_ReportView = Host + "asm_api/asm_rt_report?client_id=";
    String asm_inspecte_forms = Host + "asm_api/asm_report_forms?client_id=";
    String asm_update_ropetime = Host + "asm_api/update_ropetime";

//    Safety  API's

    String get_safety_form = Host + "asm_api/safety_forms?client_id=";
    String post_safety_form = Host + "pdm_api/post_safety_managemant?";
    String safety_report_list = Host + "asm_api/safety_managerList?client_id=";
    String safety_report = Host + "asm_api/safety_management_report?client_id=";
    String safety_view = Host + "asm_api/safety_view?client_id=";

//   PDM API's

    String post_pdm_step = Host + "pdm_api/submit_inspection?";
    String get_pdm_steps = Host + "pdm_api/pdm_steps?asset_code=";
    String post_asset_count = Host + "pdm_api/pdm_asset_count";
    String post_pdm_ins_list = Host + "pdm_api/inspection_list?client_id=";
    String pdm_signature_upload = Host + "pdm_api/pdm_inspection_signature";
    String pdm_pending_report = Host + "pdm_api/pdm_report/";
    String get_maintenance_sites = Host + "api_controller/pdm_register_count?user_id=";

//   Sensor API's
    String calibration_threshold_api = Host + "api_controller/sensor_data?";
    String calibration_threshold_update = Host + "api_controller/sensor_update?";
    String post_SensorVibrations = Host + "api_controller/sensor_threshold";
    String post_SensorNotification = Host + "api_controller/set_sensor_notification";
    String getSensorVibrations = Host + "api_controller/sensor_info?client_id=";
    String attachSensor = Host + "api_controller/sensor_uin";
    String hook_sensor_history = Host + "api_controller/sensor_history?client_id=";

//   Factory Manage Api
    String warehouse_count_api = Host + "api_controller/warehouse_counts?user_id=";
    String create_job_api = Host + "api_controller/create_job";
    String get_rental_jobcards = Host + "api_controller/get_jobs?client_id=";
    String get_rental_users_api = Host + "api_controller/rental_users?client_id=";
    String register_rental_assets = Host + "api_controller/register_rental_assets";
    String return_asset_api = Host + "api_controller/deregister_rental_assets";

// Retrofit methods

    @GET
    Call<String> getRequest(@Url String url);

    @POST
    Call<ResponseBody> PostRequest(@Url String url, @FieldMap HashMap<String, String> keys);

    @Multipart
    @POST
    Call<String> multipartPost(@Url String url, @FieldMap() HashMap<String, String> keys, @PartMap() HashMap<String, File> files);

    @POST
    Call<ResponseBody> objectPost(@Url String url, @Body JSONObject data);

    @POST
    Call<ResponseBody> arrayPost(@Url String url, @Body JSONArray data);

}

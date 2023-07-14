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

package app.com.arresto.arresto_connect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Upload_database {
    private static final String DATABASE_NAME = "upload_DB_Arresto";
    private static final int DATABASE_VERSION = 6;
    private static final String UNIQUE_ID = "unique_id",
            CLIENT_NAME = "client_name", TODAY_DATE = "today_date", WORKPERMIT_NUMBER = "workPermit_number", SITEID_NAME = "siteID_name", PERMITDATE_FROM = "permitDate_from",
            PERMITVALID_UPTO = "permitValid_upto", SITEID_ADDRESS = "siteID_address", WORKPERMIT_LATTITUDE = "workPermit_lattitude", WORKPERMIT_LONGITUDE = "workPermit_longitude",
            WORKPERMIT_ASSET_SERIES = "workPermit_asset_series", WORKPERMIT_BATCH_NO = "workPermit_batch_no", WORKPERMIT_SERIAL_NO = "workPermit_serial_no", HARNESS = "harness",
            HELMET = "helmet", SHOES = "shoes", GLOVES = "gloves", GOGGLE = "goggle", WORK_POSITION = "work_position", EQUIPMENT_USE = "equipment_use",
            WORKER_TRAINED = "worker_trained", WEATHER_GOOD = "weather_good",
            ENOUGH_LIGHT = "enough_light",
            EQUIPMENT_CONDITION = "equipment_condition", PHYSICALLY_FITNESS = "physically_fitness", WORK_AT_HEIGHT = "work_at_height", ALCOHOL_INFLUENCE = "alcohol_influence",
            CLIENT_APPROVAL = "client_approval", DOCUMENTATION_WITH_CLIENT = "documentation_with_client", IMAGE_PATH = "image_path";
    private static final String
            ASSET = "asset", COMP_POSITION = "comp_position", INS_SUBAST_POS = "ins_subast_pos", SUB_ASSET = "sub_asset", OBSERVATION = "observation",
            ACTION_PROPOSED = "action_proposed",
            ACTION_TAKEN = "action_taken", IMAGE_PATES = "images_path";

    private static final String
            REPORTNO = "reportNo", SITEID = "siteID", SITENAME = "siteName", JOBCARD = "jobCard", SMS = "sms",
            TODAYDATE = "todayDate", ASSETSERIES = "assetSeries", PONUMBER = "poNumber", BATCHNUMBER = "batchNumber", SERIALNUMBER = "serialNumber", LATTITUDE = "lattitude",
            LONGITUDE = "longitude", USERID = "userID", INPUTTYPE = "inputType", INPUTVALUE = "inputValue", MASTER_ID = "master_id";

    private static final String
            NAME = "name", DESIGNATION = "designation", SIGNATURE_PATH = "signature_path", STATUS = "status", REMARK = "remark", SPARES = "spares", IS_CONFIRM = "is_confirm";

    private static final String DATABASE_TABLE_WORK_PERMIT = "work_permit";
    private static final String DATABASE_TABLE_ASSET_INSPECTION = "asset_inspection";
    private static final String DATABASE_TABLE_INPCTD_ASST = "inpctd_asst";
    private static final String DATABASE_TABLE_CLIENT_TBL = "client_tbl";
    private static final String DATABASE_TABLE_INSPCTR_TBL = "inspctr_tbl";
    private static final String DATABASE_TABLE_INSPECTION_FRG_TBL = "inspection_frgmnt_tbl";

    private static final String DATABASE_CREATE_WORK_PERMIT = "create table work_permit (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text, client_name text,today_date text," +
            "workPermit_number text,siteID_name text,permitDate_from text,permitValid_upto text,siteID_address text,workPermit_lattitude text,workPermit_longitude text," +
            "workPermit_asset_series text,workPermit_batch_no text,workPermit_serial_no text,harness text,helmet text,shoes text,gloves text,goggle text,work_position text," +
            "equipment_use text,worker_trained text,weather_good text,enough_light text,equipment_condition text,physically_fitness text,work_at_height text" +
            ",alcohol_influence text,client_approval text,documentation_with_client text,image_path text);";

    private static final String DATABASE_CREATE_INSPECTION_FRG_TBL = "create table inspection_frgmnt_tbl (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text,reportNo text,siteID text," +
            "siteName text,jobCard text,sms text,todayDate text,assetSeries text,poNumber text,batchNumber text,serialNumber text," +
            "lattitude text,longitude text,userID text,inputType text,inputValue text,master_id text);";

    private static final String DATABASE_CREATE_ASSET_INSPECTION = "create table asset_inspection (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text, asset text,comp_position integer,ins_subast_pos integer,sub_asset text," +
            "observation text,action_proposed text,action_taken text,images_path text,status text,remark text);";

    private static final String DATABASE_CREATE_INSPECTD_ASSET = "create table inpctd_asst (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text, comp_position integer);";

    private static final String DATABASE_CREATE_INSPCTR_TBL = "create table inspctr_tbl (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text, name text,designation text," +
            "signature_path text,remark text,spares text,is_confirm integer);";
    private static final String DATABASE_CREATE_CLIENT_TBL = "create table client_tbl (id INTEGER PRIMARY KEY AUTOINCREMENT,unique_id text, name text,designation text," +
            "signature_path text,remark text);";

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public Upload_database(Context ctx) {

//        File datbase_dir = new File(datbase_directory);
//        if (!datbase_dir.exists())
//            datbase_dir.mkdirs();

        DBHelper = new DatabaseHelper(ctx);
    }

    //---open SQLite DB---
    public Upload_database open() throws SQLException {
//       Log.e("path",""+context.getDatabasePath(DATABASE_NAME).toString());
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---close SQLite DB---
    public void close() {
        DBHelper.close();
    }

    public void insert_workpermit_tbl(
            String unique_id, String client_name, String curr_date, String workPermit_number, String sub_site_id, String s, String toString, String site,
            String lat, String longi, String asset, String string, String s1, String slctd_harnss, String slctd_helmet, String slctd_safty, String slctd_gloves,
            String slctd_goggle, String slctd_wrk_position, String slctd_ppe_lst, String slctd_traning1, String slctd_situation1, String slctd_situation2,
            String slctd_equipment1, String slctd_medical1, String slctd_medical2, String slctd_medical3, String slctd_misclncs1, String slctd_misclncs2, String image_path) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, unique_id);
        initialValues.put(CLIENT_NAME, client_name);
        initialValues.put(TODAY_DATE, curr_date);
        initialValues.put(WORKPERMIT_NUMBER, workPermit_number);
        initialValues.put(SITEID_NAME, sub_site_id);
        initialValues.put(PERMITDATE_FROM, s);
        initialValues.put(PERMITVALID_UPTO, toString);
        initialValues.put(SITEID_ADDRESS, site);
        initialValues.put(WORKPERMIT_LATTITUDE, lat);
        initialValues.put(WORKPERMIT_LONGITUDE, longi);
        initialValues.put(WORKPERMIT_ASSET_SERIES, asset);
        initialValues.put(WORKPERMIT_BATCH_NO, string);
        initialValues.put(WORKPERMIT_SERIAL_NO, s1);
        initialValues.put(HARNESS, slctd_harnss);
        initialValues.put(HELMET, slctd_helmet);
        initialValues.put(SHOES, slctd_safty);
        initialValues.put(GLOVES, slctd_gloves);
        initialValues.put(GOGGLE, slctd_goggle);
        initialValues.put(WORK_POSITION, slctd_wrk_position);
        initialValues.put(EQUIPMENT_USE, slctd_ppe_lst);
        initialValues.put(WORKER_TRAINED, slctd_traning1);
        initialValues.put(WEATHER_GOOD, slctd_situation1);
        initialValues.put(ENOUGH_LIGHT, slctd_situation2);
        initialValues.put(EQUIPMENT_CONDITION, slctd_equipment1);
        initialValues.put(PHYSICALLY_FITNESS, slctd_medical1);
        initialValues.put(WORK_AT_HEIGHT, slctd_medical2);
        initialValues.put(ALCOHOL_INFLUENCE, slctd_medical3);
        initialValues.put(CLIENT_APPROVAL, slctd_misclncs1);
        initialValues.put(DOCUMENTATION_WITH_CLIENT, slctd_misclncs2);
        initialValues.put(IMAGE_PATH, image_path);

        db.insert(DATABASE_TABLE_WORK_PERMIT, null, initialValues);
    }

    public void insert_asset_inspection(String s, String s1, int comp_position, int ins_subast_pos, String s2, String s3, String s4, String s5, String s6, String status, String remark) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, s);
        initialValues.put(ASSET, s1);
        initialValues.put(COMP_POSITION, comp_position);
        initialValues.put(INS_SUBAST_POS, ins_subast_pos);
        initialValues.put(SUB_ASSET, s2);
        initialValues.put(OBSERVATION, s3);
        initialValues.put(ACTION_PROPOSED, s4);
        initialValues.put(ACTION_TAKEN, s5);
        initialValues.put(IMAGE_PATES, s6);
        initialValues.put(STATUS, status);
        initialValues.put(REMARK, remark);
//        initialValues.put(SPARES, spares);
        db.insert(DATABASE_TABLE_ASSET_INSPECTION, null, initialValues);
    }

    public void insert_asset_inspctd(String s, int comp_position) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, s);
        initialValues.put(COMP_POSITION, comp_position);
        db.insert(DATABASE_TABLE_INPCTD_ASST, null, initialValues);
    }

    public void insert_inspection_frg_tbl(
            String uneque_id, String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10,
            String s11, String s12, String s13, String s14, String s15) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, uneque_id);
        initialValues.put(REPORTNO, s);
        initialValues.put(SITEID, s1);
        initialValues.put(SITENAME, s2);
        initialValues.put(JOBCARD, s3);
        initialValues.put(SMS, s4);
        initialValues.put(TODAYDATE, s5);
        initialValues.put(ASSETSERIES, s6);
        initialValues.put(PONUMBER, s7);
        initialValues.put(BATCHNUMBER, s8);
        initialValues.put(SERIALNUMBER, s9);
        initialValues.put(LATTITUDE, s10);
        initialValues.put(LONGITUDE, s11);
        initialValues.put(USERID, s12);
        initialValues.put(INPUTTYPE, s13);
        initialValues.put(INPUTVALUE, s14);
        initialValues.put(MASTER_ID, s15);
        db.insert(DATABASE_TABLE_INSPECTION_FRG_TBL, null, initialValues);
    }

    public void update_asset(String unique_id, String assetSeries) {
        db.execSQL("UPDATE inspection_frgmnt_tbl SET assetSeries = '" + assetSeries + "' WHERE unique_id ='" + unique_id + "'");
        db.execSQL("UPDATE work_permit SET workPermit_asset_series = '" + assetSeries + "' WHERE unique_id ='" + unique_id + "'");
    }

    public void insert_inspctr_tbl(String uneque_id, String s1, String s2, String remark, String s3, String spares, boolean checked) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, uneque_id);
        initialValues.put(NAME, s1);
        initialValues.put(DESIGNATION, s2);
        initialValues.put(SIGNATURE_PATH, s3);
        initialValues.put(REMARK, remark);
        initialValues.put(SPARES, spares);
        if (checked)
            initialValues.put(IS_CONFIRM, 1);
        else
            initialValues.put(IS_CONFIRM, 0);

        db.insert(DATABASE_TABLE_INSPCTR_TBL, null, initialValues);
    }

    public void insert_client_tbl(String uneque_id, String s1, String s2, String s3, String s4) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(UNIQUE_ID, uneque_id);
        initialValues.put(NAME, s1);
        initialValues.put(DESIGNATION, s2);
        initialValues.put(SIGNATURE_PATH, s3);
        initialValues.put(REMARK, s4);
        db.insert(DATABASE_TABLE_CLIENT_TBL, null, initialValues);
    }

    public void delete_wrkprmit_row(String name) {
        db.delete(DATABASE_TABLE_WORK_PERMIT, UNIQUE_ID + "='" + name + "'", null);
    }
    //---Delete single row data from table in SQLite DB---

    public void delete_frgtbl_row(String name) {
        db.delete(DATABASE_TABLE_INSPECTION_FRG_TBL, UNIQUE_ID + "='" + name + "'", null);
    }

    public void delete_asset_row(String name) {
        db.delete(DATABASE_TABLE_ASSET_INSPECTION, UNIQUE_ID + "='" + name + "'", null);
    }

    public void delete_asset_inspctd(String name) {
        db.delete(DATABASE_TABLE_INPCTD_ASST, UNIQUE_ID + "='" + name + "'", null);
    }

    public void delete_inspctr_row(String name) {
        db.delete(DATABASE_TABLE_INSPCTR_TBL, UNIQUE_ID + "='" + name + "'", null);
    }

    public void delete_client_row(String name) {
        db.delete(DATABASE_TABLE_CLIENT_TBL, UNIQUE_ID + "='" + name + "'", null);
    }

    public Cursor getSingle_Rowdata(String query) {
        return db.rawQuery(query, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//            super(context, datbase_directory + DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_WORK_PERMIT);
                db.execSQL(DATABASE_CREATE_INSPECTION_FRG_TBL);
                db.execSQL(DATABASE_CREATE_ASSET_INSPECTION);
                db.execSQL(DATABASE_CREATE_INSPECTD_ASSET);
                db.execSQL(DATABASE_CREATE_INSPCTR_TBL);
                db.execSQL(DATABASE_CREATE_CLIENT_TBL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Database upload", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_WORK_PERMIT);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INSPECTION_FRG_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ASSET_INSPECTION);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INPCTD_ASST);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INSPCTR_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CLIENT_TBL);
            onCreate(db);
        }
    }
}
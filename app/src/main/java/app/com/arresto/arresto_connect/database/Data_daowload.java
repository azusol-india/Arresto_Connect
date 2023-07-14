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
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Data_daowload {
    private static final String
            COMPONENT_CODE = "code", COMPONENT_DESCRIPTION = "component_description", UOM = "uom", COMPONENT_SUB_ASSETS = "component_sub_assets",
            EXPECTED_RESULT = "expected_result", COMPONENT_OBSRVSN = "observation", COMPONENT_INSPECTIONTYPE = "inspection_type",
            PASS_IMAGEPATH = "pass_imagepath", FAIL_IMAGEPATH = "fail_imagepath", REPAIR_IMAGEPATH = "repair_imagepath";

    private static final String SUB_ASSETS_CODE = "code", SUB_DESCRIPTION = "sub_description", SUB_UOM = "sub_uom",
            SUB_INSPECTION = "sub_inspection", SUB_RESULT = "sub_result", SUB_OBSERVATION = "sub_observation",
            SUB_PASS_IMAGEPATH = "sub_pass_imagepath", SUB_FAIL_IMAGEPATH = "sub_fail_imagepath", SUB_REPAIR_IMAGEPATH = "sub_repair_imagepath";

    private static final String
            USER_ID = "user_id", OBSERVATION_ID = "observation_id", ACTION_ID = "action_id", ACTION_PROPOSE = "action_propose", MESSAGE = "message",
            STATUS = "status", MESSAGE_TIME = "message_time";

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "download_DB_Arresto";

    private static final String DATABASE_TABLE_SUBASSET = "subasset",
            DATABASE_TABLE_ACTIONPROPOSE = "action_propose", DATABASE_TABLE_COMPONENTS = "components", NOTIFICATION_TBL = "notification_tbl";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE_COMPONENT =
            "create table components (id INTEGER PRIMARY KEY AUTOINCREMENT,code text, component_description text, " +
                    "component_sub_assets text,uom text, inspection_type text, expected_result text, observation text, pass_imagepath text, fail_imagepath text, repair_imagepath text);";
    private static final String DATABASE_CREATE_SUBASSET =
            "create table subasset (id INTEGER PRIMARY KEY AUTOINCREMENT,code text, sub_description text, " +
                    "sub_uom text,sub_inspection text, sub_result text, sub_observation text, sub_pass_imagepath text, sub_fail_imagepath text, sub_repair_imagepath text);";

    private static final String DATABASE_CREATE_ACTIONPROPOSE = "create table action_propose (id INTEGER PRIMARY KEY AUTOINCREMENT,observation_id text," +
            " action_id text,action_propose text);";
    private static final String CREATE_NOTIFICATION_TABLE = "create table notification_tbl (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id text,message text,status text,message_time text);";

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public Data_daowload(Context ctx) {
        DBHelper = new DatabaseHelper(ctx);
    }

    //---open SQLite DB---
    public Data_daowload open() throws SQLException {
//       Log.e("path",""+context.getDatabasePath(DATABASE_NAME).toString());
        db = DBHelper.getWritableDatabase();

        return this;
    }

    //---close SQLite DB---
    public void close() {
        DBHelper.close();
    }

    public void insert_component(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COMPONENT_CODE, s);
        initialValues.put(COMPONENT_DESCRIPTION, s1);
        initialValues.put(COMPONENT_SUB_ASSETS, s2);
        initialValues.put(UOM, s3);
        initialValues.put(COMPONENT_INSPECTIONTYPE, s4);
        initialValues.put(EXPECTED_RESULT, s5);
        initialValues.put(COMPONENT_OBSRVSN, s6);
        initialValues.put(PASS_IMAGEPATH, s7);
        initialValues.put(FAIL_IMAGEPATH, s8);
        initialValues.put(REPAIR_IMAGEPATH, s9);
        db.insert(DATABASE_TABLE_COMPONENTS, null, initialValues);
    }

    //---insert data into SQLite DB---

    public void insert_subasset(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(SUB_ASSETS_CODE, s);
        initialValues.put(SUB_DESCRIPTION, s1);
        initialValues.put(SUB_UOM, s2);
        initialValues.put(SUB_INSPECTION, s3);
        initialValues.put(SUB_RESULT, s4);
        initialValues.put(SUB_OBSERVATION, s5);
        initialValues.put(SUB_PASS_IMAGEPATH, s6);
        initialValues.put(SUB_FAIL_IMAGEPATH, s7);
        initialValues.put(SUB_REPAIR_IMAGEPATH, s8);
        db.insert(DATABASE_TABLE_SUBASSET, null, initialValues);
    }

    public void insert_action_prpos(String observ_id, String excerpt_id, String excerpt) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(OBSERVATION_ID, observ_id);
        initialValues.put(ACTION_ID, excerpt_id);
        initialValues.put(ACTION_PROPOSE, excerpt);
        db.insert(DATABASE_TABLE_ACTIONPROPOSE, null, initialValues);
    }

    public long insert_notification(String user_id, String message, String status, String message_time) {
        Log.e("data ", "  insert now  " + user_id + "  \n  " + status);
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, user_id);
        cv.put(MESSAGE, message);
        cv.put(STATUS, status);
        cv.put(MESSAGE_TIME, message_time);
        return db.insert(NOTIFICATION_TBL, null, cv);
    }

    public void change_msg_status(String id, String action) {
        db.execSQL("UPDATE notification_tbl SET status = '" + action + "' WHERE id ='" + id + "'");
    }

    //---Delete All data from table in SQLite DB---
    public void deleteAll_component() {
        db.delete(DATABASE_TABLE_COMPONENTS, null, null);
    }

    public void deleteAll_subaset() {
        db.delete(DATABASE_TABLE_SUBASSET, null, null);
    }

    public void deleteAll_acprpos() {
        db.delete(DATABASE_TABLE_ACTIONPROPOSE, null, null);
    }


    public Cursor getSingle_Rowdata(String query) {
        return db.rawQuery(query, null);
    }

    public long checktblsize(String tablename) {
        return DatabaseUtils.queryNumEntries(db, tablename);
    }

    public long DISTINCTtblsize(String tablename) {
        return DatabaseUtils.longForQuery(db, "select count(DISTINCT observation_id) from " + tablename, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//            super(context, datbase_directory + DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_COMPONENT);
                db.execSQL(DATABASE_CREATE_SUBASSET);
                db.execSQL(DATABASE_CREATE_ACTIONPROPOSE);
                db.execSQL(CREATE_NOTIFICATION_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_COMPONENTS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SUBASSET);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ACTIONPROPOSE);
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TBL);
            onCreate(db);
        }
    }
}
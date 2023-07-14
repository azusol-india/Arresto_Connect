/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import app.com.arresto.arresto_connect.database.ec_tables.Category_Table;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;
import app.com.arresto.arresto_connect.database.factory_tables.FactoryMasterTable;
import app.com.arresto.arresto_connect.database.factory_tables.Factory_BachTable;
import app.com.arresto.arresto_connect.database.inspection_tables.Asset_Positions_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Components_table;
import app.com.arresto.arresto_connect.database.inspection_tables.InspectionSignature_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Detail_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.ThermalAsset_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.WorkPermitTable;
import app.com.arresto.arresto_connect.database.pdm_tables.Asset_steps_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Pdm_Inspected_steps_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Signature_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Step_subitem_table;


@Database(entities = {Language_table.class, WorkPermitTable.class, Inspection_Detail_Table.class, Inspection_Table.class,ThermalAsset_Table.class,
        Asset_Positions_Table.class, InspectionSignature_Table.class, Pdm_Inspected_steps_table.class, Asset_steps_table.class, Master_data_table.class,
        Sites_data_table.class, Components_table.class, Step_subitem_table.class, Signature_table.class, EC_productsTable.class,
        Category_Table.class, Project_Boq_table.class, Factory_BachTable.class, FactoryMasterTable.class}, version = 16)

public abstract class AppDatabase extends RoomDatabase {
    public abstract Language_table.LanguageDao getLanguageDao();

    public abstract WorkPermitTable.WorkPermit_Dao getWorkPermit_Dao();

    public abstract Inspection_Detail_Table.Inspection_Detail_Dao getInspection_Detail_Dao();

    public abstract Inspection_Table.Inspection_Asset_Dao getInspection_Asset_dao();

    public abstract ThermalAsset_Table.ThermalAsset_Dao getThermalAsset_dao();

    public abstract Asset_Positions_Table.Asset_Position_Dao getAsset_position_dao();

    public abstract InspectionSignature_Table.InspectionSignature_Dao getInspectionSignature_Dao();

    public abstract Pdm_Inspected_steps_table.Pdm_stepsDao getPdm_stepsDao();

    public abstract Asset_steps_table.Asset_stepsDao getAsset_stepsDao();

    public abstract Sites_data_table.Sites_data_Dao getSites_data_Dao();

    public abstract Master_data_table.Master_data_Dao getMaster_dataDao();

    public abstract Components_table.Components_Dao getComponents_Dao();

    public abstract Step_subitem_table.Step_subitem_Dao getStep_subitem_Dao();

    public abstract Signature_table.Signature_Dao getSignature_Dao();

    public abstract EC_productsTable.EC_productsTable_Dao getEC_products_Dao();

    public abstract Category_Table.Category_Table_Dao getCategory_Table_Dao();

    public abstract Project_Boq_table.Project_Boq_Dao getProject_Boq_Dao();

    public abstract Factory_BachTable.Factory_BachTable_Dao getFactory_bachTable_dao();

    public abstract FactoryMasterTable.FactoryMasterTable_Dao getFactoryMasterTable_dao();

//    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//
//            database.execSQL("DROP TABLE IF EXISTS language_table");
//            database.execSQL("DROP TABLE IF EXISTS pdm_inspected_steps_table");
//            database.execSQL("DROP TABLE IF EXISTS asset_steps_table");
//            database.execSQL("DROP TABLE IF EXISTS master_data_table");
//
//            database.execSQL("CREATE TABLE `language_table` (`id` INTEGER, `name` TEXT, PRIMARY KEY(`id`))");
//        }
//    };
}

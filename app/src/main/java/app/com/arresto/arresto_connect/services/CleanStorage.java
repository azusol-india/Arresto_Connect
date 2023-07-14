package app.com.arresto.arresto_connect.services;

import static app.com.arresto.arresto_connect.constants.AppUtils.deleteRecursive;
import static app.com.arresto.arresto_connect.constants.AppUtils.delete_uploadedPdm_data;
import static app.com.arresto.arresto_connect.constants.AppUtils.delete_uploadedsite_data;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;

public class CleanStorage extends Service {
    private static final String TAG = "MyService";
    AppDatabase db;

    @Override
    public void onCreate() {
        startCommand();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    private void startCommand() {
        if (user_id != null && !user_id.equals("")) {
            List<Sites_data_table> allSites = db.getSites_data_Dao().getAllInspectedSites(user_id, client_id);
            db = AppController.getInstance().getDatabase();
            Calendar daysBefore = Calendar.getInstance();
            daysBefore.add(Calendar.DAY_OF_YEAR, -4);
//            daysBefore.add(Calendar.MINUTE, -5);
            cleanInspectionData(allSites,daysBefore);
            cleanPdmData(allSites,daysBefore);
        }
        stopSelf();
    }


    public void cleanInspectionData(List<Sites_data_table> allInspectedSites, Calendar daysBefore) {
        if (allInspectedSites != null && allInspectedSites.size() > 0) {
            for (Sites_data_table sitesDataTable : allInspectedSites) {
                // Delete 3 days old sites and sites those uploaded
                if (sitesDataTable.getIsUploaded() != null && sitesDataTable.getIsUploaded().equals("yes")
                        && sitesDataTable.getInspectionTime() < daysBefore.getTimeInMillis()) {
                    delete_uploadedsite_data(sitesDataTable.getUnique_id(), "");
                    Log.d("Data deleted", " site " + sitesDataTable.getUnique_id());
                }
            }
        }
    }

    private void cleanPdmData(List<Sites_data_table> allPdmSites, Calendar daysBefore) {
        for (Sites_data_table pdmSite : allPdmSites) {
            // Delete 3 days old sites and sites those uploaded
            if (pdmSite.getIsUploaded() != null && pdmSite.getIsUploaded().equals("yes")
                    && pdmSite.getInspectionTime() < daysBefore.getTimeInMillis()) {
                delete_uploadedPdm_data(pdmSite.getUnique_id(), "");
                Log.d("Data deleted", " site " + pdmSite.getUnique_id());
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

}
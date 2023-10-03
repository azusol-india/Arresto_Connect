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

package app.com.arresto.arresto_connect.appcontroller;

import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.room.Room;

import com.bumptech.glide.request.target.ViewTarget;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.FontsOverride;
import app.com.arresto.arresto_connect.data.Preferences;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.ToStringConverterFactory;
import retrofit2.Retrofit;

@ReportsCrashes(formKey = "", mailTo = "satyam@arresto.in,varun@flashonmind.com",
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.SILENT)
public class AppController extends Application {

    private static String Deeplink = "";
    public static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(LanguageHelper.onAttach(newBase, LanguageHelper.getLanguage(newBase)));
        ACRA.init(this);
    }

    public AppDatabase getDatabase() {
        return database;
    }

    AppDatabase database;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
//        FontsOverride.setDefaultAssetFont(this, "DEFAULT", "fonts/roboto.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", R.font.helvetica_font);
        database = Room.databaseBuilder(this, AppDatabase.class, "db_arresto").fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        mInstance = this;
        mPrefrence = Preferences.getInstance(getApplicationContext());
        ViewTarget.setTagId(R.id.glide_tag);

        retrofit = new Retrofit.Builder()
                .baseUrl(All_Api.Host)
                .addConverterFactory(new ToStringConverterFactory())
//                 .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getApplicationContext().registerReceiver(broadCastReceiver, intentFilter);
    }

//    private String BLE_PIN = "123456";
    private String BLE_PIN = "000000";
//    private String BLE_PIN = "000012";

    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevice.setPin(BLE_PIN.getBytes());
//                abortBroadcast();
                Log.e(TAG, "Auto-entering pin: " + BLE_PIN);
            }
        }
    };

    public static String get_Deeplink() {
        return Deeplink;
    }

    public static void set_Deeplink(String is_Deeplink) {
        AppController.Deeplink = is_Deeplink;
    }

}
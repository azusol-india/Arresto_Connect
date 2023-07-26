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

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.NFC;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.SCHEDULE_EXACT_ALARM;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class Check_permissions {
//    ,BLUETOOTH_SCAN
    public static String[] CAMERA_STORAGE_PERMISSIONS = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA};
    public static String[] CAMERA_STORAGE_PERMISSIONS_10 = {ACCESS_MEDIA_LOCATION, CAMERA};
    public static
    String[] OTHER_PERMISSIONS = {WAKE_LOCK, NFC, RECEIVE_BOOT_COMPLETED,
            READ_PHONE_STATE, ACCESS_WIFI_STATE, ACCESS_NETWORK_STATE, SCHEDULE_EXACT_ALARM};

    public static String[] LOCATION_PERMISSIONS = {ACCESS_FINE_LOCATION};

    public static boolean hasPermissions(Activity activity, String[] PERMISSIONS) {
        if (SDK_INT >= Build.VERSION_CODES.M && activity != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void request_permissions(Activity activity, String[] PERMISSIONS) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
    }

    //    public static void request_LocationPermission(Activity activity) {
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("This app needs background location access");
//                    builder.setMessage("Please grant location access so this app can detect sites in the background.");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1011);
//                        }
//                    });
//                    builder.show();
//                }
//        } else {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1012);
//            }
//        }
//    }

 /*   public static void request_LocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("This app needs background location access");
                    builder.setMessage("Please grant location access so this app can detect sites in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1011);
                        }
                    });
                    builder.show();
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover sites in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            }
                        });
                        builder.show();
                    }
                }
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1012);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover sites.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        // This will take the user to a page where they have to click twice to drill down to grant the permission
                        activity.startActivity(intent);
                    }
                });
                builder.show();
            }
        }
    }
    */


    public static void request_LocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("This app needs background location access");
                    builder.setMessage("Please grant location access so this app can detect sites in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1011);
                        }
                    });
                    builder.show();
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover sites in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            }
                        });
                        builder.show();
                    }
                }
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1012);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover sites.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        // This will take the user to a page where they have to click twice to drill down to grant the permission
                        activity.startActivity(intent);
                    }
                });
                builder.show();
            }
        }
    }

    public static void addAutoStartup(Activity activity) {

//        try {
//            Intent intent = new Intent();
//            String manufacturer = android.os.Build.MANUFACTURER;
//            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
//                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//            }
//
//            List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            if  (list.size() > 0) {
//                activity.startActivity(intent);
//            }
//        } catch (Exception e) {
//            Log.e("exc" , String.valueOf(e));
//        }
    }
}

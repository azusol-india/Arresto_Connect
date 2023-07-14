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

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER;
import static app.com.arresto.Flavor_Id.FlavourInfo.server_alert;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ALL_LANGUAGES;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CURRENT_LANGUAGE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.INSPECTED_PDM;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_LOGIN;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.current_language;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.com.arresto.Flavor_Id.FlavourInfo;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.custom_views.switch_lib.IconSwitch;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.Language_table;
import app.com.arresto.arresto_connect.database.ec_tables.Category_Table;
import app.com.arresto.arresto_connect.interfaces.DelayCallback;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Main_Fragment;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class AppUtils {

    public static boolean isLogin() {
        return mPrefrence.getBoolean(IS_LOGIN);
    }

    public static int DpToPixel(int dp) {
//        context.getResources().getDisplayMetrics().density
        return (int) (dp * AppController.getInstance().getResources().getDisplayMetrics().density);
    }

    public static int getDisplayWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

    public static DisplayMetrics getDisplaySize(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static void delay(double secs, final DelayCallback delayCallback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, (long) (secs * 1000)); // afterDelay will be executed after (secs*1000) milliseconds.
    }
    //todo crash check
    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(contentUri, "r");
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    FileInputStream inputStream = new FileInputStream(fileDescriptor);
                    File file = new File(activity.getCacheDir(), "temp_file");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();
                    parcelFileDescriptor.close();
                    return file.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null) {
                try {
                    int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                    if (cursor.moveToFirst()) {
                        String filePath = cursor.getString(column_index);
                        if (filePath != null) {
                            return filePath;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }

// If all else fails, return the path obtained from content URI
        return contentUri.getPath();
        /*try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }*/
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        return gsonBuilder.create();

    }

//

    public static void setupUI(View view, final Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e("Service status", "Running");
                return true;
            }
        }
        Log.e("Service status", "Not running");
        return false;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            String packageName = context.getApplicationContext().getPackageName();

            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }

    public static String getResString(String aString) {
        Context context = AppController.getInstance();
        if (!Static_values.current_language.equals("english") && Static_values.language_data.has(aString)) {
            try {
                return Static_values.language_data.getString(aString);
            } catch (JSONException e) {
                e.printStackTrace();
                int resId = context.getResources().getIdentifier(aString, "string", context.getPackageName());
                return context.getString(resId);
            }
        } else {
            try {
                int resId = context.getResources().getIdentifier(aString, "string", context.getPackageName());
                return context.getString(resId);
            } catch (Exception e) {
                return "";
            }
        }

    }

    public static int getResSize(int resId) {
        return (int) AppController.getInstance().getResources().getDimension(resId);
    }

    public static int getResColor(int resId) {
        return ContextCompat.getColor(AppController.getInstance(), resId);
    }

    public static Drawable getResDrawable(int resId) {
        return ContextCompat.getDrawable(AppController.getInstance(), resId);
    }

    public static void show_snak(Activity activity, String msg) {
        if (activity != null)
            Snackbar.make(activity.findViewById(android.R.id.content), "" + msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showInputMethod(Activity activity, View focus) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(focus, 0);
        }
    }

    public static boolean isNetworkAvailable(Context activity) {
        if (activity != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(156), rnd.nextInt(256));
    }

    public Bitmap check_rotate(String image_path) {
        Bitmap bitmap1;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap1 = BitmapFactory.decodeFile(image_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap1, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap1, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap1, 270);
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap1;
            default:
                return bitmap1;
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }


    public static File getfile(Bitmap bm, String fileName) {
        final String dirPath = directory + "Screenshots/";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void Change_language(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.lbl_slct_lang);
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);

//        final ListView lvLangs = dialog.findViewById(R.id.langusge_list);
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        List<String> sLanguages = mPrefrence.getArray_Data(ALL_LANGUAGES);
        String cr_lang = mPrefrence.getData(CURRENT_LANGUAGE);
        CustomRecyclerAdapter ad;
        if (sLanguages.size() > 0) {
            int lastSelectedIndex = sLanguages.indexOf(cr_lang);
            ad = new CustomRecyclerAdapter(activity, sLanguages);
            ad.lastSelected = lastSelectedIndex;
            lvLangs.setAdapter(ad);
            dialog.show();
        } else {
            ad = null;
            show_snak(activity, getResString("lbl_language_login"));
        }
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null && ad.lastSelected > -1) {
                    final String t = sLanguages.get(ad.lastSelected);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (t.equalsIgnoreCase("english")) {
                                mPrefrence.saveData(CURRENT_LANGUAGE, t);
                                relaunch(activity);
                            } else
                                getLanguage(activity, t, null);
                        }
                    }, 300);
                }
                dialog.dismiss();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public static void getLanguage(final Activity activity, final String key, final Handler is_data) {
        NetworkRequest.getLanguage(activity, key, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj != null && (msg.obj.toString()).equals("true")) {
                    mPrefrence.saveData(CURRENT_LANGUAGE, key);
                    if (is_data != null) {
                        AppUtils.update_language_data();
                        Message mesg = new Message();
                        mesg.obj = "true";
                        is_data.sendMessage(mesg);
                    } else {
                        relaunch(activity);
                    }
                }
            }
        });
    }

    private static void relaunch(Activity activity) {
        update_language_data();
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);

//        Intent intent = new Intent(activity, clas);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        activity.startActivity(intent);
//        Runtime.getRuntime().exit(0);
//        activity.finish();
    }


    public static void update_language_data() {
        if (mPrefrence.getData(CURRENT_LANGUAGE) != null && !mPrefrence.getData(CURRENT_LANGUAGE).isEmpty()) {
            current_language = mPrefrence.getData(CURRENT_LANGUAGE);
            DataHolder_Model.getInstance().setHomeModels(null);
            if (current_language.equalsIgnoreCase("english")) {
                Static_values.language_data = new JSONObject();
                return;
            }
            Language_table data = AppController.getInstance().getDatabase().getLanguageDao().getLanguage(current_language);
            if (data != null) {
                try {
                    Static_values.language_data = new JSONObject(data.getLanguage_data());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String Image_toBase64(String path) {
        if (!path.equals("")) {
            Bitmap bm = BitmapFactory.decodeFile(path);
            if (bm != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                return Base64.encodeToString(baos.toByteArray(), Base64.CRLF);
            }else return "";
        } else return "";
    }

    public static byte[] Image_toBytes(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }

    public static String bytes_toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.CRLF);
    }

    public static void group_email_alert(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Info");
        builder.setMessage(getResString("lbl_competent_person_email"));
        builder.setPositiveButton(getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                builder.
            }
        });
        builder.show();
    }

    public static void server_alert(final Activity activity) {
        final Dialog builder = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.server_alert_dialog, null);
        TextView html_tv = dialoglayout.findViewById(R.id.html_tv);
//        html_tv.setText(Html.fromHtml(server_alert, null, new MyTagHandler()));
        WebView web_view = dialoglayout.findViewById(R.id.web_view);
        WebSettings settings = web_view.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultFontSize(30);
        web_view.loadData(server_alert, "text/html", "utf-8");
        (dialoglayout.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                activity.finishAffinity();
                System.exit(0);
            }
        });
        builder.setContentView(dialoglayout);
        builder.setCancelable(false);
        builder.show();
        Window window = builder.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static void app_close_alert(final Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setIcon(R.drawable.error)
                .setTitle(getResString("lbl_close_titl"))
                .setMessage(getResString("lbl_close_app_msg"))
                .setPositiveButton(getResString("lbl_yes"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton(getResString("lbl_no"), null)
                .show();
    }

    public static void create_addAstDialog(final Main_Fragment fragment) { // for user
        Activity activity = fragment.getActivity();
        if (DataHolder_Model.getInstance().getProject_models() != null && DataHolder_Model.getInstance().getProject_models().size() > 0) {
            final android.app.AlertDialog builder = new android.app.AlertDialog.Builder(activity).create();
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.usr_addto_store_lay, null);

            TextView add_btn = dialoglayout.findViewById(R.id.add_btn);
            final Spinner spinner = dialoglayout.findViewById(R.id.prjct_spnr);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, DataHolder_Model.getInstance().getProject_models());
            spinner.setAdapter(arrayAdapter);

            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.add_to_Store(((Project_Model) spinner.getSelectedItem()).getUp_id());
                    builder.cancel();
                }
            });
            builder.setView(dialoglayout);
            builder.show();
        } else {
            show_snak(activity, "Please create a project");
        }

    }

    public static String extractYTId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }


    public static Chronometer set_Timer(final Chronometer timeElapsed, long timeWhenStopped) {
        timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String up_time = String.format("%02d:%02d:%02d", h, m, s);
                timeElapsed.setText(up_time);
            }
        });
        timeElapsed.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timeElapsed.start();
        return timeElapsed;
    }

    public static void revealToolbar(IconSwitch iconSwitch, Point revealCenter, View content, int duration) {
        iconSwitch.getThumbCenter(revealCenter);
        revealCenter.set(revealCenter.x + iconSwitch.getLeft(), revealCenter.y + iconSwitch.getTop());
        ViewAnimationUtils.createCircularReveal(content, revealCenter.x, revealCenter.y, iconSwitch.getHeight(), content.getWidth())
                .setDuration(duration)
                .start();
    }

    public static ValueAnimator createArgbAnimator(int leftColor, int rightColor, int duration) {
        ValueAnimator animator = ValueAnimator.ofArgb(leftColor, rightColor);
        animator.setDuration(duration);
        return animator;
    }


    public static void showUIN_Dialog(BaseActivity activity, Handler handler) { // for user
        final Dialog dialog = new Dialog(activity, R.style.theme_dialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_uin);
        if (!logo_url.equals("")) {
            ImageView logo_img = dialog.findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
        final EditText edt_uin = dialog.findViewById(R.id.edt_dialog);

        dialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edt_uin.getText().toString();
                if (!FlavourInfo.Fl_CLIENT_ID.equals("376") && !client_id.equals("376"))
                    text = text.replaceFirst("^0+(?!$)", "");
                final Message mesg = new Message();
                mesg.obj = text;
                handler.sendMessage(mesg);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cncl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static long TimeToMilli(String time) { // for user
        String[] data = time.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        return TimeUnit.MILLISECONDS.convert(seconds + 60 * minutes + 3600 * hours, TimeUnit.SECONDS);
    }


    public static long StringTimeToMilli(String time) {
        if (time.equals(""))
            return 0;
        long tm = Long.parseLong(time);
        return System.currentTimeMillis() - (TimeUnit.MILLISECONDS.convert(tm, TimeUnit.SECONDS));
//        return System.currentTimeMillis()-(TimeUnit.MILLISECONDS.convert(tm, TimeUnit.SECONDS) + TimeZone.getDefault().getRawOffset());
//        return TimeUnit.MILLISECONDS.convert(tm, TimeUnit.SECONDS);
    }

    // Image Loader functions

    public static void save_logo(Activity activity, final String logo_url, final String image_path) {
        final File file = new File(image_path);
        Log.e("save_logo == ", logo_url);
        Glide.with(AppController.getInstance())
                .asBitmap()
                .load(logo_url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(new SimpleTarget<Bitmap>(200, 200) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        File dir = new File(directory);
                        Canvas canvas = new Canvas(resource);
                        canvas.drawBitmap(resource, 0f, 0f, null);
                        if (!dir.exists())
                            dir.mkdirs();
                        try {
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 90, fOut);
                            fOut.flush();
                            fOut.close();
                            Log.e("logo saved== ", image_path);
                            activity.finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("save image 1 ", " Exception is  " + e.getMessage());
                            activity.finish();
                        }
                    }
                });

    }

    private static Map<String, Bitmap> bitmapMap = Collections.synchronizedMap(new HashMap<>());

    public static void load_image_file(final String imageurl, final ImageView imageView) {
        Log.e("file url ", " is " + imageurl);
        Glide.with(AppController.getInstance())
                .load(imageurl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(imageView);

    }

    @SuppressLint("CheckResult")
    public static void load_image(String imageurl, final ImageView imageView) {
        if (imageurl == null)
            return;
        if (!imageurl.equals("") && !imageurl.contains("http"))
            imageurl = Host + imageurl;

        if (URLUtil.isValidUrl(imageurl) && !imageurl.equals(Host)) {
            if (bitmapMap.containsKey(imageurl) && bitmapMap.get(imageurl) != null) {
                imageView.setImageBitmap(bitmapMap.get(imageurl));
            } else {
//                Log.e("image ","is "+imageurl);
//                final String finalImageurl = imageurl;
                imageView.setTag(imageurl);
                Glide.with(AppController.getInstance())
                        .asBitmap()
                        .load(imageurl)
//                        .apply(new RequestOptions().placeholder(R.drawable.progress_bar_bg).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().placeholder(R.drawable.progress_bar_bg).error(R.drawable.noimage).diskCacheStrategy(DiskCacheStrategy.NONE))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                                imageView.setImageResource(R.drawable.noimage);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setImageBitmap(resource);
                                bitmapMap.put(imageView.getTag().toString(), resource);
                                return false;
                            }
                        }).into(imageView);
            }
        } else imageView.setImageResource(R.drawable.noimage);
    }

    @SuppressLint("CheckResult")
    public static void load_profile(String imageurl, final ImageView imageView) {
        if (imageurl == null)
            return;
        if (!imageurl.equals("") && !imageurl.contains("http"))
            imageurl = Host + imageurl;

        if (URLUtil.isValidUrl(imageurl) && !imageurl.equals(Host)) {
            if (bitmapMap.containsKey(imageurl) && bitmapMap.get(imageurl) != null) {
                imageView.setImageBitmap(bitmapMap.get(imageurl));
            } else {
//                Log.e("image ","is "+imageurl);
//                final String finalImageurl = imageurl;
                imageView.setTag(imageurl);
                Glide.with(AppController.getInstance())
                        .asBitmap()
                        .load(imageurl)
//                        .apply(new RequestOptions().placeholder(R.drawable.progress_bar_bg).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().placeholder(R.drawable.progress_bar_bg).error(R.drawable.profile_pic).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                                imageView.setImageDrawable(getResDrawable(R.drawable.profile));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setImageBitmap(resource);
                                bitmapMap.put(imageView.getTag().toString(), resource);
                                return false;
                            }
                        }).into(imageView);
            }
        } else imageView.setImageResource(R.drawable.profile_pic);
    }


    public static void load_image_cache(final String imageurl, final ImageView imageView) {
        Glide.with(AppController.getInstance())
                .load(imageurl)
                .apply(new RequestOptions().placeholder(R.drawable.progress_bar_bg).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);

    }

    public static void load_Gif_image(final int resource, final ImageView imageView) {
        Glide.with(AppController.getInstance())
                .asGif()
                .listener(getRequest())
                .load(resource)
                .into(imageView);
    }

    public static RequestListener<GifDrawable> getRequest() {
        return new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(LOOP_FOREVER);
                return false;
            }
        };
    }

    public static int calculate_inspection(String due_date, int days_forNotification) {
        if (due_date == null || due_date.isEmpty()) {
            return 0;
        } else {
            Date startDate = Calendar.getInstance().getTime(), endDate = Calendar.getInstance().getTime();
            try {
                endDate = BaseActivity.Date_Format().parse(due_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
            if (diffInDays < 1) {
                return 0;
            } else if (diffInDays < days_forNotification) {
                return 1;
            } else {
                return 2;
            }
        }
    }


    public static double meterDistanceBetweenPoints(double a11, double a12, String b11, String b12) {
        if (!b11.equals("") && !b12.equals("")) {
            float lat_a = (float) a11;
            float lng_a = (float) a12;
            float lat_b = Float.parseFloat(b11);
            float lng_b = Float.parseFloat(b12);

            float pk = (float) (180.f / Math.PI);

            float a1 = lat_a / pk;
            float a2 = lng_a / pk;
            float b1 = lat_b / pk;
            float b2 = lng_b / pk;

            double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
            double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
            double t3 = Math.sin(a1) * Math.sin(b1);
            double tt = Math.acos(t1 + t2 + t3);

            return 6366000 * tt;
        } else
            return 0;
    }

    public static void load_data(final Activity activity, final String string_data, final String url) {
        Glide.with(AppController.getInstance())
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmapMap.put(url, resource);
                        share_data(activity, string_data, resource);
                    }
                });
//        }
    }


    private static void share_data(Activity activity, String string_data, Bitmap resource) {
        String path = MediaStore.Images.Media.insertImage(AppController.getInstance().getContentResolver(), resource, "", null);
        Uri imageUri = Uri.parse(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(string_data).toString());
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        activity.startActivity(intent);
    }

    public static void share_text(Activity activity, String string_data) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, string_data);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("text/*");
        activity.startActivity(intent);
    }

    public static void shareImage(Activity activity, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }
            activity.startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    public static void share_file(final Activity activity, String url, final String file_name) {
        new NetworkRequest(activity).download_file(url, file_name + ".pdf", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && msg.obj.equals("true")) {
                    sharePDF(activity, Static_values.directory + file_name + ".pdf");
                }
            }
        });
    }

    public static void sharePDF(Activity activity, String path) {
        File pdf = new File(path);
        if (pdf.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
//            Uri uri = FileProvider.getUriForFile(activity, activity.getString(R.string.file_provider_authority), pdf);
            Uri uri = FileProvider.getUriForFile(activity, FlavourInfo.APP_ID + ".provider", pdf);
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdf));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("application/pdf");
            activity.startActivity(intent);
        } else {
            Log.i("DEBUG", "File doesn't exist");
        }
    }


    public static boolean check_status(String boq_id, final String parent, int pos) {
        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String treeData) {
                boolean nodeOk = treeData.contains(parent);
                return nodeOk ? 0 : 1;
            }
        };

        TreeNode treeRoot = treeNodes.get(parent);
        TreeNode found = treeRoot.findTreeNode(searchCriteria);
        Log.e("is leaf ", " " + found.isLeaf());
        if (!found.isLeaf()) {
            List<TreeNode> childs = found.getChildrens();
            for (TreeNode treeNode : childs) {
//                    Log.e("nodes ", " " + ((Category_Model) treeNode.getData()).getId());
                if (!check_status(boq_id, ((Category_Model) treeNode.getData()).getId(), pos)) {
                    return false;
                }
            }
            return true;
        } else {
            Category_Model child_model = (Category_Model) found.getData();
            return check_leaf(boq_id, child_model, pos);
        }
    }

    public static void clear_key(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("preference", MODE_PRIVATE);
        preferences.edit().remove("local_data").commit();
    }

    public static boolean check_singleNode(String boq_id, final String parent, int pos) {
        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String treeData) {
                boolean nodeOk = treeData.contains(parent);
                return nodeOk ? 0 : 1;
            }
        };
        TreeNode treeRoot = treeNodes.get(parent);
        TreeNode found = treeRoot.findTreeNode(searchCriteria);
        Log.e("is leaf ", " " + found.isLeaf());
        if (!found.isLeaf()) {
            Category_Model category_model = ((Category_Model) found.getData());
            if (boq_id != null && category_table_dao.isItemExist(user_id, category_model.getId(), boq_id))
                category_model.setIs_completed(true);
            else category_model.setIs_completed(false);

            if (category_model.is_completed())
                return true;
            else {
                show_snak(homeActivity, category_model.getCat_name() + " is incomplete in BOQ " + (pos + 1));
                return false;
            }
        } else {
            return false;
        }
    }


    public static Category_Table.Category_Table_Dao category_table_dao;

    private static boolean check_leaf(String boq_id, Category_Model child_model, int pos) {
        if (boq_id != null && category_table_dao.isItemExist(user_id, child_model.getId(), boq_id))
            child_model.setIs_completed(true);
        else child_model.setIs_completed(false);
        if (child_model.is_completed()) {
            return true;
        } else {
            show_snak(homeActivity, child_model.getCat_name() + " is incomplete in BOQ " + (pos + 1));
            return false;
        }
    }

    private static ArrayList<String> table_headings = new ArrayList<>(Arrays.asList("Client Name", "Contact Name", "Contact Number", "Item Series", "UIN", "Report No.", "Site Id", "Job Card", "SMS", "Status"));

    public static void createExcelSheet(Activity activity, String prifix_name, List<Site_Model> dashbrd_searches) {
        String Fnamexls = prifix_name + "_" + System.currentTimeMillis() + ".xls";
        File directory = new File(Static_values.directory);
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("Search Sheet", 0);
            try {
                for (int i = 0; i <= dashbrd_searches.size(); i++) {
                    Label label;
                    if (i != 0) {
                        ArrayList<String> data_index = dashbrd_searches.get(i - 1).getALL();
                        for (int j = 0; j < table_headings.size(); j++) {
                            label = new Label(j, i, data_index.get(j));
                            sheet.addCell(label);
                        }
                    } else {
                        for (int j = 0; j < table_headings.size(); j++) {
                            label = new Label(j, i, table_headings.get(j));
                            sheet.addCell(label);
                        }
                    }
                }

            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //createExcel(excelSheet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        show_snak(activity, "data Sheet Saved Successfully.");
    }

    public static void printHashKey(Activity activity) {

        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo("app.com.arresto.Arresto_Connect", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public static String getIMEI(Activity activity) {
//        TelephonyManager mngr = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return "";
//        }
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
//        String imei = mngr.getDeviceId();
        return android_id;
    }

    public static Double parsh_Double(String s) {
        Scanner st = new Scanner(s);
        while (!st.hasNextDouble()) {
            st.next();
        }
        return st.nextDouble();
    }

    public static int add_two_String(String s1, String s2) {
        int i1 = 0, i2 = 0;
        if (s1 != null && !s1.equals(""))
            i1 = Integer.parseInt(s1);
        if (s2 != null && !s2.equals(""))
            i2 = Integer.parseInt(s2);
        return (i1 + i2);
    }

    public static void delete_uploadedsite_data(String unique_id, String master_id) {
        AppDatabase db = AppController.getInstance().getDatabase();
        db.getMaster_dataDao().deleteMaster_data(master_id, client_id);
        db.getSites_data_Dao().deleteSites_data(unique_id, user_id);
        db.getWorkPermit_Dao().deleteWorkPermit_data(user_id, client_id, unique_id);
        db.getInspection_Detail_Dao().deleteInspection_Detail(user_id, unique_id);
        db.getInspection_Asset_dao().deleteInspected_Asset(user_id, unique_id);
        db.getAsset_position_dao().deleteInspected_AssetPos(user_id, unique_id);
        db.getInspectionSignature_Dao().deleteSignature(user_id, unique_id);
        File dir = new File(directory + "inspection/" + unique_id);
        if (dir.exists()) {
            deleteRecursive(dir);
        }
    }


    public static void delete_uploadedPdm_data(String unique_id, String master_id) {
        AppDatabase db = AppController.getInstance().getDatabase();
        db.getMaster_dataDao().deleteMaster_data(master_id, client_id);
        db.getSites_data_Dao().deleteSites_data(unique_id, user_id);
        db.getPdm_stepsDao().deletePdm_step(unique_id);
        db.getStep_subitem_Dao().deleteStep_subitem(unique_id, client_id);
        ;
        db.getSignature_Dao().deleteSignature_data(unique_id, client_id);
        File dir = new File(directory + "pdm/" + unique_id);
        if (dir.exists()) {
            deleteRecursive(dir);
        }
        List<String> inspected_pdm = mPrefrence.getArray_Data(INSPECTED_PDM);
        inspected_pdm.remove(unique_id);
        mPrefrence.saveArray_Data(INSPECTED_PDM, inspected_pdm);
    }

    public static Drawable bitmap2Drawable(@Nullable final Bitmap bitmap, Activity activity) {
        return bitmap == null ? null : new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static Bitmap drawable2Bitmap(@Nullable final Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

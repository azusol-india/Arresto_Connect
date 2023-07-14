/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;

public class GetVersionCode extends AsyncTask<Void, String, String> {
    String currentVersion, package_name;
    Activity activity;

    public GetVersionCode(Activity activity, String currentVersion, String package_name, boolean is_show) {
        this.currentVersion = currentVersion;
        this.package_name = package_name;
        this.activity = activity;
        this.is_show = is_show;
    }

    @Override

    protected String doInBackground(Void... voids) {
        String newVersion = null;
        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + package_name + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;
    }

    boolean is_show;

    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            if (!currentVersion.equalsIgnoreCase(onlineVersion)) {
                //show anything
                show_alert();
            } else if (is_show) {
                show_snak(activity, getResString("lbl_no_update_available"));
            }

        }

        Log.e("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

    }

    private void show_alert() {
        new AlertDialog.Builder(activity)
                .setTitle(getResString("lbl_updated_app_available"))
                .setMessage(getResString("lbl_want_to_update_app"))
                .setPositiveButton(getResString("lbl_update"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + package_name)));
//                            Toast.makeText(activity, "App is in BETA version cannot update", Toast.LENGTH_SHORT).show();
//                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
                        } catch (ActivityNotFoundException anfe) {
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

//                        new MyAsyncTask().execute();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
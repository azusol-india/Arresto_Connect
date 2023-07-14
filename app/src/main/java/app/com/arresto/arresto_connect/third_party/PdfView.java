package app.com.arresto.arresto_connect.third_party;

import static android.os.Build.VERSION.SDK_INT;
import static app.com.arresto.arresto_connect.constants.Check_permissions.CAMERA_STORAGE_PERMISSIONS_10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.print.PdfPrint;
import android.print.PdfPrint.CallbackPrint;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintAttributes.Resolution;
import android.webkit.WebView;

import androidx.core.content.FileProvider;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Check_permissions;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import java.io.File;

public class PdfView {
    public interface Callback {
        void failure();
        void success(String str);
    }

    public static void createWebPrintJob(BaseActivity activity, WebView webView, File directory, String fileName, final Callback callback) {
        if (activity.isStoragePermissionGranted()) {
            StringBuilder sb = new StringBuilder();
            sb.append(activity.getString(R.string.app_name));
            sb.append(" Document");
            String jobName = sb.toString();
            PrintAttributes attributes = null;
            if (VERSION.SDK_INT >= 19) {
                attributes = new Builder().setMediaSize(MediaSize.ISO_A4).setResolution(new Resolution("pdf", "pdf", 600, 600)).setMinMargins(Margins.NO_MARGINS).build();
            }
            PdfPrint pdfPrint = new PdfPrint(attributes);
            if (VERSION.SDK_INT >= 21) {
                pdfPrint.print(webView.createPrintDocumentAdapter(jobName), directory, fileName, new CallbackPrint() {
                    public void success(String path) {
                        callback.success(path);
                    }

                    public void onFailure() {
                        callback.failure();
                    }
                });
                return;
            }
            return;
        }
        callback.failure();
    }

    public static void openPdfFile(final BaseActivity activity, String title, String message, final String path) {
        if (activity.isStoragePermissionGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Open", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PdfView.fileChooser(activity, path);
                }
            });
            builder.setNegativeButton("Dismiss", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
    }

    /* access modifiers changed from: private */
    public static void fileChooser(Activity activity, String path) {
        File file = new File(path);
        Intent target = new Intent("android.intent.action.VIEW");
        target.setDataAndType(FileProvider.getUriForFile(activity, "com.package.name.fileprovider", file), "application/pdf");
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            activity.startActivity(Intent.createChooser(target, "Open File"));
        } catch (ActivityNotFoundException var7) {
            var7.printStackTrace();
        }
    }
}

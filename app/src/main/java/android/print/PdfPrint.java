/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package android.print;

import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;


public class PdfPrint {
    private static final String TAG = PdfPrint.class.getSimpleName();
    private final PrintAttributes printAttributes;

    public PdfPrint(PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

    public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName, final CallbackPrint callback) {
        if (Build.VERSION.SDK_INT >= 19) {
            printAdapter.onLayout(null, this.printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
                public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, PdfPrint.this.getOutputFile(path, fileName), new CancellationSignal(), new PrintDocumentAdapter.WriteResultCallback() {
                            public void onWriteFinished(PageRange[] pages) {
                                super.onWriteFinished(pages);
                                if (pages.length > 0) {
                                    File file = new File(path, fileName);
                                    String pathx = file.getAbsolutePath();
                                    callback.success(pathx);
                                } else {
                                    callback.onFailure();
                                }

                            }
                        });
                    }

                }
            }, null);
        }

    }

    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, fileName);

        try {
            file.createNewFile();
            return ParcelFileDescriptor.open(file, 805306368);
        } catch (Exception var5) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", var5);
            return null;
        }
    }

    public interface CallbackPrint {
        void success(String var1);

        void onFailure();
    }
}

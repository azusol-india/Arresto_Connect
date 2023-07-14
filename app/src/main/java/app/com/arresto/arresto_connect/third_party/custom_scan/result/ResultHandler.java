/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan.result;

import android.app.Activity;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

import app.com.arresto.arresto_connect.BuildConfig;

public abstract class ResultHandler {
    private final Activity activity;
    private final ParsedResult result;

    ResultHandler(Activity activity, ParsedResult parsedResult) {
        this.result = parsedResult;
        this.activity = activity;
    }

    public ParsedResult getResult() {
        return this.result;
    }

    /* Access modifiers changed, original: 0000 */
    public Activity getActivity() {
        return this.activity;
    }


    public CharSequence getDisplayContents() {
        return this.result.getDisplayResult().replace("\r", BuildConfig.FLAVOR);
    }

    public final ParsedResultType getType() {
        return this.result.getType();
    }
}

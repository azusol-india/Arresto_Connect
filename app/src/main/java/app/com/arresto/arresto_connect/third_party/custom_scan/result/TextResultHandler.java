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
import com.google.zxing.client.result.TextParsedResult;

public class TextResultHandler extends ResultHandler {
    public TextResultHandler(Activity activity, ParsedResult parsedResult) {
        super(activity, parsedResult);
    }

    public CharSequence getDisplayContents() {
        TextParsedResult textParsedResult = (TextParsedResult) getResult();
        StringBuilder stringBuilder = new StringBuilder(300);
        String text = textParsedResult.getText();
        StringBuilder stringBuilder2;
        if (text.startsWith("http://")) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("http://");
            stringBuilder2.append(text.substring(7).trim());
            text = stringBuilder2.toString();
        } else if (text.startsWith("https://")) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("https://");
            stringBuilder2.append(text.substring(8).trim());
            text = stringBuilder2.toString();
        }
        ParsedResult.maybeAppend(text, stringBuilder);
        stringBuilder.trimToSize();
        return stringBuilder.toString();
    }
}

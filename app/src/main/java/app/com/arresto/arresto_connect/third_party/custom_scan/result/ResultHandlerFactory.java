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
import com.google.zxing.client.result.ResultParser;

public class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    public static ResultHandler makeResultHandler(Activity activity, Result result) {
        ParsedResult parseResult = parseResult(result);
        switch (parseResult.getType()) {
//            case ADDRESSBOOK:
//                return new AddressBookResultHandler(activity, parseResult);
//            case EMAIL_ADDRESS:
//                return new EmailAddressResultHandler(activity, parseResult);
//            case PRODUCT:
//                return new ProductResultHandler(activity, parseResult, result);
//            case URI:
//                return new URIResultHandler(activity, parseResult);
//            case WIFI:
//                return new WifiResultHandler(activity, parseResult);
            case TEXT:
                return new TextResultHandler(activity, parseResult);
//            case GEO:
//                return new GeoResultHandler(activity, parseResult);
//            case TEL:
//                return new TelResultHandler(activity, parseResult);
//            case SMS:
//                return new SMSResultHandler(activity, parseResult);
//            case CALENDAR:
//                return new CalendarResultHandler(activity, parseResult);
//            case ISBN:
//                return new ISBNResultHandler(activity, parseResult, result);
//            case VIN:
//                return new VINResultHandler(activity, parseResult, result);
            default:
                return new TextResultHandler(activity, parseResult);
        }
    }

    private static ParsedResult parseResult(Result result) {
        return ResultParser.parseResult(result);
    }
}

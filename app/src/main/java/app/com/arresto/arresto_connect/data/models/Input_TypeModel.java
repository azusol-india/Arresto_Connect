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

package app.com.arresto.arresto_connect.data.models;

import static android.os.Build.VERSION.SDK_INT;
import static app.com.arresto.arresto_connect.constants.Check_permissions.CAMERA_STORAGE_PERMISSIONS;
import static app.com.arresto.arresto_connect.constants.Check_permissions.CAMERA_STORAGE_PERMISSIONS_10;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;

import app.com.arresto.arresto_connect.BR;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Check_permissions;
import app.com.arresto.arresto_connect.third_party.custom_scan.DecoderActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

public class Input_TypeModel extends BaseObservable {
    private Fragment fragment;
    private Activity activity;
    private String input_type;
    private String input_value;
    private boolean flag = true;

    @Bindable
    public String getInput_value() {
        return input_value;
    }

    public void setInput_value(String input_value, boolean isUpdate) {
        this.input_value = input_value;
        if (isUpdate)
            notifyPropertyChanged(BR.input_value);
    }


    public Input_TypeModel(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }


    @Bindable
    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        flag = !flag;
        setInput_value("", true);
        this.input_type = input_type;
        notifyPropertyChanged(BR.input_type);

    }

    public void onCheckChanged(RadioGroup radioGroup, int id) {
        if (radioGroup.getId() == R.id.rg1 && flag) {
            flag = false;
            Log.e("check change run", "1111 id " + radioGroup);
            ((RadioGroup) activity.findViewById(R.id.rg2)).clearCheck();
            setInput_type("" + (radioGroup.findViewById(id)).getTag());

        } else if (radioGroup.getId() == R.id.rg2 && flag) {
            flag = false;
            Log.e("check change run", " 222id " + radioGroup);
            ((RadioGroup) activity.findViewById(R.id.rg1)).clearCheck();
            setInput_type("" + (radioGroup.findViewById(id)).getTag());

            if (getInput_type().equals("RFID")) {
                scanRFID();
            }
        }
    }

    public void OnClick(View view) {
        // Sign in now
        switch (view.getId()) {
            case R.id.rfid_btn:
                scanRFID();
                break;
            case R.id.barcode_btn:
                scanBarcode();
                break;
        }
    }

    public void onTextChanged(CharSequence text) {
        setInput_value("" + text, false);
    }

    private void scanRFID() {
        String scan_dat = HomeActivity.homeActivity.scan_rfid.get_RfidTAG();
        if (!scan_dat.equals("")) {
            setInput_value(scan_dat, true);
        } else {
            Toast.makeText(activity, "No data for Scan.", Toast.LENGTH_LONG).show();
        }
    }

    private void scanBarcode() {
        if (SDK_INT > Build.VERSION_CODES.P && !Check_permissions.hasPermissions(activity, CAMERA_STORAGE_PERMISSIONS_10)) {
            Check_permissions.request_permissions(activity, CAMERA_STORAGE_PERMISSIONS_10);
            return;
        } else if (SDK_INT < Build.VERSION_CODES.Q && !Check_permissions.hasPermissions(activity, CAMERA_STORAGE_PERMISSIONS)) {
            Check_permissions.request_permissions(activity, CAMERA_STORAGE_PERMISSIONS);
            return;
        }

        Intent intent = new Intent(activity, DecoderActivity.class);
        fragment.startActivityForResult(intent, 10);

//        IntentIntegrator integrator = new IntentIntegrator(activity);
//        integrator.setDesiredBarcodeFormats(Collections.unmodifiableList(Arrays.asList("UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39", "CODE_93", "CODE_128",
//                "ITF", "RSS_14", "RSS_EXPANDED","UPC_A","UPC_E", "EAN_8", "EAN_13", "RSS_14","QR_CODE","DATA_MATRIX")));
//        Intent intent = integrator.createScanIntent();
//        fragment.startActivityForResult(intent, 10);
    }
}

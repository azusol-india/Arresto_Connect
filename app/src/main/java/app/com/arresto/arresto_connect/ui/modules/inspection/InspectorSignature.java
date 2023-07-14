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

package app.com.arresto.arresto_connect.ui.modules.inspection;

import static app.com.arresto.arresto_connect.constants.AppUtils.Image_toBase64;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.INSPECTED_PDM;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.selected_Site_model;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.asm_signature_upload;
import static app.com.arresto.arresto_connect.network.All_Api.post_safety_form;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;
import static app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps.inspection_id;
import static app.com.arresto.arresto_connect.ui.modules.safety_management.Safety_Select_Forms.params;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CaptureSignatureView;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.inspection_tables.InspectionSignature_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Signature_table;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.network.Upload_Pdm_Steps;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

public class InspectorSignature extends Fragment {
    View view;
    EditText name, designation, remark_edt;
    CaptureSignatureView signatureView;
    LinearLayout signHere;
    String ofline_sign_path = directory;
    File oflinefile;
    MaterialButton continueBtn, clear_sign;
    private Spinner remark_spinr;
    String signature_type = "";
    RadioButton confirm_btn;

    public static InspectorSignature newInstance(String type) {
        InspectorSignature inspection3 = new InspectorSignature();
        Bundle args = new Bundle();
        args.putString("type", type);
        inspection3.setArguments(args);
        return inspection3;
    }

    public static InspectorSignature newInstance(String type, String spares) {
        InspectorSignature inspection3 = new InspectorSignature();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("spares", spares);
        inspection3.setArguments(args);
        return inspection3;
    }

    String spares_data;
    AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.inspection3, container, false);
            db = AppController.getInstance().getDatabase();
            find_id();
            if (getArguments() != null) {
                signature_type = getArguments().getString("type");
                spares_data = getArguments().getString("spares", "");
            }

            if (signature_type.equals("Safety")) {
                designation.setVisibility(View.GONE);
                ((LinearLayout) designation.getParent()).getChildAt(0).setVisibility(View.GONE);
            } else if (signature_type.equals("inspection")) {
                ((LinearLayout) remark_edt.getParent()).setVisibility(View.VISIBLE);
                ((ViewGroup) confirm_btn.getParent()).setVisibility(View.VISIBLE);
                ofline_sign_path = ofline_sign_path + "inspection/" + unique_id + "/";
            } else if (signature_type.equals("Pdm_Sign")) {
                ofline_sign_path = ofline_sign_path + "PDM/" + unique_id + "/";
            }

            File file = new File(ofline_sign_path);
            if (!file.exists()) {
                file.mkdirs();
            }


            ofline_sign_path = ofline_sign_path + System.currentTimeMillis() + "sign1.jpg";

            oflinefile = new File(ofline_sign_path);
            signatureView = new CaptureSignatureView(getActivity());
            name.setText(Profile_Model.getInstance().getUpro_first_name() + " " + Profile_Model.getInstance().getUpro_last_name());
            designation.setText(Static_values.user_typ);
            signHere.addView(signatureView);
            clear_sign.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    signatureView.ClearCanvas();
                }
            });


            continueBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bitmap b = Bitmap.createBitmap(signHere.getDrawingCache());
                    OutputStream stream = null;
                    if (!signatureView.drawing) {
                        Toast.makeText(getActivity(), getResString("lbl_signatr_msg"), Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            stream = new FileOutputStream(oflinefile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        b.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                        if (signature_type.equals("Asm_Sign"))
                            post_data(asm_signature_upload);
                        else if (signature_type.equals("Pdm_Sign")) {
                            save_signature_pdm();
//                            post_data(pdm_signature_upload);
                        } else if (signature_type.equals("Safety")) {
                            post_safetydata();
//                            post_data(pdm_signature_upload);
                        } else
                            save_inspctr_data();
                    }
                }
            });
        } else {

            return view;
        }
        return view;
    }

    ArrayList<String> remark_arr = new ArrayList<>(Arrays.asList(getResString("lbl_remark1"), getResString("lbl_remark2"), getResString("lbl_remark3")
            , getResString("lbl_remark4"), getResString("lbl_remark5"), getResString("lbl_remark6"), getResString("lbl_remark7"),
            getResString("lbl_remark8"), getResString("lbl_remark9"), getResString("lbl_remark10"), getResString("lbl_othr_st")));

    public void find_id() {
        continueBtn = view.findViewById(R.id.continue_btn_in3);
        name = view.findViewById(R.id.name_1);
        designation = view.findViewById(R.id.dsgntion_1);
        remark_edt = view.findViewById(R.id.remark);
        signHere = view.findViewById(R.id.sign_here);
        signHere.setDrawingCacheEnabled(true);
        clear_sign = view.findViewById(R.id.clear_sign);
        remark_spinr = view.findViewById(R.id.remark_spinr);
        confirm_btn = view.findViewById(R.id.confirm_btn);

        ArrayAdapter adapter3 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, remark_arr);
        adapter3.setDropDownViewResource(R.layout.spinner_item);
        remark_spinr.setAdapter(adapter3);

        remark_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String txt = parent.getItemAtPosition(position).toString();
                if (txt.equalsIgnoreCase(getResString("lbl_othr_st"))) {
                    remark_edt.setVisibility(View.VISIBLE);
                } else {
                    remark_edt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void save_inspctr_data() {
        String remark;
//        if (remark_spinr.getSelectedItem().toString().equalsIgnoreCase(getResString("lbl_othr_st")))
        remark = remark_edt.getText().toString();
//        else
//            remark = remark_spinr.getSelectedItem().toString();
        if (!confirm_btn.isChecked()) {
            show_snak(getActivity(), "Please select I confirm message.");
            return;
        }

        InspectionSignature_Table.InspectionSignature_Dao signature_dao = db.getInspectionSignature_Dao();
        signature_dao.deleteSignature(user_id, unique_id, 0);
        InspectionSignature_Table signatureTable = new InspectionSignature_Table();
        signatureTable.setInspector_Sign(user_id, client_id, unique_id, remark, spares_data, ofline_sign_path, confirm_btn.isChecked());
        long id = signature_dao.insert(signatureTable);
        if (id > 0) {
            LoadFragment.replace(ClientSignatureFragment.newInstance("inspection"), getActivity(), getResString("lbl_client_remark"));
        } else {
            show_snak(getActivity(), getResString("lbl_try_again_msg"));
        }
    }

    public void post_data(final String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Static_values.user_id);
            if (signature_type.equals("Asm_Sign")) {
                params.put("project_id", slctd_project.getUp_id());
                params.put("report_no", Static_values.report_no);
            } else
                params.put("inspection_id", inspection_id);

            params.put("client_id", client_id);
            params.put("client_designation", designation.getText().toString());
            params.put("client_name", name.getText().toString());
            params.put("signature_of", "technician");
            String extension = MimeTypeMap.getFileExtensionFromUrl(ofline_sign_path);
            String type = null;
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            params.put("signature_image", "data:" + type + ";base64," + Image_toBase64(ofline_sign_path));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(url,
                params, new NetworkRequest.VolleyResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        Object json;
                        try {
                            json = new JSONTokener(response).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg_code = jsonObject.getString("status_code");
                                show_snak(getActivity(), jsonObject.getString("message"));
                                if (msg_code.equals("200")) {
                                    if (url.equals(asm_signature_upload))
                                        HomeActivity.homeActivity.submit_action("rams_report");
                                    else {
                                        db.getPdm_stepsDao().deletePdm_step(unique_id);
                                        HomeActivity.homeActivity.submit_action("pdm_report");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", "" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("error", "" + message);
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    public void save_signature_pdm() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Static_values.user_id);
            params.put("client_id", client_id);
            params.put("client_designation", designation.getText().toString());
            params.put("client_name", name.getText().toString());
            params.put("signature_of", "technician");
            params.put("signature_image", ofline_sign_path);
            Signature_table signature_table = new Signature_table();
            signature_table.setData(client_id, unique_id, "pdm", "technician", params.toString());
            signature_table.setEnd_time(System.currentTimeMillis());
            db.getSignature_Dao().insert(signature_table);

            Sites_data_table siteTbl = db.getSites_data_Dao().getSingleSites(user_id, unique_id);
            if (siteTbl == null) {
                siteTbl = new Sites_data_table();
                siteTbl.setData(user_id, client_id, unique_id, new Gson().toJson(selected_Site_model));
                db.getSites_data_Dao().insert(siteTbl);
            }
            siteTbl.setInspectionTime(System.currentTimeMillis());
            db.getSites_data_Dao().updateSite(siteTbl);

            String entry = AppController.getInstance().getDatabase().getMaster_dataDao().checkEntry(selectedMasterData_model.getMdata_id(), client_id);
            if (entry == null || entry.equals("")) {
                Master_data_table master_data_table = new Master_data_table();
                master_data_table.setData(client_id, selectedMasterData_model.getMdata_id(), new Gson().toJson(selectedMasterData_model));
                db.getMaster_dataDao().insert(master_data_table);
            }

            List<String> inspected_pdm = mPrefrence.getArray_Data(INSPECTED_PDM);
            if (!inspected_pdm.contains(unique_id))
                inspected_pdm.add(unique_id);

            mPrefrence.saveArray_Data(INSPECTED_PDM, inspected_pdm);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isNetworkAvailable(getActivity())) {
            Upload_Pdm_Steps upload_pdm_steps = new Upload_Pdm_Steps(getActivity());
            upload_pdm_steps.upload_steps(unique_id, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.obj != null && msg.what == 1) {
                        AppUtils.show_snak(getActivity(), "data submitted success.");
                        HomeActivity.homeActivity.submit_action("pdm_report");
                    }
                }
            });
        } else {
            HomeActivity.homeActivity.submit_action("dashboard");
        }
    }

    public void post_safetydata() {
        String extension = MimeTypeMap.getFileExtensionFromUrl(ofline_sign_path);
        String type = null;
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        try {
            params.put("contractor_signature", "data:" + type + ";base64," + Image_toBase64(ofline_sign_path));
            params.put("cgrp_id", Static_values.role_id);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        Log.e("field set ", " is " + params);
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(post_safety_form, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                        if (msg_code.equals("200")) {
                            HomeActivity.homeActivity.submit_action("safety_report");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }
}

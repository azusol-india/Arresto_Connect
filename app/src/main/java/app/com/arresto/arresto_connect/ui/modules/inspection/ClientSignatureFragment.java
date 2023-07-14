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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CaptureSignatureView;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.inspection_tables.InspectionSignature_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Signature_table;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.network.Upload_site_data;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.Image_toBase64;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.INSPECTED_SITES;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.SITES_TOUPLOAD;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.selected_Site_model;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.asm_signature_upload;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;
import static app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps.inspection_id;
import static app.com.arresto.arresto_connect.ui.modules.safety_management.Safety_Select_Forms.params;

public class ClientSignatureFragment extends Base_Fragment implements View.OnClickListener {
    private LinearLayout clSignHere;
    private EditText name, designation, remark_edt;
    private CaptureSignatureView signatureView;
    private File oflinefile;
    private String ofline_sign_path = directory;
    private Upload_site_data upload_site_data;
    private String signature_type = "";

    public static ClientSignatureFragment newInstance(String type) {
        ClientSignatureFragment inspection4 = new ClientSignatureFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        inspection4.setArguments(args);
        return inspection4;
    }

    View view;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inspection4, parent, false);
        find_id();

        if (getArguments() != null) {
            signature_type = getArguments().getString("type");
        }

        if (signature_type.equals("Safety")) {
            designation.setVisibility(View.GONE);
            ((LinearLayout) designation.getParent()).getChildAt(0).setVisibility(View.GONE);
        } else if (signature_type.equals("inspection")) {
            skip_btn.setVisibility(View.VISIBLE);
            ofline_sign_path = ofline_sign_path + "inspection/" + unique_id + "/";
        } else if (signature_type.equals("Pdm_Sign")) {
            ofline_sign_path = ofline_sign_path + "PDM/" + unique_id + "/";
        }

        signatureView = new CaptureSignatureView(getActivity());
        clSignHere.addView(signatureView);
        File file = new File(ofline_sign_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ofline_sign_path = ofline_sign_path + System.currentTimeMillis() + "sign.jpg";
        oflinefile = new File(ofline_sign_path);
        upload_site_data = new Upload_site_data(getActivity());

        return view;
    }

    MaterialButton skip_btn;

    private void find_id() {
        name = view.findViewById(R.id.name_1);
        designation = view.findViewById(R.id.dsgntion_1);
        remark_edt = view.findViewById(R.id.remark);
        clSignHere = view.findViewById(R.id.client_sign_here);
        MaterialButton clearSign = view.findViewById(R.id.clear_sign);
        skip_btn = view.findViewById(R.id.skip_btn);
        MaterialButton continueBtn = view.findViewById(R.id.continue_btn);

        skip_btn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        clSignHere.setDrawingCacheEnabled(true);
        clearSign.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_sign:
                signatureView.ClearCanvas();
                break;
            case R.id.skip_btn:
                save_inspctr_data(true);
                break;

            case R.id.continue_btn:
                if (!signatureView.drawing) {
                    Toast.makeText(getActivity(), getResString("lbl_signatr_msg"), Toast.LENGTH_LONG).show();
                } else if (name.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResString("lbl_blank_name_msg"), Toast.LENGTH_LONG).show();
                } else if (remark_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getResString("lbl_rmrk_name_msg"), Toast.LENGTH_LONG).show();
                }
//                else if (!signature_type.equals("Safety") && designation.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), getResString("lbl_desig_name_msg"), Toast.LENGTH_LONG).show();
//                }
                else {
                    create_signature_file();
                    if (signature_type.equals("Asm_Sign"))
                        post_data(asm_signature_upload);
                    else if (signature_type.equals("Pdm_Sign")) {
                        save_signature_pdm();
                    } else if (signature_type.equals("Safety")) {
                        load_fragmant();
                    } else {
                        save_inspctr_data(false); //for inspection
                    }
                }
                break;
        }
    }

    private void load_fragmant() {
        String extension = MimeTypeMap.getFileExtensionFromUrl(ofline_sign_path);
        String type = null;
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        try {
            params.put("remarks", remark_edt.getText().toString());
            params.put("manager_name", name.getText().toString());
            params.put("manager_signature", "data:" + type + ";base64," + Image_toBase64(ofline_sign_path));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        LoadFragment.replace(InspectorSignature.newInstance(signature_type), getActivity(), getResString("lbl_add_signature"));
    }

    private void create_signature_file() {
        Bitmap b = Bitmap.createBitmap(clSignHere.getDrawingCache());
        OutputStream stream;
        try {
            stream = new FileOutputStream(oflinefile);
            b.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("create image", "error is " + e.getMessage());
        }

    }

    private void save_inspctr_data(boolean isSkipped) {
        AppDatabase db = AppController.getInstance().getDatabase();
        InspectionSignature_Table.InspectionSignature_Dao signature_dao = db.getInspectionSignature_Dao();
        signature_dao.deleteSignature(user_id, unique_id, 0);
        InspectionSignature_Table signatureTable = new InspectionSignature_Table();
        if (!signatureView.drawing || isSkipped)
            signatureTable.setClient_Sign(user_id, client_id, unique_id, name.getText().toString(), designation.getText().toString(), remark_edt.getText().toString(), "");
        else
            signatureTable.setClient_Sign(user_id, client_id, unique_id, name.getText().toString(), designation.getText().toString(), remark_edt.getText().toString(), ofline_sign_path);
        long id = signature_dao.insert(signatureTable);

        List<String> inspected_sites = mPrefrence.getArray_Data(INSPECTED_SITES);
        List<String> upload_sites = mPrefrence.getArray_Data(SITES_TOUPLOAD);
        if (!inspected_sites.contains(unique_id))
            inspected_sites.add(unique_id);
        if (!upload_sites.contains(unique_id))
            upload_sites.add(unique_id);

        Sites_data_table sites_data_table = new Sites_data_table();
        sites_data_table.setInspectionSiteData(user_id, client_id, unique_id, new Gson().toJson(selected_Site_model), System.currentTimeMillis());
        db.getSites_data_Dao().insert(sites_data_table);

        Master_data_table master_data_table = new Master_data_table();
        master_data_table.setData(client_id, selectedMasterData_model.getMdata_id(), new Gson().toJson(selectedMasterData_model));
        db.getMaster_dataDao().insert(master_data_table);
        if (id > 0) {
            mPrefrence.saveArray_Data(INSPECTED_SITES, inspected_sites);
            mPrefrence.saveArray_Data(SITES_TOUPLOAD, upload_sites);

            if (isNetworkAvailable(baseActivity)) {
                upload_site_data.startUpload(Static_values.unique_id);
            } else {
                Toast.makeText(baseActivity, getResString("lbl_ofline_dtaSav_msg"), Toast.LENGTH_LONG).show();
                if (slctd_project != null) {
                    attachFragment();
                } else homeActivity.load_home_fragment(false);
            }
        } else {
            show_snak(baseActivity, getResString("lbl_try_again_msg"));
        }
    }

    private void attachFragment() {
        String tag = "Project data";
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment != null) {
            manager.popBackStack(tag, 0);
            // No fragment in backStack with same tag.
//            ft.add(HomeActivity.fragContainer, fragment, tag).addToBackStack(tag).commit();
        }
    }

    private void post_data(String pdm_inspection_signature) {
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
            params.put("client_remarks", remark_edt.getText().toString());
            params.put("signature_of", "client");

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
        network_request.make_contentpost_request(pdm_inspection_signature, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
//                                    show_snak(getActivity(), "Project completed successfully.");
//                                   go_main_list();
                            LoadFragment.replace(InspectorSignature.newInstance(signature_type), getActivity(), getResString("lbl_add_signature"));
                        } else {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
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

    private void save_signature_pdm() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Static_values.user_id);
            params.put("client_id", client_id);
            params.put("client_designation", designation.getText().toString());
            params.put("client_name", name.getText().toString());
            params.put("client_remarks", remark_edt.getText().toString());
            params.put("signature_of", "client");
            params.put("signature_image", ofline_sign_path);
            Signature_table signature_table = new Signature_table();
            signature_table.setData(client_id, unique_id, "pdm", "client", params.toString());
            AppController.getInstance().getDatabase().getSignature_Dao().insert(signature_table);
            LoadFragment.replace(InspectorSignature.newInstance(signature_type), getActivity(), getResString("lbl_add_signature"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.modules.add_data.AddAssetPager.selected_asset;

public class AddAssetPage4 extends Base_Fragment implements View.OnClickListener {
    View view;
    AddAssetModel assetModel;

    String heading;

    public static AddAssetPage4 newInstance(String heading) {
        AddAssetPage4 fragmentFirst = new AddAssetPage4();
        Bundle args = new Bundle();
        args.putString("heading", heading);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_asset_page4, parent, false);
            assetModel = AddAssetModel.getInstance();
            if (getArguments() != null) {
                heading = getArguments().getString("heading");
            }
            all_Ids();
            fetch_data(All_Api.getStandards + client_id);
            setdata();
        }
        return view;
    }

    TextView heading_tv;
    EditText certificate_edt;
    private Spinner standrd_spinr, notified_certificate_spinr, article_body_spinr, status_spinr;

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };
    MaterialButton back_btn, continue_btn;

    private void all_Ids() {
        back_btn = view.findViewById(R.id.back_btn);
        continue_btn = view.findViewById(R.id.continue_btn);
        back_btn.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        heading_tv = view.findViewById(R.id.heading_tv);

        certificate_edt = view.findViewById(R.id.certificate_edt);
        status_spinr = view.findViewById(R.id.status_spinr);
        status_spinr.setSelection(1);
        standrd_spinr = view.findViewById(R.id.standrd_spinr);
        notified_certificate_spinr = view.findViewById(R.id.notified_body_spinr);
        article_body_spinr = view.findViewById(R.id.article_body_spinr);

        heading_tv.setText(heading);
        new CustomTextWatcher(certificate_edt, textWatcher);
        standrd_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_standrd = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        notified_certificate_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_certificate = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        article_body_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_body = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setdata() {
        if (selected_asset != null) {
//            certificate_edt.setText(selected_asset.getce());
        }
        if (assetModel.getAsset_code() != null) {
            certificate_edt.setText(assetModel.getCertificate_text());
//        repair_rg.setText(assetModel.get);
//        geo_rg.setText(assetModel.get);
//        wpermit_rg.setText(assetModel.get);
//        uom_spinr.setText(assetModel.get);
//        ins_typ_spinr.setText(assetModel.get);
//        kstatus_spinr.setText(assetModel.get);
//        status_spinr.setText(assetModel.get);

//        aset_edt
//        subasset_spnr
//        result_spnr
//        standrd_spinr = view.findViewById(R.id.standrd_spinr);
//        notified_certificate_spinr = view.findViewById(R.id.notified_body_spinr);
//        article_body_spinr = view.findViewById(R.id.article_body_spinr);
//        observation_spnr = view.findViewById(R.id.observation_spnr);
        }
    }


    String slctd_standrd, slctd_body, slctd_certificate;

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (url.contains(All_Api.getStandards)) {
                            ArrayList standards = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("standards"), Constant_model[].class)));
                            standrd_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, standards));

                            ArrayList bodys = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("body"), Constant_model[].class)));
                            article_body_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, bodys));

                            ArrayList certificates = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("certificates"), Constant_model[].class)));
                            notified_certificate_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, certificates));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                scrollPager.scrollTo(2);
                break;
            case R.id.continue_btn:
                if (isEmpty(certificate_edt)) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                assetModel.setStandard(slctd_standrd);
                assetModel.setBody(slctd_body);
                assetModel.setCertificate(slctd_certificate);
                assetModel.setCertificate_text(certificate_edt.getText().toString());
                show_Duplicatealert("confirmation", getResString("lbl_masterdata_add"));
                break;
        }
    }

    AddAssetPager.ScrollPager scrollPager;

    public void setscrolleListner(AddAssetPager.ScrollPager scrollPager) {
        this.scrollPager = scrollPager;
    }

    private void postData() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getGson().toJson(assetModel));
            String path = jsonObject.getString("asset_image");
            if (!path.contains("http")) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                if (path != null && !path.equals("") && new File(path).exists())
                    jsonObject.put("asset_image", "data:" + filetype + ";base64," + AppUtils.Image_toBase64(path));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.add_asset, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            show_Duplicatealert("submited", getResString("lbl_add_newasset"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String message) {

                    Log.e("error", "" + message);
                }
            });
        } else {
            show_snak(HomeActivity.homeActivity, getResString("lbl_try_again_msg"));
        }
    }

    public AlertDialog show_Duplicatealert(String type, String msg) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResString("lbl_confirmation"))
                .setMessage(msg)
                .setPositiveButton(getResString("lbl_yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("confirmation"))
                            LoadFragment.replace(Add_masterData.newInstance("asset_master", null), getActivity(), getResString("lbl_add_master_data"));
                        else
                            homeActivity.submit_action("ins_addAsset");
                    }
                })
                .setNegativeButton(getResString("lbl_no"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("confirmation"))
                            postData();
                        else
                            homeActivity.load_home_fragment(false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

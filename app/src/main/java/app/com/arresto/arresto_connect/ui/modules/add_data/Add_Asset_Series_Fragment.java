package app.com.arresto.arresto_connect.ui.modules.add_data;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.interfaces.OnItemClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;

public class Add_Asset_Series_Fragment extends AddData_BaseFragment implements View.OnClickListener {
    View view;
    AddAssetModel assetModel;

    public static Add_Asset_Series_Fragment newInstance(String heading) {
        Add_Asset_Series_Fragment fragmentFirst = new Add_Asset_Series_Fragment();
        Bundle args = new Bundle();
        args.putString("type", heading);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    String type = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_asset_series_fragment, parent, false);
            if (getArguments() != null) {
                type = getArguments().getString("type");
            }
            all_Ids();
            heading_tv.setText(this.getTag());
            baseActivity.headerTv.setText("");
            fetch_data(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
            initView();
        }
        return view;
    }

    public void initView() {
        assetModel = AddAssetModel.getInstance();
        setdata();
        fetch_data(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
        fetch_data(All_Api.getStandards + client_id);
    }


    private void setdata() {
        if (selected_asset_series != null) {
//            aset_edt.setText(selected_asset.getComponent_code());
            desc_edt.setText(selected_asset_series.getProduct_description());
            asset_img.setVisibility(View.VISIBLE);
            imagePath = selected_asset_series.getProduct_imagepath();
            if (!imagePath.equals("") && !imagePath.contains("http")) {
                if (imagePath.startsWith("."))
                    imagePath = imagePath.substring(2);

                imagePath = Host + imagePath;
            }
            AppUtils.load_image(imagePath, asset_img);
            slctd_asset = new ArrayList<>();
            String assets_txt = selected_asset_series.getProduct_components();
            if (assets_txt != null && !assets_txt.equals("0") && !assets_txt.equals("")) {
                final List<String> sub_ast_name = Arrays.asList(assets_txt.split("##"));
                for (int j = 0; j < sub_ast_name.size(); j++) {
                    String[] row_data = sub_ast_name.get(j).split("#");
                    if (row_data[0] != null && !row_data[0].equals(""))
                        slctd_asset.add(row_data[0]);
                }
            }

//            if (selected_asset.getComponent_inspection() != null && selected_asset.getComponent_inspection().equals("yes"))
//                inspection_rg.check(inspection_rg.getChildAt(0).getId());
//            else
//                inspection_rg.check(inspection_rg.getChildAt(1).getId());

            if (selected_asset_series.getProduct_geo_fancing().equals("yes"))
                geo_rg.check(geo_rg.getChildAt(0).getId());
            else
                geo_rg.check(geo_rg.getChildAt(1).getId());
            if (selected_asset_series.getProduct_work_permit().equals("yes"))
                wpermit_rg.check(wpermit_rg.getChildAt(0).getId());
            else
                wpermit_rg.check(wpermit_rg.getChildAt(1).getId());

            ins_freqH_edt.setText(selected_asset_series.getProduct_frequency_asset());
            ins_freqM_edt.setText(selected_asset_series.getProduct_frequency_asset());

        }

        if (assetModel.getAsset_code() != null) {
            desc_edt.setText(assetModel.getDescription());
            asset_img.setVisibility(View.VISIBLE);
            AppUtils.load_image(assetModel.getSeries_image(), asset_img);

            ins_freqM_edt.setText(assetModel.getFrequency_asset());
        }
    }

    MaterialButton continue_btn;
    private EditText ins_freqM_edt, ins_freqH_edt;
    Spinner kstatus_spinr;
    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };


    //    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
//        @Override
//        public void afterChange(TextView view, String text) {
//            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
//                ((TextInputLayout) view.getParent().getParent()).setError(null);
//        }
//    };
    FloatingActionButton choose_img;
    EditText desc_edt, aset_edt;
    ImageView asset_img;
    //    inspection_rg,
    private RadioGroup geo_rg, wpermit_rg;
    private Spinner ins_typ_spinr;
    private MultiSpinner asset_spnr;
    TextView heading_tv, slct_asset_kit_tv, msg_tv, edit_asset;
    EditText certificate_edt;
    private Spinner standrd_spinr, notified_certificate_spinr, article_body_spinr, status_spinr;
    ImageView drp_dwn_2, drp_dwn_3, drp_dwn_4;

    private void all_Ids() {
//        page 1
        heading_tv = view.findViewById(R.id.heading_tv);
        msg_tv = view.findViewById(R.id.msg_tv);
        slct_asset_kit_tv = view.findViewById(R.id.slct_asset_kit_tv);
        continue_btn = view.findViewById(R.id.continue_btn);
        desc_edt = view.findViewById(R.id.desc_edt);
        asset_img = view.findViewById(R.id.asset_img);
        choose_img = view.findViewById(R.id.choose_img);
        aset_edt = view.findViewById(R.id.aset_edt);
        edit_asset = view.findViewById(R.id.edit_asset);
        asset_spnr = view.findViewById(R.id.asset_spnr);
        new CustomTextWatcher(aset_edt, textWatcher);
        new CustomTextWatcher(desc_edt, textWatcher);

        drp_dwn_2 = view.findViewById(R.id.drp_dwn_2);
        drp_dwn_3 = view.findViewById(R.id.drp_dwn_3);
        drp_dwn_4 = view.findViewById(R.id.drp_dwn_4);
        choose_img.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        slct_asset_kit_tv.setOnClickListener(this);
        edit_asset.setOnClickListener(this);
        drp_dwn_2.setOnClickListener(this);
        drp_dwn_3.setOnClickListener(this);
        drp_dwn_4.setOnClickListener(this);

//        page 2
//        inspection_rg = view.findViewById(R.id.inspection_rg);
        geo_rg = view.findViewById(R.id.geo_rg);
        wpermit_rg = view.findViewById(R.id.wpermit_rg);
        ins_typ_spinr = view.findViewById(R.id.ins_typ_spinr);
//        inspection_rg.check(inspection_rg.getChildAt(1).getId());
        geo_rg.check(geo_rg.getChildAt(1).getId());
        wpermit_rg.check(wpermit_rg.getChildAt(1).getId());
        ins_typ_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_inspection_type = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        page 3

        ins_freqM_edt = view.findViewById(R.id.ins_freqM_edt);
        ins_freqH_edt = view.findViewById(R.id.ins_freqH_edt);

        kstatus_spinr = view.findViewById(R.id.kstatus_spinr);
        kstatus_spinr.setSelection(1);
        new CustomTextWatcher(ins_freqH_edt, textWatcher);
        new CustomTextWatcher(ins_freqM_edt, textWatcher);
        new CustomTextWatcher(ins_freqH_edt, textWatcher);

//        page 4
        certificate_edt = view.findViewById(R.id.certificate_edt);
        status_spinr = view.findViewById(R.id.status_spinr);
        status_spinr.setSelection(1);
        standrd_spinr = view.findViewById(R.id.standrd_spinr);
        notified_certificate_spinr = view.findViewById(R.id.notified_body_spinr);
        article_body_spinr = view.findViewById(R.id.article_body_spinr);

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

    List<String> slctd_asset;

    String slctd_inspection_type;
    String slctd_standrd, slctd_body, slctd_certificate;

    ArrayList insTyps;

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (url.contains(All_Api.getAllSeries)) {
                            asset_series = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Collections.sort(asset_series, new Comparator<Constant_model>() {
                                public int compare(Constant_model obj1, Constant_model obj2) {
                                    return obj1.getName().compareToIgnoreCase(obj2.getName());
                                }
                            });
//                            chooseAssetDialog(assets);
                        } else if (url.contains(All_Api.getAllAssets)) {
                            assets = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Collections.sort(assets, new Comparator<Constant_model>() {
                                public int compare(Constant_model obj1, Constant_model obj2) {
                                    return obj1.getName().compareToIgnoreCase(obj2.getName());
                                }
                            });
                            asset_spnr.setItems(assets, slctd_asset, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(Boolean[] selected) {
                                    slctd_asset = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            slctd_asset.add(assets.get(i).getId());
                                    }
                                }
                            });
                        } else if (url.contains(All_Api.getStandards)) {
                            ArrayList standards = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("standards"), Constant_model[].class)));
                            standrd_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, standards));

                            ArrayList bodys = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("body"), Constant_model[].class)));
                            article_body_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, bodys));

                            ArrayList certificates = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("certificates"), Constant_model[].class)));
                            notified_certificate_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, certificates));

                            insTyps = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("inspectionType"), Constant_model[].class)));
                            ins_typ_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, insTyps));

                            setSpinnerData();
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

    private void setSpinnerData() {
        if (selected_asset_series != null) {
            if (insTyps != null && insTyps.size() > 0 && selected_asset_series.getType() != null) {
                int t_ind = findIndex(insTyps, selected_asset_series.getType());
                if (t_ind > -1) {
                    ins_typ_spinr.setSelection(t_ind);
                }
            }
        }

    }

    private int findIndex(ArrayList<Constant_model> list, String string) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return index;

    }

    public Product_model selected_asset_series;

    private void chooseAssetDialog(ArrayList<Constant_model> kits) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select Asset Kit from the list below");
        if (kits != null && kits.size() > 0) {
            OnItemClickListener onItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position, View v, Object data) {
                    Constant_model slctdAsset = (Constant_model) data;
                    if (slctdAsset != null) {
                        get_Product_data(All_Api.product_service + slctdAsset.getId() + "?client_id=" + client_id);
                    }
                    dialog.dismiss();
                }
            };
            Log.e("data length ", " is " + kits.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), kits, onItemClickListener);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1)
//                        setSelection(ad.lastSelected);
                        dialog.cancel();
                }
            });
            srch_prdct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!srch_prdct.getText().toString().equals("")) {
                        filter(editable.toString().toLowerCase(), kits, ad);
                    }
                }
            });

        }
        dialog.show();

    }

    void filter(String filter_txt, ArrayList<Constant_model> assets, CustomRecyclerAdapter ad) {
        List<Constant_model> temp = new ArrayList();
        for (Constant_model d : assets) {
            if (d.getName().toLowerCase().contains(filter_txt)) {
                temp.add(d);
            }
        }
        ad.UpdateData(temp);
    }


    @SuppressLint("HandlerLeak")
    private void get_Product_data(String product_service) {
        selected_asset_series = null;
        NetworkRequest.getProduct(getActivity(), product_service, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    selected_asset_series = DataHolder_Model.getInstance().getProduct_models().get(0);
                    heading_tv.setText("Edit Asset Kit Details");
                    baseActivity.headerTv.setText("Edit Asset Kit Details");
                    msg_tv.setText("Please edit the inspection parameters according to your requirements");
                }
                initView();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_asset:
                asset_spnr.performClick();
                break;
            case R.id.slct_asset_kit_tv:
                if (asset_series != null && asset_series.size() > 0)
                    chooseAssetDialog(asset_series);
                else show_snak(baseActivity, "Template Assets not available");
                break;
            case R.id.drp_dwn_2:
                View view = (LinearLayout) ins_typ_spinr.getParent();
                if (view.getVisibility() == VISIBLE) {
                    Gone(view);
                    rotate_image(drp_dwn_2, 270, 180);
                } else {
                    visible(view);
                    rotate_image(drp_dwn_2, 180, 270);
                }
                break;
            case R.id.drp_dwn_3:
                View view2 = (LinearLayout) kstatus_spinr.getParent();
                if (view2.getVisibility() == VISIBLE) {
                    Gone(view2);
                    rotate_image(drp_dwn_3, 270, 180);
                } else {
                    visible(view2);
                    rotate_image(drp_dwn_3, 180, 270);
                }
                break;
            case R.id.drp_dwn_4:
                View view3 = (LinearLayout) standrd_spinr.getParent();
                if (view3.getVisibility() == VISIBLE) {
                    Gone(view3);
                    rotate_image(drp_dwn_4, 270, 180);
                } else {
                    visible(view3);
                    rotate_image(drp_dwn_4, 180, 270);
                }
                break;
            case R.id.choose_img:
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        asset_img.setVisibility(View.VISIBLE);
                        AppUtils.load_image_file(imagePath, asset_img);
                    }
                });
                break;
            case R.id.continue_btn:
                if (isEmpty(aset_edt) || isEmpty(desc_edt) || imagePath.length() < 2) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                assetModel.setClient_id(client_id);
                assetModel.setUser_id(user_id);
                assetModel.setSeries_code(aset_edt.getText().toString());
                assetModel.setDescription(desc_edt.getText().toString());
                assetModel.setAssets(slctd_asset);
                assetModel.setInspection_type(slctd_inspection_type);
                assetModel.setFrequency_series(ins_freqM_edt.getText().toString());
                assetModel.setFrequency_hours(ins_freqH_edt.getText().toString());
                assetModel.setGeo_fancing(((RadioButton) geo_rg.findViewById(geo_rg.getCheckedRadioButtonId())).getText().toString());
                assetModel.setWork_permit(((RadioButton) wpermit_rg.findViewById(wpermit_rg.getCheckedRadioButtonId())).getText().toString());
//                "status":"value",
                assetModel.setStandard(slctd_standrd);
                assetModel.setBody(slctd_body);
                assetModel.setCertificate(slctd_certificate);
                assetModel.setCertificate_text(certificate_edt.getText().toString());

                assetModel.setSeries_image(imagePath);
//                assetModel.setComponent_inspection(((RadioButton) inspection_rg.findViewById(inspection_rg.getCheckedRadioButtonId())).getText().toString());

                if (!type.equals("") && type.equals("pro")) {
                    postData();
                } else
                    show_Duplicatealert("confirmation", getResString("lbl_masterdata_add"));
                break;
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

    private void postData() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getGson().toJson(assetModel));
            String path = jsonObject.getString("series_image");
            if (!path.contains("http")) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                if (path != null && !path.equals("") && new File(path).exists())
                    jsonObject.put("series_image", "data:" + filetype + ";base64," + AppUtils.Image_toBase64(path));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.add_asset_series, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            if (!type.equals("") && type.equals("pro")) {
                                LoadFragment.backTo_fragment(baseActivity, "Link QR code to an asset");
                            } else
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

}
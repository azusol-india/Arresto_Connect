package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.AddSubAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.SubAssetModel;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

public class AddSubAsset_Fragment extends Base_Fragment implements View.OnClickListener {
    View view;
    AddSubAssetModel assetModel;

    public static AddSubAsset_Fragment newInstance() {
        AddSubAsset_Fragment fragmentFirst = new AddSubAsset_Fragment();
//        Bundle args = new Bundle();
//        args.putString("heading", heading);
//        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    //    TextInputLayout asset_input;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.addsubasset_fragment, parent, false);
            if (getArguments() != null) {
//                heading = getArguments().getString("heading");
            }
            assetModel = AddSubAssetModel.getInstance();
            all_Ids();
            fetch_data(All_Api.getAllSubassets + client_id);
            fetch_data(All_Api.getObservations + client_id);
            fetch_data(All_Api.getResults + client_id);
            fetch_data(All_Api.getStandards + client_id);
            setdata();
        }
        return view;
    }

    private void setdata() {
        if (assetModel.getSubasset_code() != null) {
            aset_edt.setText(assetModel.getSubasset_code());
            desc_edt.setText(assetModel.getDescription());
            asset_img.setVisibility(View.VISIBLE);
            AppUtils.load_image(assetModel.getImage(), asset_img);
        }
    }

    private void set_data(SubAssetModel subAssetModel) {
        desc_edt.setText(subAssetModel.getSubAssetsDescription());
        aset_edt.setText(subAssetModel.getSubAssetsCode());
        asset_img.setVisibility(View.VISIBLE);
        imagePath = subAssetModel.getSubAssetsImagepath();
        if (!imagePath.equals("") && !imagePath.contains("http")) {
            if (imagePath.startsWith("."))
                imagePath = imagePath.substring(2);
            imagePath = Host + imagePath;
        }
        AppUtils.load_image(imagePath, asset_img);
        result_spnr.performClick();
    }

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };
    FloatingActionButton choose_img;
    MaterialButton continue_btn;
    EditText desc_edt;
    TextView aset_edt;
    ImageView asset_img;
    private RadioGroup repair_rg;
    private Spinner uom_spinr, ins_typ_spinr;
    private MultiSpinner result_spnr, observation_spnr;

    private void all_Ids() {
        continue_btn = view.findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        desc_edt = view.findViewById(R.id.desc_edt);
        asset_img = view.findViewById(R.id.asset_img);
        choose_img = view.findViewById(R.id.choose_img);
        aset_edt = view.findViewById(R.id.aset_edt);
        choose_img.setOnClickListener(this);
//        new CustomTextWatcher(aset_edt, textWatcher);
        new CustomTextWatcher(desc_edt, textWatcher);

        repair_rg = view.findViewById(R.id.repair_rg);
        uom_spinr = view.findViewById(R.id.uom_spinr);
        ins_typ_spinr = view.findViewById(R.id.ins_typ_spinr);
        result_spnr = view.findViewById(R.id.result_spnr);
        observation_spnr = view.findViewById(R.id.observation_spnr);
        repair_rg.check(repair_rg.getChildAt(1).getId());


        aset_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subAssets != null && subAssets.size() > 0) {
                    chooseSubassetDialog(subAssets);
                } else {
                    fetch_data(All_Api.getAllSubassets + client_id);
                }
            }
        });
        ins_typ_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_inspection_type = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        uom_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slctd_uom = ((Constant_model) parent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    String slctd_uom, slctd_inspection_type;
    List<String> slctd_observation, slctd_result;
    private ArrayList<Constant_model> observations = new ArrayList<>();
    private ArrayList<Constant_model> expectedResults = new ArrayList<>();
    private ArrayList<Constant_model> subAssets = new ArrayList<>();

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (url.contains(All_Api.getAllSubassets)) {
                            subAssets = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Collections.sort(subAssets, new Comparator<Constant_model>() {
                                public int compare(Constant_model obj1, Constant_model obj2) {
                                    return obj1.getName().compareToIgnoreCase(obj2.getName());
                                }
                            });
                            chooseSubassetDialog(subAssets);
                        } else if (url.contains(All_Api.getObservations)) {
                            observations = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
//                            ArrayAdapter name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, observations);
                            observation_spnr.setItems(observations, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(Boolean[] selected) {
                                    slctd_observation = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            slctd_observation.add(observations.get(i).getId());
                                    }
                                }
                            });
                        } else if (url.contains(All_Api.getResults)) {
                            expectedResults = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            result_spnr.setItems(expectedResults, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(Boolean[] selected) {
                                    slctd_result = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            slctd_result.add(expectedResults.get(i).getId());
                                    }
                                    observation_spnr.performClick();
                                }
                            });
                        } else if (url.contains(All_Api.getStandards)) {
                            ArrayList uoms = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("uom"), Constant_model[].class)));
                            uom_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, uoms));
                            uom_spinr.setSelection(1);
                            ArrayList insTyps = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("inspectionType"), Constant_model[].class)));
                            ins_typ_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, insTyps));
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

    private void chooseSubassetDialog(ArrayList<Constant_model> subAssets) {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        dialog.setTitle("Select Sub Asset");
        TextView header = dialog.findViewById(R.id.header);
        header.setText("Select Sub Asset");
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        CustomRecyclerAdapter ad = new CustomRecyclerAdapter(baseActivity, subAssets);
        lvLangs.setAdapter(ad);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object slected_item= ad.getItem(ad.lastSelected);
                if(slected_item!=null){
                fetchSubAsset(slected_item.toString());}
                dialog.dismiss();
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
//                if (!srch_prdct.getText().toString().equals(""))
//                    ad.getFilter().filter(srch_prdct.getText().toString());
                filter(editable.toString().toLowerCase(), subAssets, ad);
            }
        });
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_btn:
                if (isEmpty(aset_edt) || isEmpty(desc_edt) || imagePath.length() < 2) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                assetModel.setClient_id(client_id);
                assetModel.setSubasset_code(aset_edt.getText().toString());
                assetModel.setDescription(desc_edt.getText().toString());
                assetModel.setImage(imagePath);
                assetModel.setUom(slctd_uom);
                assetModel.setInspection_type(slctd_inspection_type);
                assetModel.setRepair(((RadioButton) repair_rg.findViewById(repair_rg.getCheckedRadioButtonId())).getText().toString());
                assetModel.setResults(slctd_result);
                assetModel.setObservations(slctd_observation);
                postData();

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
        }
    }

    private void postData() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getGson().toJson(assetModel));
            String path = jsonObject.getString("image");
            if (!path.contains("http")) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                if (path != null && !path.equals("") && new File(path).exists())
                    jsonObject.put("subasset_image", "data:" + filetype + ";base64," + AppUtils.Image_toBase64(path));
            } else {
                jsonObject.put("subasset_image", path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.add_subasset, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            show_Duplicatealert();
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

    private void fetchSubAsset(final String assetcode1) {
        NetworkRequest networkRequest = new NetworkRequest(baseActivity);
        networkRequest.make_get_request(All_Api.subasset_service + assetcode1 + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("status_code")) {
                            if (jsonObject.getString("status_code").equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                ArrayList<SubAssetModel> subAssetModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), SubAssetModel[].class)));
                                if (subAssetModels.size() > 0)
                                    set_data(subAssetModels.get(0));
                            } else {
                                show_snak(baseActivity, "" + jsonObject.getString("message"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
            }
        });

    }

    public AlertDialog show_Duplicatealert() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResString("lbl_confirmation"))
                .setMessage(getResString("lbl_new_subasset"))
                .setPositiveButton(getResString("lbl_duplicate"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        homeActivity.submit_action("ins_addsubAsset");
                    }
                })
                .setNegativeButton(getResString("lbl_no"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        homeActivity.load_home_fragment(false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}


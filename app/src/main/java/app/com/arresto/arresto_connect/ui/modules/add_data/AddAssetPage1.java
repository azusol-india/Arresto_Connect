package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;
import static app.com.arresto.arresto_connect.ui.modules.add_data.AddAssetPager.selected_asset;

public class AddAssetPage1 extends Base_Fragment implements View.OnClickListener {
    View view;
    AddAssetModel assetModel;

    public static AddAssetPage1 newInstance(String heading) {
        AddAssetPage1 fragmentFirst = new AddAssetPage1();
        Bundle args = new Bundle();
        args.putString("heading", heading);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    //    TextInputLayout asset_input;

    String heading;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_asset_page1, parent, false);
            if (getArguments() != null) {
                heading = getArguments().getString("heading");
            }
            assetModel = AddAssetModel.getInstance();
            all_Ids();
            setdata();
        }
        return view;
    }

    private void setdata() {
        if (selected_asset != null) {
            aset_edt.setText(selected_asset.getComponent_code());
            desc_edt.setText(selected_asset.getComponent_description());
            asset_img.setVisibility(View.VISIBLE);
            imagePath = selected_asset.getComponent_imagepath();
            if (!imagePath.equals("") && !imagePath.contains("http")) {
                if (imagePath.startsWith("."))
                    imagePath = imagePath.substring(2);

                imagePath = Host + imagePath;
            }
            AppUtils.load_image(imagePath, asset_img);
            slctd_Sasset = new ArrayList<>();
            String subAssets_txt = selected_asset.getComponent_sub_assets();
            if (subAssets_txt != null && !subAssets_txt.equals("0") && !subAssets_txt.equals("")) {
                final List<String> sub_ast_name = Arrays.asList(subAssets_txt.split("##"));
                for (int j = 0; j < sub_ast_name.size(); j++) {
                    String[] row_data = sub_ast_name.get(j).split("#");
                    if (row_data[0] != null && !row_data[0].equals(""))
                        slctd_Sasset.add(row_data[0]);
                }
            }

        }
        if (assetModel.getAsset_code() != null) {
            desc_edt.setText(assetModel.getDescription());
            asset_img.setVisibility(View.VISIBLE);
            AppUtils.load_image(assetModel.getAsset_image(), asset_img);
        }
        fetch_data(All_Api.getAllSubassets + client_id);
    }

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };
    TextView heading_tv;
    FloatingActionButton choose_img;
    MaterialButton continue_btn;
    EditText desc_edt, aset_edt;
    private MultiSpinner subasset_spnr;
    ImageView asset_img;

    private void all_Ids() {
        continue_btn = view.findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);
        heading_tv = view.findViewById(R.id.heading_tv);
        desc_edt = view.findViewById(R.id.desc_edt);
        asset_img = view.findViewById(R.id.asset_img);
        choose_img = view.findViewById(R.id.choose_img);
        aset_edt = view.findViewById(R.id.aset_edt);
        subasset_spnr = view.findViewById(R.id.subasset_spnr);
        heading_tv.setText(heading);
        choose_img.setOnClickListener(this);
        new CustomTextWatcher(aset_edt, textWatcher);
        new CustomTextWatcher(desc_edt, textWatcher);
    }

    List<String> slctd_Sasset;
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
                            subasset_spnr.setItems(subAssets, slctd_Sasset, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(Boolean[] selected) {
                                    slctd_Sasset = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            slctd_Sasset.add(subAssets.get(i).getId());
                                    }
                                }
                            });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_btn:
                if (isEmpty(aset_edt) || isEmpty(desc_edt) || imagePath.length() < 2) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                assetModel.setClient_id(client_id);
                assetModel.setUser_id(user_id);
                assetModel.setAsset_code(aset_edt.getText().toString());
                assetModel.setDescription(desc_edt.getText().toString());
                assetModel.setSub_assets(slctd_Sasset);
                assetModel.setAsset_image(imagePath);
                scrollPager.scrollTo(1);
                break;
//            case R.id.save_btn:
//                if (isEmpty(aset_edt) || isEmpty(desc_edt) || isEmpty(ins_freqM_edt) || isEmpty(pdm_freq_edt) || isEmpty(certificate_edt) || isEmpty(lifeH_edt)
//                        || isEmpty(lifeM_edt) || imagePath.length() < 2) {
//                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
//                    return;
//                }
//                assetModel.setClient_id(client_id);
//                assetModel.setAsset_code(aset_edt.getText().toString());
//                assetModel.setDescription(desc_edt.getText().toString());
//                assetModel.setUom(slctd_uom);
//                assetModel.setInspection_type(slctd_inspection_type);
//                assetModel.setFrequency_asset(ins_freqM_edt.getText().toString());
//                assetModel.setFrequency_hours(ins_freqH_edt.getText().toString());
//                assetModel.setPdm_frequency(pdm_freq_edt.getText().toString());
//                assetModel.setRepair(((RadioButton) repair_rg.findViewById(repair_rg.getCheckedRadioButtonId())).getText().toString());
//                assetModel.setGeo_fancing(((RadioButton) geo_rg.findViewById(geo_rg.getCheckedRadioButtonId())).getText().toString());
//                assetModel.setWork_permit(((RadioButton) wpermit_rg.findViewById(wpermit_rg.getCheckedRadioButtonId())).getText().toString());
//                assetModel.setResults(slctd_result);
//                assetModel.setObservations(slctd_observation);
//                assetModel.setSub_assets(slctd_Sasset);
//                assetModel.setStandard(slctd_standrd);
//                assetModel.setBody(slctd_body);
//                assetModel.setCertificate(slctd_certificate);
//                assetModel.setCertificate_text(certificate_edt.getText().toString());
//                assetModel.setLifespan_hours(lifeH_edt.getText().toString());
//                assetModel.setLifespan_month(lifeM_edt.getText().toString());
//                assetModel.setAsset_image(imagePath);
//                LoadFragment.replace(new Add_masterData(), getActivity(), "Add Master data");
//                break;
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

    AddAssetPager.ScrollPager scrollPager;

    public void setscrolleListner(AddAssetPager.ScrollPager scrollPager) {
        this.scrollPager = scrollPager;
    }
}

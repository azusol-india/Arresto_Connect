package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.ui.modules.add_data.AddAssetPager.selected_asset;

public class AddAssetPage2 extends Base_Fragment implements View.OnClickListener {
    View view;
    AddAssetModel assetModel;

    String heading;

    public static AddAssetPage2 newInstance(String heading) {
        AddAssetPage2 fragmentFirst = new AddAssetPage2();
        Bundle args = new Bundle();
        args.putString("heading", heading);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_asset_page2, parent, false);
            assetModel = AddAssetModel.getInstance();
            if (getArguments() != null) {
                heading = getArguments().getString("heading");
            }
            all_Ids();
            setdata();
            fetch_data(All_Api.getObservations + client_id);
            fetch_data(All_Api.getResults + client_id);
            fetch_data(All_Api.getStandards + client_id);
        }
        return view;
    }

    TextView heading_tv;
    private RadioGroup repair_rg, geo_rg, wpermit_rg;
    private Spinner uom_spinr, ins_typ_spinr;
    private MultiSpinner result_spnr, observation_spnr;
    MaterialButton back_btn, continue_btn;

    private void all_Ids() {
        back_btn = view.findViewById(R.id.back_btn);
        continue_btn = view.findViewById(R.id.continue_btn);
        back_btn.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        heading_tv = view.findViewById(R.id.heading_tv);
        repair_rg = view.findViewById(R.id.repair_rg);
        geo_rg = view.findViewById(R.id.geo_rg);
        wpermit_rg = view.findViewById(R.id.wpermit_rg);
        uom_spinr = view.findViewById(R.id.uom_spinr);
        ins_typ_spinr = view.findViewById(R.id.ins_typ_spinr);
        result_spnr = view.findViewById(R.id.result_spnr);
        observation_spnr = view.findViewById(R.id.observation_spnr);
        repair_rg.check(repair_rg.getChildAt(1).getId());
        geo_rg.check(geo_rg.getChildAt(1).getId());
        wpermit_rg.check(wpermit_rg.getChildAt(1).getId());

        heading_tv.setText(heading);
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

    private void setdata() {
        if (selected_asset != null) {
            if (selected_asset.getComponent_repair().equals("yes"))
                repair_rg.check(repair_rg.getChildAt(0).getId());
            else
                repair_rg.check(repair_rg.getChildAt(1).getId());
            if (selected_asset.getComponent_geo_fancing().equals("yes"))
                geo_rg.check(geo_rg.getChildAt(0).getId());
            else
                geo_rg.check(geo_rg.getChildAt(1).getId());
            if (selected_asset.getComponent_work_permit().equals("yes"))
                wpermit_rg.check(wpermit_rg.getChildAt(0).getId());
            else
                wpermit_rg.check(wpermit_rg.getChildAt(1).getId());

            slctd_result = new ArrayList<>();
            String ex_results = selected_asset.getComponent_expectedresult();
            if (ex_results != null && !ex_results.equals("0") && !ex_results.equals("")) {
                slctd_result = new ArrayList(Arrays.asList(ex_results.split("##")));
            }
            slctd_observation = new ArrayList<>();
            String obser_string = selected_asset.getComponent_observation();
            if (obser_string != null && !obser_string.equals("0") && !obser_string.equals("")) {
                final List<String> obs = Arrays.asList(obser_string.split("##"));
                for (int j = 0; j < obs.size(); j++) {
                    String[] row_data = obs.get(j).split("#");
                    if (row_data[1] != null && !row_data[1].equals(""))
                        slctd_observation.add(row_data[1]);
                }
            }
        }
    }

    String slctd_uom, slctd_inspection_type;

    List<String> slctd_observation, slctd_result;
    private ArrayList<Constant_model> observations = new ArrayList<>();
    private ArrayList<Constant_model> expectedResults = new ArrayList<>();

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (url.contains(All_Api.getObservations)) {
                            observations = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
//                            ArrayAdapter name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, observations);
                            observation_spnr.setItems(observations, slctd_observation, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
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
                            result_spnr.setItems(expectedResults,slctd_result, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(Boolean[] selected) {
                                    slctd_result = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            slctd_result.add(expectedResults.get(i).getId());
                                    }
                                }
                            });
//                            product_edt.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, expectedResults));
                        } else if (url.contains(All_Api.getStandards)) {
//                            ArrayList standards = new ArrayList<>(Arrays.asList(gson.fromJson(object.getString("standards"), Constant_model[].class)));
//                            standrd_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, standards));
//
//                            ArrayList bodys = new ArrayList<>(Arrays.asList(gson.fromJson(object.getString("body"), Constant_model[].class)));
//                            article_body_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, bodys));
//
//                            ArrayList certificates = new ArrayList<>(Arrays.asList(gson.fromJson(object.getString("certificates"), Constant_model[].class)));
//                            notified_certificate_spinr.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, certificates));

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                scrollPager.scrollTo(0);
                break;
            case R.id.continue_btn:
                assetModel.setUom(slctd_uom);
                assetModel.setInspection_type(slctd_inspection_type);
                assetModel.setRepair(((RadioButton) repair_rg.findViewById(repair_rg.getCheckedRadioButtonId())).getText().toString());
                assetModel.setGeo_fancing(((RadioButton) geo_rg.findViewById(geo_rg.getCheckedRadioButtonId())).getText().toString());
                assetModel.setWork_permit(((RadioButton) wpermit_rg.findViewById(wpermit_rg.getCheckedRadioButtonId())).getText().toString());
                assetModel.setResults(slctd_result);
                assetModel.setObservations(slctd_observation);
                scrollPager.scrollTo(2);
                break;
        }
    }

    AddAssetPager.ScrollPager scrollPager;

    public void setscrolleListner(AddAssetPager.ScrollPager scrollPager) {
        this.scrollPager = scrollPager;
    }
}

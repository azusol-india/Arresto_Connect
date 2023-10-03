package app.com.arresto.arresto_connect.ui.modules.inspection;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResSize;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.adapters.Report_listAdapter.lastInspectData;
import static app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems.isSpare;
import static app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems.spare_code;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.custom_views.switch_lib.SwitchTrackTextDrawable;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.Observation_Model;
import app.com.arresto.arresto_connect.data.models.ReportInspectionModel;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.database.inspection_tables.Asset_Positions_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.adapters.Quality_image_adepter;
import app.com.arresto.arresto_connect.ui.adapters.Recycler_adapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class InspectionAssetFragment_V1 extends Base_Fragment implements View.OnClickListener {
    private Spinner remark_spinr;
    private TextView inspection_text, imege_ch_txt;
    private View view;
    private ArrayList<String> pass_fail_image, observation_main, observation_id, befor_img_arr,
            aftr_img_arr;
    private String observation, inspection, result, asset, component_sub_assets;
    private int pos, component_pos;
    private String[] img_ch_txt = {getResString("lbl_pass_image"), getResString("lbl_fail_image")
            , getResString("lbl_repair_image")};
    private RecyclerViewPager recyclerView;
    private Data_daowload data_daowload;
    private RecyclerView before_image_view, aftr_image_view;
    private Quality_image_adepter before_adapter, after_adapter;
    private LinearLayout obsr_cntnr_lay;
    private ArrayAdapter<String> na_adapter;
    private TextView continueBtn, observer_heading, bfr_msg_tv, aftr_msg_tv;
    private RadioGroup radioGroup;
    Switch before_check, after_check;
    private FloatingActionButton openCamera_after, opencamera_before;

    public String image_dir = directory + "inspection/" + unique_id + "/";

    public String inspection_id;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d("TAG", "Activity FragmentView:InspectionAssetFragment_V1");
        if (view == null) {
            view = inflater.inflate(R.layout.inspectionassetfragment, parent, false);
            find_id();
            set_listner();
            data_daowload = new Data_daowload(getActivity());
            if (getArguments() != null) {
//              String description = getArguments().getString("description");
                component_sub_assets = getArguments().getString("component_sub_assets");
                inspection = getArguments().getString("inspection");
                result = getArguments().getString("result");
                asset = getArguments().getString("asset");
//               String return_id1 = getArguments().getString("return_id1");
                observation = getArguments().getString("observation");
                pass_fail_image = getArguments().getStringArrayList("pass_fail_imagepath");
                pos = getArguments().getInt("position");
                component_pos = getArguments().getInt("component_pos");
                inspection_id = getArguments().getString("inspection_id", "");
                if (!inspection_id.equals("")) {
                    getLastInspectionModel();
                }
            }
            array_object();

            before_image_view.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                    GridLayoutManager.VERTICAL, false));
            before_image_view.setPadding(3, 6, 3, 6);

            aftr_image_view.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                    GridLayoutManager.VERTICAL, false));
            aftr_image_view.setPadding(3, 6, 3, 6);
            before_adapter = new Quality_image_adepter(baseActivity, befor_img_arr);
            after_adapter = new Quality_image_adepter(baseActivity, aftr_img_arr);
            before_image_view.setAdapter(before_adapter);
            aftr_image_view.setAdapter(after_adapter);
            observation_main.add(getResString("lbl_pl_slct_msg"));
            setData();
            String[] observation_arr = observation.split("##");
            String[] result_arr = result.split("##");
            if (observation.length() > 0) {
                for (String anObservation : observation_arr) {
                    if (anObservation.contains("#")) {
                        String[] n1 = anObservation.split("#");
                        if (n1.length > 1) {
                            observation_id.add(n1[0]);
                            observation_main.add(n1[1]);
                        }
                    }
                }
            }
            observation_models = new ArrayList<>();
            if (result.length() > 0) {
                for (int i = 0; i < result_arr.length; i++) {
                    Observation_Model observation_model = new Observation_Model();
                    observation_model.setExpected_result(result_arr[i]);
                    observation_models.add(observation_model);
                    add_single_observ(observation_model, getOldExpectedData(result_arr[i]));
                }
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false));
            Recycler_adapter mAdapter = new Recycler_adapter(baseActivity, pass_fail_image);
            recyclerView.setAdapter(mAdapter);

            radioGroup.check(radioGroup.getChildAt(0).getId());
            imege_ch_txt.setText(img_ch_txt[0]);
            recyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {

                public void OnPageChanged(int i, int i1) {
                    radioGroup.check(radioGroup.getChildAt(i1).getId());
                    imege_ch_txt.setText(img_ch_txt[i1]);
                }
            });
            na_adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv,
                    new ArrayList<>(Arrays.asList("NA")));
            ArrayAdapter adapter3 = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv,
                    remark_arr);
            adapter3.setDropDownViewResource(R.layout.spinner_item);
            remark_spinr.setAdapter(adapter3);
            show_showCase();
            return view;
        } else {
            return view;
        }
    }

    private int findIndex(ArrayList list, String string) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Constant_model) {
                if (((Constant_model) list.get(i)).getId().equals(string)) {
                    return i;
                }
            } else {
                if (list.get(i).toString().equalsIgnoreCase(string)) {
                    return i;
                }
            }
        }
        return index;

    }

    private ReportInspectionModel.ObservationType getOldExpectedData(String str) {
        if (lastInspectionModel != null && lastInspectionModel.getObservationType().size() > 0) {
            for (ReportInspectionModel.ObservationType item :
                    lastInspectionModel.getObservationType()) {
                if (item.getExpected_result() != null && item.getExpected_result().equalsIgnoreCase(str)) {
                    return item;
                }
            }
            return null;
        } else return null;
    }

    ReportInspectionModel lastInspectionModel;

    private void getLastInspectionModel() {
        for (ReportInspectionModel inspectionModel : lastInspectData) {
            if (inspectionModel.getAssetName().equalsIgnoreCase(asset) && inspectionModel.getSubAssetName().equalsIgnoreCase(component_sub_assets)) {
                lastInspectionModel = inspectionModel;
                return;
            }
        }
    }

    ArrayList<String> remark_arr = new ArrayList<>(Arrays.asList(getResString("lbl_remark1"),
            getResString("lbl_remark2"), getResString("lbl_remark3"), getResString("lbl_remark4")
            , getResString("lbl_remark5"), getResString("lbl_remark6"), getResString("lbl_remark7"
            ), getResString("lbl_remark8"), getResString("lbl_remark9"), getResString(
                    "lbl_remark10"), getResString("lbl_othr_st")));

    ArrayList<String> action_taken =
            new ArrayList<>(Arrays.asList(getResString("lbl_pl_slct_msg"), getResString(
                    "lbl_remove_and_repair"), getResString("lbl_done"), getResString("lbl_ok_st")
                    , getResString("lbl_no_action_taken"), getResString("lbl_remove_and_replace")));

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList(getResString(
                "lbl_featured_image_for_repair"), getResString(
                        "lbl_observation_actionproposed_actiontaken")));
        ArrayList<View> views = new ArrayList(Arrays.asList(recyclerView, observer_heading));
        Add_Showcase.getInstance(getActivity()).setData(mesages, views);
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "inspection process");
    }

    public void find_id() {
        observer_heading = view.findViewById(R.id.observer_heading);
        observer_heading.setText(getResString("lbl_expected_result"));
        recyclerView = view.findViewById(R.id.recycler);
        radioGroup = view.findViewById(R.id.radioGroup);
        inspection_text = view.findViewById(R.id.typ_of_inspection);
        continueBtn = view.findViewById(R.id.continue_btn);
        view.findViewById(R.id.expected_txt).setVisibility(View.GONE);
        view.findViewById(R.id.expected_tv).setVisibility(View.GONE);
        imege_ch_txt = view.findViewById(R.id.imege_ch_txt);

        opencamera_before = view.findViewById(R.id.open_camera_before);
        openCamera_after = view.findViewById(R.id.open_camera);

        before_image_view = view.findViewById(R.id.before_image_view);
        aftr_image_view = view.findViewById(R.id.aftr_image_view);

        view.findViewById(R.id.addview_btn).setVisibility(View.GONE);

        obsr_cntnr_lay = view.findViewById(R.id.obsrvsn_lay_cntnr);
        remark_edt = view.findViewById(R.id.remark);
        status_rg = view.findViewById(R.id.status_rg);
        remark_spinr = view.findViewById(R.id.remark_spinr);

        bfr_msg_tv = view.findViewById(R.id.bfr_msg_tv);
        aftr_msg_tv = view.findViewById(R.id.aftr_msg_tv);
        before_check = view.findViewById(R.id.before_check);
        after_check = view.findViewById(R.id.after_check);

        before_check.setTextOff(getResString("lbl_add_before_image"));
        before_check.setTextOn(getResString("lbl_skip_before_image"));
        after_check.setTextOff(getResString("lbl_add_after_image"));
        after_check.setTextOn(getResString("lbl_skip_after_image"));
        if (client_id.equalsIgnoreCase("376")) {
            before_check.setVisibility(View.GONE);
            after_check.setVisibility(View.GONE);
        } else {
            before_check.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(),
                    R.string.lbl_add_before_image, R.string.lbl_skip_before_image));
            after_check.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(),
                    R.string.lbl_add_after_image, R.string.lbl_skip_after_image));

            before_check.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    before_check.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    LayerDrawable drawable1 = (LayerDrawable) before_check.getThumbDrawable();
                    ((GradientDrawable) drawable1.getDrawable(0)).setSize((before_check.getWidth() / 2) - 25, getResSize(R.dimen.margin_30dp));
                    before_check.setThumbDrawable(drawable1);
                    after_check.setThumbDrawable(drawable1);
                }
            });
        }
    }


    private void set_listner() {
        opencamera_before.setOnClickListener(this);
        openCamera_after.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        before_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (after_check.isChecked() && !isChecked) {
                    show_snak(getActivity(), "After image skipped, you can't add before image");
                    before_check.setChecked(true);
                } else if (isChecked) {
                    ((ViewGroup) opencamera_before.getParent()).setVisibility(View.INVISIBLE);
                    bfr_msg_tv.setText(getResString("lbl_before_skip_msg"));
                    befor_img_arr.clear();
                    if (before_adapter != null) before_adapter.notifyDataSetChanged();
                } else {
                    ((ViewGroup) opencamera_before.getParent()).setVisibility(View.VISIBLE);
                    bfr_msg_tv.setText(getResString("lbl_ad_imgbfor_st"));
                }
            }
        });

        after_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ((ViewGroup) openCamera_after.getParent()).setVisibility(View.VISIBLE);
                    aftr_msg_tv.setText(getResString("lbl_ad_imgaftr_st"));
                } else if (!before_check.isChecked() && !client_id.equals("952")) {
                    show_snak(getActivity(), "Before image selected, you can't skip after image");
                    after_check.setChecked(false);
                } else {
                    ((ViewGroup) openCamera_after.getParent()).setVisibility(View.INVISIBLE);
                    aftr_msg_tv.setText(getResString("lbl_after_skip_msg"));
                    if (!client_id.equals("952")) {
                        before_check.setChecked(true);
                        befor_img_arr.clear();
                    }
                    aftr_img_arr.clear();
                    if (before_adapter != null) before_adapter.notifyDataSetChanged();
                    if (after_adapter != null) after_adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void setData() {
        inspection_text.setText(inspection);
        if (lastInspectionModel != null) {
            if (lastInspectionModel.getSkipFlag().equals("1")) {
                if (lastInspectionModel.getSkipResult().equalsIgnoreCase("pass")) {
                    status_rg.check(status_rg.getChildAt(0).getId());
                } else {
                    status_rg.check(status_rg.getChildAt(1).getId());
                }
                remark_edt.setText(lastInspectionModel.getSkipRemarks());
            }
            if (lastInspectionModel.getBeforeRepairImage().size() > 0) {
                befor_img_arr = lastInspectionModel.getBeforeRepairImage();
                before_adapter.add_item(befor_img_arr);
            }
            if (lastInspectionModel.getAfterRepairImage().size() > 0) {
                aftr_img_arr = lastInspectionModel.getAfterRepairImage();
                after_adapter.add_item(aftr_img_arr);
            }
        }

    }

    private void array_object() {
        observation_id = new ArrayList<>();
        observation_main = new ArrayList<>();
        befor_img_arr = new ArrayList<>();
        aftr_img_arr = new ArrayList<>();
    }

    public void startCamera(String name, String type) {
        chooseImage(image_dir, name, new OnImageCapture() {
            @Override
            public void onCapture(String path) {
                if (type.equals("before")) {
                    befor_img_arr.add(path);
                    before_adapter.add_item(befor_img_arr);
                } else if (type.equals("after")) {
                    aftr_img_arr.add(path);
                    after_adapter.add_item(aftr_img_arr);
                }
            }
        });
    }

    private RadioGroup status_rg;
    private EditText remark_edt;
    private String status;
    private String remark;

    public boolean isBlankObservation() {
        if (observation_models.size() > 0) {
            for (int i = 0; i < observation_models.size(); i++) {
                Observation_Model model = observation_models.get(i);
                if (model.getObservation() == null || (!model.getObservation().equalsIgnoreCase(getResString("lbl_ok_st")) && !model.getObservation().equalsIgnoreCase(getResString("lbl_not_applicable")) && !model.getObservation().equalsIgnoreCase("Other") && (model.getAction_proposed() == null || model.getAction_proposed().equals("") || model.getResult() == null || model.getResult().equals("")))) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_camera_before:
                startCamera("before_" + System.currentTimeMillis() + ".jpg", "before");
                break;
            case R.id.open_camera:
                startCamera("after_" + System.currentTimeMillis() + ".jpg", "after");
                break;
            case R.id.continue_btn:
                if (status_rg.getCheckedRadioButtonId() != -1) {
                    status =
                            ((RadioButton) status_rg.findViewById(status_rg.getCheckedRadioButtonId())).getText().toString();
//                    if (remark_spinr.getSelectedItem().toString().equalsIgnoreCase(getResString
//                    ("lbl_othr_st")))
                    remark = remark_edt.getText().toString();
//                    else
//                        remark = remark_spinr.getSelectedItem().toString();
                }
                if (observation_main.size() == 0) {
                    Toast.makeText(getActivity(), getResString("lbl_misng_dta_msg"),
                            Toast.LENGTH_LONG).show();
                } else if (befor_img_arr.size() == 0 && !before_check.isChecked() && (status == null || status.equals(""))) {
                    Toast.makeText(getActivity(), getResString("lbl_cptr_before_image"),
                            Toast.LENGTH_LONG).show();
                    befor_img_arr.clear();
                } else if (aftr_img_arr.size() == 0 && !after_check.isChecked() && (status == null || status.equals(""))) {
                    Toast.makeText(getActivity(), getResString("lbl_cptr_aftrImg_msg"),
                            Toast.LENGTH_LONG).show();
                    aftr_img_arr.clear();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            save_data_database();
                        }
                    }, 200);
                }
                break;
        }
    }

    private void updateAssetInspection(Inspection_Table inspection_table) {
        try {
            JSONObject params = new JSONObject(new Gson().toJson(inspection_table));
            JSONArray before_imge_data = new JSONArray();
            JSONArray after_imge_data = new JSONArray();
            if (!inspection_id.equals("")) params.put("inspection_id", inspection_id);
            params.put("client_id", client_id);
            Log.e("prams asset ", " is " + params);

            if (befor_img_arr != null) for (String fileName : befor_img_arr) {
                if (!fileName.contains("http"))
                    before_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
                else before_imge_data.put(fileName);
            }
            if (aftr_img_arr != null) for (String fileName : aftr_img_arr) {
                if (!fileName.contains("http"))
                    after_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
                else after_imge_data.put(fileName);
            }
            params.put("before_images", before_imge_data);
            params.put("after_images", after_imge_data);

            String url = All_Api.abinspectionForm_service_v2;
            NetworkRequest request = new NetworkRequest(baseActivity);
            request.make_contentpost_request(url, params,
                    new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response ", "  " + response);
                    try {
                        JSONObject res = new JSONObject(response);
                        if (res.has("success") && res.get("success") instanceof Boolean && res.getBoolean("success"))
                            go_next();
                        else AppUtils.show_snak(baseActivity, res.getString("success"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("error", message);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleSilentException(e);
        }
    }

    ArrayList<Observation_Model> observation_models;

    private void add_single_observ(Observation_Model observation_model,
                                   ReportInspectionModel.ObservationType oldExpectedData) {
        final View view1 =
                getActivity().getLayoutInflater().inflate(R.layout.observation_layout_v,
                        obsr_cntnr_lay, false);
        final TextView expected_tv = view1.findViewById(R.id.expected_tv);
        expected_tv.setText(observation_model.getExpected_result());
        final ImageView right_arow = view1.findViewById(R.id.right_arow);
        final LinearLayout observer_layer = view1.findViewById(R.id.observer_layer);
        final DialogSpinner dy_obser_spinr = view1.findViewById(R.id.dy_obser_spinr);
        final DialogSpinner dy_excerpt_spnr = view1.findViewById(R.id.dy_excerpt_spnr);
        final DialogSpinner dy_result_spnr = view1.findViewById(R.id.dy_result_spnr);
        dy_obser_spinr.setItems(observation_main, "");
        dy_result_spnr.setItems(action_taken, "");
        obsr_cntnr_lay.addView(view1);

        right_arow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = observer_layer;
                if (view.getVisibility() == VISIBLE) {
                    Gone(view);
                    rotate_image(right_arow, 0, -90);
                } else {
                    visible(view);
                    rotate_image(right_arow, -90, 0);
                }
            }
        });

        dy_obser_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = dy_obser_spinr.getSelectedItem().toString();
                if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg"))) {
                    observation_model.setObservation(new_slcted_itm);
                    observation_model.setObservation_id(observation_id.get(position - 1));
                }
                if (position > 0) {
                    if (new_slcted_itm.equalsIgnoreCase(getResString("lbl_ok_st")) || new_slcted_itm.equalsIgnoreCase(getResString("lbl_not_applicable")) || new_slcted_itm.equalsIgnoreCase("Other")) {
                        dy_excerpt_spnr.setEnabled(false);
                        dy_result_spnr.setEnabled(false);
                        dy_excerpt_spnr.setAdapter(na_adapter);
                        dy_result_spnr.setAdapter(na_adapter);
                        if (new_slcted_itm.equalsIgnoreCase("Other")) {
                            showRemarkDialog(dy_obser_spinr, observation_model);
                        }

                    } else {
                        dy_excerpt_spnr.setEnabled(true);
                        dy_result_spnr.setEnabled(true);
                        if (!observation_model.isCheckingOld())
                            dy_result_spnr.setItems(action_taken, "");
                        if (AppUtils.isNetworkAvailable(getActivity())) {
                            get_action_prposed_from_api(observation_model.getObservation_id(),
                                    dy_excerpt_spnr, oldExpectedData);
                        } else {
                            get_action_prposed1(observation_model.getObservation_id(),
                                    dy_excerpt_spnr);
                        }
                        observation_model.setCheckingOld(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        dy_excerpt_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = dy_excerpt_spnr.getSelectedItem().toString();
                if (!new_slcted_itm.equalsIgnoreCase("Na")) {
                    Constant_model item = (Constant_model) parent.getSelectedItem();
                    new_slcted_itm = item.toString();
                    if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg"))) {
                        observation_model.setAction_proposed(new_slcted_itm);
                        observation_model.setAction_proposed_id(item.getId());
                    }
                } else {
                    observation_model.setAction_proposed(new_slcted_itm);
                    observation_model.setAction_proposed_id("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        dy_result_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = dy_result_spnr.getSelectedItem().toString();
                if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg"))) {
                    observation_model.setResult(new_slcted_itm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (oldExpectedData != null) {
            int observationIndex = findIndex(observation_id, oldExpectedData.getObservationType());
            if (observationIndex > -1) {
                observation_model.setCheckingOld(true);
                right_arow.performClick();
                dy_obser_spinr.setLastSelected(observationIndex + 1);
            }
            int resultIndex = findIndex(action_taken, oldExpectedData.getResult());
            if (observationIndex > -1) {
                dy_result_spnr.setLastSelected(resultIndex);
            }
        }
    }


    private void get_action_prposed_from_api(String obser_id, final DialogSpinner exrpt_spiner,
                                             ReportInspectionModel.ObservationType oldExpectedData) {
        String url = All_Api.actionProposed_service + obser_id;
        new NetworkRequest(getActivity()).make_get_request(url,
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("success").equals("No data Found")) {
                        Log.e("string object", "running");
                    } else {
                        ArrayList excerpt = new ArrayList<>();
                        Constant_model constant_model = new Constant_model();
                        constant_model.setId("");
                        constant_model.setName(getResString("lbl_pl_slct_msg"));
                        excerpt.add(constant_model);
                        Object json = new JSONTokener(object.getString("success")).nextValue();
                        if (json instanceof JSONArray)
                            excerpt.addAll(Arrays.asList(AppUtils.getGson().fromJson(object.getString("success"), Constant_model[].class)));
                        ((ViewGroup) exrpt_spiner.getParent()).setVisibility(View.VISIBLE);
                        exrpt_spiner.setItems(excerpt, "");
                        if (oldExpectedData != null) {
                            int exrptIndex = findIndex(excerpt,
                                    oldExpectedData.getActionProposed());
                            if (exrptIndex > -1) {
                                exrpt_spiner.setLastSelected(exrptIndex);
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

    private void get_action_prposed1(String observation_id, DialogSpinner exrpt_spiner) {
        data_daowload.open();
        String subquery = "select action_id,action_propose from action_propose where " +
                "observation_id='" + observation_id + "'";
        Cursor c1 = data_daowload.getSingle_Rowdata(subquery);
        if (c1.getCount() > 0) {
            c1.moveToFirst();
            ArrayList excerpt = new ArrayList<>();
            Constant_model constant_model = new Constant_model();
            constant_model.setId("");
            constant_model.setName(getResString("lbl_pl_slct_msg"));
            excerpt.add(constant_model);
            do {
                constant_model = new Constant_model();
                constant_model.setId(c1.getString(0));
                constant_model.setName(c1.getString(1));
                excerpt.add(constant_model);
            } while (c1.moveToNext());
            ((ViewGroup) exrpt_spiner.getParent()).setVisibility(View.VISIBLE);
            exrpt_spiner.setItems(excerpt, "");
        }
        data_daowload.close();
    }


    public void showRemarkDialog(DialogSpinner dy_obser_spinr,
                                 Observation_Model observation_model) {
        final Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(false);
        builder.setContentView(R.layout.dialog_uin);
        TextView cancel_btn = builder.findViewById(R.id.cncl_btn);
        TextView ok_btn = builder.findViewById(R.id.ok_btn);
        final EditText edt_dialog = builder.findViewById(R.id.edt_dialog);
        builder.findViewById(R.id.header).setVisibility(View.VISIBLE);
        builder.findViewById(R.id.ex_tv).setVisibility(View.GONE);
        builder.findViewById(R.id.logo_img).setVisibility(View.GONE);

        edt_dialog.setHint("Remark");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark = edt_dialog.getText().toString();
                if (remark.isEmpty()) {
                    show_snak(baseActivity, "Please enter remark text!");
                } else {
                    observation_model.setObservation(remark);
                    observation_model.setObservation_id("Other");
                }
                builder.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dy_obser_spinr.setSelection(0);
                builder.dismiss();
            }
        });

        builder.show();
    }

    private void save_data_database() {
        if (isBlankObservation() && (status == null || status.equals(""))) {
            show_snak(getActivity(), getResString("lbl_blank_obsrv_msg"));
        } else {
            String expected_results = "", obsvsn_to_upld = "", excrt_to_upld = "", rslt_to_upld =
                    "";
            if (observation_models.size() > 0) {
                Observation_Model model = observation_models.get(0);
                expected_results = model.getExpected_result();
                obsvsn_to_upld = model.getObservation_id();
                excrt_to_upld = model.getAction_proposed_id();
                rslt_to_upld = model.getResult();
                for (int n = 1; n < observation_models.size(); n++) {
                    model = observation_models.get(n);
                    expected_results = expected_results + "##" + model.getExpected_result();
                    obsvsn_to_upld = obsvsn_to_upld + "##" + model.getObservation_id();
                    excrt_to_upld = excrt_to_upld + "##" + model.getAction_proposed_id();
                    rslt_to_upld = rslt_to_upld + "##" + model.getResult();
                }
            }
            checkIsSpare(rslt_to_upld);
            Inspection_Table inspection_table = new Inspection_Table();
            inspection_table.set_inspection_Asset(user_id, Static_values.unique_id, asset,
                    component_sub_assets, expected_results, obsvsn_to_upld, excrt_to_upld,
                    rslt_to_upld, status, remark, component_pos, pos);
            if (!inspection_id.equals("")) {
                updateAssetInspection(inspection_table);
            } else {
                Inspection_Table.Inspection_Asset_Dao inspectionAssetDao =
                        AppController.getInstance().getDatabase().getInspection_Asset_dao();
                inspection_table.setBefore_images(new Gson().toJson(befor_img_arr,
                        new TypeToken<ArrayList<String>>() {
                }.getType()));
                inspection_table.setAfter_images(new Gson().toJson(aftr_img_arr,
                        new TypeToken<ArrayList<String>>() {
                }.getType()));
                long id = inspectionAssetDao.insert(inspection_table);
                if (id > 0) {
                    go_next();
                } else {
                    show_snak(baseActivity, getResString("lbl_try_again_msg"));
                }
            }
        }
    }

    private void checkIsSpare(String result) {
        String asset_string;
        if (!component_sub_assets.equals("0") && !component_sub_assets.equals(""))
            asset_string = component_sub_assets;
        else asset_string = asset;
        if ((asset_string.toLowerCase().startsWith("pn9000") || asset_string.toLowerCase().startsWith("pn-9000") || asset_string.toLowerCase().startsWith("pn7000") || asset_string.toLowerCase().startsWith("pn-7000")) && result.contains(getResString("lbl_remove_and_repair"))) {
            isSpare = true;
            spare_code = asset_string;
        }
    }

    private void go_next() {
        if (component_sub_assets.equals("0") || component_sub_assets.equals("")) {
            AppController.getInstance().getDatabase().getAsset_position_dao().insert(new Asset_Positions_Table(user_id, Static_values.unique_id, component_pos));
            if (!InspectionListItems.selectedPosition.contains(component_pos)) {
                InspectionListItems.selectedPosition.add(component_pos);
            }
        } else {
            if (InspectionSubassetList.subasset_selectedPosition != null && !InspectionSubassetList.subasset_selectedPosition.contains(pos)) {
                InspectionSubassetList.subasset_selectedPosition.add(pos);
            }
        }
        if (getActivity() != null) getActivity().onBackPressed();
    }
}

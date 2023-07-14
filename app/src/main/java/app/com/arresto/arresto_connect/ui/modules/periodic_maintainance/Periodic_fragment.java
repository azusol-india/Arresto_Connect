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

package app.com.arresto.arresto_connect.ui.modules.periodic_maintainance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.custom_views.switch_lib.SwitchTrackTextDrawable;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Periodic_model;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.database.pdm_tables.Pdm_Inspected_steps_table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Periodic_Viewpager;
import app.com.arresto.arresto_connect.ui.adapters.Quality_image_adepter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResSize;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_name;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps.inspected_steps;

//import app.com.arresto.Arresto_Connect.appcontroller.AppController;

public class Periodic_fragment extends Base_Fragment implements View.OnClickListener {
    View view;
    DialogSpinner spinner, excerpt_spnr, resultSpnr;
    FloatingActionButton openCamera_after, opencamera_before;
    TextView inspection_text, expected_txt, process_tv;
    ArrayList<String> pass_fail_image, observation_main, excerpt_id, excerpt, befor_img_arr, aftr_img_arr;
    String slct_observId, slctd_ActnPrposeID, slct_ActnTakn;

    ArrayList<Periodic_model.Pdm_array> result_arr;
    String description, asset, return_id1, component_sub_assets;
    int pos, component_pos;
    RecyclerViewPager recyclerView;

    Periodic_Viewpager mAdapter;
    StringBuilder s = new StringBuilder();
    Data_daowload data_daowload;
    RecyclerView before_image_view, aftr_image_view;
    Quality_image_adepter before_adapter, after_adapter;
    LinearLayout obsr_cntnr_lay;
    private TextView bfr_msg_tv, aftr_msg_tv;
    MaterialButton continueBtn, skip_btn;
    Switch before_check, after_check;
    Periodic_model periodic_model;
    public String image_dir = directory + "pdm/" + unique_id + "/";
    int step_list_pos;

    LinearLayout radioGroup;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.periodic_fragment, parent, false);
            periodic_model = DataHolder_Model.getInstance().getSlctd_periodic_model();
            find_id();
            set_listner();
            step_list_pos = getArguments().getInt("pos");
            data_daowload = new Data_daowload(getActivity());

            if (getArguments() != null) {
                description = getArguments().getString("description");
                component_sub_assets = getArguments().getString("component_sub_assets");
                asset = getArguments().getString("asset");
                return_id1 = getArguments().getString("return_id1");
                pass_fail_image = getArguments().getStringArrayList("pass_fail_imagepath");
                pos = getArguments().getInt("position");
                component_pos = getArguments().getInt("component_pos");
            }
            array_object();

            before_image_view.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
            before_image_view.setPadding(3, 6, 3, 6);

            aftr_image_view.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
            aftr_image_view.setPadding(3, 6, 3, 6);
            before_adapter = new Quality_image_adepter(baseActivity, befor_img_arr);
            after_adapter = new Quality_image_adepter(baseActivity, aftr_img_arr);
            before_image_view.setAdapter(before_adapter);
            aftr_image_view.setAdapter(after_adapter);

            settext();

            result_arr = periodic_model.getPdm_expresults();
            for (int i = 0; i < result_arr.size(); i++) {
                if (i == result_arr.size() - 1)
                    s.append(i + 1 + ") " + result_arr.get(i).getType_name());
                else
                    s.append(i + 1 + ") " + result_arr.get(i).getType_name() + "\n");
            }
            expected_txt.setText(s);

            Display display = getActivity().getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mAdapter = new Periodic_Viewpager(getActivity(), periodic_model);
            recyclerView.setAdapter(mAdapter);

            change_rodioGroup(0);
            recyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
                public void OnPageChanged(int i, int i1) {
                    change_rodioGroup(i1);
                }
            });
            radioGroup.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(0);
                    change_rodioGroup(0);
                }
            });
            radioGroup.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(1);
                    change_rodioGroup(1);
                }
            });
            radioGroup.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(2);
                    change_rodioGroup(2);
                }


            });
//            ArrayList observations = periodic_model.getPdm_observations();
            Periodic_model.Pdm_array pdmArray = new Periodic_model.Pdm_array();
            pdmArray.setId("");
            pdmArray.setType_name(getResString("lbl_pl_slct_msg"));
            periodic_model.getPdm_observations().add(0, pdmArray);
            spinner.setItems(periodic_model.getPdm_observations(), "");
            resultSpnr.setItems(action_taken, "");

            main_spinr_onclk();
            return view;
        } else {
            return view;
        }
    }

    ArrayList<String> action_taken = new ArrayList<>(Arrays.asList(getResString("lbl_pl_slct_msg"), getResString("lbl_done"), getResString("lbl_ok_st")
            , getResString("lbl_remove_and_replace"), getResString("lbl_remove_and_repair"), getResString("lbl_no_action_taken")));

    public void find_id() {
        recyclerView = view.findViewById(R.id.recycler);
        spinner = view.findViewById(R.id.obser_spinr);
        resultSpnr = view.findViewById(R.id.result_spnr);
        inspection_text = view.findViewById(R.id.typ_of_inspection);
        inspection_text.setVisibility(View.GONE);
        excerpt_spnr = view.findViewById(R.id.excerpt_spnr);
        continueBtn = view.findViewById(R.id.continue_btn);
        skip_btn = view.findViewById(R.id.skip_btn);
        expected_txt = view.findViewById(R.id.expected_txt);
        process_tv = view.findViewById(R.id.process_tv);

        opencamera_before = view.findViewById(R.id.open_camera_before);
        openCamera_after = view.findViewById(R.id.open_camera);

        before_image_view = view.findViewById(R.id.before_image_view);
        aftr_image_view = view.findViewById(R.id.aftr_image_view);

        obsr_cntnr_lay = view.findViewById(R.id.obsrvsn_lay_cntnr);

        opencamera_before.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
        openCamera_after.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());

        radioGroup = view.findViewById(R.id.radioGroup);

        bfr_msg_tv = view.findViewById(R.id.bfr_msg_tv);
        aftr_msg_tv = view.findViewById(R.id.aftr_msg_tv);
        before_check = view.findViewById(R.id.before_check);
        after_check = view.findViewById(R.id.after_check);

        before_check.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(), R.string.lbl_add_before_image, R.string.lbl_skip_before_image));
        after_check.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(), R.string.lbl_add_after_image, R.string.lbl_skip_after_image));

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

    public void change_rodioGroup(int pos) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
        }
        radioGroup.getChildAt(pos).setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
    }

    public void set_listner() {
        opencamera_before.setOnClickListener(this);
        openCamera_after.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
        skip_btn.setOnClickListener(this);

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
                    if (before_adapter != null)
                        before_adapter.notifyDataSetChanged();
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
                } else if (!before_check.isChecked()) {
                    show_snak(getActivity(), "Before image selected, you can't skip after image");
                    after_check.setChecked(false);
                } else {
                    ((ViewGroup) openCamera_after.getParent()).setVisibility(View.INVISIBLE);
                    aftr_msg_tv.setText(getResString("lbl_after_skip_msg"));
                    before_check.setChecked(true);
                    befor_img_arr.clear();
                    aftr_img_arr.clear();
                    if (before_adapter != null)
                        before_adapter.notifyDataSetChanged();
                    if (after_adapter != null)
                        after_adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void settext() {
        process_tv.setText(periodic_model.getPdm_process());
    }

    public void array_object() {
        observation_main = new ArrayList<>();
        befor_img_arr = new ArrayList<>();
        aftr_img_arr = new ArrayList<>();
    }

    public void startCamera(String name,String type) {
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
//        Intent camera = new Intent(getActivity(), CameraActivity.class);
//        camera.putExtra("path", image_dir);
//        camera.putExtra("name", name);
//        startActivityForResult(camera, request_code);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip_btn:
                remark_Dialog(getActivity());
                break;

            case R.id.open_camera_before:
                startCamera("before_" + System.currentTimeMillis() + ".jpg","before");
                break;
            case R.id.open_camera:

                startCamera("after_" + System.currentTimeMillis() + ".jpg","after");
                break;
            case R.id.continue_btn:
                if (periodic_model.getPdm_observations().size() == 0) {
                    Toast.makeText(getActivity(), getResString("lbl_misng_dta_msg"), Toast.LENGTH_LONG).show();
                } else if (slct_ActnTakn == null || slctd_ActnPrposeID == null || slct_ActnTakn.equalsIgnoreCase("") || slctd_ActnPrposeID.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), getResString("lbl_field_mndtry"), Toast.LENGTH_LONG).show();
                } else if (befor_img_arr.size() == 0 && !before_check.isChecked()) {
                    Toast.makeText(getActivity(), getResString("lbl_cptr_before_image"), Toast.LENGTH_LONG).show();
                    befor_img_arr.clear();
                } else if (aftr_img_arr.size() == 0 && !after_check.isChecked()) {
                    Toast.makeText(getActivity(), getResString("lbl_cptr_aftrImg_msg"), Toast.LENGTH_LONG).show();
                    aftr_img_arr.clear();
                } else {
                    save_data("");
                }
                break;
        }
    }

    private void remark_Dialog(final Activity activity) { // for user
        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.usr_addto_store_lay, null);

        final LinearLayout remark_lay = dialoglayout.findViewById(R.id.remark_lay);
        final EditText remark_edt = dialoglayout.findViewById(R.id.remark_edt);
        TextView title_tv = dialoglayout.findViewById(R.id.title_tv);
        title_tv.setText(getResString("lbl_add_remark"));
        TextView add_btn = dialoglayout.findViewById(R.id.add_btn);
        add_btn.setText(getResString("lbl_sbmit_st"));
        remark_lay.setVisibility(View.VISIBLE);
        dialoglayout.findViewById(R.id.prjct_spnr).setVisibility(View.GONE);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rmrk = remark_edt.getText().toString();
                if (!rmrk.isEmpty()) {
                    builder.cancel();
//                    if (!Periadic_steps.inspected_steps.contains(step_list_pos))
//                        Periadic_steps.inspected_steps.add(step_list_pos);
//                    getActivity().onBackPressed();
//                        hashMap.put("reason_detail", "" + remark_edt.getText());
//                    send_data(hashMap, All_Api.checkin_out_api);
                    save_data(rmrk);
                } else {
                    show_snak(activity, "Please add remark");
                }
            }
        });
        builder.setView(dialoglayout);
        builder.show();
    }


    public void main_spinr_onclk() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = spinner.getSelectedItem().toString();
                if (!selected_item.equals(getResString("lbl_pl_slct_msg"))) {
                    excerpt = new ArrayList<>();
                    excerpt_id = new ArrayList<>();
                    excerpt.add(getResString("lbl_pl_slct_msg"));
                    String obsr_id = periodic_model.getPdm_observations().get(position).getId();
                    if (isNetworkAvailable(getActivity())) {
                        get_action_prposed_from_api(obsr_id, excerpt_spnr);
                    } else {
                        get_action_prposed1(obsr_id, excerpt_spnr);
                    }
                    if (obsr_id != null) {
                        slct_observId = obsr_id;
                    }
                    slctd_ActnPrposeID = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("nothing slct ", "main obsr  ");
            }
        });

        excerpt_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = excerpt_spnr.getSelectedItem().toString();
                if (!selected_item.equals(getResString("lbl_pl_slct_msg"))) {
                    slctd_ActnPrposeID = excerpt_id.get(position - 1);
                }
                Log.e("slctd_exp ", "slctd  " + selected_item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        resultSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = resultSpnr.getSelectedItem().toString();
                if (!selected_item.equals(getResString("lbl_pl_slct_msg"))) {
                    slct_ActnTakn = selected_item;
                }
                Log.e("slctd_rslts ", "slctd  " + slct_ActnTakn);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void get_action_prposed_from_api(String obser_id, final DialogSpinner exrpt_spiner) {
        String url = All_Api.actionProposed_service + obser_id;
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("success").equals("No data Found")) {
                        Log.e("string object", "running");
                    } else {
                        JSONArray jsonArray = object.getJSONArray("success");
                        Log.e("Array object", "running");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            excerpt_id.add(jsonObject.getString("id"));
                            excerpt.add(jsonObject.getString("actionProposed"));
                        }
                        ((ViewGroup) exrpt_spiner.getParent()).setVisibility(View.VISIBLE);
                        exrpt_spiner.setItems(excerpt, "");
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
        String subquery = "select action_id,action_propose from action_propose where observation_id='" + observation_id + "'";
        Cursor c1 = data_daowload.getSingle_Rowdata(subquery);
        if (c1.getCount() > 0) {
            c1.moveToFirst();
            do {
                excerpt_id.add(c1.getString(0));
                excerpt.add(c1.getString(1));
            } while (c1.moveToNext());
            ((ViewGroup) exrpt_spiner.getParent()).setVisibility(View.VISIBLE);
            exrpt_spiner.setItems(excerpt, "");
        }
        data_daowload.close();
    }

    @SuppressLint("CheckResult")
    private void save_data(String remark) {
        JSONArray observId = new JSONArray();
        observId.put(slct_observId);
        JSONArray ActnTakn = new JSONArray();
        ActnTakn.put(slct_ActnTakn);
        JSONArray ActnPrposeID = new JSONArray();
        ActnPrposeID.put(slctd_ActnPrposeID);

        final Pdm_Inspected_steps_table pdm_steps_table = new Pdm_Inspected_steps_table();
        pdm_steps_table.setUnique_id(unique_id);
        pdm_steps_table.setAsset_id(slctd_product_name);
        pdm_steps_table.setStep_position(step_list_pos);
        pdm_steps_table.setObservation(observId.toString());
        pdm_steps_table.setAction_proposed(ActnPrposeID.toString());
        pdm_steps_table.setAction_taken(ActnTakn.toString());
        pdm_steps_table.setClient_id(client_id);
        pdm_steps_table.setUser_id(user_id);
        pdm_steps_table.setStep_id(periodic_model.getPdm_id());
        pdm_steps_table.setMaster_id(Static_values.selectedMasterData_model.getMdata_id());
        if (!remark.isEmpty()) {
            pdm_steps_table.setSkip_flag("1");
            pdm_steps_table.setSkip_remark(remark);
        } else {
            pdm_steps_table.setSkip_flag("0");
            pdm_steps_table.setSkip_remark("");
        }
        if (inspected_steps.size() < 1 && Periadic_steps.start_time != 0) {
            pdm_steps_table.setStart_time(Periadic_steps.start_time);
        }
        pdm_steps_table.setBefore_images(new Gson().toJson(befor_img_arr, new TypeToken<ArrayList<String>>() {
        }.getType()));
        pdm_steps_table.setAfter_images(new Gson().toJson(aftr_img_arr, new TypeToken<ArrayList<String>>() {
        }.getType()));

        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return AppController.getInstance().getDatabase().getPdm_stepsDao().insert(pdm_steps_table);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (!inspected_steps.contains(step_list_pos))
                    inspected_steps.add(step_list_pos);
                getActivity().onBackPressed();
            }
        });


    }

}

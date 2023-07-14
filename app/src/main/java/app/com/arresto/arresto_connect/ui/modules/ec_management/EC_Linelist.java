package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.EC_category_Adapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.horizontal;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.revision;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_alurail;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_wire;
import static app.com.arresto.arresto_connect.network.All_Api.Host;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

public class EC_Linelist extends EC_Base_Fragment {

    public static EC_Linelist newInstance(String page) {
        EC_Linelist fragmentFirst = new EC_Linelist();
        Bundle args = new Bundle();
        args.putString("type", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private View view;
    private RecyclerView line_list;
    private EC_category_Adapter selected_category_adapter;
    private TextView ad_line_btn;
    private TextView sbmit_btn, blank_tv;
    private String type;
    private List<String> submitted_boqs;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ec_home, parent, false);
            findAllIds(view);

            if (getArguments() != null) {
                type = getArguments().getString("type");
                if (type.equalsIgnoreCase("db_lines")) {
                    update_selected_list();
//                    ad_line_btn.setVisibility(View.VISIBLE);
                    ad_line_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAdd_Dialog(getActivity());
                        }
                    });
                    sbmit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit_dialogbox();
                        }
                    });

                    blank_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAdd_Dialog(getActivity());
                        }
                    });

                } else {
                    getSubmittedData();
                    ad_line_btn.setVisibility(View.GONE);
                }

            }

        } else {
            if (selected_category_adapter != null && selected_category_adapter.getItemCount() > 0)
                selected_category_adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec lifeline");
    }

    String getImageString(String path) {
        if (!path.contains(Host)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            String type = null;
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return "data:" + type + ";base64," + AppUtils.Image_toBase64(path);
        } else
            return path;
    }

    public void findAllIds(View view) {
//        swipe = view.findViewById(R.id.swipe);
        ad_line_btn = view.findViewById(R.id.ad_line_btn);
        sbmit_btn = view.findViewById(R.id.sbmit_btn);
        line_list = view.findViewById(R.id.select_systems_list);

        blank_tv = view.findViewById(R.id.blank_tv);
        blank_tv.setText("Add Sub Sites");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        line_list.setLayoutManager(linearLayoutManager);
        line_list.setPadding(3, 5, 5, 140);
        line_list.setClipToPadding(false);
    }

    public void showAdd_Dialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_line_dialog);

        if (!logo_url.equals("")) {
            ImageView logo_img = dialog.findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
        image_view = dialog.findViewById(R.id.image_view);
        final ImageView camera_ic = dialog.findViewById(R.id.camera_ic);
        dialog.findViewById(R.id.image_layer).setVisibility(View.VISIBLE);
        final Spinner line_spinr = dialog.findViewById(R.id.line_spinr);
        final Spinner cat_spinr = dialog.findViewById(R.id.cat_spinr);
        line_spinr.setVisibility(View.VISIBLE);
        final EditText edt_uin = dialog.findViewById(R.id.edt_dialog);
        final TextView latlng_tv = dialog.findViewById(R.id.latlng_tv);

        if (baseActivity.curr_lat != 0)
            latlng_tv.setText(baseActivity.curr_lat + "," + baseActivity.curr_lng);
        edt_uin.setHint("Enter Sub site name");

        line_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cat_spinr.setVisibility(View.GONE);
                } else {
                    cat_spinr.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_uin.getText().toString().equals("")) {
                    show_snak(getActivity(), "Please enter sub site name");
                    return;
                }
                if (image_path == null || image_path.equals("")) {
                    Toast.makeText(getActivity(), "Please add a site image.", Toast.LENGTH_LONG).show();
                    return;
                }
//                params.put("geolocation", latlng_tv.getText().toString());
                String location = latlng_tv.getText().toString();
                if (line_spinr.getSelectedItemPosition() == 0)
                    add_new_boq(horizontal, edt_uin.getText().toString(), location);
                else if (line_spinr.getSelectedItemPosition() == 1) {
                    if (cat_spinr.getSelectedItemPosition() == 0)
                        add_new_boq(vertical_wire, edt_uin.getText().toString(), location);
                    else
                        add_new_boq(vertical_alurail, edt_uin.getText().toString(), location);
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cncl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        camera_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(new Base_Fragment.OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        image_path = path;
                        load_image_file(image_path, image_view);
                        image_view.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        dialog.show();
    }

    String image_path;
    ImageView image_view;

    private void add_new_boq(String cat_id, String remark, String location) {
        Project_Boq_table project_boq_table = new Project_Boq_table();
        project_boq_table.setUser_id(user_id);
        project_boq_table.setProject_id(project_id);
        project_boq_table.setCat_id(cat_id);
        project_boq_table.setBoq_id("" + System.currentTimeMillis());
        project_boq_table.setRemark("" + remark);
        project_boq_table.setSite_id(site_id);
        project_boq_table.setRevision(revision);
        project_boq_table.setSubsite_image(image_path);
        project_boq_table.setGeolocation(location);
        AppController.getInstance().getDatabase().getProject_Boq_Dao().insert(project_boq_table);
        update_selected_list();
    }

    public ArrayList<Category_Model> categories;
    ArrayList<String> name_arr, selected_boqs, image_paths;

    public void update_selected_list() {
        categories = new ArrayList<>();
        name_arr = new ArrayList<>();
        selected_boqs = new ArrayList<>();
        image_paths = new ArrayList<>();
        List<Project_Boq_table> project_boq_tables = AppController.getInstance().getDatabase().getProject_Boq_Dao().getBOQ(user_id, project_id, site_id);
        for (Project_Boq_table project_boq_table : project_boq_tables) {
            categories.add((Category_Model) treeNodes.get(project_boq_table.getCat_id()).getData());
            selected_boqs.add(project_boq_table.getBoq_id());
            name_arr.add(project_boq_table.getRemark());
            image_paths.add(project_boq_table.getSubsite_image());
        }
        if (selected_category_adapter != null) {
            selected_category_adapter.update_list(categories, name_arr, selected_boqs, image_paths);
        } else {
            selected_category_adapter = new EC_category_Adapter(getActivity(), this, categories, name_arr, selected_boqs, image_paths, false);
            line_list.setAdapter(selected_category_adapter);
        }
        if (categories.size() > 0) {
            line_list.setVisibility(View.VISIBLE);
            sbmit_btn.setVisibility(View.VISIBLE);
            blank_tv.setVisibility(View.GONE);
            ad_line_btn.setVisibility(View.VISIBLE);
        } else {
            sbmit_btn.setVisibility(View.GONE);
            blank_tv.setVisibility(View.VISIBLE);
            ad_line_btn.setVisibility(View.GONE);
        }

    }

    //  get old submitted lines

    ArrayList<String> data_array;

    public void getSubmittedData() {
        String url = All_Api.Ec_get + client_id + "&user_id=" + user_id + "&project_id=" + project_id + "&site_id=" + site_id;
        Log.e("email id url", "" + url);
        categories = new ArrayList<>();
        name_arr = new ArrayList<>();
        data_array = new ArrayList<>();
        image_paths = new ArrayList<>();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data_object = new JSONObject(response);
                    Log.e("response", "" + response);
                    if (data_object.getString("status_code").equalsIgnoreCase("200")) {
//                         Delete boq data
                        if (data_object.getJSONArray("data").length() > 0) {
                            JSONArray data = data_object.getJSONArray("data");
                            for (int j = 0; j < data.length(); j++) {
                                JSONArray specification = data.getJSONObject(j).getJSONArray("specification");
                                for (int i = 0; i < specification.length(); i++) {
                                    JSONObject jsonObject = specification.getJSONObject(i);
                                    if (jsonObject.getString("type").contains("Horizontal"))
                                        categories.add((Category_Model) treeNodes.get(horizontal).getData());
                                    else
                                        categories.add((Category_Model) treeNodes.get(vertical).getData());
                                    data_array.add("" + jsonObject);
                                    name_arr.add(jsonObject.getString("remark"));
                                    image_paths.add(jsonObject.getString("subsite_image"));
                                }
                            }
                            if (selected_category_adapter != null) {
                                selected_category_adapter.update_list(categories, name_arr, data_array, image_paths);
                            } else {
                                selected_category_adapter = new EC_category_Adapter(getActivity(), EC_Linelist.this, categories, name_arr, data_array, image_paths, true);
                                line_list.setAdapter(selected_category_adapter);
                            }
                            revision = 0;
                            if (categories.size() > 0) {
                                line_list.setVisibility(View.VISIBLE);
                                revision = 1;
                            }
                        }
                    }
//                    show_snak(getActivity(), data_object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError", "" + message);
            }
        });

    }


    private void submit_dialogbox() {
        final Dialog builder = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.alert_dialog, null);
        TextView msg_tv = dialoglayout.findViewById(R.id.msg_tv);
        final TextView add_btn = dialoglayout.findViewById(R.id.add_btn);
        final TextView submit_btn = dialoglayout.findViewById(R.id.submit_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdd_Dialog(getActivity());
                builder.dismiss();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
                builder.dismiss();
            }
        });

        builder.setContentView(dialoglayout);
        builder.show();
        Window window = builder.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void submitData() {
        List<Project_Boq_table> project_boq_tables = AppController.getInstance().getDatabase().getProject_Boq_Dao().getBOQ(user_id, project_id, site_id);
        JSONArray jsonArray = new JSONArray();
        submitted_boqs = new ArrayList<>();
        for (int i = 0; i < project_boq_tables.size(); i++) {
            Project_Boq_table boq = project_boq_tables.get(i);
            if (boq.getData() != null && !boq.getData().equals("")) {
                String image = getImageString(boq.getSubsite_image());
                try {
                    JSONObject jsonObject = new JSONObject(boq.getData());
                    jsonObject.put("subsite_image", image);
                    jsonObject.put("geolocation", boq.getGeolocation());
                    jsonObject.put("site_id", boq.getSite_id());
                    jsonObject.put("remark", boq.getRemark());
                    jsonObject.put("boq_id", boq.getBoq_id());
                    jsonObject.put("revision", boq.getRevision());
                    if (jsonObject.has("maximum_load"))
                        jsonObject.put("maximum_load", getDigits(jsonObject.getString("maximum_load")));
                    boolean isMeter = jsonObject.getBoolean("meter");
//                    if (isMeter && !jsonObject.getString("tention").contains("Pound"))
//                        jsonObject.put("tention", "" + round(Double.parseDouble(jsonObject.getString("tention")) * to_pound, 2));
//                    else
                    if (jsonObject.has("tention"))
                        jsonObject.put("tention", getDigits(jsonObject.getString("tention")));

                    if (jsonObject.has("segment_1")) {
                        jsonObject.getJSONObject("segment_1").put("span", getDigits(jsonObject.getJSONObject("segment_1").getString("span")));
                        if (!isMeter)
                            processData(jsonObject.getJSONObject("segment_1"));
                    }
                    if (jsonObject.has("segment_2")) {
                        jsonObject.getJSONObject("segment_2").put("span", getDigits(jsonObject.getJSONObject("segment_2").getString("span")));
                        if (!isMeter)
                            processData(jsonObject.getJSONObject("segment_2"));
                    }
                    if (jsonObject.has("segment_3")) {
                        jsonObject.getJSONObject("segment_3").put("span", getDigits(jsonObject.getJSONObject("segment_3").getString("span")));
                        if (!isMeter)
                            processData(jsonObject.getJSONObject("segment_3"));
                    }

                    submitted_boqs.add(boq.getBoq_id());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                show_snak(homeActivity, "Please complete calculation of BOQ " + (i + 1));
                return;
            }
        }
        Log.e("final json ", "====== is " + jsonArray);
        post_ec_data(jsonArray);
    }

    public String getDigits(String str) {
        return str.replaceAll("[^.|0-9]", "");
    }

    double to_feet = 3.280839895;
    int to_pound = 225;

    public void processData(JSONObject object) {
        try {
            if (!object.getString("deflection").equals(""))
                object.put("deflection", round(Double.parseDouble(object.getString("deflection")) * to_feet, 2));
            if (!object.getString("cusp_sagSpan").equals(""))
                object.put("cusp_sagSpan", round(Double.parseDouble(object.getString("cusp_sagSpan")) * to_feet, 2));
            if (!object.getString("cusp_sagTemp").equals(""))
                object.put("cusp_sagTemp", round(Double.parseDouble(object.getString("cusp_sagTemp")) * to_feet, 2));
            if (!object.getString("tention").equals(""))
                object.put("tention", round(Double.parseDouble(object.getString("tention")) * to_pound, 2));
            if (!object.getString("maximum_strength").equals(""))
                object.put("maximum_strength", round(Double.parseDouble(object.getString("maximum_strength")) * to_pound, 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void post_ec_data(JSONArray data) {
        String url = All_Api.Ec_data_post;
        Log.e("email id url", "" + url);

        new NetworkRequest(getActivity()).make_Arraypost_request(url, data, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data_object = new JSONObject(response);
                    Log.e("response", "" + response);
                    if (data_object.getString("status_code").equalsIgnoreCase("200")) {
                        delete_dbData();
                    }
                    show_snak(getActivity(), data_object.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError", "" + message);
            }
        });

    }


    private void delete_dbData() {
        for (String boq_id : submitted_boqs) {
            delete_boq(project_id, site_id, boq_id);
        }
//        update_selected_list();
        LoadFragment.replace(new EC_Report_Fragment(), getActivity(), getResString("lbl_ec_reports"));
    }
}

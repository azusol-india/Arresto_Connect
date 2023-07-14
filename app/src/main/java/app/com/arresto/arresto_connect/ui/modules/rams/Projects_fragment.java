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

package app.com.arresto.arresto_connect.ui.modules.rams;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class Projects_fragment extends Base_Fragment {
    View view;
    ImageView ad_project_btn;
    RecyclerView projects_list;
    Project_listAdapter project_listAdapter;
    public static boolean project_refresh = false;
//
//    public static double curr_lat = 0.0;
//    public static double curr_lng = 0.0;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, parent, false);
            setupUI(view, getActivity());
//            new AppLocationService(baseActivity, new AppLocationService.OnLocationChange() {
//                @Override
//                public void locationChange(Location location, double latitude, double longitude) {
//                    curr_lat = latitude;
//                    curr_lng = longitude;
//                }
//            });

            find_id();
            update_list();
            projects_list.setAdapter(project_listAdapter);
            ad_project_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetch_data(null, All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" + user_id);
//                    create_ProjectDialog(null);
                }
            });
        } else {
            if (project_refresh) {
                update_list();
                project_refresh = false;
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataHolder_Model.getInstance().getCustomViewsData_models() != null)
            DataHolder_Model.getInstance().setCustomViewsData_models(null);
    }

    private void setdata() {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        projects_list.setLayoutManager(layoutManager);
        projects_list.setPadding(3, 5, 5, DpToPixel(50));
        projects_list.setClipToPadding(false);
        projects_list.getLayoutManager().setMeasurementCacheEnabled(false);
    }

    private void find_id() {
        ad_project_btn = view.findViewById(R.id.ad_project_btn);
        projects_list = view.findViewById(R.id.projects_list);
        ad_project_btn.setVisibility(View.VISIBLE);
//        ad_project_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_text()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ad_project_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_text()));
        }

        setdata();
    }

    ArrayList<GroupUsers> groupUsers;

    public void fetch_data(Project_Model project_model, final String url) {
        groupUsers = new ArrayList<>();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (object.get("data") instanceof JSONArray)
                            groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), GroupUsers[].class)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    create_ProjectDialog(project_model);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    String site_path = "", subSitePath = "";
    String selected_project_id = "";

    public void create_ProjectDialog(final Project_Model project_model) {
        final Dialog builder = new Dialog(getActivity(), R.style.AppTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.create_project_lay, null);
        final ImageView cancel_btn = dialoglayout.findViewById(R.id.cancel_btn);
        TextView create_btn = dialoglayout.findViewById(R.id.create_btn);
        final TextView head_tv = dialoglayout.findViewById(R.id.head_tv);
        final EditText prjct_edt = dialoglayout.findViewById(R.id.prjct_edt);
        final EditText custmr_edt = dialoglayout.findViewById(R.id.custmr_edt);
        final EditText custmr_addrs_edt = dialoglayout.findViewById(R.id.custmr_addrs_edt);
        final EditText email_edt = dialoglayout.findViewById(R.id.email_edt);
        final EditText site_edt = dialoglayout.findViewById(R.id.site_edt);
        final EditText subSite_edt = dialoglayout.findViewById(R.id.subSite_edt);
        final EditText cell_edt = dialoglayout.findViewById(R.id.cell_edt);
        final EditText work_type_edt = dialoglayout.findViewById(R.id.work_type_edt);
        final TextView latlng_tv = dialoglayout.findViewById(R.id.latlng_tv);
        final TextView start_date_tv = dialoglayout.findViewById(R.id.start_date_tv);
        final TextView end_date_tv = dialoglayout.findViewById(R.id.end_date_tv);
        final MultiSpinner form_spinr = dialoglayout.findViewById(R.id.form_spinr);
        final MultiSpinner users_spinr = dialoglayout.findViewById(R.id.users_spinr);
        final Spinner cl_project_spinr = dialoglayout.findViewById(R.id.cl_project_spinr);

        final TextView site_img = dialoglayout.findViewById(R.id.site_img);
        final TextView sub_site_img = dialoglayout.findViewById(R.id.sub_site_img);
        final ImageView site_view = dialoglayout.findViewById(R.id.site_view);
        final ImageView subSite_view = dialoglayout.findViewById(R.id.subSite_view);
        final ImageView site_rmv_img = dialoglayout.findViewById(R.id.site_rmv_img);
        final ImageView subSite_rmv_img = dialoglayout.findViewById(R.id.subSite_rmv_img);

        if (baseActivity.curr_lat != 0)
            latlng_tv.setText(baseActivity.curr_lat + "," + baseActivity.curr_lng);
        ArrayList<String> last_selected = new ArrayList();
        List<String> last_users = new ArrayList<>();

        if (project_model != null) {
            head_tv.setText("Update Project");
            create_btn.setText("Update Project");
            prjct_edt.setText(project_model.getUp_project_name());
            custmr_edt.setText(project_model.getUp_customer());
            custmr_addrs_edt.setText(project_model.getUp_customer_address());
            email_edt.setText(project_model.getUp_email());
            site_edt.setText(project_model.getUp_site());
            subSite_edt.setText(project_model.getUp_subsite());
            cell_edt.setText(project_model.getUp_mobile());
            work_type_edt.setText(project_model.getUp_worktype());
            if (project_model.getSite_image() != null && !project_model.getSite_image().equals("")) {
                site_path = project_model.getSite_image();
                ((ViewGroup) site_view.getParent()).setVisibility(View.VISIBLE);
                AppUtils.load_image(site_path, site_view);
            }
            if (project_model.getSubsite_image() != null && !project_model.getSubsite_image().equals("")) {
                subSitePath = project_model.getSubsite_image();
                ((ViewGroup) subSite_view.getParent()).setVisibility(View.VISIBLE);
                AppUtils.load_image(subSitePath, subSite_view);
            }
            try {
                start_date_tv.setText(baseActivity.Date_Format().format(baseActivity.server_date_format.parse(project_model.getUp_startdate())));
                end_date_tv.setText(baseActivity.Date_Format().format(baseActivity.server_date_format.parse(project_model.getUp_enddate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (CustomForm_Model customForm_model : DataHolder_Model.getInstance().getCustomViews_models()) {
                if (project_model.getUp_forms().contains(customForm_model.getCf_id()))
                    last_selected.add(customForm_model.getForm_name());
            }

            if (project_model.getTeam() != null)
                for (GroupUsers user : project_model.getTeam()) {
                    last_users.add(user.toString().trim());
                }
        } else {
            ((ViewGroup) cl_project_spinr.getParent()).setVisibility(View.VISIBLE);
            cl_project_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        selected_project_id = ((Project_Model) parent.getItemAtPosition(position)).getUp_id();
                    } else selected_project_id = "";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (DataHolder_Model.getInstance().getProject_models() != null) {
                ArrayList<Project_Model> all_projects = new ArrayList<>();
                Project_Model blank_project = new Project_Model();
                blank_project.setUp_project_name(getResString("lbl_pl_slct_msg"));
                all_projects.add(blank_project);
                all_projects.addAll(DataHolder_Model.getInstance().getProject_models());
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(baseActivity, android.R.layout.simple_list_item_1, all_projects);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                cl_project_spinr.setAdapter(arrayAdapter);
            }
        }
        site_rmv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                site_path = "";
                ((ViewGroup) site_rmv_img.getParent()).setVisibility(View.GONE);
            }
        });
        subSite_rmv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subSitePath = "";
                ((ViewGroup) subSite_rmv_img.getParent()).setVisibility(View.GONE);
            }
        });
        site_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        site_path = path;
                        ((ViewGroup) site_view.getParent()).setVisibility(View.VISIBLE);
                        AppUtils.load_image_file(site_path, site_view);
                    }
                });
            }
        });
        sub_site_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        subSitePath = path;
                        ((ViewGroup) subSite_view.getParent()).setVisibility(View.VISIBLE);
                        AppUtils.load_image_file(subSitePath, subSite_view);
                    }
                });
            }
        });

        final ArrayList<String> selected_forms = new ArrayList<>();
        final ArrayList<String> selected_users = new ArrayList<>();
        form_spinr.setItems(DataHolder_Model.getInstance().getCustomViews_models(), last_selected, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(Boolean[] selected) {
                selected_forms.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i])
                        selected_forms.add(DataHolder_Model.getInstance().getCustomViews_models().get(i).getCf_id());
                }
            }
        });

        users_spinr.setItems(groupUsers, last_users, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(Boolean[] selected) {
                selected_users.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i])
                        selected_users.add(groupUsers.get(i).getUacc_id());
                }
            }
        });
        if (last_selected.size() > 0) {
            form_spinr.setSelected_Text();
        }
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_forms.size() < 1) {
                    Toast.makeText(getActivity(), "Please select at least one form.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!prjct_edt.getText().toString().equals("")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("project_name", prjct_edt.getText().toString());
                    params.put("geolocation", latlng_tv.getText().toString());
                    params.put("customer", custmr_edt.getText().toString());
                    params.put("customer_address", custmr_addrs_edt.getText().toString());
                    params.put("email", email_edt.getText().toString());
                    params.put("site", site_edt.getText().toString());
                    params.put("sub_site", subSite_edt.getText().toString());
                    params.put("mobile_no", cell_edt.getText().toString());
                    params.put("work_type", work_type_edt.getText().toString());
                    params.put("user_id", Static_values.user_id);
                    params.put("client_id", client_id);
                    params.put("forms", "" + selected_forms);

                    if (site_path.contains("storage")) {
                        String extension = MimeTypeMap.getFileExtensionFromUrl(site_path);
                        String type = null;
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        params.put("site_image", "" + "data:" + type + ";base64," + AppUtils.Image_toBase64(site_path));
                    } else if (site_path.equals(""))
                        params.put("site_image", "");
                    if (subSitePath.contains("storage")) {
                        String extension = MimeTypeMap.getFileExtensionFromUrl(subSitePath);
                        String type = null;
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        params.put("subsite_image", "" + "data:" + type + ";base64," + AppUtils.Image_toBase64(subSitePath));
                    } else if (subSitePath.equals(""))
                        params.put("subsite_image", "");
                    try {
                        params.put("startdate", baseActivity.server_date_format.format(baseActivity.Date_Format().parse(start_date_tv.getText().toString().trim())));
                        params.put("enddate", baseActivity.server_date_format.format(baseActivity.Date_Format().parse(end_date_tv.getText().toString().trim())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (!selected_users.contains(user_id))
                        selected_users.add(0, user_id);
                    params.put("users", ("" + selected_users).replaceAll("\"|\\[|\\]", ""));


                    if (project_model != null) {
                        params.put("project_id", project_model.getUp_id());
                        params.put("team_id", "" + project_model.getTeam_id());
                        send_data(params, All_Api.edit_project);
                    } else {
                        params.put("old_project_id", "" + selected_project_id);
                        send_data(params, All_Api.add_project);
                    }
                    builder.dismiss();
                } else
                    Toast.makeText(getActivity(), "Please Enter Project Name.", Toast.LENGTH_LONG).show();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        start_date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Date Sd = null;
//                try {
//                if(project_model!=null)
//                    Sd = baseActivity.server_date_format.parse(project_model.getUp_startdate());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (Sd != null && new Date().after(Sd))
//                        Toast.makeText(getActivity(), "Project has started now dates can't change!", Toast.LENGTH_LONG).show();
//                    else
                ((BaseActivity) getActivity()).show_Date_piker(start_date_tv);
//                }
            }
        });
        end_date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Date Sd = null;
//                try {
//                if(project_model!=null)
//                    Sd = baseActivity.server_date_format.parse(project_model.getUp_startdate());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (Sd != null && new Date().after(Sd))
//                        Toast.makeText(getActivity(), "Project has started now dates can't change!", Toast.LENGTH_LONG).show();
//                    else
                ((BaseActivity) getActivity()).show_Date_piker(end_date_tv);
//                }
            }
        });

        builder.setContentView(dialoglayout);
        builder.show();
    }

    public void send_data(HashMap<String, String> params, String url) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_post_request(url, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (!msg_code.equals("200")) {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                        } else {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            update_list();
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
    public void update_list() {
        NetworkRequest.get_projects_data(getActivity(), All_Api.project_list + Static_values.user_id + "&client_id=" + client_id, false, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    if (project_listAdapter != null) {
                        project_listAdapter.update_list(DataHolder_Model.getInstance().getProject_models());
                        project_listAdapter.notifyDataSetChanged();
                    } else {
                        project_listAdapter = new Project_listAdapter(Projects_fragment.this);
                        projects_list.setAdapter(project_listAdapter);
                    }
                }
            }
        });
    }


}

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_Project;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;

/**
 * Created by AZUSOL-PC-02 on 9/11/2019.
 */
public class EC_Projects_Fragment extends Base_Fragment {
    View view;
    TextView ad_project_btn;
    RecyclerView projects_list;
    TextView blank_tv;
    Project_listAdapter project_listAdapter;
    public String page_type = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, parent, false);
//            setupUI(view, getActivity());

            find_id();
            update_list();
//                float offsetPx = getResources().getDimension(R.dimen.bottom_offset_dp);
            projects_list.setClipToPadding(true);
            projects_list.setAdapter(project_listAdapter);
            if (this.getTag().equalsIgnoreCase(getResString("lbl_ec_reports"))) {
                projects_list.setPadding(3, 5, 5, 5);
                page_type = "reports";
                ad_project_btn.setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.search_sec).setVisibility(View.VISIBLE);
                projects_list.setPadding(3, 5, 5, DpToPixel(50));
                ad_project_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        create_ProjectDialog(null, true);
                    }
                });
                blank_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        create_ProjectDialog(null, true);
                    }
                });
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec project");
    }


    private void find_id() {
        view.findViewById(R.id.ad_project_btn).setVisibility(View.GONE);
        searchView = view.findViewById(R.id.search_view);
        ad_project_btn = view.findViewById(R.id.finish_btn);
        projects_list = view.findViewById(R.id.projects_list);
        blank_tv = view.findViewById(R.id.blank_tv);
        blank_tv.setText(getResString("lbl_create_project"));
        ad_project_btn.setText(getResString("lbl_create_site_measurement_sheet_project"));
//        ad_project_btn.setVisibility(View.VISIBLE);
        setdata();
    }

    public EditText searchView;

    private void setdata() {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        projects_list.setLayoutManager(layoutManager);
        projects_list.getLayoutManager().setMeasurementCacheEnabled(false);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                if (TextUtils.isEmpty(newText)) {
                    setUpList(DataHolder_Model.getInstance().getEc_project());
                } else {
                    filter_project_Data(newText);
                }
            }
        });
    }


    ImageView image_view;

    public void create_ProjectDialog(final EC_Project ec_project, boolean is_edit) {
        final Dialog builder = new Dialog(getActivity(), R.style.AppTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.ec_create_project, null);
        final ImageView cancel_btn = dialoglayout.findViewById(R.id.cancel_btn);
        image_view = dialoglayout.findViewById(R.id.image_view);
        final FloatingActionButton camera_ic = dialoglayout.findViewById(R.id.camera_ic);
        MaterialButton create_btn = dialoglayout.findViewById(R.id.create_btn);
        final TextView head_tv = dialoglayout.findViewById(R.id.head_tv);
        final EditText prjct_edt = dialoglayout.findViewById(R.id.prjct_edt);
        final AutoCompleteTextView custmr_edt = dialoglayout.findViewById(R.id.custmr_edt);
        final EditText custmr_addrs_edt = dialoglayout.findViewById(R.id.custmr_addrs_edt);
        final EditText email_edt = dialoglayout.findViewById(R.id.email_edt);
//        final EditText site_edt = dialoglayout.findViewById(R.id.site_edt);
        final EditText subSite_edt = dialoglayout.findViewById(R.id.subSite_edt);
        final EditText cell_edt = dialoglayout.findViewById(R.id.cell_edt);

        get_client_data(All_Api.all_clientinfo + "?client_id=" + client_id, custmr_edt);
        final Spinner aplication_spinr = dialoglayout.findViewById(R.id.aplication_spinr);

        if (ec_project != null) {
            head_tv.setText("Update Project");
            create_btn.setText("Update Project");
            prjct_edt.setText(ec_project.getProject_name());
            custmr_edt.setText(ec_project.getCustomer_name());
            custmr_addrs_edt.setText(ec_project.getCustomer_address());
            email_edt.setText(ec_project.getCustomer_email());
//            site_edt.setText(ec_project.getSite());
            subSite_edt.setText(ec_project.getRegion());
            cell_edt.setText(ec_project.getCustomer_mobile());
            aplication_spinr.setSelection(((ArrayAdapter<String>) aplication_spinr.getAdapter()).getPosition(ec_project.getApplication()));

            if (!ec_project.getProject_image().equals("")) {
                image_path = ec_project.getProject_image();
                image_view.setVisibility(View.VISIBLE);
                AppUtils.load_image(ec_project.getProject_image(), image_view);
            }

            if (!is_edit) {
                head_tv.setText(getResString("lbl_life_line_details"));
                create_btn.setVisibility(View.GONE);
                camera_ic.hide();
                prjct_edt.setEnabled(false);
                custmr_edt.setEnabled(false);
                custmr_addrs_edt.setEnabled(false);
                email_edt.setEnabled(false);
                subSite_edt.setEnabled(false);
                cell_edt.setEnabled(false);
                aplication_spinr.setEnabled(false);
            }
        }

        custmr_edt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Client_model item = (Client_model) adapterView.getItemAtPosition(i);
                custmr_addrs_edt.setText(item.getClientAddress());
                email_edt.setText(item.getClientContactPersonEmail());
                subSite_edt.setText(item.getClientDistrict());
                cell_edt.setText(item.getClientContactNo());
            }
        });
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_path == null || image_path.equals("")) {
                    Toast.makeText(getActivity(), "Please add a site image.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!prjct_edt.getText().toString().equals("")) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("project_name", prjct_edt.getText().toString());
                        params.put("customer_name", custmr_edt.getText().toString());
                        params.put("customer_address", custmr_addrs_edt.getText().toString());
                        params.put("customer_email", email_edt.getText().toString());
                        params.put("site", "");
                        params.put("region", subSite_edt.getText().toString());
                        params.put("customer_mobile", cell_edt.getText().toString());
                        params.put("application", aplication_spinr.getSelectedItem().toString());
                        params.put("user_id", Static_values.user_id);
                        params.put("client_id", client_id);
                        if (image_path.contains(Host)) {
                            params.put("site_image", "");
                        } else {
                            String extension = MimeTypeMap.getFileExtensionFromUrl(image_path);
                            String type = null;
                            if (extension != null) {
                                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            }
                            params.put("site_image", "data:" + type + ";base64," + AppUtils.Image_toBase64(image_path));
                        }

                        if (ec_project != null) {
                            params.put("ecp_id", ec_project.getEcp_id());
                            send_data(params, All_Api.ec_update_project);
                        } else
                            send_data(params, All_Api.ec_add_project);
                        builder.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        camera_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        image_path = path;
                        load_image_file(image_path, image_view);
                        image_view.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        builder.setContentView(dialoglayout);
        builder.show();
    }

    String image_path;


    public void send_data(JSONObject params, String url) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(url, params, new NetworkRequest.VolleyResponseListener() {
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
        NetworkRequest.get_projects_data(getActivity(), All_Api.Ec_project_list + Static_values.user_id + "&client_id=" + client_id, true, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    setUpList(DataHolder_Model.getInstance().getEc_project());
                    if (DataHolder_Model.getInstance().getEc_project().size() > 0) {
                        blank_tv.setVisibility(View.GONE);
                        ad_project_btn.setVisibility(View.VISIBLE);
                    } else {
                        blank_tv.setVisibility(View.VISIBLE);
                        ad_project_btn.setVisibility(View.GONE);
                    }
                } else {
                    blank_tv.setVisibility(View.VISIBLE);
                    ad_project_btn.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setUpList(ArrayList<EC_Project> listData) {
        if (project_listAdapter != null) {
            project_listAdapter.update_EClist(listData);
            project_listAdapter.notifyDataSetChanged();
        } else {
            project_listAdapter = new Project_listAdapter(EC_Projects_Fragment.this);
            projects_list.setAdapter(project_listAdapter);
        }
    }

    private void filter_project_Data(String newText) {
        if (DataHolder_Model.getInstance().getEc_project().size() > 0) {
            ArrayList<EC_Project> new_listData = new ArrayList<>();
            for (int i = 0; i < DataHolder_Model.getInstance().getEc_project().size(); i++) {
                EC_Project project = DataHolder_Model.getInstance().getEc_project().get(i);
                if (project.getProject_name().contains(newText) || project.getRegion().contains(newText)
                        || project.getCustomer_name().contains(newText) || project.getCustomer_email().contains(newText)) {
                    new_listData.add(project);
                }
            }
            setUpList(new_listData);
        }
    }

    ArrayList<Client_model> client_models;

    public void get_client_data(String url, final AutoCompleteTextView custmr_edt) {
        client_models = new ArrayList<>();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    Object json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONArray) {
                        client_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Client_model[].class)));
                        ArrayAdapter client_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, client_models);
                        custmr_edt.setAdapter(client_adapter);
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
}
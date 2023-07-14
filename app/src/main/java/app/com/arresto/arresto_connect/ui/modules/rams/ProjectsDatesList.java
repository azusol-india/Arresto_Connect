package app.com.arresto.arresto_connect.ui.modules.rams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DateFormModel;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.modules.rams.Projects_fragment.project_refresh;

public class ProjectsDatesList extends Base_Fragment {
    View view;
    RecyclerView projects_list;
    ProjectDateAdapter projectDateAdapter;

    public static ProjectsDatesList newInstance(String project_id) {
        ProjectsDatesList fragment = new ProjectsDatesList();
        Bundle args = new Bundle();
        args.putString("project_id", project_id);
        fragment.setArguments(args);
        return fragment;
    }

    String project_id;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, parent, false);
            find_id();
            projects_list.setAdapter(projectDateAdapter);
            if (getArguments() != null) {
                project_id = getArguments().getString("project_id");
                fetch_data(All_Api.datecustom_forms + client_id + "&user_id=" + user_id + "&project_id=" + project_id);
            }

        }
        return view;
    }

    TextView return_btn;

    private void find_id() {
        view.findViewById(R.id.ad_project_btn).setVisibility(View.GONE);
        projects_list = view.findViewById(R.id.projects_list);
        return_btn = view.findViewById(R.id.return_btn);
        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        projects_list.setLayoutManager(layoutManager);
        projects_list.setPadding(3, 5, 5, DpToPixel(50));
        projects_list.setClipToPadding(false);
        projects_list.getLayoutManager().setMeasurementCacheEnabled(false);
        return_btn.setVisibility(VISIBLE);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToStoreDialog();
            }
        });

    }

    ArrayList<DateFormModel> dateFormModels;

    private void fetch_data(String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        GsonBuilder b = new GsonBuilder();
                        b.registerTypeAdapter(CustomForm_Model.FieldData.class, new CustomForm_Model.Custom_Form_Deserial());
                        dateFormModels = new ArrayList<>(Arrays.asList(b.create().fromJson(object.getString("data"), DateFormModel[].class)));
                        if (projectDateAdapter != null) {
                            projectDateAdapter.update_list(dateFormModels);
                            projectDateAdapter.notifyDataSetChanged();
                        } else {
                            projectDateAdapter = new ProjectDateAdapter(ProjectsDatesList.this, dateFormModels);
                            projects_list.setAdapter(projectDateAdapter);
                        }
                    } else {
                        AppUtils.show_snak(getActivity(), object.getString("message"));
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


    private class ProjectDateAdapter extends RecyclerView.Adapter<ProjectDateAdapter.ViewHolder> {
        private Fragment fragment;
        ArrayList<DateFormModel> dateFormModels;

        public ProjectDateAdapter(Fragment fragment, ArrayList<DateFormModel> dateFormModels) {
            this.fragment = fragment;
            this.dateFormModels = dateFormModels;
        }

        public int getItemCount() {
            return dateFormModels.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_form_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final DateFormModel model = dateFormModels.get(position);
            holder.day_tv.setText("Day " + (position + 1));
            holder.date_tv.setText(model.getFormatedDate());
            holder.drp_dwn_img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (holder.btn_layer.getVisibility() == VISIBLE) {
                        Gone(holder.btn_layer);
                        rotate_image(holder.drp_dwn_img, 270, 180);
                    } else {
                        visible(holder.btn_layer);
                        rotate_image(holder.drp_dwn_img, 180, 270);
                    }
                }
            });
            holder.form_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LoadFragment.replace(FormsListFragment.newInstance(project_id, model.getForms()), baseActivity, getResString("lbl_days_form")+"(" + model.getFormatedDate() + ")");
                }
            });
            holder.asset_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LoadFragment.replace(AssetDetail_Fragment.newInstance(model.getDate()), baseActivity, getResString("lbl_asset_details")+"(" + model.getFormatedDate() + ")");
                }
            });
            holder.rope_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LoadFragment.replace(UserRopePreview.newInstance("preview", model.getDate()), baseActivity, getResString("lbl_rope_status"));
                }
            });
        }

        public void update_list(ArrayList<DateFormModel> dateFormModels) {


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView day_tv, date_tv;
            ImageView drp_dwn_img;
            LinearLayout btn_layer, form_btn, asset_btn, rope_btn;

            public ViewHolder(View v) {
                super(v);
                day_tv = v.findViewById(R.id.day_tv);
                date_tv = v.findViewById(R.id.date_tv);
                drp_dwn_img = v.findViewById(R.id.drp_dwn_img);
                btn_layer = v.findViewById(R.id.btn_layer);
                form_btn = v.findViewById(R.id.form_btn);
                asset_btn = v.findViewById(R.id.asset_btn);
                rope_btn = v.findViewById(R.id.rope_btn);
            }
        }
    }

    //  return to store code here


    private ArrayList<Component_model> component_models;

    private void returnToStoreDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Product return to store!")
                .setMessage("Are you sure you want to return all product to store?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        get_projectcomponent();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void get_projectcomponent() {
        component_models = new ArrayList<>();
        if (isNetworkAvailable(getActivity())) {
            String url;
            if (!project_id.equals(""))
                url = All_Api.project_data + user_id + "&project_id=" + project_id;
            else
                return;
            url = url + "&client_id=" + client_id;

            url = url.replace(" ", "%20");
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                component_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_model[].class)));
                                return_toStoreAll(component_models);
                            } else {
                                show_snak(getActivity(), jsonObject.getString("message"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "" + error);
                }
            });
        } else {
            show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }

    public void return_toStoreAll(ArrayList<Component_model> component_models) {
        HashMap<String, String> params = new HashMap<>();
        params.put("store_id", "");
        params.put("user_id", user_id);
        params.put("product_id", "");
        params.put("project_id", project_id);
        params.put("client_id", client_id);
        String master_id = "";
        for (Component_model component_model : component_models) {
            if (master_id.equals(""))
                master_id = component_model.getMdata_id();
            else
                master_id = master_id + "," + component_model.getMdata_id();
        }

        params.put("mdata_id", master_id);
        NetworkRequest.post_data(baseActivity, All_Api.return_to_store, params, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    project_refresh = true;
                    AppUtils.show_snak(baseActivity, "All products returned to store");
                }
            }
        });
    }

}

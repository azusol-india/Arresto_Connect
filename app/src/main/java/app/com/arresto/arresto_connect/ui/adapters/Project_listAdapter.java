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

package app.com.arresto.arresto_connect.ui.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_Project;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Projects_Fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.Site_ListFragment;
import app.com.arresto.arresto_connect.ui.modules.rams.Project_AssetsPage;
import app.com.arresto.arresto_connect.ui.modules.rams.ProjectsDatesList;
import app.com.arresto.arresto_connect.ui.modules.rams.Projects_fragment;
import app.com.arresto.arresto_connect.ui.modules.rams.UsersListFragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image;
import static app.com.arresto.arresto_connect.constants.AppUtils.meterDistanceBetweenPoints;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class Project_listAdapter extends RecyclerView.Adapter<Project_listAdapter.ViewHolder> {
    AppCompatActivity activity;
    private ArrayList project_models;
    public static Project_Model slctd_project;
    private Projects_fragment projects_fragment;
    private EC_Projects_Fragment EC_fragment;
    boolean is_EC;

    public Project_listAdapter(Projects_fragment projects_fragment) {
        this.projects_fragment = projects_fragment;
        this.activity = (AppCompatActivity) projects_fragment.getActivity();
        this.project_models = DataHolder_Model.getInstance().getProject_models();
    }

    public Project_listAdapter(EC_Projects_Fragment projects_fragment) {
        this.EC_fragment = projects_fragment;
        this.activity = (AppCompatActivity) projects_fragment.getActivity();
        this.project_models = DataHolder_Model.getInstance().getEc_project();
        is_EC = true;
    }


    public void update_list(ArrayList<Project_Model> project_models) {
        this.project_models = project_models;
        is_EC = false;
    }

    public void update_EClist(ArrayList<EC_Project> project_models) {
        this.project_models = project_models;
        is_EC = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_item, parent, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ((ViewGroup) holder.delete_btn.getParent()).setVisibility(View.VISIBLE);
        Project_Model project_model = null;
        if (is_EC) {
            final EC_Project ec_projects = (EC_Project) project_models.get(position);
            ((CardView) holder.project_img.getParent()).setVisibility(View.VISIBLE);
            holder.view_btn.setVisibility(View.VISIBLE);
            load_image(ec_projects.getProject_image(), holder.project_img);
            holder.name_tv.setText(ec_projects.getProject_name());
            holder.client_name.setText(getResString("lbl_client") + " : " + ec_projects.getCustomer_name());
            holder.count_tv.setText(getResString("lbl_email") + " : " + ec_projects.getCustomer_email());
            holder.date_tv.setText(getResString("lbl_created_date") + " : " + ec_projects.getCreated_date());
            if (EC_fragment.page_type.equalsIgnoreCase("reports")) {
                holder.edit_btn.setVisibility(View.GONE);
                holder.delete_btn.setVisibility(View.GONE);
            } else {
                holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show_alert(position, ec_projects.getEcp_id());
                    }
                });

                holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EC_fragment.create_ProjectDialog(ec_projects, true);
                    }
                });
            }
            holder.view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.performClick();
                }
            });

        } else {
            project_model = (Project_Model) project_models.get(position);

            holder.view_btn.setVisibility(View.VISIBLE);
            holder.view_btn.setText(getResString("lbl_days_report"));
            holder.name_tv.setText(project_model.getUp_project_name());
            holder.count_tv.setText(getResString("lbl_asset") + " : " + project_model.getProduct_count());

            holder.client_name.setText(getResString("lbl_client") + " : " + project_model.getUp_customer());
            holder.date_tv.setText(getResString("lbl_created_date") + " : " + project_model.getUp_created_date());

            final Project_Model finalProject_model = project_model;
            if (!project_model.getProduct_count().equals("0"))
                holder.delete_btn.setVisibility(View.INVISIBLE);
            else {
                holder.delete_btn.setVisibility(View.VISIBLE);

            }
            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_alert(position, finalProject_model.getUp_id());
                }
            });
            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    projects_fragment.fetch_data(finalProject_model, All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" + user_id);
//                    projects_fragment.create_ProjectDialog(finalProject_model);
                }
            });
            Project_Model final1 = project_model;
            holder.view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slctd_project = final1;
                    LoadFragment.replace(ProjectsDatesList.newInstance(final1.getUp_id()), activity, "Day Reports");
                }
            });
        }
        final Project_Model finalProject = project_model;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_EC) {
                    load_EC((EC_Project) project_models.get(position));
                } else {
                    BaseActivity baseActivity = (BaseActivity) activity;
                    Date start_date = null, end_date = null, current_date = new Date();
                    try {
                        if (finalProject.getUp_startdate() != null && !finalProject.getUp_startdate().equals(""))
                            start_date = baseActivity.server_date_format.parse(finalProject.getUp_startdate());
                        if (finalProject.getUp_enddate() != null && !finalProject.getUp_enddate().equals("")) {
                            end_date = baseActivity.server_date_format.parse(finalProject.getUp_enddate());
                            end_date.setHours(23);
                            end_date.setMinutes(59);
                            end_date.setSeconds(59);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (start_date != null && current_date.before(start_date)) {
                        show_snak(activity, "Can not start project before " + finalProject.getUp_startdate());
                        return;
                    }
//                    if (end_date != null && current_date.after(end_date)) {
//                        show_snak(activity, "Project already finished.");
//                        return;
//                    }
                    if (Profile_Model.getInstance().getConfig() == null || Profile_Model.getInstance().getConfig().getRam_geofencing_flag().equals("0") || finalProject.getUp_geolocation().getLatitude() == null) {
                        load(finalProject);
                    } else if (meterDistanceBetweenPoints(baseActivity.curr_lat, baseActivity.curr_lng, finalProject.getUp_geolocation().getLatitude(), finalProject.getUp_geolocation().getLongitude()) < Integer.parseInt(Profile_Model.getInstance().getConfig().getRam_geofencing_radious())) {
                        load(finalProject);
                    } else {
                        show_snak(activity, "You seem to be out of location.");
                    }
                }
            }
        });
    }

    private void load_EC(EC_Project project_model) {
        DataHolder_Model.getInstance().setSlctd_ec_project(project_model);
        if (EC_fragment.page_type.equalsIgnoreCase("reports")) {
            LoadFragment.replace(new Site_ListFragment(), activity, getResString("lbl_ec_reports"));
        } else {
            LoadFragment.replace(new Site_ListFragment(), activity, getResString("lbl_ec_project_sites"));
        }
    }

    private void load(Project_Model project_model) {
        slctd_project = project_model;
        if (project_model.getTeam() == null || project_model.getTeam().size() == 0 || project_model.getAttendance_marked().equals("1")) {
            Project_AssetsPage mainFragment = new Project_AssetsPage();
            Bundle bundle = new Bundle();
            bundle.putString("page_type", "project_data");
            mainFragment.setArguments(bundle);
            LoadFragment.replace(mainFragment, activity, "Project data");
        } else {
            LoadFragment.replace(UsersListFragment.newInstance("attendance", false), activity, getResString("lbl_team_users"));
        }
    }

    @Override
    public int getItemCount() {
        if (project_models != null)
            return project_models.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, count_tv, client_name, date_tv, view_btn, edit_btn, delete_btn;
        ImageView project_img;

        public ViewHolder(View itemView) {
            super(itemView);
            project_img = itemView.findViewById(R.id.project_img);
            view_btn = itemView.findViewById(R.id.view_btn);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            name_tv = itemView.findViewById(R.id.name_tv);
            count_tv = itemView.findViewById(R.id.count_tv);
            client_name = itemView.findViewById(R.id.client_name);
            date_tv = itemView.findViewById(R.id.date_tv);
        }
    }

    private void show_alert(final int pos, final String project_id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle("Confirmation!")
                .setMessage("Are You Sure you want to delete this project!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        delete_project(pos, project_id);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void delete_project(final int position, String project_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", Static_values.user_id);
        params.put("client_id", client_id);
        String url;
        if (is_EC) {
            params.put("ecp_id", project_id);
            url = All_Api.EC_delete_project;
        } else {
            params.put("project_id", project_id);
            url = All_Api.asm_delete_project;
        }
        Log.e("params", "" + params);
        NetworkRequest network_request = new NetworkRequest(activity);
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
                        AppUtils.show_snak(activity, jsonObject.getString("message"));
                        if (msg_code.equals("200")) {
                            project_models.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
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


}

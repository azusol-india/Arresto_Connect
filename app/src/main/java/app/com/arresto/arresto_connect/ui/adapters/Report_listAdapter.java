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

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.currently_inspected;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.OnLoadMoreListener;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.ReportInspectionModel;
import app.com.arresto.arresto_connect.data.models.Reports_model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;
import app.com.arresto.arresto_connect.ui.modules.inspection.thermal.ThermalImageProcessing;
import app.com.arresto.arresto_connect.ui.modules.rams.Inspected_FormsFragment;

public class Report_listAdapter extends RecyclerView.Adapter {
    Base_Fragment base_fragment;
    BaseActivity activity;
    private String type = "";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Reports_model> list_data;

    public Report_listAdapter(Base_Fragment base_fragment, List<Reports_model> list_data, String type) {
        this.base_fragment = base_fragment;
        this.activity = base_fragment.baseActivity;
        this.list_data = list_data;
        this.type = type;
    }

    public void addData(ArrayList<Reports_model> list_data) {
        this.list_data = list_data;
        notifyDataSetChanged();
    }

    public List<Reports_model> get_data() {
        return list_data;
    }

    public int getItemCount() {
        return list_data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list_data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_item, parent, false);
            vh = new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //    public ShowcaseView getView(final int position, ShowcaseView view, final ViewGroup parent){
    public void onBindViewHolder(final RecyclerView.ViewHolder Vholder, final int position) {
        if (Vholder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) Vholder;
            Reports_model reports_model = list_data.get(position);
            String status = reports_model.getApproved_status().substring(0, 1).toUpperCase() + reports_model.getApproved_status().substring(1);
            holder.textView2.setText(status);
            if (reports_model.getSite_id() != null && type.equals("asm_report")) {
                holder.textView0.setText("Project" + " - " + reports_model.getSite_id());
                holder.textView1.setText("Technician" + " - " + reports_model.getProduct_code() + "\n" + getResString("lbl_report_no.") + " - " + reports_model.getReport_no());
            } else if (reports_model.getCreated_date() != null && type.equals("pdm_report")) {
                try {
                    holder.textView0.setText("Maintenance Date - " + activity.Date_Format().format(activity.server_date_time.parse(reports_model.getCreated_date())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.textView1.setText(getResString("lbl_product_name") + " - " + reports_model.getProduct_code() + "\n" + getResString("lbl_report_no.") + " - " + reports_model.getReport_no());
            } else if (type.equals("safety")) {
                holder.textView0.setText("Form name" + " - " + reports_model.getSite_id());
                try {
                    holder.textView1.setText("Safety Manager - " + reports_model.getProduct_code() +
                            "\n" + getResString("lbl_created_date") + " - " + activity.Date_Format().format(activity.server_date_time.parse(reports_model.getCreated_date())) +
                            "\n" + getResString("lbl_report_no.") + " - " + reports_model.getReport_no());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("preuse")) {
                holder.textView0.setText(getResString("lbl_manual_entry") + " - " + reports_model.getSite_id());
                holder.textView1.setText(getResString("lbl_product_name") + " - " + reports_model.getProduct_code() +
                        "\n" + getResString("lbl_report_no.") + " - " + reports_model.getReport_no() +
                        "\n" + getResString("lbl_confirmation_date") + " - " + reports_model.getCreated_date());
            } else {
                if (!client_id.equals("2069") && !reports_model.getApproved_status().equalsIgnoreCase("approved") && !reports_model.getApproved_status().equalsIgnoreCase("rejected") && group_id.equals("9")) {
                    holder.edit_btn.setVisibility(View.VISIBLE);
                    if (reports_model.getThermal_id() != null && !reports_model.getThermal_id().equals(""))
                        holder.edit_btn.setText(getResString("lbl_edit_thermography"));
                    else
                        holder.edit_btn.setText("Edit Inspection");
                } else {
                    holder.edit_btn.setVisibility(View.GONE);
                }

                if (reports_model.getApprover() != null && reports_model.getUser_type() != null && group_id.equals("8")) {
                    if (!reports_model.getApprover().equals(""))
                        holder.name_tv.setVisibility(View.VISIBLE);
                    else
                        holder.name_tv.setVisibility(View.GONE);
                    holder.name_tv.setText(getResString("lbl_name") + " - " + reports_model.getApprover());
                    holder.textView2.setText(status + "\n(" + reports_model.getUser_type() + ")");
                } else {
                    holder.name_tv.setVisibility(View.GONE);
                }
                if (reports_model.getSite_id() == null || reports_model.getSite_id().equals(""))
                    holder.textView0.setText(getResString("lbl_site_st") + " - NA");
                else
                    holder.textView0.setText(getResString("lbl_site_st") + " - " + reports_model.getSite_id());
                holder.textView1.setText(getResString("lbl_product_name") + " - " + reports_model.getProduct_code() +
                        "\n" + getResString("lbl_report_no.") + " - " + reports_model.getReport_no() +
                        "\n" + getResString("lbl_inspection_date") + " - " + reports_model.getCreated_date());
            }

            holder.view_btn.setOnClickListener(new OnItemClickListener(position));
            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (reports_model.getThermal_id() != null && !reports_model.getThermal_id().equals("")) {
                        if (reports_model.getApproved_status().equalsIgnoreCase("replace")) {
                            base_fragment.chooseThermographyImage(Static_values.directory, new Base_Fragment.OnImageCapture() {
                                @Override
                                public void onCapture(String path) {
                                    if (!path.equals("")) {
                                        synchronized (view) {
                                            Intent camera = new Intent(activity, ThermalImageProcessing.class);
                                            camera.putExtra("imagePath", path);
                                            camera.putExtra("image_dir", Static_values.directory);
                                            camera.putExtra("inspection_id", reports_model.getId());
                                            camera.putExtra("thermal_id", reports_model.getThermal_id());
                                            camera.putExtra("isReplace", true);
                                            activity.startActivity(camera);
                                        }
                                    } else {
                                        show_snak(activity, "Please select or capture an image.");
                                    }
                                }
                            });
                        } else {
                            Intent camera = new Intent(activity, ThermalImageProcessing.class);
                            camera.putExtra("inspection_id", reports_model.getId());
                            camera.putExtra("thermal_id", reports_model.getThermal_id());
                            activity.startActivity(camera);
                        }
                    } else {
                        getAllSubAsset(reports_model);
                    }
                }
            });

            switch (reports_model.getApproved_status().toLowerCase()) {
                case "pending":
                    holder.indctr_img.setImageResource(R.drawable.light_yellow);
                    if (currently_inspected && position == 0) {
                        holder.view_btn.performClick();
                        currently_inspected = false;
                    }
                    break;
                case "replace":
                    holder.indctr_img.setImageResource(R.drawable.light_yellow);
                    break;
                case "approved":
                    holder.indctr_img.setImageResource(R.drawable.light_green);
                    break;
                default:
                    holder.indctr_img.setImageResource(R.drawable.light_red);
                    break;
            }
//            if (type.equals("asm_report") || type.equals("safety")) {
//                holder.indctr_img.setVisibility(View.GONE);
//                holder.textView2.setVisibility(View.GONE);
//            }

        }
    }

    private class OnItemClickListener implements View.OnClickListener {

        private int position;

        OnItemClickListener(int pos) {
            this.position = pos;
        }

        //        asset_code/ inspector id
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.view_btn) {
                Report_webview report_webview = new Report_webview();
                Bundle bundle = new Bundle();
                bundle.putString("status", list_data.get(position).getApproved_status().toLowerCase());
                bundle.putString("type", type);
                switch (type) {
                    case "asm_report":
                        String url = All_Api.asm_inspecte_forms + client_id + "&project_id=" + list_data.get(position).getId() + "&user_id=" + Static_values.user_id
                                + "&report_no=" + list_data.get(position).getReport_no();
                        LoadFragment.replace(Inspected_FormsFragment.new_instance(url, "report", list_data.get(position).getReport_no()), activity, getResString("lbl_rams_report"));
                        break;
                    case "pdm_report":
                        if (list_data.get(position).getApproved_status().equals("pending")) {
                            bundle.putString("url", All_Api.pdm_pending_report + list_data.get(position).getReport_no());
                            bundle.putString("id", list_data.get(position).getId());
                            bundle.putString("report_no", list_data.get(position).getReport_no());
                        } else
                            bundle.putString("url", list_data.get(position).getReport_url());
                        report_webview.setArguments(bundle);
                        LoadFragment.replace(report_webview, activity, getResString("lbl_maintenance_report"));
                        break;
                    case "safety":
                        if (list_data.get(position).getApproved_status().equals("pending")) {
                            bundle.putString("url", All_Api.safety_view + client_id + "&user_id=" + Static_values.user_id + "&sf_id=" + list_data.get(position).getId() + "&report_no=" + list_data.get(position).getReport_no());
                            bundle.putString("id", list_data.get(position).getId());
                            bundle.putString("report_no", list_data.get(position).getReport_no());
                        } else {
                            bundle.putString("url", All_Api.safety_report + client_id + "&user_id=" + Static_values.user_id + "&report_no=" + list_data.get(position).getReport_no());
                        }
                        report_webview.setArguments(bundle);
                        LoadFragment.replace(report_webview, activity, getResString("lbl_safety_management_report"));
                        break;

                    case "preuse":
                        bundle.putString("url", All_Api.preuse_report + client_id + "&ins_id=" + list_data.get(position).getId());
//                        bundle.putString("url", list_data.get(position).getReport_url());
                        report_webview.setArguments(bundle);
                        LoadFragment.replace(report_webview, activity, getResString("lbl_confirm_report"));
                        break;
                    default:
                        if (list_data.get(position).getReport_url() != null && !list_data.get(position).getReport_url().equals(""))
                            bundle.putString("url", list_data.get(position).getReport_url());
                        else
                            bundle.putString("url", All_Api.inspectorInspectionListbyID + list_data.get(position).getId() + "&userID=" + Static_values.user_id + "&client_id=" + client_id);
                        bundle.putString("report_no", list_data.get(position).getReport_no());
                        bundle.putString("thermal_id", list_data.get(position).getThermal_id());
                        bundle.putString("id", list_data.get(position).getId());
                        bundle.putString("inspected_by", list_data.get(position).getInspected_by());
                        report_webview.setArguments(bundle);
                        LoadFragment.replace(report_webview, activity, getResString("lbl_inspection_report"));
                        break;
                }
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView0, textView1, textView2, name_tv;
        MaterialButton edit_btn, view_btn;
        ImageView indctr_img;

        public ViewHolder(View view) {
            super(view);
            textView0 = view.findViewById(R.id.inspector_text0);
            textView1 = view.findViewById(R.id.inspector_text1);
            textView2 = view.findViewById(R.id.inspector_text2);
            name_tv = view.findViewById(R.id.name_tv);
            edit_btn = view.findViewById(R.id.edit_btn);
            view_btn = view.findViewById(R.id.view_btn);
            indctr_img = view.findViewById(R.id.indctr_img);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
//            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


    public void getAllSubAsset(Reports_model reports_model) {
//        https://arresto.in/connectkare/api_controller/get_inspection_data/25827?client_id=376
        new NetworkRequest(activity).getMasterData(reports_model.getMdata_id(), user_id, new ObjectListener() {
            @Override
            public void onResponse(Object obj) {
                MasterData_model masterData_model = (MasterData_model) obj;
                getLastInspectionData(reports_model, masterData_model);
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }

    public static List<ReportInspectionModel> lastInspectData;

    public void getLastInspectionData(Reports_model reports_model, MasterData_model masterData_model) {
        new NetworkRequest(activity).make_get_request(All_Api.inspection_data_api + reports_model.getId() + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    String inspection_result = res.getJSONObject("data").getString("inspection_result");
                    lastInspectData = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(inspection_result, ReportInspectionModel[].class)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("data == ", "is = " + lastInspectData);
                InspectionListItems inspectionListItems = new InspectionListItems();
                Bundle bundle = new Bundle();
                bundle.putString("inspection_id", reports_model.getId());
                bundle.putStringArray("asset_code", masterData_model.getAssetCodes().split("####"));
                inspectionListItems.setArguments(bundle);
                LoadFragment.replace(inspectionListItems, activity, getResString("lbl_inspection"));
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }
}

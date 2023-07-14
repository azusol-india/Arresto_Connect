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

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Inspected_Form_Model;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

public class Inspected_FormsFragment extends Fragment {
    public static Inspected_FormsFragment new_instance(String url, String project_id, String report_no) {
        Inspected_FormsFragment inspected_formsFragment = new Inspected_FormsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("project_id", project_id);
        bundle.putString("report_no", report_no);
        inspected_formsFragment.setArguments(bundle);
        return inspected_formsFragment;
    }

    View view;
    LinearLayout all_views;
    String url,project_id, report_no;
    TextView complte_report;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.inspected_form_fragment, container, false);
            setupUI(view, getActivity());
            find_id();
            if (getArguments() != null) {
                url = getArguments().getString("url");

                report_no = getArguments().getString("report_no");
                project_id = getArguments().getString("project_id");
                get_project_inspected_data();
            }
        }
        return view;
    }

    private void find_id() {
        all_views = view.findViewById(R.id.all_views);
        complte_report = view.findViewById(R.id.complte_report);
        complte_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inspected_form_models != null) {
                    Report_webview report_webview = new Report_webview();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "ASM");
                    bundle.putString("url", All_Api.asm_custom_formReportView + client_id + "&project_id=" + project_id + "&user_id=" + Static_values.user_id + "&report_no=" + report_no);
                    report_webview.setArguments(bundle);
                    LoadFragment.replace(report_webview,  getActivity(), getResString("lbl_rams_report"));
                }
            }
        });
    }

    ArrayList<Inspected_Form_Model> inspected_form_models;

    public void get_project_inspected_data() {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();

                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status_code").equals("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if (jsonObject1.has("form_list")) {
                                inspected_form_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getJSONObject("data").getString("form_list"), Inspected_Form_Model[].class)));

                                sort_data();
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

    //    ArrayList<String> all_dates;
    ArrayList<String> unique_dates;

    public void sort_data() {
//        all_dates = new ArrayList<>();
        unique_dates = new ArrayList<>();

        Collections.sort(inspected_form_models, new Comparator<Inspected_Form_Model>() {
            public int compare(Inspected_Form_Model obj1, Inspected_Form_Model obj2) {
                return obj2.get_date().compareTo(obj1.get_date());
            }
        });

        all_views.removeAllViews();
        for (int i = 0; i < inspected_form_models.size(); i++) {
            final Inspected_Form_Model inspected_form_model = inspected_form_models.get(i);
//            all_dates.add(inspected_form_model.getCreated_date());

            if (!unique_dates.contains(inspected_form_model.getCreated_date())) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.date_form_item, null);
                TextView tvTime = view.findViewById(R.id.date_tv);
                TextView rope_btn = view.findViewById(R.id.rope_btn);
                TextView aset_btn = view.findViewById(R.id.aset_btn);
                LinearLayout forms_list = view.findViewById(R.id.forms_list);
                tvTime.setText(inspected_form_model.getCreated_date());
                add_tab(forms_list, inspected_form_model);
                all_views.addView(view);
                unique_dates.add(inspected_form_model.getCreated_date());

                rope_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = All_Api.asm_rope_ReportView + client_id + "&project_id=" + inspected_form_model.getProject_id() + "&user_id=" + Static_values.user_id + "&report_no=" + report_no;
                        load_Fragment(url);
                    }
                });
                aset_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = All_Api.asm_custom_formLogView + client_id + "&project_id=" + inspected_form_model.getProject_id() + "&user_id=" + Static_values.user_id + "&report_no=" + report_no + "&form_index=" + "" + inspected_form_model.getCf_id();
                        load_Fragment(url);
                    }
                });

            } else {
                View view = all_views.getChildAt(unique_dates.size() - 1);
                LinearLayout forms_list = view.findViewById(R.id.forms_list);
                add_tab(forms_list, inspected_form_model);
            }
        }
//        add_rope_tabs(unique_dates,all_views);
//        Log.e("all dates", " is " + all_dates);
        Log.e("unique_dates", " is " + unique_dates);
    }


    public void add_tab(LinearLayout list, final Inspected_Form_Model inspected_form_model) {
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView tab_label = new TextView(getActivity());
        params1.setMargins(5, 3, 5, 5);
        tab_label.setLayoutParams(params1);
        tab_label.setGravity(Gravity.CENTER);
        tab_label.setSingleLine(false);
        tab_label.setLines(2);
        tab_label.setMaxLines(2);
        tab_label.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        tab_label.setBackgroundResource(R.drawable.edittext_bg1);
        GradientDrawable gradientDrawable = (GradientDrawable) tab_label.getBackground();
        gradientDrawable.setStroke(1, Dynamic_Var.getInstance().getBtn_bg_clr());
        tab_label.setPadding(10, 0, 10, 0);
        tab_label.setBackground(gradientDrawable);
        tab_label.setText(inspected_form_model.getForm_name());
        tab_label.setTextColor(Dynamic_Var.getInstance().getApp_background());
        gradientDrawable.setColor(Dynamic_Var.getInstance().getBtn_bg_clr());

        tab_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = All_Api.asm_custom_formReportView + client_id + "&project_id=" + inspected_form_model.getProject_id() + "&user_id=" + Static_values.user_id + "&report_no=" + report_no + "&form_index=" + "" + inspected_form_model.getCf_id();
                load_Fragment(url);
            }
        });

        list.addView(tab_label);
    }

    public void load_Fragment(String url) {
        Report_webview report_webview = new Report_webview();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("type", "ASM");
        report_webview.setArguments(bundle);
        LoadFragment.replace(report_webview,  getActivity(), getResString("lbl_rams_report"));
    }

}

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

package app.com.arresto.arresto_connect.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class Inspection_parameter_fragment extends Base_Fragment {
    String product_name, product_type, master_id, page_type, is_confirm, history_available;
    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inspection_params_lay, parent, false);
        if (getArguments() != null) {
            product_name = getArguments().getString("product_name");
            product_type = getArguments().getString("product_type");
            master_id = getArguments().getString("master_id");
            page_type = getArguments().getString("page_type", "");
            is_confirm = getArguments().getString("is_confirm", "0");
            history_available = getArguments().getString("preuse_time");
            String url = All_Api.ins_parameter_api + product_type + "/" + product_name + "?client_id=" + client_id;
            if (!page_type.equals("") && (page_type.contains("periodic") || page_type.contains("maintenanceDue"))) {
                page_type = "pdm";
                url = url + "&pdm=1";
            } else {
                page_type = "inspection";
            }
            find_all_id(view);
            get_product_data(url);
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (is_confirm.equals("0"))
                        confirm_alert();
                    else
                        showConfirmDialog();
                }
            });

            report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", "preuse");
                    bundle.putString("preuse_url", "" + All_Api.preuse_reportList + client_id + "&mdata_id=" + master_id);
                    bundle.putInt("index", 0);
                    Close_projectFragment close_projectFragment1 = new Close_projectFragment();
                    close_projectFragment1.setArguments(bundle);
                    LoadFragment.replace(close_projectFragment1, getActivity(), getResString("lbl_confirm_report"));
                }
            });
        }
        return view;
    }

    //    {"user_id":12,"client_id":376,"mdata_id":112233,"is_confirmed":1,"type":"pdm/inspection"}
    private void showConfirmDialog() {
        JSONObject post_data = new JSONObject();
        try {
            post_data.put("user_id", user_id);
            post_data.put("client_id", client_id);
            post_data.put("mdata_id", master_id);
            post_data.put("type", page_type);
            post_data.put("geo_location", baseActivity.curr_lat + "," + baseActivity.curr_lng);
            post_data.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog builder = new Dialog(getActivity(), R.style.theme_dialog);
//        final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.confirm_dialog, null);

        TextView not_ok_btn = dialoglayout.findViewById(R.id.not_ok_btn);
        TextView ok_btn = dialoglayout.findViewById(R.id.ok_btn);
        EditText edt_remark = dialoglayout.findViewById(R.id.edt_remark);
        edt_remark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                ok_btn.setVisibility(View.GONE);
            }
        });
        not_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post_data.put("is_confirmed", "0");
                    if (edt_remark.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), getResString("lbl_remark_msg"), Toast.LENGTH_LONG).show();
                    } else {
                        post_data.put("remark", edt_remark.getText().toString());
                        ok_alert(post_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.dismiss();
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post_data.put("is_confirmed", "1");
                    post_data.put("remark", edt_remark.getText().toString());
                    send_data(post_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.dismiss();
            }
        });

        builder.setContentView(dialoglayout);
        builder.show();
    }

    TextView code_txt, description_txt, confirm_btn,disclaimer_txt, report_btn, uom_tv, inspect_txt, expect_text;
    ImageView head_img;
    LinearLayout sub_productlist;

    private void find_all_id(View view) {
        code_txt = view.findViewById(R.id.code_txt);
        head_img = view.findViewById(R.id.head_img);
        description_txt = view.findViewById(R.id.description_txt);
        confirm_btn = view.findViewById(R.id.confirm_btn);
        report_btn = view.findViewById(R.id.report_btn);
        uom_tv = view.findViewById(R.id.uom_tv);
        inspect_txt = view.findViewById(R.id.inspect_txt);
        expect_text = view.findViewById(R.id.expect_text);
        disclaimer_txt = view.findViewById(R.id.disclaimer_txt);
        sub_productlist = view.findViewById(R.id.sub_productlist);
        code_txt.setText(product_name);
        disclaimer_txt.setText(Html.fromHtml(getResString("lbl_disclaimer")).toString().replace("\"", ""));
        if (!history_available.equals(""))
            report_btn.setVisibility(VISIBLE);
    }

    public void get_product_data(String url) {
        url = url.replace(" ", "%20");
        if (isNetworkAvailable(getActivity())) {
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONArray) {
                            Log.e("response is", response);
                        } else if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);

                            description_txt.setText(jsonObject.getString("product_description"));
                            inspect_txt.setText(jsonObject.getString("product_inspectiontype"));
                            if (jsonObject.has("product_expectedresult")) {
                                expect_text.setText(jsonObject.getString("product_expectedresult"));
                                uom_tv.setText(jsonObject.getString("product_uom"));
                            }
                            final String img_url = jsonObject.getString("product_imagepath");

                            AppUtils.load_image(img_url, head_img);
                            set_obsrve_data(jsonObject.getJSONArray("data"));

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
            AppUtils.show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }

    ArrayList<String> split_observation, split_expcted_rslt;

    public void set_obsrve_data(JSONArray jsonArray) {
        Log.e("jsonArray", "" + jsonArray);
        sub_productlist.removeAllViews();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jresponse = jsonArray.getJSONObject(i);
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_added_asset_item, sub_productlist, false);
                TextView ast_sris_txtx = view1.findViewById(R.id.aset_serestxt);
                final ImageView drp_dwn_img = view1.findViewById(R.id.drp_dwn_img);
                ImageView ast_sris_img = view1.findViewById(R.id.item_img);
                final LinearLayout dynamic_layer = view1.findViewById(R.id.tep_list);
                dynamic_layer.setVisibility(View.GONE);

                ast_sris_txtx.setText(jresponse.getString("component_code"));
                AppUtils.load_image(jresponse.getString("component_imagepath"), ast_sris_img);
                split_expcted_rslt = new ArrayList<>();
                split_observation = new ArrayList<>();
                split_expcted_rslt.addAll(Arrays.asList(jresponse.getString("component_expectedresult").split("##")));
                split_observation.addAll(Arrays.asList(jresponse.getString("component_observation").split("##")));
                sub_productlist.addView(view1);
                add_observation_expectedresult(dynamic_layer, split_expcted_rslt, "results");
                drp_dwn_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = dynamic_layer;
                        if (view.getVisibility() == VISIBLE) {
//                            view.setVisibility(View.GONE);
                            Gone(view);
                            rotate_image(drp_dwn_img, 270, 180);
                        } else {
//                            view.setVisibility(View.VISIBLE);
                            visible(view);
                            rotate_image(drp_dwn_img, 180, 270);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void add_observation_expectedresult(LinearLayout dynamic_layer, final ArrayList<String> array_data, String type) {
        final TextView textView = new TextView(getActivity());
        textView.setTextSize(14);
        textView.setTextColor(Dynamic_Var.getInstance().getBtn_bg_clr());
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (type.equals("results")) {
            textView.setText(getResString("lbl_expected_result"));
        } else {
            textView.setPadding(0, 7, 0, 0);
            textView.setText(getResString("lbl_common_observation"));
        }
        dynamic_layer.addView(textView);
        for (int i = 0; i < array_data.size(); i++) {
            final TextView textView1 = new TextView(getActivity());
            textView1.setPadding(5, 2, 5, 1);
            textView1.setTextColor(AppUtils.getResColor(R.color.app_text));
            textView1.setText((i + 1) + ". " + array_data.get(i));
            dynamic_layer.addView(textView1);
        }
        if (type.equals("results")) {
            add_observation_expectedresult(dynamic_layer, split_observation, "observation");
        } else {
//            sub_productlist.addView(dynamic_layer);
        }
    }

    public void ok_alert(JSONObject post_data) {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.error)
                .setTitle(getResString("lbl_caution"))
                .setMessage(getResString("lbl_trafficlight_inspector"))
                .setPositiveButton(getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        send_data(post_data);
                    }
                })
                .show();
    }

    public void confirm_alert() {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.error)
                .setTitle(getResString("lbl_caution"))
                .setMessage(getResString("lbl_iconfirm"))
                .setPositiveButton(getResString("lbl_ok_st"), null)
                .show();
    }

    public void send_data(JSONObject params) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(All_Api.post_confirmation, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        AppUtils.show_snak(getActivity(), jsonObject.getString("msg"));
                        if (msg_code.equals("200")) {
                            report_btn.setVisibility(VISIBLE);
                            if (params.getString("is_confirmed").equals("0")) {
                                is_confirm = "0";
                            }
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

/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.network.All_Api.Ec_quote;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.EC_project_Model;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;

public class EC_Project_BOQFragment extends EC_Base_Fragment {
    View view;
    TextView obserber_tv, termination_tv, wire_tv, wire_heading_tv, tensioner_tv, intermediate_tv, extremity_tv,
            intrmedt_post_tv, corner_tv, harness_tv, element_tv, body_tv, finish_btn, tp_connection_tv;
    EC_project_Model ec_project_model;
    LinearLayout segment_root, user_container;
    ImageView sgmnt_img;
    TextView right_tv, top_tv, btm_tv, lft_tv;
    boolean isQuote;

    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            if (getArguments() != null) {
                ec_project_model = AppUtils.getGson().fromJson(getArguments().getString("data"), EC_project_Model.class);
                isQuote = getArguments().getBoolean("isQuote", false);
                if (ec_project_model.getType().equals("Horizontal Life Line Systems")) {
                    view = inflater.inflate(R.layout.ec_project_boq, container, false);
                    findAllIds(view);
                } else {
                    view = inflater.inflate(R.layout.ec_vertical_boq, container, false);
                    findAllId_vertical(view);
                }
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec boq");
    }

    public void findAllIds(View view) {
        segment_root = view.findViewById(R.id.segment_root);
        obserber_tv = view.findViewById(R.id.obserber_tv);
        termination_tv = view.findViewById(R.id.termination_tv);
        wire_heading_tv = view.findViewById(R.id.wire_heading_tv);
        wire_tv = view.findViewById(R.id.wire_tv);
        tensioner_tv = view.findViewById(R.id.tensioner_tv);
        intermediate_tv = view.findViewById(R.id.intermediate_tv);
        extremity_tv = view.findViewById(R.id.extremity_tv);
        intrmedt_post_tv = view.findViewById(R.id.intrmedt_post_tv);
        corner_tv = view.findViewById(R.id.corner_tv);
        harness_tv = view.findViewById(R.id.harness_tv);
        element_tv = view.findViewById(R.id.element_tv);
        body_tv = view.findViewById(R.id.body_tv);
        sgmnt_img = view.findViewById(R.id.sgmnt_img);
        user_container = view.findViewById(R.id.user_container);
        finish_btn = view.findViewById(R.id.finish_btn);
        top_tv = view.findViewById(R.id.top_tv);
        right_tv = view.findViewById(R.id.right_tv);
        btm_tv = view.findViewById(R.id.btm_tv);
        lft_tv = view.findViewById(R.id.lft_tv);
        if (isQuote) {
            finish_btn.setText(getResString("lbl_sbmit_st"));
            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject quoteData = new JSONObject(getGson().toJson(ec_project_model));
                        quoteData.put("client_id", Static_values.client_id);
                        quoteData.put("user_id", Static_values.user_id);
                        Log.e("quote data", " is " + quoteData);
                        send_data(quoteData, Ec_quote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            submit here and share
        } else {
            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ec_project_model.setExtremity_post("2");
                    EC_Manual_overview ec_manual_overview = new EC_Manual_overview();
                    Bundle bundle = new Bundle();
                    bundle.putString("data", AppUtils.getGson().toJson(ec_project_model));
                    ec_manual_overview.setArguments(bundle);
                    LoadFragment.replace(ec_manual_overview, getActivity(), "Bill of Quantity(BOQ) & Quote");
                }
            });
        }
        if (ec_project_model != null)
            set_data();
    }

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
                            String report_url = jsonObject.getJSONObject("data").getString("report");
                            Report_webview report_webview = new Report_webview();
                            Bundle bundle = new Bundle();
                            bundle.putString("url", report_url);
                            report_webview.setArguments(bundle);
                            HomeActivity.homeActivity.getSupportFragmentManager().popBackStackImmediate();
                            HomeActivity.homeActivity.getSupportFragmentManager().popBackStackImmediate();
                            LoadFragment.replace(report_webview, HomeActivity.homeActivity, getResString("lbl_ec_reports"));
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

    private void set_data() {
        obserber_tv.setText(ec_project_model.getAbsorber());
        wire_tv.setText(ec_project_model.getTotal_sgment());
        int C = 0, E = 0, G = 0, H = 0;
        if (ec_project_model.getSegment_1() != null) {
            C = getIntermediates(ec_project_model.getSegment_1().getLength(), ec_project_model.getSegment_1().getSpan());
            top_tv.setVisibility(View.VISIBLE);
            if (!ec_project_model.isMeter()) {
                top_tv.setText(ec_project_model.getSegment_1().getLength() + " Ft.");
            } else {
                top_tv.setText(ec_project_model.getSegment_1().getLength() + " Mtr.");
            }
        }
        if (ec_project_model.getSegment_2() != null) {
            E = getIntermediates(ec_project_model.getSegment_2().getLength(), ec_project_model.getSegment_2().getSpan());
            right_tv.setVisibility(View.VISIBLE);
            if (!ec_project_model.isMeter()) {
                right_tv.setText(ec_project_model.getSegment_2().getLength() + " Ft.");
            } else {
                right_tv.setText(ec_project_model.getSegment_2().getLength() + " Mtr.");
            }
        }
        if (ec_project_model.getSegment_3() != null) {
            G = getIntermediates(ec_project_model.getSegment_3().getLength(), ec_project_model.getSegment_3().getSpan());
            btm_tv.setVisibility(View.VISIBLE);
            if (!ec_project_model.isMeter()) {
                btm_tv.setText(ec_project_model.getSegment_3().getLength() + " Ft.");
            } else {
                btm_tv.setText(ec_project_model.getSegment_3().getLength() + " Mtr.");
            }
        }
        if (ec_project_model.getSegment_4() != null) {
            H = getIntermediates(ec_project_model.getSegment_4().getLength(), ec_project_model.getSegment_4().getSpan());
            lft_tv.setVisibility(View.VISIBLE);
            if (!ec_project_model.isMeter()) {
                lft_tv.setText(ec_project_model.getSegment_4().getLength() + " Ft.");
            } else {
                lft_tv.setText(ec_project_model.getSegment_4().getLength() + " Mtr.");
            }
        }
        int intrmdit_pst = C + E + G + H;
        ec_project_model.setIntermediate_post("" + intrmdit_pst);
        intermediate_tv.setText("" + intrmdit_pst);
        intrmedt_post_tv.setText("" + intrmdit_pst);
        corner_tv.setText(ec_project_model.getCorner());
        harness_tv.setText(ec_project_model.getUsers());
        element_tv.setText(ec_project_model.getUsers());
        body_tv.setText(ec_project_model.getUsers());
        segment_root.removeAllViews();
        calculate_data(ec_project_model, segment_root);
        sgmnt_img.setImageResource(getResources().getIdentifier("segment_" + ec_project_model.getCorner(), "drawable", getActivity().getPackageName()));
        design_user();
        if (ec_project_model.isMeter()) {
            wire_heading_tv.setText("Wire (Mtr)");
        } else {
            wire_heading_tv.setText("Wire (Ft)");
        }
    }

    private void design_user() {
        user_container.removeAllViews();
        for (int i = 0; i < user; i++) {
            user_container.addView(new_image());
        }
    }

    private ImageView new_image() {
        ImageView imageView = new ImageView(getActivity());
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.margin_50dp), (int) getResources().getDimension(R.dimen.margin_70dp));
        vp.setMargins(-15, 0, 0, 0);
        imageView.setImageResource(R.drawable.ec_user);
        imageView.setLayoutParams(vp);
        return imageView;
    }


    TextView botm_cbl_tv, top_cbl_tv, mounting_tv, rope_grab_tv, extension_tv, alu_intr_tv, alu_extension_tv,
            junction_tv, nut_tv, bracket_tv, trolley_tv, rung_tv, left_tv, full_rung_tv, mounting_post_tv, fasteners_tv, folding_aluminum_tv;
    LinearLayout wire_lay, alurail_lay;

    public void findAllId_vertical(View view) {
        obserber_tv = view.findViewById(R.id.obserber_tv);
        wire_heading_tv = view.findViewById(R.id.wire_heading_tv);
        wire_tv = view.findViewById(R.id.wire_tv);
        tensioner_tv = view.findViewById(R.id.tensioner_tv);
        intermediate_tv = view.findViewById(R.id.intermediate_tv);
        extremity_tv = view.findViewById(R.id.extremity_tv);
        tp_connection_tv = view.findViewById(R.id.tp_connection_tv);

        botm_cbl_tv = view.findViewById(R.id.botm_cbl_tv);
        top_cbl_tv = view.findViewById(R.id.top_cbl_tv);
        mounting_tv = view.findViewById(R.id.mounting_tv);
        rope_grab_tv = view.findViewById(R.id.rope_grab_tv);
        extension_tv = view.findViewById(R.id.extension_tv);
        alu_intr_tv = view.findViewById(R.id.alu_intr_tv);
        alu_extension_tv = view.findViewById(R.id.alu_extension_tv);
        junction_tv = view.findViewById(R.id.junction_tv);
        nut_tv = view.findViewById(R.id.nut_tv);
        bracket_tv = view.findViewById(R.id.bracket_tv);
        trolley_tv = view.findViewById(R.id.trolley_tv);
        wire_lay = view.findViewById(R.id.wire_lay);
        alurail_lay = view.findViewById(R.id.alurail_lay);
        mounting_post_tv = view.findViewById(R.id.mounting_post_tv);
        fasteners_tv = view.findViewById(R.id.fasteners_tv);
        folding_aluminum_tv = view.findViewById(R.id.folding_aluminum_tv);
        rung_tv = view.findViewById(R.id.rung_tv);
        left_tv = view.findViewById(R.id.left_tv);
        right_tv = view.findViewById(R.id.right_tv);
        full_rung_tv = view.findViewById(R.id.full_rung_tv);

        finish_btn = view.findViewById(R.id.finish_btn);
        if (isQuote) {
            finish_btn.setText(getResString("lbl_sbmit_st"));
            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject quoteData = new JSONObject(getGson().toJson(ec_project_model));
                        quoteData.put("client_id", Static_values.client_id);
                        quoteData.put("user_id", Static_values.user_id);
                        Log.e("quote data", " is " + quoteData);
                        send_data(quoteData, Ec_quote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            submit here and shares
        } else {
            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EC_Manual_overview ec_manual_overview = new EC_Manual_overview();
                    Bundle bundle = new Bundle();
                    bundle.putString("data", AppUtils.getGson().toJson(ec_project_model));
                    ec_manual_overview.setArguments(bundle);
                    LoadFragment.replace(ec_manual_overview, getActivity(), "Bill of Quantity(BOQ) & Quote");
                }
            });
        }
        if (ec_project_model != null)
            set_data_vertical();
    }

    private void set_data_vertical() {
        if (ec_project_model.getType().equals("Wire Rope")) {
            wire_lay.setVisibility(View.VISIBLE);
            alurail_lay.setVisibility(View.GONE);
            if (ec_project_model.getSystem_type().equals("7000")) {
                obserber_tv.setText("1");
                tp_connection_tv.setText("0");
            } else {
                obserber_tv.setText("0");
                tp_connection_tv.setText("1");
            }
            if (ec_project_model.getExtension_arm().equals("Yes")) {
                wire_tv.setText("" + (Double.parseDouble(ec_project_model.getLife_lineLength()) + 1.5));
                extension_tv.setText("1");
                mounting_tv.setText("1");
            } else {
                wire_tv.setText(ec_project_model.getLife_lineLength());
                extension_tv.setText("0");
                mounting_tv.setText("2");
            }
            intermediate_tv.setText("" + getV_Intermediates(wire_tv.getText().toString(), ec_project_model.getIntermediate_spacing()));

            if (ec_project_model.isMeter()) {
                wire_heading_tv.setText("Wire (Mtr)");
            } else {
                wire_heading_tv.setText("Wire (Ft)");
            }
            ec_project_model.setAbsorber(obserber_tv.getText().toString());
            ec_project_model.setLife_lineLength(wire_tv.getText().toString());
            ec_project_model.setMounting_brackets(mounting_tv.getText().toString());
            ec_project_model.setTop_connection(tp_connection_tv.getText().toString());
            ec_project_model.setExtension(extension_tv.getText().toString());
            ec_project_model.setIntermediate_post(intermediate_tv.getText().toString());
        } else {
            wire_lay.setVisibility(View.GONE);
            alurail_lay.setVisibility(View.VISIBLE);
            int extremity;
            int extension;
            if (ec_project_model.getExtension_arm().equalsIgnoreCase("Yes")) {
                extremity = 1;
                extension = 1;
            } else {
                extremity = 2;
                extension = 0;
            }

            if (ec_project_model.getIsFolding_aluminum() != null) {
                if (ec_project_model.getIsFolding_aluminum().equalsIgnoreCase("yes")) {
                    extremity = extremity - 1;
                    ec_project_model.setFolding_aluminum("1");
                } else {
                    ec_project_model.setFolding_aluminum("0");
                }
            }

            int intermediate = (int) Math.round((Double.parseDouble(ec_project_model.getLife_lineLength()) / 3) - extremity - extension + 0.49);
            int junction = extremity + extension + intermediate - 1;
            int rung_clamp = (extremity + extension + intermediate) * 2;

            extremity_tv.setText("" + extremity);
            alu_extension_tv.setText("" + extension);
            alu_intr_tv.setText("" + intermediate);
            junction_tv.setText("" + junction);

            ec_project_model.setExtremity_post("" + extremity);
            ec_project_model.setExtension("" + extension);
            ec_project_model.setIntermediate_post("" + intermediate);
            ec_project_model.setJunction("" + junction);

            if (ec_project_model.getSystem_type().equals("PN9000")) {
                ((ViewGroup) mounting_post_tv.getParent()).setVisibility(View.VISIBLE);
                ((ViewGroup) rung_tv.getParent()).setVisibility(View.GONE);
                ec_project_model.setMounting_nut("" + rung_clamp);
                ec_project_model.setMounting_brackets("" + rung_clamp);
                nut_tv.setText("" + rung_clamp);
                bracket_tv.setText("" + rung_clamp);
                folding_aluminum_tv.setText(ec_project_model.getFolding_aluminum());
                int rungs_qty = (extremity + intermediate) * 11;
                if (ec_project_model.getIsHalf_rung().equalsIgnoreCase("yes")) {
                    double rungs = rungs_qty;
                    int half_rungs = (int) Math.round((rungs / 2) + 0.49);
                    ec_project_model.setLeft_rungs("" + half_rungs);
                    ec_project_model.setRight_rungs("" + half_rungs);
                    ec_project_model.setFull_rungs("1");
                    left_tv.setText("" + half_rungs);
                    right_tv.setText("" + half_rungs);
                    full_rung_tv.setText("1");
                } else {
                    ec_project_model.setFull_rungs("" + rungs_qty);
                    full_rung_tv.setText("" + rungs_qty);
                }
                if (ec_project_model.getIsPost().equalsIgnoreCase("yes")) {
                    mounting_post_tv.setText("" + rung_clamp);
                    ec_project_model.setMounting_post("" + rung_clamp);
                } else {
                    mounting_post_tv.setText("0");
                    ec_project_model.setMounting_post("0");
                }
                if (ec_project_model.getIsChemical_fastener().equalsIgnoreCase("yes")) {
                    fasteners_tv.setText("" + rung_clamp);
                    ec_project_model.setChemical_fasteners("" + rung_clamp);
                } else {
                    ec_project_model.setChemical_fasteners("0");
                    fasteners_tv.setText("0");
                }
                trolley_tv.setText("1");
            } else {
                ((ViewGroup) mounting_post_tv.getParent()).setVisibility(View.GONE);
                ((ViewGroup) rung_tv.getParent()).setVisibility(View.VISIBLE);
                rung_tv.setText("" + rung_clamp);
                ec_project_model.setRung_clamp("" + rung_clamp);
            }

//            ec_project_model.setTrolley("1");

        }
    }

}

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.EC_project_Model;
import app.com.arresto.arresto_connect.database.ec_tables.Category_Table;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.horizontal;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_alurail;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_wire;

public class EC_Preview_Fragment extends EC_Base_Fragment {
    View view;
    private TextView dimension_tv, obserber_tv, termination_tv, wire_tv, intermediate_tv, wire_heading_tv, tensioner_tv, v_extremity_tv, intrmedt_post_tv,
            corner_tv, harness_tv, element_tv, body_tv, edit_btn;
    private EC_project_Model ec_project_model;
    private LinearLayout segment_root, user_container;
    private ImageView sgmnt_img;
    private TextView right_tv, top_tv, btm_tv, lft_tv,ppe_tv, service_tv, srvc_note_tv;
    String type, line_type, boq_id, lastSubmitted_data;

    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ec_preview, parent, false);
            if (getArguments() != null) {
                type = getArguments().getString("type", "");
                boq_id = getArguments().getString("boq_id", "");
                lastSubmitted_data = getArguments().getString("data", "");
            }
            findAllIds(view);
        }
        return view;
    }

    private TableLayout stk, ppe_table, service_table;

    private TextView vdimension_tv, v_wire_tv, v_intermediate_tv, botm_cbl_tv, top_cbl_tv, mounting_tv, rope_grab_tv, extension_tv, alu_intr_tv, alu_extension_tv, junction_tv, nut_tv, bracket_tv, trolley_tv;
    private LinearLayout wire_lay, alurail_lay;

    public void findAllIds(View view) {
//        horizontal line ids
        dimension_tv = view.findViewById(R.id.dimension_tv);
        segment_root = view.findViewById(R.id.segment_root);
        obserber_tv = view.findViewById(R.id.obserber_tv);
        termination_tv = view.findViewById(R.id.termination_tv);
        wire_heading_tv = view.findViewById(R.id.wire_heading_tv);
        wire_tv = view.findViewById(R.id.wire_tv);
        intermediate_tv = view.findViewById(R.id.intermediate_tv);
        tensioner_tv = view.findViewById(R.id.tensioner_tv);
        v_extremity_tv = view.findViewById(R.id.v_extremity_tv);
        intrmedt_post_tv = view.findViewById(R.id.intrmedt_post_tv);
        corner_tv = view.findViewById(R.id.corner_tv);
        harness_tv = view.findViewById(R.id.harness_tv);
        element_tv = view.findViewById(R.id.element_tv);
        body_tv = view.findViewById(R.id.body_tv);
        sgmnt_img = view.findViewById(R.id.sgmnt_img);
        user_container = view.findViewById(R.id.user_container);
        edit_btn = view.findViewById(R.id.edit_btn);
        top_tv = view.findViewById(R.id.top_tv);
        right_tv = view.findViewById(R.id.right_tv);
        btm_tv = view.findViewById(R.id.btm_tv);
        lft_tv = view.findViewById(R.id.lft_tv);
        stk = view.findViewById(R.id.table_main);
        ppe_table = view.findViewById(R.id.ppe_table);
        ppe_tv = view.findViewById(R.id.ppe_tv);
        service_table = view.findViewById(R.id.service_table);
        service_tv = view.findViewById(R.id.service_tv);
        srvc_note_tv = view.findViewById(R.id.srvc_note_tv);

// vertical line ids
        vdimension_tv = view.findViewById(R.id.vdimension_tv);
        v_wire_tv = view.findViewById(R.id.v_wire_tv);
        v_intermediate_tv = view.findViewById(R.id.v_intermediate_tv);
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

        set_data();
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("db")) {
                    if (!AppController.getInstance().getDatabase().getProject_Boq_Dao().isBOQExist(user_id, project_id, site_id, boq_id))
                        save_onlineData();
                    else
                        AppUtils.show_snak(getActivity(), "Line is already in list please go back and refresh sub sites list.");
                } else {
//                    AppUtils.show_snak(getActivity(), "Functionality under process...");
                    Bundle bundle = new Bundle();
                    bundle.putString("boq_id", boq_id);
                    bundle.putString("line_type", line_type);
                    EC_Project_details ec_project = new EC_Project_details();
                    ec_project.setArguments(bundle);
                    LoadFragment.replace(ec_project, getActivity(), getResString("lbl_life_line_details"));
                }
            }
        });
    }

    JSONObject json_data;

    private void set_data() {
        if (type.equalsIgnoreCase("db")) {
            Project_Boq_table project_boq_table = AppController.getInstance().getDatabase().getProject_Boq_Dao().getSingle_boq(user_id, project_id, site_id, boq_id);
            if (project_boq_table != null && project_boq_table.getData() != null) {
                try {
                    json_data = new JSONObject(project_boq_table.getData());
                    ec_project_model = new Gson().fromJson("" + json_data, EC_project_Model.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (lastSubmitted_data != null && !lastSubmitted_data.equals("")) {
            edit_btn.setText("Revise");
            try {
                json_data = new JSONObject(lastSubmitted_data);
                ec_project_model = new Gson().fromJson("" + json_data, EC_project_Model.class);
                boq_id = json_data.getString("boq_id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (json_data != null) {
            init(getActivity(), json_data);
        }


        if (ec_project_model != null) {
            if (ec_project_model.isMeter()) {
                length_type = " Mtr.";
                load_type = " kN.";
                to_feet = 1;
                to_pound = 1;
                dimension_tv.setText("Metric");
                wire_heading_tv.setText("Wire (Mtr)");
            } else {
                length_type = " Ft.";
                load_type = " Pounds";
                to_feet = 3.280839895;
                to_pound = 225;
                temprature = (temprature - 32) * 5 / 9;
                dimension_tv.setText("Imperial");
                wire_heading_tv.setText("Wire (Ft)");
            }

            if (ec_project_model.getType().equals("Horizontal Life Line Systems")) {
                ((LinearLayout) dimension_tv.getParent()).setVisibility(View.VISIBLE);
                line_type = horizontal;
                set_data_horizontal();
            } else {
//                design for vertical
                ((LinearLayout) vdimension_tv.getParent()).setVisibility(View.VISIBLE);
                set_data_vertical();
            }

        }
    }

    private void set_data_horizontal() {
        obserber_tv.setText(ec_project_model.getAbsorber());
        wire_tv.setText(ec_project_model.getTotal_sgment());
        segment_root.removeAllViews();
        if (ec_project_model.getSegment_1() != null) {
            top_tv.setVisibility(View.VISIBLE);
            top_tv.setText(ec_project_model.getSegment_1().getLength() + length_type);
            add_segment_view(ec_project_model.getSegment_1(), segment_root, "Segment 1");
        }
        if (ec_project_model.getSegment_2() != null) {
            right_tv.setVisibility(View.VISIBLE);
            right_tv.setText(ec_project_model.getSegment_2().getLength() + length_type);
            add_segment_view(ec_project_model.getSegment_2(), segment_root, "Segment 2");
        }
        if (ec_project_model.getSegment_3() != null) {
            btm_tv.setVisibility(View.VISIBLE);
            btm_tv.setText(ec_project_model.getSegment_3().getLength() + length_type);
            add_segment_view(ec_project_model.getSegment_3(), segment_root, "Segment 3");
        }
        if (ec_project_model.getSegment_4() != null) {
            lft_tv.setVisibility(View.VISIBLE);
            lft_tv.setText(ec_project_model.getSegment_4().getLength() + length_type);
            add_segment_view(ec_project_model.getSegment_4(), segment_root, "Segment 3");
        }
        intermediate_tv.setText("" + ec_project_model.getIntermediate_post());
        intrmedt_post_tv.setText("" + ec_project_model.getIntermediate_post());
        corner_tv.setText(ec_project_model.getCorner());
        harness_tv.setText(ec_project_model.getUsers());
        element_tv.setText(ec_project_model.getUsers());
        body_tv.setText(ec_project_model.getUsers());
        sgmnt_img.setImageResource(getResources().getIdentifier("segment_" + ec_project_model.getCorner(), "drawable", getActivity().getPackageName()));
        design_user(Integer.parseInt(ec_project_model.getUsers()));
    }

    private void set_data_vertical() {
        if (ec_project_model.getType().equals("Wire Rope")) {
            line_type = vertical_wire;
            wire_lay.setVisibility(View.VISIBLE);
            alurail_lay.setVisibility(View.GONE);
            if (ec_project_model.getExtension_arm().equals("Yes")) {
                v_wire_tv.setText("" + (Double.parseDouble(ec_project_model.getLife_lineLength()) + 1.5));
                extension_tv.setText("1");
            } else {
                v_wire_tv.setText(ec_project_model.getLife_lineLength());
                extension_tv.setText("0");
            }
            mounting_tv.setText(ec_project_model.getMounting_brackets());
            v_intermediate_tv.setText(ec_project_model.getIntermediate_post());
//            intermediate_tv.setText("" + getV_Intermediates(wire_tv.getText().toString(), ec_project_model.getIntermediate_spacing()));

            if (ec_project_model.isMeter()) {
                wire_heading_tv.setText("Wire (Mtr)");
            } else {
                wire_heading_tv.setText("Wire (Ft)");
            }
            ec_project_model.setAbsorber("1");
            ec_project_model.setMounting_brackets("2");
            ec_project_model.setLife_lineLength(wire_tv.getText().toString());
            ec_project_model.setExtension(extension_tv.getText().toString());
            ec_project_model.setIntermediate_post(intermediate_tv.getText().toString());
        } else {
            line_type = vertical_alurail;
            wire_lay.setVisibility(View.GONE);
            alurail_lay.setVisibility(View.VISIBLE);
//            if (ec_project_model.getExtension_arm().equalsIgnoreCase("Yes")) {
//                v_extremity_tv.setText("1");
//                alu_extension_tv.setText("1");
//            } else {
//                v_extremity_tv.setText("2");
//                alu_extension_tv.setText("0");
//            }
            v_extremity_tv.setText(ec_project_model.getExtremity_post());
            alu_extension_tv.setText(ec_project_model.getExtension());
            mounting_tv.setText(ec_project_model.getMounting_brackets());
            junction_tv.setText("" + ec_project_model.getJunction());
            alu_intr_tv.setText("" + ec_project_model.getIntermediate_post());
//            int intemediat = Integer.parseInt(ec_project_model.getLife_lineLength()) / 3;
//            alu_intr_tv.setText("" + intemediat);
//            junction_tv.setText("" + (add_two_String(ec_project_model.getExtremity_post(), ec_project_model.getIntermediate_post()) + intemediat - 1));

            ec_project_model.setExtremity_post(v_extremity_tv.getText().toString());
            ec_project_model.setIntermediate_post(alu_intr_tv.getText().toString());
            ec_project_model.setExtension(alu_extension_tv.getText().toString());
            ec_project_model.setJunction(junction_tv.getText().toString());
            ec_project_model.setMounting_nut(alu_intr_tv.getText().toString());
            ec_project_model.setMounting_brackets(alu_intr_tv.getText().toString());
        }
    }

    private void design_user(int user) {
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

    ArrayList<String> headings = new ArrayList<>(Arrays.asList(" Sl.No ", " Product ", " Image ", " Category ", " System Qty. ", " No. of lines ",
            " Total Qty. ", " Price ", " Discount ", " Total "));

    public void init(Activity activity, JSONObject json_data) {
        TableRow tbrow0 = new TableRow(activity);
        for (String h : headings) {
            TextView tv0 = new TextView(activity);
            tv0.setText(h);
            tv0.setGravity(Gravity.CENTER);
            tv0.setTextColor(Color.WHITE);
            tv0.setBackgroundColor(getResColor(R.color.app_text));
            tv0.setPadding(10, 10, 10, 10);
            tbrow0.addView(tv0);
        }

        stk.addView(tbrow0);

        tbrow0 = new TableRow(activity);
        TextView product_tv = new TextView(activity);
        product_tv.setText(getResString("lbl_product_details"));
        product_tv.setGravity(Gravity.CENTER_VERTICAL);
        product_tv.setTextColor(getResColor(R.color.app_text));
        product_tv.setTextSize(18);
        tbrow0.addView(new TextView(activity));
        tbrow0.addView(product_tv);
        stk.addView(tbrow0);
//        service_table.addView(tbrow1);

        try {
            JSONArray products = json_data.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                TableRow tbrow = new TableRow(activity);

                TextView t1 = newTextview("" + (i + 1));
                t1.setLayoutParams(layoutParams0);
                tbrow.addView(t1);

                TextView t2 = newTextview(product.getString("product_name"));
                tbrow.addView(t2);

                ImageView image = new ImageView(getActivity());
                image.setBackgroundResource(R.drawable.table_divider1);
                image.setPadding(10, 10, 10, 10);
                image.setImageResource(R.drawable.logo);
                image.setLayoutParams(layoutParams1);
                AppUtils.load_image(product.getString("image_url"), image);
                tbrow.addView(image);

                TextView t4 = newTextview(product.getString("category"));
                tbrow.addView(t4);


                TextView t5 = newTextview(product.getString("quantity"));
                tbrow.addView(t5);

                TextView t6 = newTextview(ec_project_model.getNumber_of_line());
                tbrow.addView(t6);

                TextView t7;
                if (product.has("total_quantity")) {
                    t7 = newTextview(product.getString("total_quantity"));
                    tbrow.addView(t7);
                } else {
                    t7 = newTextview(product.getString("edited_quantity"));
                    tbrow.addView(t7);
                }

                TextView t8 = newTextview(product.getString("price"));
                tbrow.addView(t8);

                TextView t9 = newTextview(product.getString("discount"));
                tbrow.addView(t9);

                TextView t10 = newTextview(product.getString("total"));
                tbrow.addView(t10);
                String cat = product.getString("category");

                if ((cat.equalsIgnoreCase("harness")
                        || cat.equalsIgnoreCase("connecting element") || cat.equalsIgnoreCase("mobile anchor")) && !json_data.getString("isPPE").equalsIgnoreCase("no")) {
                    t1.setText("" + (ppe_table.getChildCount() + 1));
                    ppe_table.addView(tbrow);
                    ppe_table.setVisibility(View.VISIBLE);
                    ppe_tv.setVisibility(View.VISIBLE);
                } else if (product.getString("group").equalsIgnoreCase("service") && !json_data.getString("isService").equalsIgnoreCase("no")) {
                    t1.setText("" + (service_table.getChildCount() + 1));
                    service_table.addView(tbrow);
                    service_table.setVisibility(View.VISIBLE);
                    service_tv.setVisibility(View.VISIBLE);
                    srvc_note_tv.setVisibility(View.VISIBLE);
                } else {
                    t1.setText(" " + (stk.getChildCount() - 1));
                    stk.addView(tbrow);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    TableRow.LayoutParams layoutParams0 = new TableRow.LayoutParams(140, 200);
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
    TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(250, 200);

    private TextView newTextview(String text) {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setMinWidth(300);
        textView.setTextColor(getResColor(R.color.app_text));
        textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textView.setSingleLine(false);
        textView.setBackgroundResource(R.drawable.table_divider1);
        textView.setPadding(10, 5, 10, 5);
        return textView;
    }

    private void save_onlineData() {
        if (json_data != null && ec_project_model != null) {
            insertBoq();
            insertProducts();
            getActivity().onBackPressed();
        }
    }

    private void insertProducts() {
        JSONArray products;
        try {
            products = json_data.getJSONArray("products");
            for (int j = 0; j < products.length(); j++) {
                JSONObject product = products.getJSONObject(j);
                EC_productsTable ec_productsTable = new EC_productsTable();
                ec_productsTable.setClient_id(Static_values.client_id);
                ec_productsTable.setUser_id(Static_values.user_id);
                ec_productsTable.setName(getJSONString(product, "product_name"));
                ec_productsTable.setUrl(getJSONString(product, "image_url"));
                ec_productsTable.setType("asset");
                ec_productsTable.setDescription("");
                ec_productsTable.setHsn_code(getJSONString(product, "hsn_code"));
                ec_productsTable.setTax(getJSONString(product, "tax"));
                ec_productsTable.setGroup(getJSONString(product, "group"));
                ec_productsTable.setPrice(getJSONString(product, "price"));
                ec_productsTable.setCat_id(getJSONString(product, "cat_id"));
                ec_productsTable.setCat_name(getJSONString(product, "category"));
                ec_productsTable.setBOQ_id(getJSONString(product, "boq_id"));
                AppController.getInstance().getDatabase().getEC_products_Dao().insert(ec_productsTable);
                insertCategories(getJSONString(product, "cat_id"), getJSONString(product, "boq_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertCategories(String cat_id, String boq_id) {
        Category_Model category_model = (Category_Model) treeNodes.get(cat_id).getData();
        if (category_model != null) {
            category_model.setMissing(0);
            category_model.setIs_completed(true);
            Category_Table category_table = new Category_Table();
            category_table.setUser_id(user_id);
            category_table.setClient_id(client_id);
            category_table.setCat_id(category_model.getId());
            category_table.setParent_cat_id(category_model.getCat_parentid());
            category_table.setBOQ_id(boq_id);
            AppController.getInstance().getDatabase().getCategory_Table_Dao().insert(category_table);
            if (!cat_id.equals(horizontal) && !cat_id.equals(vertical))
                insertCategories(category_model.getCat_parentid(), boq_id);
        }
    }


    private void insertBoq() {
        Project_Boq_table project_boq_table = new Project_Boq_table();
        project_boq_table.setUser_id(getJSONString(json_data, "user_id"));
        project_boq_table.setProject_id(getJSONString(json_data, "project_id"));
        if (getJSONString(json_data, "type").contains("Horizontal"))
            project_boq_table.setCat_id(horizontal);
        else
            project_boq_table.setCat_id(vertical);

        project_boq_table.setBoq_id(getJSONString(json_data, "boq_id"));
        project_boq_table.setRevision(1);
        project_boq_table.setRemark(getJSONString(json_data, "remark"));
        project_boq_table.setSite_id(getJSONString(json_data, "site_id"));
        project_boq_table.setSubsite_image(getJSONString(json_data, "subsite_image"));
        project_boq_table.setGeolocation(getJSONString(json_data, "geolocation"));
        project_boq_table.setData(json_data.toString());
        AppController.getInstance().getDatabase().getProject_Boq_Dao().insert(project_boq_table);
    }


    public String getJSONString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}

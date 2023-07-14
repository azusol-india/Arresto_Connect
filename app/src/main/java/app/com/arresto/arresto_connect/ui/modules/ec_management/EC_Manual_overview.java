/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.CurrencyModel;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_project_Model;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.add_two_String;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment.round;

public class EC_Manual_overview extends Fragment {
    private View view;
    private EC_project_Model ec_project_model;
    private TextView finish_btn;
    private HorizontalScrollView horizontalView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ec_manual_overview, container, false);
            fetch_currency(All_Api.getcurrency + client_id);
            if (getArguments() != null) {
                ec_project_model = AppUtils.getGson().fromJson(getArguments().getString("data"), EC_project_Model.class);
                if (ec_project_model.getType().equals("Horizontal Life Line Systems")) {
                    if (!ec_project_model.getNumber_of_line().isEmpty())
                        number_of_line = Integer.parseInt(ec_project_model.getNumber_of_line());
                }
            }
//            get_DatabaseData();
            findViewId(view);
            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject post_json = make_Json();
                        Project_Boq_table project_boq_table = AppController.getInstance().getDatabase().getProject_Boq_Dao().getSingle_boq(user_id,
                                DataHolder_Model.getInstance().getSlctd_ec_project().getEcp_id(), DataHolder_Model.getInstance().getSlctd_site().getId(), boq_id);
                        post_json.put("products", products_data);
                        project_boq_table.setData(post_json.toString());
                        AppController.getInstance().getDatabase().getProject_Boq_Dao().updateBOQ(project_boq_table);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack(getResString("lbl_sub_sites"), 0);
//                        LoadFragment.replace(new EC_LinePager(), (AppCompatActivity) getActivity(), getResString("lbl_engineering_calculations"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
//            ec_project_model.setIsService("yes");

            ppe_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int i) {
                    isPPE = "" + (group.findViewById(i)).getTag();
                    ec_project_model.setIsPPE(isPPE);
                    if (isPPE.equalsIgnoreCase("yes")) {
                        ppe_table.setVisibility(View.VISIBLE);
                    } else {
                        ppe_table.setVisibility(View.GONE);
                    }
                }
            });
            ppe_rg.check(ppe_rg.getChildAt(0).getId());

            service_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int i) {
                    isService = "" + (group.findViewById(i)).getTag();
                    ec_project_model.setIsService(isService);
                    if (isService.equalsIgnoreCase("yes")) {
                        service_table.setVisibility(View.VISIBLE);
                    } else {
                        service_table.setVisibility(View.GONE);
                    }
                }
            });
            service_rg.check(service_rg.getChildAt(0).getId());
        }
        return view;
    }

    private TableLayout table_1, ppe_table, service_table;
    private Spinner currency_spinr;
    RelativeLayout service_layer, ppe_layer;
    RadioGroup service_rg, ppe_rg;
    String isService = "yes", isPPE = "yes";

    private void findViewId(View view) {
        finish_btn = view.findViewById(R.id.finish_btn);
        TextView discount_tv = view.findViewById(R.id.discount_tv);
        currency_spinr = view.findViewById(R.id.currency_spinr);
        table_1 = view.findViewById(R.id.table_1);
        ppe_table = view.findViewById(R.id.ppe_table);
        ppe_rg = view.findViewById(R.id.ppe_rg);
        ppe_layer = view.findViewById(R.id.ppe_layer);
        service_table = view.findViewById(R.id.service_table);
        service_rg = view.findViewById(R.id.service_rg);
        service_layer = view.findViewById(R.id.service_layer);
        horizontalView = view.findViewById(R.id.horizontalView);

        currency_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurrencyModel.Currencies currencies = (CurrencyModel.Currencies) parent.getItemAtPosition(position);
                ec_project_model.setCurrency(currencies.getId());
                if (currencies.getValue() != null && !currencies.getValue().equals("")) {
                    currency_rate = Double.parseDouble(currencies.getValue());
                } else {
                    currency_rate = 1;
                }
                make_inTable(current_discount, currency_rate);
                horizontalView.scrollTo(0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        discount_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_discount();
            }
        });

        make_inTable(current_discount, currency_rate);
    }

    private int current_discount = 0;
    private double currency_rate = 1;

    private void add_discount() {
        final Dialog dialoglayout = new Dialog(getActivity());
        dialoglayout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglayout.setCancelable(true);
        dialoglayout.setContentView(R.layout.discount_dialog);
        TextView cancel_btn = dialoglayout.findViewById(R.id.cncl_btn);
        TextView ok_btn = dialoglayout.findViewById(R.id.ok_btn);
        final EditText per_edt = dialoglayout.findViewById(R.id.edt_dialog);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (per_edt.getText().toString().isEmpty()) {
                    show_snak(getActivity(), "Please enter discount amount");
                    return;
                }
                current_discount = Integer.parseInt(per_edt.getText().toString());
                make_inTable(current_discount, currency_rate);
                dialoglayout.dismiss();
            }

        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglayout.dismiss();
            }
        });
        dialoglayout.show();
    }


    private JSONObject make_Json() throws JSONException {
        JSONObject post_json = new JSONObject(new Gson().toJson(ec_project_model));
        post_json.put("user_id", user_id);
        post_json.put("client_id", client_id);
        post_json.put("project_id", DataHolder_Model.getInstance().getSlctd_ec_project().getEcp_id());
        Log.e("params to pot ", "  is " + post_json);
        return post_json;
    }

    private JSONArray products_data;

    private void make_inTable(int discount, double currency_rate) {
        table_1.removeAllViews();
        ppe_table.removeAllViews();
        service_table.removeAllViews();

        TableRow tbrow0 = new TableRow(getActivity());
        for (String h : headings) {
            TextView tv0 = new TextView(getActivity());
            tv0.setText(h);
            tv0.setGravity(Gravity.CENTER);
            tv0.setTextColor(Color.WHITE);
            tv0.setBackgroundColor(getResColor(R.color.app_text));
            tv0.setPadding(10, 10, 10, 10);
            tbrow0.addView(tv0);
        }

        table_1.addView(tbrow0);

        tbrow0 = new TableRow(getActivity());
        TextView product_tv = new TextView(getActivity());
        product_tv.setText(getResString("lbl_product_details"));
        product_tv.setGravity(Gravity.CENTER_VERTICAL);
        product_tv.setTextColor(getResColor(R.color.app_text));
        product_tv.setTextSize(18);
        tbrow0.addView(new TextView(getActivity()));
        tbrow0.addView(product_tv);
        table_1.addView(tbrow0);


        List<EC_productsTable> ec_productsTables = DataHolder_Model.getInstance().getBoq_products();
        products_data = new JSONArray();
        if (ec_productsTables != null) {
            for (int j = 0; j < ec_productsTables.size(); j++) {
                EC_productsTable ec_productsTable = ec_productsTables.get(j);
                JSONObject product_item = new JSONObject();
                products_data.put(product_item);
                make_single_row(j, ec_productsTable, discount, product_item);
            }
        }
    }

    private String boq_id = "";
    private TableRow.LayoutParams layoutParams0 = new TableRow.LayoutParams(140, 200);
    private TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
    private TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(250, 200);
    private int number_of_line = 1;
    private ArrayList<String> headings = new ArrayList<>(Arrays.asList(" Sl.No ", " Product ", " Image ", " Category ", " System Qty. ", " No. of lines ",
            " Total Qty.(Editable)", " Price ", " Discount ", " Total "));

    private void make_single_row(int pos, final EC_productsTable ec_productsTable, final int discount, final JSONObject post_obj) {
        try {
            double unit_price = 0;
            if (ec_productsTable.getPrice() != null && !ec_productsTable.getPrice().equals("")) {
                if (currency_rate > 0)
                    unit_price = round(Integer.parseInt(ec_productsTable.getPrice()) * currency_rate, 3);
                else
                    unit_price = Integer.parseInt(ec_productsTable.getPrice());
//                ec_productsTable.setPrice("" + unit_price);
            }

            TableRow row = new TableRow(getActivity());
            post_obj.put("product_name", ec_productsTable.getName());
            post_obj.put("category", ec_productsTable.getCat_name());
            post_obj.put("cat_id", ec_productsTable.getCat_id());
            post_obj.put("image_url", ec_productsTable.getUrl());
            post_obj.put("price", "" + unit_price);
            post_obj.put("tax", ec_productsTable.getTax());
            post_obj.put("group", ec_productsTable.getGroup());
            post_obj.put("hsn_code", ec_productsTable.getHsn_code());
            post_obj.put("boq_id", ec_productsTable.getBOQ_id());
            post_obj.put("isEdited", false);
            boq_id = ec_productsTable.getBOQ_id();

            TextView serial_tv = new_Textview("" + (pos + 1));
            serial_tv.setLayoutParams(layoutParams0);

            TextView name_tv = new_Textview("" + ec_productsTable.getName());
            TextView cat_tv = new_Textview("" + ec_productsTable.getCat_name());
            TextView price_tv = new_Textview("" + unit_price);

            final TextView total_tv = new_Textview("0");

            ImageView image = new ImageView(getActivity());
            image.setBackgroundResource(R.drawable.table_divider1);
            image.setPadding(10, 10, 10, 10);
            image.setImageResource(R.drawable.logo);
            image.setLayoutParams(layoutParams1);
            AppUtils.load_image(ec_productsTable.getUrl(), image);

            final TextView qty_tv = new_Textview(set_quantity(ec_productsTable.getCat_name()));

            final EditText qty_edt = new EditText(getActivity());
            qty_edt.setBackgroundResource(R.drawable.table_divider1);
            qty_edt.setLayoutParams(layoutParams);
            qty_edt.setMinWidth(300);
            qty_edt.setTextSize(15);
            String qty_str = qty_tv.getText().toString().replace(" ", "");
            if (qty_str != "") {
                int qty = (int) Double.parseDouble(qty_tv.getText().toString());
                if (number_of_line != 0 && !ec_productsTable.getCat_name().equalsIgnoreCase("harness")
                        && !ec_productsTable.getCat_name().equalsIgnoreCase("connecting element")
                        && !ec_productsTable.getCat_name().equalsIgnoreCase("mobile anchor"))
                    qty = number_of_line * qty;
                qty_edt.setText(""+qty);
                post_obj.put("total_quantity", ""+qty);
            }else {
                post_obj.put("total_quantity", "");
            }
            qty_edt.setGravity(Gravity.CENTER);
            qty_edt.setInputType(InputType.TYPE_CLASS_NUMBER);

            final EditText disc_tv = new EditText(getActivity());
            disc_tv.setBackgroundResource(R.drawable.table_divider1);
            disc_tv.setLayoutParams(layoutParams);
            disc_tv.setMinWidth(300);
            disc_tv.setTextSize(14);
            disc_tv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            disc_tv.setInputType(InputType.TYPE_CLASS_NUMBER);
            disc_tv.setText(discount + "%");
            disc_tv.setGravity(Gravity.CENTER);

            post_obj.put("quantity", qty_tv.getText().toString());
            setTotal(total_tv, post_obj, "" + unit_price, discount, ec_productsTable.getCat_name());

            double final_price1 = unit_price;
            qty_edt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("")) {
                        String qty_str = s.toString().replace(" ", "");
                        int qty = Integer.parseInt(qty_str);
//                        if (number_of_line != 0 && !ec_productsTable.getCat_name().equalsIgnoreCase("harness")
//                                && !ec_productsTable.getCat_name().equalsIgnoreCase("connecting element") && !ec_productsTable.getCat_name().equalsIgnoreCase("mobile anchor"))
//                            qty = qty * number_of_line;

                        int discount = 0;
                        if (!disc_tv.getText().toString().equals("") && !disc_tv.getText().toString().equalsIgnoreCase("manual"))
                            discount = Integer.parseInt(disc_tv.getText().toString().replace("%", ""));

                        double base_price = qty * final_price1;
                        total_tv.setText(String.format("%.2f", base_price - taxPercent(base_price, discount)));
                        try {
                            post_obj.put("isEdited", true);
                            post_obj.put("total_quantity", qty_str);
                            post_obj.put("discount", discount);
                            post_obj.put("total", total_tv.getText().toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            post_obj.put("isEdited", true);
                            post_obj.put("total_quantity", "");
                            setTotal(total_tv, post_obj, "" + final_price1, discount, ec_productsTable.getCat_name());
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            disc_tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("") && !s.toString().equals("%")) {
                        String disc_str = s.toString().replace("%", "");
                        double qty = 0;

                        if (!qty_edt.getText().toString().equals("") && !qty_edt.getText().toString().equalsIgnoreCase("manual"))
                            qty = Double.parseDouble(qty_edt.getText().toString());
                        else if (!qty_tv.getText().toString().equals("") && !qty_tv.getText().toString().equalsIgnoreCase("manual")) {
                            qty = Double.parseDouble(qty_tv.getText().toString());
                            if (number_of_line != 0 && !ec_productsTable.getCat_name().equalsIgnoreCase("harness")
                                    && !ec_productsTable.getCat_name().equalsIgnoreCase("connecting element") && !ec_productsTable.getCat_name().equalsIgnoreCase("mobile anchor"))
                                qty = number_of_line * qty;
                        }
                        int discount = Integer.parseInt(disc_str);
                        double base_price = qty * final_price1;
                        total_tv.setText(String.format("%.2f", base_price - taxPercent(base_price, discount)));
                        try {
                            post_obj.put("discount", s.toString().replace("%", ""));
                            post_obj.put("total", total_tv.getText().toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        total_tv.setText("0");
                    }
                }
            });

            row.addView(serial_tv);
            row.addView(image);
            row.addView(name_tv);
            row.addView(cat_tv);
            row.addView(qty_tv);
            if (ec_project_model.getType().equals("Horizontal Life Line Systems")) {
                TextView line_tv = new_Textview(ec_project_model.getNumber_of_line());
                row.addView(line_tv);
            }
            row.addView(qty_edt);
            row.addView(price_tv);
            row.addView(disc_tv);
            row.addView(total_tv);

            if (ec_productsTable.getCat_name().equalsIgnoreCase("harness") || ec_productsTable.getCat_name().equalsIgnoreCase("connecting element")
                    || ec_productsTable.getCat_name().equalsIgnoreCase("mobile anchor")) {
                serial_tv.setText("" + (ppe_table.getChildCount() + 1));
                ppe_table.addView(row);
                if (isPPE.equals("yes"))
                    ppe_table.setVisibility(View.VISIBLE);
                ppe_layer.setVisibility(View.VISIBLE);
            } else if (ec_productsTable.getGroup().equalsIgnoreCase("service")) {
                serial_tv.setText("" + (service_table.getChildCount() + 1));
                service_table.addView(row);
                if (isService.equals("yes"))
                    service_table.setVisibility(View.VISIBLE);
                service_layer.setVisibility(View.VISIBLE);
            } else {
                serial_tv.setText("" + (table_1.getChildCount() - 1));
                table_1.addView(row);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public void setTotal(TextView total_tv, JSONObject post_obj, String price, int discount, String cat_name) {
        try {
            double qty = 0;
            double unit_price = 0;
            if (!post_obj.getString("quantity").equals("") && !post_obj.getString("quantity").equalsIgnoreCase("manual"))
                qty = Double.parseDouble(post_obj.getString("quantity"));
            if (!price.equalsIgnoreCase(""))
                unit_price = Double.parseDouble(price);

            double base_price = qty * unit_price;
            if (number_of_line != 0 && !cat_name.equalsIgnoreCase("harness") && !cat_name.equalsIgnoreCase("connecting element") && !cat_name.equalsIgnoreCase("mobile anchor"))
                base_price = base_price * number_of_line;
            total_tv.setText(String.format("%.2f", base_price - taxPercent(base_price, discount)));

            post_obj.put("discount", discount);
            post_obj.put("total", total_tv.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextView new_Textview(String text) {
        final TextView tv = new TextView(getActivity());
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.table_divider1);
        tv.setLayoutParams(layoutParams);
        tv.setText(text);
        tv.setMinWidth(300);
        tv.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        tv.setSingleLine(false);
        tv.setPadding(30, 10, 30, 10);
        return tv;
    }


    private String set_quantity(String cat_name) {
        switch (cat_name.toLowerCase().trim()) {
            case "wire rope":
                return ec_project_model.getTotal_sgment();
            case "shock absorber":
                return ec_project_model.getAbsorber();
            case "tensioner":
                return "1";
            case "cable termination":
                return "1";
            case "intermediate":
                return ec_project_model.getIntermediate_post();
            case "harness":
//                return ec_project_model.getUsers();
                return "1";
            case "connecting element":
//                return ec_project_model.getUsers();
                return "1";
            case "mobile anchor":
//                return ec_project_model.getUsers();
                return "1";
            case "no of lines":
                return "";
            case "engineering cost":
                return "1";
//                return "0";
//                return "manual";
            case "mobilization cost":
                return "1";
            case "cost of access":
//                return "0";
                return "1";
//                return "manual";
            case "cost of installation":
                return "" + add_two_String(ec_project_model.getExtremity_post(), ec_project_model.getIntermediate_post());
            //Post and Corners
            case "extremity post":
                return ec_project_model.getExtremity_post();
            case "intermediate post":
                return ec_project_model.getIntermediate_post();
            case "corner post":
                return ec_project_model.getCorner();
            case "extension arm":
                return ec_project_model.getExtension();
            case "mounting brackets":
                return ec_project_model.getMounting_brackets();
            case "wire":
                return ec_project_model.getLife_lineLength();
            case "junction":
                return ec_project_model.getJunction();
            case "mounting nut":
                return ec_project_model.getMounting_nut();
            default:
                return "1";
        }
    }

    private static double taxPercent(double basePrice, double perVal) {
        return (basePrice * perVal) / 100;
    }

    private void fetch_currency(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        CurrencyModel currencyModel = new Gson().fromJson(object.getString("data"), CurrencyModel.class);
                        List<CurrencyModel.Currencies> currencies = currencyModel.getSupportedCcy();
                        CurrencyModel.Currencies currencies1 = new CurrencyModel.Currencies();
                        currencies1.setId("");
                        ec_project_model.setCurrency("");
                        currencies1.setName(currencyModel.getHomeCcy());
                        currencies1.setValue("1");
                        currencies.add(0, currencies1);
                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv, currencies);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                        currency_spinr.setAdapter(arrayAdapter);
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

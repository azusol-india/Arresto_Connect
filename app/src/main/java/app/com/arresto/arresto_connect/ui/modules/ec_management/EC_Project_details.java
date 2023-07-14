/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.custom_views.switch_lib.SwitchTrackTextDrawable;
import app.com.arresto.arresto_connect.data.models.EC_project_Model;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.horizontal;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_alurail;

public class EC_Project_details extends EC_Base_Fragment {
    View view;
    EditText segment_1_edt, segment_2_edt, segment_3_edt, segment_4_edt, clearance_edit, temperature_edt, tension_edt, load_edt, line_edt;
    TextView total_sgment_tv, cntnu_btn;
    DialogSpinner dimeter_spnr, corner_spnr, span_1_spnr, span_2_spnr, span_3_spnr, span_4_spnr, user_spnr,
            absorber_spnr, maxload_spnr, strctr_typ_spnr, foundation_spnr, conecting_spnr, cnstctn_spnr,
            constant_force_spnr, tension_spnr;

    //    1 m=3.280839895 ft
    String boq_id, line_type, customer_name, applicationName;
    boolean isQuote;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            if (getArguments() != null) {
                boq_id = getArguments().getString("boq_id");
                line_type = getArguments().getString("line_type");
                customer_name = getArguments().getString("customer_name");
                applicationName = getArguments().getString("applicationName");
                isQuote = getArguments().getBoolean("isQuote", false);
            }
            if (line_type == null || line_type.equals(horizontal)) {
                view = inflater.inflate(R.layout.horizontal_dataform, parent, false);
                findAllIds(view);
                check_isDataExist();
                cntnu_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        load_fragment();
                    }
                });
            } else {
                view = inflater.inflate(R.layout.vertical_dataform, parent, false);
                findAllIds_vertical(view);
                check_isDataExist();
                cntnu_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        load_fragment_vertical();
                    }
                });
            }

        }
        return view;
    }

    // horizontal code here
    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec boq");
    }

    private void check_isDataExist() {
        Project_Boq_table.Project_Boq_Dao project_boq_dao = AppController.getInstance().getDatabase().getProject_Boq_Dao();
        if (project_boq_dao.isBOQExist(user_id, project_id, site_id, boq_id)) {
            Project_Boq_table project_boq_table = AppController.getInstance().getDatabase().getProject_Boq_Dao().getSingle_boq(user_id, project_id, site_id, boq_id);
            if (project_boq_table != null && project_boq_table.getData() != null) {
                try {
                    JSONObject json_data = new JSONObject(project_boq_table.getData());
                    EC_project_Model ec_project_model = new Gson().fromJson("" + json_data, EC_project_Model.class);
                    set_preFillData(ec_project_model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    TextView segment_1_tv, segment_2_tv, segment_3_tv, segment_4_tv, clearance_tv, temperature_tv, total_tv, maxload_tv, span_1_tv, span_2_tv, span_3_tv, span_4_tv, tension_tv;

    public void findAllIds(View view) {
        segment_1_tv = view.findViewById(R.id.segment_1_tv);
        segment_2_tv = view.findViewById(R.id.segment_2_tv);
        segment_3_tv = view.findViewById(R.id.segment_3_tv);
        segment_4_tv = view.findViewById(R.id.segment_4_tv);
        span_1_tv = view.findViewById(R.id.span_1_tv);
        span_2_tv = view.findViewById(R.id.span_2_tv);
        span_3_tv = view.findViewById(R.id.span_3_tv);
        span_4_tv = view.findViewById(R.id.span_4_tv);
        clearance_tv = view.findViewById(R.id.clearance_tv);
        temperature_tv = view.findViewById(R.id.temperature_tv);
        total_tv = view.findViewById(R.id.total_tv);
        maxload_tv = view.findViewById(R.id.maxload_tv);


        segment_1_edt = view.findViewById(R.id.segment_1_edt);
        segment_2_edt = view.findViewById(R.id.segment_2_edt);
        segment_3_edt = view.findViewById(R.id.segment_3_edt);
        segment_4_edt = view.findViewById(R.id.segment_4_edt);
        clearance_edit = view.findViewById(R.id.clearance_edit);
        temperature_edt = view.findViewById(R.id.temperature_edt);

        tension_tv = view.findViewById(R.id.tension_tv);
        tension_edt = view.findViewById(R.id.tension_edt);
        load_edt = view.findViewById(R.id.load_edt);
        line_edt = view.findViewById(R.id.line_edt);
        total_sgment_tv = view.findViewById(R.id.total_sgment_tv);
        cntnu_btn = view.findViewById(R.id.cntnu_btn);


        maxload_spnr = view.findViewById(R.id.maxload_spnr);
        strctr_typ_spnr = view.findViewById(R.id.strctr_typ_spnr);
        foundation_spnr = view.findViewById(R.id.foundation_spnr);
        conecting_spnr = view.findViewById(R.id.conecting_spnr);
        tension_spnr = view.findViewById(R.id.tention_spnr);
        dimeter_spnr = view.findViewById(R.id.dimtr_spinr);
        corner_spnr = view.findViewById(R.id.corner_spnr);
        span_1_spnr = view.findViewById(R.id.span_1_spnr);
        span_2_spnr = view.findViewById(R.id.span_2_spnr);
        span_3_spnr = view.findViewById(R.id.span_3_spnr);
        span_4_spnr = view.findViewById(R.id.span_4_spnr);
        user_spnr = view.findViewById(R.id.user_spnr);
        absorber_spnr = view.findViewById(R.id.observer_spnr);
        cnstctn_spnr = view.findViewById(R.id.cnstctn_spnr);
        constant_force_spnr = view.findViewById(R.id.constant_force_spnr);
        meter_switch = view.findViewById(R.id.btn_accessible);

        meter_switch.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(), R.string.lbl_metric, R.string.lbl_imperial));
        set_listners();
    }

    private void set_preFillData(EC_project_Model ec_project_model) {
        if (!ec_project_model.isMeter()) {
            meter_switch.setChecked(true);
        }

        if (ec_project_model.getType().equals("Horizontal Life Line Systems") && ec_project_model.getSegment_1() != null) {
            segment_1_edt.setText(ec_project_model.getSegment_1().getLength());
            set_spiner(span_1_spnr, ec_project_model.getSegment_1().getSpan());
            if (ec_project_model.getSegment_2() != null) {
                segment_2_edt.setText(ec_project_model.getSegment_2().getLength());
                set_spiner(span_2_spnr, ec_project_model.getSegment_2().getSpan());
            }
            if (ec_project_model.getSegment_3() != null) {
                segment_3_edt.setText(ec_project_model.getSegment_3().getLength());
                set_spiner(span_3_spnr, ec_project_model.getSegment_3().getSpan());
            }
            if (ec_project_model.getSegment_4() != null) {
                segment_4_edt.setText(ec_project_model.getSegment_4().getLength());
                set_spiner(span_4_spnr, ec_project_model.getSegment_4().getSpan());
            }

            clearance_edit.setText(ec_project_model.getClearance());
            temperature_edt.setText(ec_project_model.getTemperature());
            tension_edt.setText(ec_project_model.getTention());
            load_edt.setText(ec_project_model.getMaximum_load());
            line_edt.setText(ec_project_model.getNumber_of_line());
            set_spiner(maxload_spnr, ec_project_model.getMaximum_load());
            set_spiner(tension_spnr, ec_project_model.getTention());
            set_spiner(corner_spnr, ec_project_model.getCorner());
            set_spiner(strctr_typ_spnr, ec_project_model.getStructure_type());
            set_spiner(foundation_spnr, ec_project_model.getFoundation_material());
            set_spiner(conecting_spnr, ec_project_model.getConnecting_element());
            set_spiner(dimeter_spnr, ec_project_model.getDiameter());
            set_spiner(user_spnr, ec_project_model.getUsers());
            set_spiner(absorber_spnr, ec_project_model.getAbsorber());
            set_spiner(cnstctn_spnr, ec_project_model.getConstruction_of_wire());
            set_spiner(constant_force_spnr, ec_project_model.getConstant_force_post());
//            meter_switch
        } else {
            if (ec_project_model.getSystem_type() != null)
                set_spiner(type_spinr, ec_project_model.getSystem_type());
            if (ec_project_model.getLife_lineLength() != null)
                length_edt.setText(ec_project_model.getLife_lineLength());
            if (ec_project_model.getIntermediate_spacing() != null)
                spacing_edt.setText(ec_project_model.getIntermediate_spacing());
            if (ec_project_model.getExtension_arm().equalsIgnoreCase("yes"))
                ((RadioButton) extnsn_rg.getChildAt(0)).setChecked(true);
            else
                ((RadioButton) extnsn_rg.getChildAt(1)).setChecked(true);
            if (ec_project_model.getRung_dimension() != null)
                rung_dimnsn_edt.setText(ec_project_model.getRung_dimension());
            if (ec_project_model.getRung_distance() != null)
                distance_edt.setText(ec_project_model.getRung_distance());
            if (ec_project_model.getSection_size() != null)
                side_sec_edt.setText(ec_project_model.getSection_size());
            if (ec_project_model.getDiameter() != null)
                set_spiner(dimeter_spnr, ec_project_model.getDiameter());
            if (ec_project_model.getSection_rung() != null)
                set_spiner(rung_spinr, ec_project_model.getSection_rung());
            if (ec_project_model.getSection_ladder() != null)
                set_spiner(section_spnr, ec_project_model.getSection_ladder());
            if (ec_project_model.getNumber_of_line() != null)
                line_edt.setText(ec_project_model.getNumber_of_line());
            if (ec_project_model.getStructure_type() != null)
                set_spiner(strctr_typ_spnr, ec_project_model.getStructure_type());
        }
    }

    private void set_spiner(Spinner spnr, String value) {
        spnr.setSelection(((ArrayAdapter<String>) spnr.getAdapter()).getPosition(value));
    }

    Switch meter_switch;

    private void change_spiner_array(DialogSpinner spnr, String[] array) {
        int pos = spnr.getSelectedItemPosition();
        List<String> strings = new ArrayList<>(Arrays.asList(array));
        if (!strings.contains(getResString("lbl_pl_slct_msg")))
            strings.add(0, getResString("lbl_pl_slct_msg"));
        spnr.setItems(strings, "");
        spnr.setSelection(pos);
    }

    private void change_heading(boolean is_feet) {
        segment_1_edt.setText(convert_length(segment_1_edt.getText().toString(), is_feet));
        segment_2_edt.setText(convert_length(segment_2_edt.getText().toString(), is_feet));
        segment_3_edt.setText(convert_length(segment_3_edt.getText().toString(), is_feet));
        segment_4_edt.setText(convert_length(segment_4_edt.getText().toString(), is_feet));
        clearance_edit.setText(convert_length(clearance_edit.getText().toString(), is_feet));
        temperature_edt.setText(convert_temp(temperature_edt.getText().toString(), is_feet));

        if (is_feet) {
            segment_1_tv.setText("Length of First Segment(Ft)");
            segment_2_tv.setText("Length of Second Segment(Ft)");
            segment_3_tv.setText("Length of Third Segment(Ft)");
            segment_4_tv.setText("Length of Fourth Segment(Ft)");
            span_1_tv.setText("Maximum Span(Ft)");
            span_2_tv.setText("Maximum Span(Ft)");
            span_3_tv.setText("Maximum Span(Ft)");
            span_4_tv.setText("Maximum Span(Ft)");
            clearance_tv.setText("Available Fall clearance below the Work Platform(Ft)");
            temperature_tv.setText("Temperature during the installation(\u2109)");
            total_tv.setText("Total Length of Wire(Ft)");
            maxload_tv.setText("Maximum Load(Pound)");
            tension_tv.setText("Pre-tension (Pound)");

        } else {
            segment_1_tv.setText("Length of First Segment(Mtr.)");
            segment_2_tv.setText("Length of Second Segment(Mtr.)");
            segment_3_tv.setText("Length of Third Segment(Mtr.)");
            segment_4_tv.setText("Length of Fourth Segment(Mtr.)");
            span_1_tv.setText("Maximum Span(Mtr.)");
            span_2_tv.setText("Maximum Span(Mtr.)");
            span_3_tv.setText("Maximum Span(Mtr.)");
            span_4_tv.setText("Maximum Span(Mtr.)");
            clearance_tv.setText("Available Fall clearance below the Work Platform(Mtr.)");
            temperature_tv.setText("Temperature during the installation(\u2103)");
            total_tv.setText("Total Length of Wire(Mtr.)");
            maxload_tv.setText("Maximum Load(kN)");
            tension_tv.setText("Pre-tension (kN)");
        }
    }

    double to_feet = 3.280839895;
    double to_mm = 25.4;

    public String convert_length(String s, boolean is_feet) {
        if (s != null && !s.equals("")) {
            if (is_feet)
                return "" + round("" + (Double.parseDouble(s) * to_feet));
            else
                return "" + round("" + (Double.parseDouble(s) / to_feet));
        } else return "";

    }

    public String convert_length_mm(String s, boolean is_feet) {
        if (s != null && !s.equals("")) {
            if (is_feet)
                return "" + round("" + (Double.parseDouble(s) / to_mm));
            else
                return "" + round("" + (Double.parseDouble(s) * to_mm));
        } else return "";

    }

    public String convert_temp(String s, boolean is_feet) {
        if (s != null && !s.equals("")) {
            if (is_feet)
                return "" + ((Integer.parseInt(s) * 9 / 5) + 32);
            else
                return "" + ((Integer.parseInt(s) - 32) * 5 / 9);
        } else return "";
    }

    private void set_listners() {
        change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr));
        change_spiner_array(span_1_spnr, getResources().getStringArray(R.array.span_arr));
        change_spiner_array(span_2_spnr, getResources().getStringArray(R.array.span_arr));
        change_spiner_array(span_3_spnr, getResources().getStringArray(R.array.span_arr));
        change_spiner_array(span_4_spnr, getResources().getStringArray(R.array.span_arr));
        change_spiner_array(maxload_spnr, getResources().getStringArray(R.array.load_arr));
        change_spiner_array(tension_spnr, getResources().getStringArray(R.array.pretention_arr));

        change_spiner_array(strctr_typ_spnr, getResources().getStringArray(R.array.structure_arr));
        change_spiner_array(foundation_spnr, getResources().getStringArray(R.array.foundation_arr));
        change_spiner_array(conecting_spnr, getResources().getStringArray(R.array.connecting_arr));
        change_spiner_array(cnstctn_spnr, getResources().getStringArray(R.array.cnstrctn_arr));

        change_spiner_array(corner_spnr, getResources().getStringArray(R.array.cornr_arr));
        change_spiner_array(user_spnr, getResources().getStringArray(R.array.users_arr));
        change_spiner_array(absorber_spnr, getResources().getStringArray(R.array.absorber_array));
        change_spiner_array(constant_force_spnr, getResources().getStringArray(R.array.yes_no_arr));

        meter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change_heading(isChecked);
                if (isChecked) {
                    change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr_imp));
                    change_spiner_array(span_1_spnr, getResources().getStringArray(R.array.span_arr_imp));
                    change_spiner_array(span_2_spnr, getResources().getStringArray(R.array.span_arr_imp));
                    change_spiner_array(span_3_spnr, getResources().getStringArray(R.array.span_arr_imp));
                    change_spiner_array(span_4_spnr, getResources().getStringArray(R.array.span_arr_imp));
                    change_spiner_array(maxload_spnr, getResources().getStringArray(R.array.load_arr_imp));
                    change_spiner_array(tension_spnr, getResources().getStringArray(R.array.pretention_arr_imp));
                } else {
                    change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr));
                    change_spiner_array(span_1_spnr, getResources().getStringArray(R.array.span_arr));
                    change_spiner_array(span_2_spnr, getResources().getStringArray(R.array.span_arr));
                    change_spiner_array(span_3_spnr, getResources().getStringArray(R.array.span_arr));
                    change_spiner_array(span_4_spnr, getResources().getStringArray(R.array.span_arr));
                    change_spiner_array(maxload_spnr, getResources().getStringArray(R.array.load_arr));
                    change_spiner_array(tension_spnr, getResources().getStringArray(R.array.pretention_arr));
                }
            }
        });

        tension_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 2) {
                    tension_edt.setVisibility(View.VISIBLE);
                } else {
                    tension_edt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        maxload_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 2) {
                    load_edt.setVisibility(View.VISIBLE);
                } else {
                    load_edt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        corner_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        ((LinearLayout) span_2_spnr.getParent()).setVisibility(View.GONE);
                        ((LinearLayout) span_3_spnr.getParent()).setVisibility(View.GONE);
                        ((LinearLayout) span_4_spnr.getParent()).setVisibility(View.GONE);
                        segment_2_edt.setText("");
                        segment_3_edt.setText("");
                        segment_4_edt.setText("");
                        break;
                    case 2:
                        ((LinearLayout) span_2_spnr.getParent()).setVisibility(View.VISIBLE);
                        ((LinearLayout) span_3_spnr.getParent()).setVisibility(View.GONE);
                        ((LinearLayout) span_4_spnr.getParent()).setVisibility(View.GONE);
                        segment_3_edt.setText("");
                        segment_4_edt.setText("");
                        break;
                    case 3:
                        ((LinearLayout) span_2_spnr.getParent()).setVisibility(View.VISIBLE);
                        ((LinearLayout) span_3_spnr.getParent()).setVisibility(View.VISIBLE);
                        ((LinearLayout) span_4_spnr.getParent()).setVisibility(View.GONE);
                        segment_4_edt.setText("");
                        break;
                    case 4:
                        ((LinearLayout) span_2_spnr.getParent()).setVisibility(View.VISIBLE);
                        ((LinearLayout) span_3_spnr.getParent()).setVisibility(View.VISIBLE);
                        ((LinearLayout) span_4_spnr.getParent()).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        constant_force_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    absorber_spnr.setLastSelected(3);
                    absorber_spnr.setEnabled(false);
                } else {
                    absorber_spnr.setLastSelected(0);
                    absorber_spnr.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        segment_1_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                set_total();
            }
        });

        segment_2_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                set_total();
            }
        });

        segment_3_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                set_total();
            }
        });
        segment_4_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                set_total();
            }
        });

    }

    public void set_total() {
        double s1 = 0, s2 = 0, s3 = 0, s4 = 0;
        if (!TextUtils.isEmpty(segment_1_edt.getText()))
            s1 = round(segment_1_edt.getText().toString());
        if (!TextUtils.isEmpty(segment_2_edt.getText()))
            s2 = round(segment_2_edt.getText().toString());
        if (!TextUtils.isEmpty(segment_3_edt.getText()))
            s3 = round(segment_3_edt.getText().toString());
        if (!TextUtils.isEmpty(segment_4_edt.getText()))
            s4 = round(segment_4_edt.getText().toString());
        total_sgment_tv.setText("" + (s1 + s2 + s3 + s4));
    }

    private static double round(String value) {
        return new BigDecimal(Double.parseDouble(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public void load_fragment() {
        EC_Project_BOQFragment ec_project_boq = new EC_Project_BOQFragment();
        Bundle bundle = new Bundle();
        EC_project_Model ec_project_model = new EC_project_Model();
        EC_project_Model.Segment_data segment_data = new EC_project_Model.Segment_data();
        double sg;
        long spm;
        if (strctr_typ_spnr.getSelectedItemPosition() < 1 || foundation_spnr.getSelectedItemPosition() < 1 || absorber_spnr.getSelectedItemPosition() < 1 ||
                conecting_spnr.getSelectedItemPosition() < 1 || corner_spnr.getSelectedItemPosition() < 1 || cnstctn_spnr.getSelectedItemPosition() < 1 ||
                dimeter_spnr.getSelectedItemPosition() < 1 || user_spnr.getSelectedItemPosition() < 1 || constant_force_spnr.getSelectedItemPosition() < 1) {
            show_snak(baseActivity, getResString("lbl_field_mndtry"));
            return;
        }
        if (!segment_1_edt.getText().toString().equals("")) {
            sg = Double.parseDouble(segment_1_edt.getText().toString());
            spm = Long.parseLong(span_1_spnr.getSelectedItem().toString());
            if (sg < spm) {
                segment_1_edt.setError(getResString("lbl_segment_should_greter"));
                show_snak(baseActivity, getResString("lbl_segment_should_greter"));
                return;
            }
            segment_data.setLength(segment_1_edt.getText().toString());
            segment_data.setSpan(span_1_spnr.getSelectedItem().toString());
            ec_project_model.setSegment_1(segment_data);
        } else {
            show_snak(baseActivity, getResString("lbl_blank_segment_msg"));
            return;
        }

        if (!segment_2_edt.getText().toString().equals("")) {
            sg = Double.parseDouble(segment_2_edt.getText().toString());
            spm = Long.parseLong(span_2_spnr.getSelectedItem().toString());
            if (sg < spm) {
                segment_2_edt.setError(getResString("lbl_segment_should_greter"));
                show_snak(baseActivity, getResString("lbl_segment_should_greter"));
                return;
            }
            segment_data = new EC_project_Model.Segment_data();
            segment_data.setLength(segment_2_edt.getText().toString());
            segment_data.setSpan(span_2_spnr.getSelectedItem().toString());
            ec_project_model.setSegment_2(segment_data);
        }
        if (!segment_3_edt.getText().toString().equals("")) {
            sg = Double.parseDouble(segment_3_edt.getText().toString());
            spm = Long.parseLong(span_3_spnr.getSelectedItem().toString());
            if (sg < spm) {
                segment_3_edt.setError(getResString("lbl_segment_should_greter"));
                show_snak(baseActivity, getResString("lbl_segment_should_greter"));
                return;
            }
            segment_data = new EC_project_Model.Segment_data();
            segment_data.setLength(segment_3_edt.getText().toString());
            segment_data.setSpan(span_3_spnr.getSelectedItem().toString());
            ec_project_model.setSegment_3(segment_data);
        }

        if (!segment_4_edt.getText().toString().equals("")) {
            sg = Double.parseDouble(segment_4_edt.getText().toString());
            spm = Long.parseLong(span_4_spnr.getSelectedItem().toString());
            if (sg < spm) {
                segment_4_edt.setError(getResString("lbl_segment_should_greter"));
                show_snak(baseActivity, getResString("lbl_segment_should_greter"));
                return;
            }
            segment_data = new EC_project_Model.Segment_data();
            segment_data.setLength(segment_4_edt.getText().toString());
            segment_data.setSpan(span_4_spnr.getSelectedItem().toString());
            ec_project_model.setSegment_4(segment_data);
        }

        if (maxload_spnr.getSelectedItemPosition() > 2)
            ec_project_model.setMaximum_load(load_edt.getText().toString());
        else
            ec_project_model.setMaximum_load(maxload_spnr.getSelectedItem().toString());
        if (tension_spnr.getSelectedItemPosition() > 2)
            ec_project_model.setTention(tension_edt.getText().toString());
        else
            ec_project_model.setTention(tension_spnr.getSelectedItem().toString());

        ec_project_model.setStructure_type(strctr_typ_spnr.getSelectedItem().toString());
        ec_project_model.setFoundation_material(foundation_spnr.getSelectedItem().toString());
        ec_project_model.setConnecting_element(conecting_spnr.getSelectedItem().toString());
        ec_project_model.setCorner(corner_spnr.getSelectedItem().toString());
        ec_project_model.setDiameter(dimeter_spnr.getSelectedItem().toString());
        ec_project_model.setUsers(user_spnr.getSelectedItem().toString());
        ec_project_model.setAbsorber(absorber_spnr.getSelectedItem().toString());
        ec_project_model.setConstruction_of_wire(cnstctn_spnr.getSelectedItem().toString());
        ec_project_model.setConstant_force_post(constant_force_spnr.getSelectedItem().toString());

        ec_project_model.setNumber_of_line(line_edt.getText().toString());
        ec_project_model.setTotal_sgment(total_sgment_tv.getText().toString());
        ec_project_model.setTemperature(temperature_edt.getText().toString());
        ec_project_model.setClearance(clearance_edit.getText().toString());
        ec_project_model.setType("Horizontal Life Line Systems");
        ec_project_model.setMeter(!meter_switch.isChecked());
        if (isQuote) {
            ec_project_model.setCustomer_name(customer_name);
            ec_project_model.setApplication(applicationName);
        }
        bundle.putString("data", AppUtils.getGson().toJson(ec_project_model));
        bundle.putBoolean("isQuote", isQuote);
        ec_project_boq.setArguments(bundle);
        LoadFragment.replace(ec_project_boq, getActivity(), getResString("lbl_ec_calculation"));
    }

    // vertical code here

    DialogSpinner rung_spinr, section_spnr, type_spinr;
    EditText length_edt, spacing_edt, rung_dimnsn_edt, side_sec_edt, distance_edt;
    RadioGroup extnsn_rg, chemical_fastener_rg, post_rg, half_rung_rg, folding_aluminum_rg;

    public void findAllIds_vertical(View view) {
        length_tv = view.findViewById(R.id.length_tv);
        type_spinr = view.findViewById(R.id.type_spinr);
        spacing_tv = view.findViewById(R.id.spacing_tv);
        ext_tv = view.findViewById(R.id.ext_tv);
        dimtr_tv = view.findViewById(R.id.dimtr_tv);
        rung_sec_tv = view.findViewById(R.id.rung_sec_tv);
        rung_dimnsn_tv = view.findViewById(R.id.rung_dimnsn_tv);
        distance_tv = view.findViewById(R.id.distance_tv);
        side_ladder_tv = view.findViewById(R.id.side_ladder_tv);
        side_size_tv = view.findViewById(R.id.side_size_tv);
        line_edt = view.findViewById(R.id.line_edt);

        length_edt = view.findViewById(R.id.length_edt);
        spacing_edt = view.findViewById(R.id.spacing_edt);
        extnsn_rg = view.findViewById(R.id.extnsn_rg);
        dimeter_spnr = view.findViewById(R.id.dimtr_spinr);
        rung_spinr = view.findViewById(R.id.rung_spinr);
        section_spnr = view.findViewById(R.id.section_spnr);
        rung_dimnsn_edt = view.findViewById(R.id.rung_dimnsn_edt);
        distance_edt = view.findViewById(R.id.distance_edt);
        cntnu_btn = view.findViewById(R.id.cntnu_btn);
        side_sec_edt = view.findViewById(R.id.side_sec_edt);
        ((RadioButton) extnsn_rg.getChildAt(0)).setChecked(true);
        meter_switch = view.findViewById(R.id.btn_accessible);
        chemical_fastener_rg = view.findViewById(R.id.chemical_fastener_rg);
        post_rg = view.findViewById(R.id.post_rg);
        half_rung_rg = view.findViewById(R.id.half_rung_rg);
        folding_aluminum_rg = view.findViewById(R.id.folding_aluminum_rg);
        strctr_typ_spnr = view.findViewById(R.id.strctr_typ_spnr);

        meter_switch.setTrackDrawable(new SwitchTrackTextDrawable(getActivity(), R.string.lbl_metric, R.string.lbl_imperial));

        if (line_type.equals(vertical_alurail)) {
            type_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                    if (type_spinr.getSelectedItem().toString().equals("PN9000")) {
                        view.findViewById(R.id.alu_lay).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.cmn_lay).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.alu_lay).setVisibility(View.GONE);
                        view.findViewById(R.id.cmn_lay).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            view.findViewById(R.id.wire_lay).setVisibility(View.GONE);
            change_spiner_array(type_spinr, getResources().getStringArray(R.array.system_type_alu_arr));
            change_spiner_array(strctr_typ_spnr, getResources().getStringArray(R.array.alu_structure_arr));
        } else {
            change_spiner_array(type_spinr, getResources().getStringArray(R.array.system_type_wire_arr));
            change_spiner_array(section_spnr, getResources().getStringArray(R.array.side_section_arr));
        }
        change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr));
        change_spiner_array(rung_spinr, getResources().getStringArray(R.array.rung_arr));

        meter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change_heading_vertical(isChecked);
                if (isChecked) {
                    change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr_imp));
                } else {
                    change_spiner_array(dimeter_spnr, getResources().getStringArray(R.array.diameter_arr));
                }
            }
        });
    }

    TextView length_tv, spacing_tv, ext_tv, dimtr_tv, rung_sec_tv, rung_dimnsn_tv, distance_tv, side_ladder_tv, side_size_tv;

    private void change_heading_vertical(boolean is_feet) {
        length_edt.setText(convert_length(length_edt.getText().toString(), is_feet));
        spacing_edt.setText(convert_length(spacing_edt.getText().toString(), is_feet));
        distance_edt.setText(convert_length_mm(distance_edt.getText().toString(), is_feet));
        rung_dimnsn_edt.setText(convert_length_mm(rung_dimnsn_edt.getText().toString(), is_feet));
        side_sec_edt.setText(convert_length_mm(side_sec_edt.getText().toString(), is_feet));

        if (is_feet) {
            length_tv.setText("System Height(Ft.)");
            spacing_tv.setText("Spacing of Intermediate(Ft.)");
            rung_dimnsn_tv.setText("Dimension of  Rung(Inch)");
            distance_tv.setText("Distance Between two rung(Inch)");
            side_size_tv.setText("Size of Side Section (Fill if Bracket needs to be installed on it)(Inch)");
        } else {
            length_tv.setText("System Height(Mtr.)");
            spacing_tv.setText("Spacing of Intermediate(Mtr.)");
            rung_dimnsn_tv.setText("Dimension of  Rung(mm)");
            distance_tv.setText("Distance Between two rung(mm)");
            side_size_tv.setText("Size of Side Section (Fill if Bracket needs to be installed on it)(mm)");
        }
    }

    public void load_fragment_vertical() {
        EC_Project_BOQFragment ec_project_boq = new EC_Project_BOQFragment();
        Bundle bundle = new Bundle();
        EC_project_Model ec_project_model = new EC_project_Model();
        if (type_spinr.getSelectedItemPosition() < 1) {
            show_snak(baseActivity, "Please select system type");
            return;
        }
        if (length_edt.getText().toString().equals("")) {
            show_snak(baseActivity, "Please enter system height");
            return;
        }

        if (line_type.equals(vertical_alurail)) {
            ec_project_model.setType("Aluminium");
            if (type_spinr.getSelectedItem().toString().equals("PN9000")) {
                if (strctr_typ_spnr.getSelectedItemPosition() < 1) {
                    show_snak(baseActivity, "Please select structure type");
                    return;
                }
                ec_project_model.setStructure_type(strctr_typ_spnr.getSelectedItem().toString());
                ec_project_model.setIsChemical_fastener(((RadioButton) chemical_fastener_rg.findViewById(chemical_fastener_rg.getCheckedRadioButtonId())).getText().toString());
                ec_project_model.setIsPost(((RadioButton) post_rg.findViewById(post_rg.getCheckedRadioButtonId())).getText().toString());
                ec_project_model.setIsHalf_rung(((RadioButton) half_rung_rg.findViewById(half_rung_rg.getCheckedRadioButtonId())).getText().toString());
                ec_project_model.setIsFolding_aluminum(((RadioButton) folding_aluminum_rg.findViewById(folding_aluminum_rg.getCheckedRadioButtonId())).getText().toString());
            } else {
                if (rung_spinr.getSelectedItemPosition() < 1) {
                    show_snak(baseActivity, getResString("lbl_field_mndtry"));
                    return;
                }
                if (rung_dimnsn_edt.getText().toString().equals("") || distance_edt.getText().toString().equals("")) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                ec_project_model.setSection_rung(rung_spinr.getSelectedItem().toString());
                ec_project_model.setRung_dimension(rung_dimnsn_edt.getText().toString());
                ec_project_model.setRung_distance(distance_edt.getText().toString());
            }
        } else {
            ec_project_model.setType("Wire Rope");
            if (section_spnr.getSelectedItemPosition() < 1 || dimeter_spnr.getSelectedItemPosition() < 1 || rung_spinr.getSelectedItemPosition() < 1) {
                show_snak(baseActivity, getResString("lbl_field_mndtry"));
                return;
            }
            if (rung_dimnsn_edt.getText().toString().equals("") || distance_edt.getText().toString().equals("") ||
                    spacing_edt.getText().toString().equals("") || side_sec_edt.getText().toString().equals("")) {
                show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                return;
            }
            ec_project_model.setDiameter(dimeter_spnr.getSelectedItem().toString());
            ec_project_model.setSection_ladder(section_spnr.getSelectedItem().toString());
            ec_project_model.setSection_rung(rung_spinr.getSelectedItem().toString());
            ec_project_model.setRung_dimension(rung_dimnsn_edt.getText().toString());
            ec_project_model.setRung_distance(distance_edt.getText().toString());
            ec_project_model.setIntermediate_spacing(spacing_edt.getText().toString());
            ec_project_model.setSection_size(side_sec_edt.getText().toString());
        }

        ec_project_model.setSystem_type(type_spinr.getSelectedItem().toString());
        ec_project_model.setLife_lineLength(length_edt.getText().toString());
        ec_project_model.setExtension_arm(((RadioButton) extnsn_rg.findViewById(extnsn_rg.getCheckedRadioButtonId())).getText().toString());
        ec_project_model.setNumber_of_line(line_edt.getText().toString());
        ec_project_model.setMeter(!meter_switch.isChecked());

        if (isQuote) {
            ec_project_model.setCustomer_name(customer_name);
            ec_project_model.setApplication(applicationName);
        }
        bundle.putString("data", AppUtils.getGson().toJson(ec_project_model));
        bundle.putBoolean("isQuote", isQuote);
        ec_project_boq.setArguments(bundle);
        LoadFragment.replace(ec_project_boq, getActivity(), getResString("lbl_ec_calculation"));
    }

}

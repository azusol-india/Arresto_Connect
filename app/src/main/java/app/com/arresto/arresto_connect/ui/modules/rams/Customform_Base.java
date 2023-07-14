package app.com.arresto.arresto_connect.ui.modules.rams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CameraActivity;
import app.com.arresto.arresto_connect.constants.CaptureSignatureView;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Repeaters_Model;
import app.com.arresto.arresto_connect.data.models.Signature_objcet;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static android.app.Activity.RESULT_OK;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResSize;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.custom_views.Font_type.set_background;

public abstract class Customform_Base extends Base_Fragment {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout dynamic_root;
    ArrayList<String> submitted_field_names;
    ArrayList<CustomForm_Model.FieldData> data_field_sets;
    int data_index;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        View view = FragmentView(inflater, parent, savedInstanseState);
        dynamic_root = view.findViewById(R.id.dynamic_root);
        return view;
    }

//    public abstract View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);


    public void remove_all_views() {
        dynamic_root.removeAllViews();
    }

    int[] color_arry = {Dynamic_Var.getInstance().getApp_transparent(), Dynamic_Var.getInstance().getApp_background(), Dynamic_Var.getInstance().getApp_text()
            , Dynamic_Var.getInstance().getBtn_txt_clr(), Dynamic_Var.getInstance().getBtn_bg_clr()};

    public JSONArray singleSet_array;
    public JSONArray repeterSet_Array;
    public String form_id;

    // set form by singleton form position
    public void setForm(final int i) {
        submitted_field_names = new ArrayList<>();
        if (data_index > -1 && DataHolder_Model.getInstance().getCustomViewsData_models() != null && DataHolder_Model.getInstance().getCustomViewsData_models().size() > 0) {
            data_field_sets = DataHolder_Model.getInstance().getCustomViewsData_models().get(data_index).getField_set().getSingle_set();
            for (CustomForm_Model.FieldData field_set : data_field_sets) {
                if (field_set != null)
                    submitted_field_names.add(field_set.getField_name());
            }
//            continue_btn.setVisibility(continue_status);
        }
        create_new_form(DataHolder_Model.getInstance().getCustomViews_models().get(i));
    }

    // set form by form data
    public void setForm(CustomForm_Model form, CustomForm_Model sbmtd_data) {
        submitted_field_names = new ArrayList<>();
        if (sbmtd_data != null && sbmtd_data.getCf_id() != null) {
            data_field_sets = sbmtd_data.getField_set().getSingle_set();
            for (CustomForm_Model.FieldData field_set : data_field_sets) {
                if (field_set != null)
                    submitted_field_names.add(field_set.getField_name());
            }
        }
        create_new_form(form);
    }

    public void create_new_form(CustomForm_Model customViews_model) {
        singleSet_array = new JSONArray();
        form_id = customViews_model.getCf_id();
        TextView headin_tv = getTextView();
        set_background(headin_tv, color_arry[4], "trans_bg");
        headin_tv.setText(customViews_model.getForm_name());
        headin_tv.setTextColor(color_arry[1]);
        headin_tv.setTypeface(Typeface.DEFAULT_BOLD);
        headin_tv.setGravity(Gravity.CENTER);
        headin_tv.setPadding(getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_2dp));

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResSize(R.dimen.margin_35dp), 1.0f);
        params.setMargins(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_5dp), 0);
        headin_tv.setLayoutParams(params);
        dynamic_root.addView(headin_tv);
        LinearLayout linearLayout = make_itemLayer();
//        Collections.sort(customViews_model.getField_set().getSingle_set(), new Comparator<CustomForm_Model.FieldData>() {
//            public int compare(CustomForm_Model.FieldData obj1, CustomForm_Model.FieldData obj2) {
//                return obj1.getGrp_heading().compareTo(obj2.getGrp_heading());
//            }
//        });

        for (int j = 0; j < customViews_model.getField_set().getSingle_set().size(); j++) {
            CustomForm_Model.FieldData item = customViews_model.getField_set().getSingle_set().get(j);
            if (item.getGrp_heading() != null && !item.getGrp_heading().equals("")
                    && !single_lastHeading.equals(item.getGrp_heading())) {
                single_lastHeading = item.getGrp_heading();
                addHeading(linearLayout, single_lastHeading);
            }

            add_new_field(j, customViews_model.getField_set().getSingle_set().get(j), linearLayout, singleSet_array);
        }

        if (linearLayout.getChildCount() > 0)
            dynamic_root.addView(linearLayout);

        if (customViews_model.getField_set().getRepeater_set() != null && customViews_model.getField_set().getRepeater_set().size() > 0) {
            TextView parameter_tv = getTextView();
            set_background(parameter_tv, getResColor(R.color.grey), "trans_bg");
            parameter_tv.setText("Parameters of " + customViews_model.getForm_name());
            parameter_tv.setTextColor(color_arry[1]);
            parameter_tv.setGravity(Gravity.CENTER);
            parameter_tv.setPadding(getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_8dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_8dp));
            dynamic_root.addView(parameter_tv);

            repeaters_models = new ArrayList<>();
            repeterSet_Array = new JSONArray();

//            Collections.sort(customViews_model.getField_set().getRepeater_set(), new Comparator<CustomForm_Model.FieldData>() {
//                public int compare(CustomForm_Model.FieldData obj1, CustomForm_Model.FieldData obj2) {
//                    return obj1.getGrp_heading().compareTo(obj2.getGrp_heading());
//                }
//            });

            add_new_repeated_layer(customViews_model.getField_set().getRepeater_set(), null, null, null, false);
        }

    }


    ArrayList<Repeaters_Model> repeaters_models;
    String single_lastHeading = "";
//            , lastHeading = "";

    public void add_new_repeated_layer(ArrayList<CustomForm_Model.FieldData> repeater_set, LinearLayout parnt_lay, LinearLayout butn_layer, final JSONObject data, boolean add_delete) {
        final LinearLayout parent_lay;
        boolean is_new = false;
        if (parnt_lay != null) {
            parent_lay = parnt_lay;
        } else {
            parent_lay = new LinearLayout(getActivity());
            parent_lay.setOrientation(LinearLayout.VERTICAL);
            is_new = true;
        }


        JSONArray field_array = null;
        for (int k = 0; k < repeater_set.size(); k++) {
            if (repeater_set.get(k).getCondition().getDepend() == null || repeater_set.get(k).getCondition().getDepend().equals("0")) {
                //  create a new layer
                final LinearLayout item_layer = make_itemLayer();
                final Repeaters_Model repeaters_model = new Repeaters_Model();
                repeaters_model.addChilds(repeater_set.get(k).getField_name().toLowerCase());
                repeaters_model.addChilds_data(repeater_set.get(k));
                repeaters_model.setParent_layer(item_layer);

                final LinearLayout button_layer;
                field_array = new JSONArray();
                JSONObject jsonObject = new JSONObject();

                TextView count_tv = getTextView();
                set_background(count_tv, getResColor(R.color.grey), "trans_bg");
                count_tv.setTextColor(color_arry[1]);
                count_tv.setGravity(Gravity.CENTER);
                count_tv.setPadding(getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_8dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_8dp));
//                TextView count_tv = new TextView(getActivity());
                item_layer.addView(count_tv);

                if (is_new) {
                    repeterSet_Array.put(jsonObject);
                    final JSONObject finalJsonObject1 = jsonObject;
                    button_layer = new LinearLayout(getActivity());
                    button_layer.setOrientation(LinearLayout.VERTICAL);
                    TextView add_btn = makeAddBtn();
                    add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add_new_repeated_layer(repeaters_model.getChilds_data(), parent_lay, button_layer, finalJsonObject1, true);
                        }
                    });
                    add_btn.setVisibility(View.VISIBLE);
                    button_layer.addView(add_btn);
                    parent_lay.addView(button_layer);

                    if (repeater_set.get(k).getGrp_heading() != null && !repeater_set.get(k).getGrp_heading().equals("")) {
                        repeaters_model.setLastHeading(repeater_set.get(k).getGrp_heading());
                        addHeading(item_layer, repeaters_model.getLastHeading());
                    }
                } else {
                    button_layer = butn_layer;
                    jsonObject = data;
                    if (add_delete) {
                        ImageView delete_btn = new ImageView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResSize(R.dimen.margin_30dp), getResSize(R.dimen.margin_30dp));
                        params.setMargins(0, getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_12dp), getResSize(R.dimen.margin_2dp));
                        params.gravity = Gravity.RIGHT;
                        delete_btn.setLayoutParams(params);
                        delete_btn.setPadding(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp));
                        delete_btn.setImageResource(R.drawable.delete);
                        item_layer.addView(delete_btn);

                        final JSONObject finalJsonObject = jsonObject;
                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalJsonObject.remove("" + button_layer.getChildCount());
                                button_layer.removeView(item_layer);
                                repeaters_models.remove(repeaters_model);
                            }
                        });
                    }
                    if (repeater_set.get(k).getGrp_heading() != null && !repeater_set.get(k).getGrp_heading().equals("")
                            && !repeaters_model.getLastHeading().equals(repeater_set.get(k).getGrp_heading())) {
                        repeaters_model.setLastHeading(repeater_set.get(k).getGrp_heading());
                        addHeading(item_layer, repeaters_model.getLastHeading());
                    }
                }
                putJson(jsonObject, "" + (button_layer.getChildCount() - 1), field_array);
                button_layer.addView(item_layer, button_layer.getChildCount() - 1);
                count_tv.setText("Parameter " + (button_layer.getChildCount() - 1));
                add_new_field(k, repeater_set.get(k), item_layer, field_array);
                repeaters_models.add(repeaters_model);
            } else {
                //    search parent layer
                int index = find_itemIndex(repeater_set.get(k));
                if (index > -1) {
                    Repeaters_Model repeaters_model = repeaters_models.get(index);
                    final LinearLayout item_layer = repeaters_model.getParent_layer();
                    if (item_layer != null && field_array != null) {
                        if (repeater_set.get(k).getGrp_heading() != null && !repeater_set.get(k).getGrp_heading().equals("")
                                && !repeaters_model.getLastHeading().equals(repeater_set.get(k).getGrp_heading())) {
                            repeaters_model.setLastHeading(repeater_set.get(k).getGrp_heading());
                            addHeading(item_layer, repeaters_model.getLastHeading());
                        }
                        add_new_field(k, repeater_set.get(k), item_layer, field_array);
                    }
                }
            }
        }
        if (is_new)
            dynamic_root.addView(parent_lay);

    }

    private void addHeading(ViewGroup parent, String name) {
        TextView heading_tv = getTextView();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp));
        heading_tv.setLayoutParams(params);
        set_background(heading_tv, color_arry[2], "color_bg");
        heading_tv.setTextColor(color_arry[1]);
//        heading_tv.setTextSize(getResSize(R.dimen.margin_14dp));
        heading_tv.setTypeface(Typeface.DEFAULT_BOLD);
        heading_tv.setPadding(0, getResSize(R.dimen.margin_5dp), 0, getResSize(R.dimen.margin_5dp));
        heading_tv.setGravity(Gravity.CENTER);
        heading_tv.setText(name);
        parent.addView(heading_tv);
    }

    private int find_itemIndex(CustomForm_Model.FieldData fieldData) {
        for (int a = repeaters_models.size() - 1; a >= 0; a--) {
            Repeaters_Model repeaters_model = repeaters_models.get(a);
            if (repeaters_model.getChilds().contains(fieldData.getCondition().getMap_field().toLowerCase())) {
                repeaters_model.addChilds(fieldData.getField_name().toLowerCase());
                repeaters_model.addChilds_data(fieldData);
                return a;
            }
        }
        return -1;
    }


    private TextView makeAddBtn() {
        TextView add_btn = new TextView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = getResSize(R.dimen.margin_10dp);
        params.topMargin = getResSize(R.dimen.margin_10dp);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        add_btn.setLayoutParams(params);
        add_btn.setText(getResString("lbl_repeat"));
//        add_btn.setTextSize(getResSize(R.dimen.margin_10dp));
        add_btn.setBackgroundResource(R.drawable.round_bg);
        set_background(add_btn, color_arry[2], "round_bg");
        add_btn.setTextColor(color_arry[1]);
        add_btn.setPadding(getResSize(R.dimen.margin_15dp), getResSize(R.dimen.margin_2dp), getResSize(R.dimen.margin_15dp), getResSize(R.dimen.margin_2dp));
        add_btn.setLayoutParams(params);
        return add_btn;
    }

    private LinearLayout make_itemLayer() {
        LinearLayout item_layer = new LinearLayout(getActivity());
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_10dp));
        item_layer.setPadding(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp));
        item_layer.setBackgroundResource(R.drawable.border_bg);
        set_background(item_layer, color_arry[2], "border_bg");
        item_layer.setOrientation(LinearLayout.VERTICAL);
        item_layer.setLayoutParams(params);
        return item_layer;
    }

    ArrayList<Signature_objcet> signatures = new ArrayList<>();

    public void add_new_field(final int index, final CustomForm_Model.FieldData field_set, LinearLayout parent_Layout, final JSONArray post_array) {
//        post_array.put("");
        TextView name_tv = getTextView();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_1dp));
        name_tv.setLayoutParams(params);
        name_tv.setTextColor(color_arry[2]);
//        name_tv.setTextSize(getResSize(R.dimen.margin_14dp));
        name_tv.setTypeface(Typeface.DEFAULT_BOLD);
        name_tv.setText(field_set.getField_label());
        parent_Layout.addView(name_tv);
        switch (field_set.getField_type()) {
            case "text":
                EditText text_tv = getEditText(index, field_set, post_array);
                text_tv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text_tv.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(text_tv, color_arry[2], "bottom_line_bg");
                text_tv.setTextColor(color_arry[2]);
                if (submitted_field_names.contains(field_set.getField_name())) {
                    if (data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())) != null)
                        text_tv.setText(data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())).getField_value().get(0));
                }
                parent_Layout.addView(text_tv);
                break;
            case "email":
                EditText text_email = getEditText(index, field_set, post_array);
                text_email.setBackgroundResource(R.drawable.round_bg_white);
                text_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text_email.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(text_email, color_arry[2], "bottom_line_bg");
                text_email.setTextColor(color_arry[2]);
                parent_Layout.addView(text_email);
                break;
            case "number":
                EditText text_number = getEditText(index, field_set, post_array);
                text_number.setBackgroundResource(R.drawable.round_bg_white);
                text_number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text_number.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(text_number, color_arry[2], "bottom_line_bg");
                text_number.setTextColor(color_arry[2]);
                if (submitted_field_names.contains(field_set.getField_name())) {
                    if (data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())) != null)
                        text_number.setText(data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())).getField_value().get(0));
                }
                parent_Layout.addView(text_number);
                break;
            case "textarea":
                EditText text_view = getEditText(index, field_set, post_array);
                text_view.setBackgroundResource(R.drawable.round_bg_white);
                text_view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                text_view.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(text_view, color_arry[2], "bottom_line_bg");
                text_view.setTextColor(color_arry[2]);
                if (submitted_field_names.contains(field_set.getField_name())) {
                    if (data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())) != null)
                        text_view.setText(data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())).getField_value().get(0));
                }
                parent_Layout.addView(text_view);
                break;
            case "date":
                TextView date_btn = getTextView();
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResSize(R.dimen.margin_30dp));
                params1.setMargins(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), 0);
                date_btn.setLayoutParams(params);
                date_btn.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(date_btn, color_arry[2], "bottom_line_bg");
//                date_btn.setGravity(Gravity.CENTER);
                date_btn.setText("Select Date");
                date_btn.setTextColor(getResColor(R.color.grey));
                date_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        show_Date_piker(date_btn);
                    }
                });

                new CustomTextWatcher(date_btn, new CustomTextWatcher.OnTextChange() {
                    @Override
                    public void afterChange(TextView view, String text) {
                        JSONObject data = new JSONObject();
                        putJson(data, "grp_heading", field_set.getGrp_heading());
                        putJson(data, "field_name", field_set.getField_name());
                        putJson(data, "field_label", field_set.getField_label());
                        putJson(data, "field_type", "date");
                        putJson(data, "field_value", "" + text);
                        Log.e("data", " is " + post_array);
                        try {
                            post_array.put(index, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                parent_Layout.addView(date_btn);
                break;
            case "signature":
                View signView = baseActivity.getLayoutInflater().inflate(R.layout.signature_item, null);
                CaptureSignatureView signatureView = new CaptureSignatureView(getActivity());
                LinearLayout signHere = signView.findViewById(R.id.sign_here);
                signHere.setDrawingCacheEnabled(true);
                TextView clear_sign = signView.findViewById(R.id.clear_sign);
                signHere.addView(signatureView);
                clear_sign.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        signatureView.ClearCanvas();
                    }
                });
                JSONObject sign_obj = new JSONObject();
                putJson(sign_obj, "grp_heading", field_set.getGrp_heading());
                putJson(sign_obj, "field_name", field_set.getField_name());
                putJson(sign_obj, "field_label", field_set.getField_label());
                putJson(sign_obj, "field_type", "signature");
//                putJson(data, "field_value", "" + text);
                Log.e("data", " is " + post_array);
                try {
                    post_array.put(index, sign_obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Signature_objcet signature_objcet = new Signature_objcet();
                signature_objcet.setCaptureObject(signatureView);
                signature_objcet.setField_data(sign_obj);
                signature_objcet.setSign_view(signHere);
                signatures.add(signature_objcet);
                parent_Layout.addView(signView);
                break;
            case "file":
                TextView slctfile = getTextView();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResSize(R.dimen.margin_30dp));
                params.setMargins(getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), 0);
                slctfile.setLayoutParams(params);
                slctfile.setBackgroundResource(R.drawable.bg_btm_line);
                set_background(slctfile, color_arry[2], "bottom_line_bg");
                slctfile.setGravity(Gravity.CENTER);
                slctfile.setText(getResString("lbl_select_file"));
                slctfile.setTextColor(getResColor(R.color.grey));
//                layout.addView(slctfile);

                final TextView file_path_tv = getTextView();
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(getResSize(R.dimen.margin_25dp), getResSize(R.dimen.margin_5dp), getResSize(R.dimen.margin_5dp), 0);
                file_path_tv.setLayoutParams(params);
                file_path_tv.setTextColor(color_arry[2]);
                set_background(file_path_tv, color_arry[0], "trans_bg");
                file_path_tv.setVisibility(View.GONE);

                if (submitted_field_names.contains(field_set.getField_name())) {
                    if (data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())) != null)
                        file_path_tv.setText(data_field_sets.get(submitted_field_names.indexOf(field_set.getField_name())).getField_value().get(0));
                }

                slctfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imgcurTime = "" + System.currentTimeMillis();
                        File imageDirectory = new File(directory);
                        imageDirectory.mkdirs();
                        file_path_tvs = file_path_tv;
                        img_path = directory + imgcurTime + ".jpg";
                        startCamera(10, imgcurTime + ".jpg");
                    }
                });

                file_path_tv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String path = s.toString();
                        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                        String type = null;
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        JSONObject data = new JSONObject();
                        putJson(data, "grp_heading", field_set.getGrp_heading());
                        putJson(data, "field_name", field_set.getField_name());
                        putJson(data, "field_label", field_set.getField_label());
                        putJson(data, "field_type", "file");
                        putJson(data, "field_value", "data:" + type + ";base64," + AppUtils.Image_toBase64(path));
                        Log.e("data", " is " + post_array);
                        try {
                            post_array.put(index, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                parent_Layout.addView(slctfile);
                parent_Layout.addView(file_path_tv);
                break;
            case "select":
                final MultiSpinner spinner = getSpinner();
                final ArrayList entries = field_set.getEnum_list();
                List last_selected = new ArrayList();
                if (submitted_field_names.contains(field_set.getField_name())) {
                    int pos = submitted_field_names.indexOf(field_set.getField_name());
                    if (data_field_sets.get(pos) != null && data_field_sets.get(pos).getField_value() != null)
                        last_selected = data_field_sets.get(pos).getField_value();
                }
                spinner.setTag(entries);
//                spiners_map.put(field_set.getField_name(), spinner);
//                final List finallast_selected = last_selected;
//                if (field_set.getCondition().getMap_field() != null && !field_set.getCondition().getMap_field().equals("")) {
//                    Log.e("field is ", " map field " + index);
//                    MultiSpinner old_spnr = spiners_map.get(field_set.getCondition().getMap_field());
//                    old_spnr.setSingle_select(true);
//                    old_spnr.setChildSpinner(spinner);
//                }

                if (field_set.getCondition().getMultiselect() != null && field_set.getCondition().getMultiselect().equals("1")) {
                    spinner.setSingle_select(false);
                } else {
                    spinner.setSingle_select(true);
                }

//                Log.e("last selected ", " is " + last_selected);

                spinner.setItems(entries, last_selected, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void onItemsSelected(Boolean[] selected) {
                        ArrayList data_items = (ArrayList) spinner.getTag();
                        int position = ArrayUtils.toArrayList(selected).indexOf(true);
                        if (position > -1) {
//                            if (spinner.getChildSpinner() != null) {
//                                final MultiSpinner child_spiner = spinner.getChildSpinner();
//                                CustomForm_Model.data data = (CustomForm_Model.data) data_items.get(position);
//                                get_SelectedData(data.getHf_id(), new Handler() {
//                                    @Override
//                                    public void handleMessage(Message msg) {
//                                        if (msg.obj != null && !(msg.obj.toString()).equals("error")) {
//                                            try {
//                                                JSONObject jsonObject = new JSONObject(msg.obj.toString());
//                                                final ArrayList<CustomForm_Model.data> new_data = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), CustomForm_Model.data[].class)));
//                                                child_spiner.setTag(new_data);
//                                                child_spiner.setItems(new_data, finallast_selected, getResString("lbl_pl_slct_msg"), child_spiner.getListener());
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                });
//                            }
                            spiner_Onselect(index, field_set, data_items, selected, post_array);
                        }
                    }
                });
                parent_Layout.addView(spinner);
                if (last_selected.size() > 0) {
                    spinner.setSelected_Text();
                }
                break;
        }
    }


    String img_path = "";
    TextView file_path_tvs;


    public void spiner_Onselect(int index, CustomForm_Model.FieldData field_set, ArrayList entries, Boolean[] selected, JSONArray post_array) {
        JSONObject data = new JSONObject();
        putJson(data, "grp_heading", field_set.getGrp_heading());
        putJson(data, "field_name", field_set.getField_name());
        putJson(data, "field_type", "select");
        putJson(data, "field_label", field_set.getField_label());
        if (selected.length > 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i])
                    jsonArray.put(entries.get(i).toString());
            }
            putJson(data, "field_value", jsonArray);
        } else {
            putJson(data, "field_value", new JSONArray());
        }

        try {
            post_array.put(index, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startCamera(int request_code, String name) {
        if (!baseActivity.isStoragePermissionGranted())
            return;
        Intent camera = new Intent(getActivity(), CameraActivity.class);
        camera.putExtra("path", directory);
        camera.putExtra("name", name);
        startActivityForResult(camera, request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            if (requestCode == requestcode) {
//                  load_image_file(img_path,profile_img);
//            } else
            if (requestCode == 10) {
                Uri selectedImage = data.getData();
//                profile_img.setImageURI(selectedImage);
//                profile_img.setVisibility(ShowcaseView.VISIBLE);
                img_path = AppUtils.getRealPathFromURI(getActivity(), selectedImage);
            }
            TextView textView = file_path_tvs;
            textView.setText(img_path);
            textView.setVisibility(View.VISIBLE);
//            file_paths.set(requestcode, img_path);
            img_path = "";
        }
    }


    private TextView getTextView() {
        TextView text_tv = new TextView(getActivity());
        text_tv.setLayoutParams(params);
        text_tv.setSingleLine(false);
        text_tv.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        return text_tv;
    }

    private EditText getEditText(final int i, final CustomForm_Model.FieldData field_set, final JSONArray post_array) {
        final EditText editText = new EditText(getActivity());
        editText.setLayoutParams(params);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        editText.setPadding(getResSize(R.dimen.margin_3dp), getResSize(R.dimen.margin_1dp), getResSize(R.dimen.margin_3dp), getResSize(R.dimen.margin_1dp));
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                JSONObject data = new JSONObject();
                putJson(data, "grp_heading", field_set.getGrp_heading());
                putJson(data, "field_name", field_set.getField_name());
                putJson(data, "field_label", field_set.getField_label());
                putJson(data, "field_type", "text");
                putJson(data, "field_value", s.toString());
                try {
                    post_array.put(i, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return editText;
    }

    private MultiSpinner getSpinner() {
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResSize(R.dimen.margin_10dp), 0, getResSize(R.dimen.margin_10dp), getResSize(R.dimen.margin_5dp));
        MultiSpinner spinner = new MultiSpinner(getActivity());
        spinner.setMinimumHeight(getResSize(R.dimen.margin_35dp));
        spinner.setLayoutParams(params);
        spinner.setBackgroundResource(R.drawable.btm_line_spinr);
        return spinner;
    }

    String sign_path = directory + "RAMS/";

    public void saveSignature() {
        for (int j = 0; j < signatures.size(); j++) {
            Signature_objcet signature_objcet = signatures.get(j);
            if (!signature_objcet.getField_data().has("field_value")) {
                File file = new File(sign_path);
                if (!file.exists()) {
                    file.mkdir();
                }
                String file_path = sign_path + System.currentTimeMillis() + "sign.jpg";
                file = new File(file_path);
                Bitmap b = Bitmap.createBitmap(signature_objcet.getSign_view().getDrawingCache());
                OutputStream stream = null;
                if (signature_objcet.getCaptureObject().drawing) {
                    try {
                        stream = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    b.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                }
                if (file.exists())
                    putJson(signature_objcet.getField_data(), "field_value", "data:image/jpeg;base64," + AppUtils.Image_toBase64(file_path));
            }
        }
    }

    public void putJson(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeNull(JSONArray jsonArray, boolean is_repeated) throws JSONException {
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            if (jsonArray.isNull(i)) {
                jsonArray.remove(i);
            } else if (is_repeated) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    JSONArray array = jsonObject.getJSONArray((String) keys.next());
                    for (int j = array.length() - 1; j >= 0; j--) {
                        if (array.isNull(j)) {
                            array.remove(j);
                        }
                    }
                }
            }
        }
    }
}

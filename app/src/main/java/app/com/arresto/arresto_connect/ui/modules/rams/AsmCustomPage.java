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

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.rams;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.post_custom_form;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;

public class AsmCustomPage extends Customform_Base {

    View view;
    TextView continue_btn;
    int form_index, continue_status;
    //    JSONArray post_array;
    boolean created;

    public static AsmCustomPage newInstance(int form_id, int data_index, int continue_btn) {
        AsmCustomPage asmCustomPage = new AsmCustomPage();
        Bundle args = new Bundle();
        args.putInt("form_index", form_id);
        args.putInt("data_index", data_index);
        args.putInt("continue_btn", continue_btn);
        asmCustomPage.setArguments(args);
        return asmCustomPage;
    }

    public static AsmCustomPage newInstance(CustomForm_Model Custom_form, CustomForm_Model form_data, String update_id) {
        AsmCustomPage asmCustomPage = new AsmCustomPage();
        Bundle args = new Bundle();
        args.putString("Custom_form", AppUtils.getGson().toJson(Custom_form));
        args.putString("update_id", update_id);
        if (form_data != null)
            args.putString("form_data", AppUtils.getGson().toJson(form_data));
        asmCustomPage.setArguments(args);
        return asmCustomPage;
    }

    CustomForm_Model Custom_form, form_data;
    String update_id = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.asm_custom_page, parent, false);
            setupUI(view, getActivity());
            continue_btn = view.findViewById(R.id.continue_btn);
            if (getArguments() != null) {
                form_index = getArguments().getInt("form_index", -1);
                data_index = getArguments().getInt("data_index", -1);
                continue_status = getArguments().getInt("continue_btn", -1);
                if (form_index < 0) {
                    Custom_form = AppUtils.getGson().fromJson(getArguments().getString("Custom_form", ""), CustomForm_Model.class);
                    update_id = getArguments().getString("update_id", "");
                    form_data = AppUtils.getGson().fromJson(getArguments().getString("form_data", ""), CustomForm_Model.class);
                    if (!update_id.equals("")) {
                        continue_btn.setText("Update");
                    }
                }
            }
            Log.e("data index is", " " + data_index);
            continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (singleSet_array != null)
                            removeNull(singleSet_array, false);
                        if (repeterSet_Array != null)
                            removeNull(repeterSet_Array, true);
                        JSONObject post_field = new JSONObject();
                        post_field.put("single_set", singleSet_array);
//                        repeterSet_Array.put()
//                        post_field.put("repeaters", new Gson().toJson(temp_Array,ArrayList.class));
                        post_field.put("repeaters", repeterSet_Array);
                        JSONObject params = new JSONObject();
                        params.put("client_id", client_id);
                        params.put("user_id", Static_values.user_id);
                        params.put("form_id", form_id);
                        params.put("project_id", slctd_project.getUp_id());
                        params.put("field_set", post_field);
                        if (!update_id.equals(""))
                            params.put("cf_id", update_id);
                        saveSignature();
                        Log.e("field set ", " is " + params);
                        post_data(params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!created) {
            if (form_index < 0)
                setForm(Custom_form, form_data);
            else
                setForm(form_index);
            created = true;
        }
    }

    public void post_data(JSONObject params) {

//        for (int i = 0; i < post_data.length(); i++) {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject = post_data.getJSONObject(i);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Iterator<String> iter = jsonObject.keys();
//            while (iter.hasNext()) {
//                String key = iter.next();
//                try {
//                    if (jsonObject.get(key) == null || jsonObject.get(key).equals("")) {
//                        AppUtils.show_snak(getActivity(), "Please Fill All Fields!");
//                        return;
//                    }
//                } catch (JSONException e) {
//                    // Something went wrong!
//                }
//            }
//        }

        Log.e("field set ", " is " + params);
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(post_custom_form, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            update_formEvent.OnUpdate();
                            getActivity().onBackPressed();
//                                    if (page_id >= (DataHolder_Model.getInstance().getCustomViews_models().size() - 1))
//                                        LoadFragment.replace(new Main_Fragment(), (AppCompatActivity) getActivity(), "" + AsmCustomPage.this.getTag());
//                                    else
//                                        LoadFragment.replace(AsmCustomPage.newInstance(page_id + 1), (AppCompatActivity) getActivity(), "" + slctd_project.getUp_project_name());
                        } else {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
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

    Update_formEvent update_formEvent;

    public void setUpdate_Event(Update_formEvent update_event) {
        this.update_formEvent = update_event;
    }

    public interface Update_formEvent {
        void OnUpdate();
    }

}

package app.com.arresto.arresto_connect.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.GetVersionCode;
import app.com.arresto.arresto_connect.constants.PrefernceConstants;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

import static app.com.arresto.Flavor_Id.FlavourInfo.date_format;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class Setting_Fragment extends Base_Fragment implements View.OnClickListener {

    View view;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting, parent, false);
            findAllIds(view);
            set_updata();
        }
        return view;
    }

    Profile_Model profile_model;

    private void set_updata() {
        profile_model = Profile_Model.getInstance();
        usr_name.setText(profile_model.getUpro_first_name() + " " + profile_model.getUpro_last_name());
        email_tv.setText(Static_values.user_email);
        company_tv.setText(profile_model.getUpro_company());
        AppUtils.load_profile(profile_model.getUpro_image(), profil_pic);
        try {
            PackageInfo pInfo = baseActivity.getPackageManager().getPackageInfo(baseActivity.getPackageName(), 0);
            update_btn.setText(update_btn.getText()+" ("+pInfo.versionName+")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        change_password_btn.setOnClickListener(this);
        change_language_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        feedback_btn.setOnClickListener(this);
        date_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
//      chng_lang.setText(Static_values.current_language);
    }

    private ImageView profil_pic;
    private TextView usr_name, email_tv, company_tv, change_password_btn, change_language_btn, update_btn, feedback_btn, date_btn;
    MaterialButton logout_btn;

    public void findAllIds(View view) {
        profil_pic = view.findViewById(R.id.profil_pic);
        usr_name = view.findViewById(R.id.usr_name);
        email_tv = view.findViewById(R.id.email_tv);
        company_tv = view.findViewById(R.id.company_tv);
        change_password_btn = view.findViewById(R.id.change_password_btn);
        change_language_btn = view.findViewById(R.id.change_language_btn);
        update_btn = view.findViewById(R.id.update_btn);
        feedback_btn = view.findViewById(R.id.feedback_btn);
        date_btn = view.findViewById(R.id.date_btn);
        logout_btn = view.findViewById(R.id.logout_btn);
    }

    public void showChange_password() {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_change_password);

        final EditText oldpass_edit = dialog.findViewById(R.id.oldpass_edit);
        final EditText pass_edit = dialog.findViewById(R.id.pass_edit);
        final EditText cnf_pass_edit = dialog.findViewById(R.id.cnf_pass_edit);
        MaterialButton update_btn = dialog.findViewById(R.id.update_btn);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pass_edit.getText().toString().equals("") && pass_edit.getText().toString().equals(cnf_pass_edit.getText().toString())) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("current_pass", oldpass_edit.getText().toString());
                    params.put("new_pass", pass_edit.getText().toString());
                    params.put("user_id", Static_values.user_id);
                    update_pass(dialog, params);
                } else {
                    Toast.makeText(getActivity(), getResString("lbl_match_pass"), Toast.LENGTH_LONG).show();
                }
            }
        });
//        cncl_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        dialog.show();

    }

    public void update_pass(final Dialog dialog, HashMap<String, String> params) {
        NetworkRequest network_request = new NetworkRequest(baseActivity);
        network_request.make_post_request(All_Api.chage_password, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                        if (msg_code.equals("200")) {
                            dialog.dismiss();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_password_btn:
                showChange_password();
                break;
            case R.id.change_language_btn:
                AppUtils.Change_language(baseActivity);
                break;
            case R.id.feedback_btn:
//                LoadFragment.replace(new FeedBack_Fragment(), getActivity(), "Feedback");
                FeedBack_Fragment feedBack_fragment = new FeedBack_Fragment(this);
                feedBack_fragment.show();
                break;
            case R.id.date_btn:
                format_dialog();
                break;
            case R.id.logout_btn:
                HomeActivity.homeActivity.submit_action("logout");
                break;
            case R.id.update_btn:
                try {
                    PackageManager manager = getActivity().getPackageManager();
                    PackageInfo pInfo = manager.getPackageInfo(getActivity().getPackageName(), 0);
                    new GetVersionCode(getActivity(), pInfo.versionName, getActivity().getPackageName(), true).execute();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    List<String> formats = new ArrayList<>(Arrays.asList("dd MMM, yyyy hh:mm a", "MMM dd, yyyy hh:mm a", "yyyy, MMM dd hh:mm a"));

    private void format_dialog() {
        new AlertDialog.Builder(baseActivity)
                .setSingleChoiceItems(R.array.date_formats, formats.indexOf(date_format), null)
                .setPositiveButton(getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int index = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        baseActivity.format = formats.get(index);
                        Static_values.mPrefrence.saveData(PrefernceConstants.DATE_FORMAT, date_format);
                        // Do something useful withe the position of the selected radio button
                        dialog.dismiss();
                    }
                })
                .show();
    }
}

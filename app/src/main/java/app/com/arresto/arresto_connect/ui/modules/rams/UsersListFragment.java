package app.com.arresto.arresto_connect.ui.modules.rams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Project_UsersAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;

public class UsersListFragment extends Base_Fragment implements View.OnClickListener {
    View view;
    Handler handler;
    TextView submit_btn, eod_btn, prodcts_btn;
    RecyclerView users_list;

    LinearLayout btn_section;
    TextView up_btn, down_btn;

    public static UsersListFragment newInstance(String page, boolean any_checkin) {
        UsersListFragment fragmentFirst = new UsersListFragment();
        Bundle args = new Bundle();
        args.putString("type", page);
        args.putBoolean("any_checkin", any_checkin);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private String type;
    boolean any_checkin;
    Project_Model projectModel;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_list_fragment, container, false);
        projectModel = slctd_project;
        if (getArguments() != null) {
            type = getArguments().getString("type");
            any_checkin = getArguments().getBoolean("any_checkin");
        }
        handler = new Handler();
        btn_section = view.findViewById(R.id.btn_section);
        up_btn = view.findViewById(R.id.up_btn);
        down_btn = view.findViewById(R.id.down_btn);
        eod_btn = view.findViewById(R.id.eod_btn);
        submit_btn = view.findViewById(R.id.submit_btn);
        users_list = view.findViewById(R.id.users_list);
        prodcts_btn = view.findViewById(R.id.prodcts_btn);
        submit_btn.setVisibility(View.VISIBLE);
        eod_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        up_btn.setOnClickListener(this);
        down_btn.setOnClickListener(this);
        if (projectModel.getTeam() != null && projectModel.getTeam().size() > 0)
            setdata();
        return view;
    }

    Project_UsersAdapter adapter;

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        users_list.setLayoutManager(layoutManager);
        users_list.getLayoutManager().setMeasurementCacheEnabled(false);
        adapter = new Project_UsersAdapter(baseActivity, projectModel, type);
        users_list.setAdapter(adapter);
        if (type.equals("rope")) {
            submit_btn.setText(getResString("lbl_update_time"));
            if (!baseActivity.isEOD(projectModel)) {
                eod_btn.setVisibility(View.VISIBLE);
            } else {
                eod_btn.setVisibility(View.GONE);
            }
            btn_section.setVisibility(View.VISIBLE);
            prodcts_btn.setVisibility(View.GONE);
        } else {
            prodcts_btn.setVisibility(View.VISIBLE);
            prodcts_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadFragment.replace(ProjectProductPreview.newInstance(projectModel.getUp_id()), baseActivity, "Products in project");
                }
            });
        }
    }

    public AlertDialog show_alert() {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Alert!")
                .setMessage("If you end of the day once after that you can't Rope Up or Down any user for today. Do you wish to continue!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
//                        new MyAsyncTask().execute();
                    }
                })
                .setPositiveButton(getResString("lbl_cntnue_st"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        save_TrainingStatus();

//                        LoadFragment.replace(UserRopePreview.newInstance("update", ""), baseActivity, "Update time");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eod_btn:
                if (type.equals("rope")) {
                    if (any_checkin)
                        show_snak(getActivity(), getResString("lbl_chk_out_msg"));
                    else if (!isAnyRopeUp())
                        show_alert();
                    else
                        AppUtils.show_snak(baseActivity, getResString("lbl_ropedown"));
                }
                break;
            case R.id.submit_btn:
                if (type.equals("rope")) {
                    if (!isAnyRopeUp())
                        LoadFragment.replace(UserRopePreview.newInstance("update", ""), baseActivity, getResString("lbl_update_time"));
                    else
                        AppUtils.show_snak(baseActivity, getResString("lbl_ropedown"));
                } else save_Attandance();
                break;
            case R.id.up_btn:
                if (!baseActivity.isEOD(projectModel)) {
                    adapter.updateStatus("allUp");
                }
                break;
            case R.id.down_btn:
                if (!baseActivity.isEOD(projectModel)) {
                    adapter.updateStatus("allDown");
                }
                break;
        }
    }

    public boolean isAnyRopeUp() {
        for (GroupUsers user : projectModel.getTeam()) {
            if (user.getRope_status().getRt_rope_status() != null && user.getRope_status().getRt_rope_status().equals("up"))
                return true;

        }
        return false;
    }

    public void save_Attandance() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("client_id", client_id);
            jsonData.put("project_id", projectModel.getUp_id());
            jsonData.put("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            jsonData.put("team", getAttendance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest(getActivity()).make_contentpost_request(All_Api.post_attendance, jsonData, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is  " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        onUpdateResult(true);
                        projectModel.setAttendance_marked("1");
                    } else {
                        onUpdateResult(false);
                        Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("social login ", " error is  " + message);
                onUpdateResult(false);
            }
        });
    }

    public void save_TrainingStatus() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("client_id", client_id);
            jsonData.put("project_id", projectModel.getUp_id());
            jsonData.put("training_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            jsonData.put("status", "EOD");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest(getActivity()).make_contentpost_request(All_Api.training_status, jsonData, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is  " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        projectModel.setTraining_status("EOD");
                        submit_btn.setVisibility(View.GONE);
                        baseActivity.onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("social login ", " error is  " + message);
                onUpdateResult(false);
            }
        });
    }

    private JSONArray getAttendance() {
        JSONArray jsonArray = new JSONArray();
        for (GroupUsers groupUsers : projectModel.getTeam()) {
            JSONObject atd = new JSONObject();
            try {
                atd.put("user_id", groupUsers.getUacc_id());
                if (groupUsers.is_Attendance())
                    atd.put("attendance", "1");
                else
                    atd.put("attendance", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(atd);
        }
        return jsonArray;
    }

    public void onUpdateResult(Boolean result) {
        if (result) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Project_AssetsPage mainFragment = new Project_AssetsPage();
                    Bundle bundle = new Bundle();
                    bundle.putString("page_type", "project_data");
                    mainFragment.setArguments(bundle);
                    baseActivity.getSupportFragmentManager().popBackStackImmediate();
                    LoadFragment.replace(mainFragment, baseActivity, "Project data");
                }
            }, 1000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppUtils.show_snak(getActivity(), getResString("lbl_something_wrong"));
                }
            }, 1000);
        }

    }

}
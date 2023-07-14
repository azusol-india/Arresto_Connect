package app.com.arresto.arresto_connect.ui.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.switch_lib.IconSwitch;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.data.models.Rope_Status;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.ToggleButton;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static app.com.arresto.arresto_connect.constants.AppUtils.StringTimeToMilli;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.set_Timer;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.update_rope_status;

public class Project_UsersAdapter extends RecyclerView.Adapter<Project_UsersAdapter.ViewHolder> {
    private BaseActivity context;
    private ArrayList team;
    public String type, project_id, team_id;

    String isAllUp;
    Project_Model projectModel;

    public Project_UsersAdapter(BaseActivity baseActivity, Project_Model projectModel, String type) {
        context = baseActivity;
        this.projectModel = projectModel;
        this.team = projectModel.getTeam();
        this.project_id = projectModel.getUp_id();
        this.team_id = projectModel.getTeam_id();
        this.type = type;
    }

    public void updateStatus(String allUp) {
        this.isAllUp = allUp;
        if (type.equals("rope")) {
            JSONArray teamPost = new JSONArray();
            try {
                for (Object o : team) {
                    GroupUsers user = (GroupUsers) o;
                    if (allUp.equals("allUp")) {
                        if ((user.getRope_status().getRt_rope_status() == null || user.getRope_status().getRt_rope_status().equals("down")) && !user.getAttendance().equals("0")) {
                            user.setAlready_up(true);
                            user.setAlready_down(false);
                            user.getRope_status().setRt_rope_status("up");
                            user.getRope_status().setRt_rope_uptime("" + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
                            JSONObject object = new JSONObject();
                            object.put("user_id", user.getUacc_id());
                            object.put("status", "up");
                            object.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                            teamPost.put(object);
                        }
                    } else if (allUp.equals("allDown")) {
                        if ((user.getRope_status().getRt_rope_status() == null || user.getRope_status().getRt_rope_status().equals("up")) && !user.getAttendance().equals("0")) {
                            user.setTimeWhenStopped(0);
                            user.setAlready_up(false);
                            user.setAlready_down(true);
                            user.getRope_status().setRt_rope_status("down");
                            JSONObject object = new JSONObject();
                            object.put("user_id", user.getUacc_id());
                            object.put("status", "down");
                            object.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                            teamPost.put(object);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (teamPost.length() > 0)
                update_rope_status(teamPost);
        }
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (team != null)
            return team.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profil_pic;
        TextView name_text, rp_tv, email_text, phone_text;
        ToggleButton atd_btn;
        Chronometer timer_ctv;
        IconSwitch iconSwitch;

        ViewHolder(View v) {
            super(v);
            profil_pic = v.findViewById(R.id.profil_img);
            name_text = v.findViewById(R.id.name_text);
            email_text = v.findViewById(R.id.email_text);
            phone_text = v.findViewById(R.id.phone_text);
            atd_btn = v.findViewById(R.id.atd_btn);
            timer_ctv = v.findViewById(R.id.timer_ctv);
            rp_tv = v.findViewById(R.id.rp_tv);
            iconSwitch = v.findViewById(R.id.icon_switch);
            if (type.equals("rope")) {
                timer_ctv.setVisibility(View.VISIBLE);
                iconSwitch.setVisibility(View.VISIBLE);
                rp_tv.setVisibility(View.VISIBLE);
                atd_btn.setVisibility(View.GONE);
                phone_text.setVisibility(View.GONE);
            } else {
                timer_ctv.setVisibility(View.GONE);
                iconSwitch.setVisibility(View.GONE);
                rp_tv.setVisibility(View.GONE);
                atd_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    //---returns an ImageView view---
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        GroupUsers user = (GroupUsers) team.get(position);
        AppUtils.load_profile(user.getUpro_image(), holder.profil_pic);
        holder.name_text.setText("Name: " + user.getUacc_username());
        holder.email_text.setText("Email: " + user.getUacc_email());
        holder.phone_text.setText("Phone: " + user.getUpro_phone());
        if (type.equals("rope")) {
            setTimer(holder.iconSwitch, holder.timer_ctv, user);
            holder.iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
                @Override
                public void onCheckChanged(IconSwitch.Checked current) {
                    updateColors(holder.iconSwitch, holder.timer_ctv, holder.rp_tv, user);
                }
            });
        } else {
            holder.atd_btn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    if (on)
                        user.setAttendance("1");
                    else
                        user.setAttendance("0");
                    user.setIs_Attendance(on);
                }
            });
        }
    }

    private void updateColors(IconSwitch iconSwitch, Chronometer timer_ctv, TextView rp_tv, GroupUsers groupUsers) {
        switch (iconSwitch.getChecked()) {
            case LEFT:
                rp_tv.setText(getResString("lbl_rop_dwn_st"));
                timer_ctv.stop();
                groupUsers.setTimeWhenStopped(timer_ctv.getBase() - SystemClock.elapsedRealtime());
                if (groupUsers.isAlready_down()) {
                    groupUsers.setAlready_down(!groupUsers.isAlready_down());
                } else {
                    if (!context.isEOD(projectModel)) {
                        groupUsers.getRope_status().setRt_rope_status("down");
                        JSONArray teamPost = new JSONArray();
                        try {
                            JSONObject object = new JSONObject();
                            object.put("user_id", groupUsers.getUacc_id());
                            object.put("status", "down");
                            object.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                            teamPost.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        update_rope_status(teamPost);
                    } else {
                        groupUsers.setAlready_down(true);
                        iconSwitch.setChecked(IconSwitch.Checked.LEFT);
                        show_alert(iconSwitch, groupUsers);
                    }
                }
                break;
            case RIGHT:
                rp_tv.setText(getResString("lbl_rop_up_st"));
                set_Timer(timer_ctv, groupUsers.getTimeWhenStopped());
                if (groupUsers.isAlready_up()) {
                    groupUsers.setAlready_up(!groupUsers.isAlready_up());
                } else {
                    if (!groupUsers.getAttendance().equals("0") && !context.isEOD(projectModel)) {
                        groupUsers.getRope_status().setRt_rope_status("up");
                        groupUsers.getRope_status().setRt_rope_uptime("" + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
                        JSONArray teamPost1 = new JSONArray();
                        try {
                            JSONObject object = new JSONObject();
                            object.put("user_id", groupUsers.getUacc_id());
                            object.put("status", "up");
                            object.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                            teamPost1.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        update_rope_status(teamPost1);
                    } else {
                        groupUsers.setAlready_down(true);
                        iconSwitch.setChecked(IconSwitch.Checked.LEFT);
                        show_alert(iconSwitch, groupUsers);
                    }
                }
                break;
        }
    }

    public void setTimer(IconSwitch iconSwitch, Chronometer timer_ctv, GroupUsers groupUsers) {
        final Rope_Status rope_status = groupUsers.getRope_status();
        Static_values.report_no = "";
        if (rope_status != null && rope_status.getRt_rope_status() != null && rope_status.getRt_rope_status().equals("up")) {
            try {
                groupUsers.setTimeWhenStopped(-(StringTimeToMilli(rope_status.getRt_rope_uptime())));
            } catch (Exception e) {
                groupUsers.setTimeWhenStopped(-0);
            }
            timer_ctv.setBase(SystemClock.elapsedRealtime() + groupUsers.getTimeWhenStopped());
            Static_values.report_no = rope_status.getRt_report_no();
            iconSwitch.post(new Runnable() {
                @Override
                public void run() {
//                    if (rope_status.getRt_rope_status().equals("up")) {
                    groupUsers.setAlready_up(true);
                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
//                    }
                }
            });
        } else {
            groupUsers.setTimeWhenStopped(0);
            timer_ctv.setBase(SystemClock.elapsedRealtime() + groupUsers.getTimeWhenStopped());
            iconSwitch.setChecked(IconSwitch.Checked.LEFT);
        }
    }

    public void update_rope_status(JSONArray team) {
        if (isNetworkAvailable(context)) {
            JSONObject params = new JSONObject();
            if (!project_id.equals("")) {
                try {
                    params.put("team_id", team_id);
                    params.put("client_id", client_id);
                    params.put("project_id", project_id);
                    params.put("user_id", user_id);
                    params.put("report_no", Static_values.report_no);
                    params.put("team", team);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            NetworkRequest networkRequest = new NetworkRequest(context);
            networkRequest.make_contentpost_request(update_rope_status, params, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                Static_values.report_no = jsonObject.getString("report_no");
                            } else {
                                show_snak(context, jsonObject.getString("message"));
                            }
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
            show_snak(context, getResString("lbl_network_alert"));
        }
    }

    public AlertDialog show_alert(IconSwitch iconSwitch, GroupUsers user) {
        return new AlertDialog.Builder(context)
                .setTitle(getResString("lbl_attendance"))
                .setMessage(getResString("lbl_member_present"))
                .setNegativeButton(getResString("lbl_no"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getResString("lbl_present"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveSingle_Attandance(iconSwitch, user);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void saveSingle_Attandance(IconSwitch iconSwitch, GroupUsers user) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("client_id", client_id);
            jsonData.put("project_id", project_id);
            jsonData.put("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            JSONArray jsonArray = new JSONArray();
            JSONObject atd = new JSONObject();
            atd.put("user_id", user.getUacc_id());
            atd.put("attendance", "1");
            jsonArray.put(atd);
            jsonData.put("team", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest(context).make_contentpost_request(All_Api.post_attendance, jsonData, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is  " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        user.setAttendance("1");
                        iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
                    } else {
                        Toast.makeText(context, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("social login ", " error is  " + message);
//                Toast.makeText(context, message), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
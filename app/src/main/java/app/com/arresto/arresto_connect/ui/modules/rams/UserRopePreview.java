package app.com.arresto.arresto_connect.ui.modules.rams;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.data.models.RopePreviewModel;
import app.com.arresto.arresto_connect.data.models.Rope_Status;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.asm_update_ropetime;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;

public class UserRopePreview extends Base_Fragment implements View.OnClickListener {
    View view;
    Handler handler;
    TextView submit_btn;
    RecyclerView users_list;

    public static UserRopePreview newInstance(String page, String date) {
        UserRopePreview fragmentFirst = new UserRopePreview();
        Bundle args = new Bundle();
        args.putString("type", page);
        args.putString("date", date);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private String type, date;
    Project_Model projectModel;
    public SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_list_fragment, container, false);
        projectModel = slctd_project;
        if (getArguments() != null) {
            type = getArguments().getString("type");
            date = getArguments().getString("date", "");
        }
        handler = new Handler();
        users_list = view.findViewById(R.id.users_list);
        submit_btn = view.findViewById(R.id.submit_btn);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        users_list.setLayoutManager(layoutManager);
        users_list.getLayoutManager().setMeasurementCacheEnabled(false);
        if (type.equals("update")) {
            submit_btn.setText("Update");
            submit_btn.setVisibility(View.VISIBLE);
            submit_btn.setOnClickListener(this);
            getRopeStatus(All_Api.getTodayRopeHistory + client_id + "&project_id=" + slctd_project.getUp_id() + "&date=" + date_format.format(new Date()));
        } else {
            submit_btn.setVisibility(View.GONE);
            try {
                if (!date.equals(""))
                    getRopeStatus(All_Api.getTodayRopeHistory + client_id + "&project_id=" + slctd_project.getUp_id() + "&date=" + date_format.format(baseActivity.server_date_format.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    TimeAdapter adapter;

    private void setdata(ArrayList<RopePreviewModel> previewModels) {
        adapter = new TimeAdapter(getActivity(), previewModels);
        users_list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                update_rope_time();
                break;
        }
    }

    ArrayList<RopePreviewModel> previewModels;

    private void getRopeStatus(String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status_code").equals("200")) {
                            previewModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), RopePreviewModel[].class)));
                            setdata(previewModels);
                        } else {
                            show_snak(getActivity(), jsonObject.getString("message"));
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
    }


    public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
        private Activity context;
        private ArrayList<RopePreviewModel> previewModels;

        public TimeAdapter(Activity c, ArrayList<RopePreviewModel> previewModels) {
            context = c;
            this.previewModels = previewModels;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (previewModels != null)
                return previewModels.size();
            else
                return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView profil_pic;
            TextView name_text;
            EditText time_tv;

            ViewHolder(View v) {
                super(v);
                profil_pic = v.findViewById(R.id.profil_pic);
                name_text = v.findViewById(R.id.name_text);
                time_tv = v.findViewById(R.id.time_tv);
            }
        }

        //---returns an ImageView view---
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_time_layout, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            RopePreviewModel data = previewModels.get(position);
            AppUtils.load_profile(data.getUserImage(), holder.profil_pic);
            holder.name_text.setText("Name: " + data.getUserName());
            long totalTime = 0;
            if (!data.getTotal_manual_time().equals("")) {
                totalTime = Long.parseLong(data.getTotal_manual_time());
            } else {
                totalTime = calculate_time(data.getRope_history());
                data.setTotal_time("" + totalTime);
            }
            long hours = totalTime / 3600;
            long minutes = (totalTime % 3600) / 60;
//            int seconds = totalTime % 60;
            holder.time_tv.setText(String.format("%02d:%02d", hours, minutes));
            if (type.equals("update")) {
                DateUtils.formatElapsedTime(totalTime);
                holder.time_tv.addTextChangedListener(new NumberTextWatcher(holder.time_tv, data));
            } else {
                holder.time_tv.setEnabled(false);
            }
        }

        private long calculate_time(ArrayList<Rope_Status> rope_history) {
            long totalTodayTime = 0;
            for (Rope_Status rope_status : rope_history) {
                if (rope_status.getRt_rope_status().equals("down")) {
                    if (rope_status.getRt_rope_duration_manual() != null && !rope_status.getRt_rope_duration_manual().equals("")) {
                        totalTodayTime = totalTodayTime + Long.parseLong(rope_status.getRt_rope_duration_manual());
                    } else if (rope_status.getTotal_rope_duraion() != null && !rope_status.getTotal_rope_duraion().equals(""))
                        totalTodayTime = totalTodayTime + Long.parseLong(rope_status.getTotal_rope_duraion());
                }
            }
            return totalTodayTime;
        }

    }

    public class NumberTextWatcher implements TextWatcher {

        private String current = "";
        private String ddmmyyyy = "HHMM";
        EditText editText;
        RopePreviewModel previewModel;

        public NumberTextWatcher(EditText txt, RopePreviewModel data) {
            editText = txt;
            previewModel = data;
        }

        public void afterTextChanged(Editable s) {
            if (previewModel != null) {
                previewModel.setEdited(true);
                previewModel.setEdited_time(s.toString());
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 4; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 4) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    int hours = Integer.parseInt(clean.substring(0, 2));
                    int min = Integer.parseInt(clean.substring(2, 4));
                    clean = String.format("%02d%02d", hours, min);
                }

                clean = String.format("%s:%s", clean.substring(0, 2), clean.substring(2, 4));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                editText.setText(current);
                editText.setSelection(sel < current.length() ? sel : current.length());
            }
        }

    }

    public void update_rope_time() {
        if (previewModels != null && previewModels.size() > 0) {
            JSONObject params = new JSONObject();
            try {
                params.put("client_id", client_id);
                JSONArray time_array = new JSONArray();
                for (RopePreviewModel model : previewModels) {
                    if (model.isEdited()) {
                        JSONObject object = new JSONObject();
                        object.put("client_id", client_id);
                        object.put("project_id", projectModel.getUp_id());
                        object.put("user_id", model.getUser_id());
                        object.put("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                        object.put("total_time", model.getTotal_time());
                        object.put("manual_rope_time", calculateEditedTime(model.getEdited_time()));
                        time_array.put(object);
                    }
                }
                if (time_array.length() > 0)
                    params.put("data", time_array);
                else return;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new NetworkRequest(baseActivity).make_contentpost_request(asm_update_ropetime, params, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            show_snak(baseActivity, jsonObject.getString("message"));
                            if (jsonObject.getString("status_code").equals("200")) {
                                baseActivity.onBackPressed();
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
        }
    }

    private String calculateEditedTime(String edited_time) {
        int timeSeconds = 0;
        if (!edited_time.equals("") && edited_time.contains(":")) {
            String[] time = edited_time.split(":");
            int hours = Integer.parseInt(time[0].replaceAll("[^0-9.]", ""));
            int minutes = Integer.parseInt(time[1].replaceAll("[^0-9.]", ""));
            timeSeconds = 60 * minutes + 3600 * hours;
        }
        return "" + timeSeconds;
    }

}

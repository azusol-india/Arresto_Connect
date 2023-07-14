package app.com.arresto.arresto_connect.ui.fragments;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.UserTrackModel;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class VisitedDatesAdapter extends RecyclerView.Adapter<VisitedDatesAdapter.ViewHolder> {

    private List<Constant_model> mValues;
    BaseActivity baseActivity;

    public VisitedDatesAdapter(BaseActivity baseActivity, List<Constant_model> items) {
        this.baseActivity = baseActivity;
        mValues = items;
    }

    public void update_list(ArrayList<Constant_model> dates_list) {
        mValues = dates_list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_visited_dates_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(getWeekDayName(mValues.get(position).getDate()));
        holder.mContentView.setText(mValues.get(position).getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate_Track(mValues.get(position).getDate());
//                LoadFragment.replace(SitesMapFragment.newInstance(site_models), baseActivity, getResString("lbl_sites_on_map"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Constant_model mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void getDate_Track(String date) {
        String url;
        url = All_Api.get_Datelocation_api + Static_values.user_id + "&date=" + date + "&client_id=" + Static_values.client_id;
        Log.e("url ", " is  " + url);
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            ArrayList<UserTrackModel> dates_list = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), UserTrackModel[].class)));
                            LoadFragment.replace(SitesMapFragment.newTrackInstance(dates_list), baseActivity, getResString("lbl_visits"));
                        } else {
//                            checkBlankPage();
                            AppUtils.show_snak(baseActivity, "" + jsonObject.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("message 1", "   " + message);
            }
        });
    }

    public static String getWeekDayName(String s) {
//        String tempDate = "2020-03-29";
        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(s);
            DayOfWeek day = date.getDayOfWeek();
            return "" + day;
        } else {
            return "";
        }
    }
}
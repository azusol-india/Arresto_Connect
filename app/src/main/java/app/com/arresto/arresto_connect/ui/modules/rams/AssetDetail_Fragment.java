package app.com.arresto.arresto_connect.ui.modules.rams;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Component_history_Model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;

public class AssetDetail_Fragment extends Base_Fragment {
    View view;
    Handler handler;
    RecyclerView asset_list;

    public static AssetDetail_Fragment newInstance(String date) {
        AssetDetail_Fragment fragmentFirst = new AssetDetail_Fragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private String date;
    Project_Model projectModel;
    public SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.asset_checkin_list, container, false);
        projectModel = slctd_project;
        if (getArguments() != null) {
            date = getArguments().getString("date");
        }
        handler = new Handler();
        asset_list = view.findViewById(R.id.asset_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        asset_list.setLayoutManager(layoutManager);
        asset_list.getLayoutManager().setMeasurementCacheEnabled(false);
        getAssetDetails(All_Api.getCheckInOut + client_id + "&user_id=" + user_id + "&project_id=" + slctd_project.getUp_id() + "&date=" + date);
        return view;
    }

    TimeAdapter adapter;

    private void setdata(ArrayList<Component_history_Model.CheckStatus> previewModels) {
        adapter = new TimeAdapter(getActivity(), previewModels);
        asset_list.setAdapter(adapter);
    }

    ArrayList<Component_history_Model.CheckStatus> assetDetails;

    private void getAssetDetails(String url) {
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
                            assetDetails = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_history_Model.CheckStatus[].class)));
                            setdata(assetDetails);
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
        private ArrayList<Component_history_Model.CheckStatus> assetDetails;

        public TimeAdapter(Activity c, ArrayList<Component_history_Model.CheckStatus> previewModels) {
            context = c;
            this.assetDetails = previewModels;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (assetDetails != null)
                return assetDetails.size();
            else
                return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView s_no, asset_tv,uin_tv, checkin_tv, checkout_tv;

            ViewHolder(View v) {
                super(v);
                s_no = v.findViewById(R.id.s_no);
                asset_tv = v.findViewById(R.id.asset_tv);
                uin_tv = v.findViewById(R.id.uin_tv);
                checkin_tv = v.findViewById(R.id.checkin_tv);
                checkout_tv = v.findViewById(R.id.checkout_tv);
            }
        }

        //---returns an ImageView view---
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_checkin_layout, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            Component_history_Model.CheckStatus data = assetDetails.get(position);
            holder.s_no.setText("" + (position + 1));
            holder.asset_tv.setText(data.getPu_product_id());
            holder.uin_tv.setText(data.getUin());
            holder.checkin_tv.setText(get_formated_date(data.getPu_checkin()));
            holder.checkout_tv.setText(get_formated_date(data.getPu_checkout()));

        }

        private String get_formated_date(String seconds) {
            Date date = new Date();
            if (seconds != null && !seconds.equals(""))
                date.setTime(TimeUnit.SECONDS.toMillis(Long.parseLong(seconds)));
            return baseActivity.Time_Format().format(date);
        }
    }
}
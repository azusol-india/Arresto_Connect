package app.com.arresto.arresto_connect.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static android.view.View.VISIBLE;

/**
 * A fragment representing a list of Items.
 */
public class VisitedDates_fragment extends Base_Fragment {

    View view;
    RecyclerView _list;
    TextView blank_tv;
    VisitedDatesAdapter visitedDatesAdapter;
    EditText search_view;
    SwipeRefreshLayout swipe;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_visited_dates_fragment_list, container, false);
            find_id();
            update_list();
            search_view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String newText = editable.toString();
                    if (TextUtils.isEmpty(newText)) {
                        set_data(dates_list);
                    } else if (newText != null) {
                        setList_Data(newText.toLowerCase());
                    }
                }
            });
        }
        return view;
    }

    private void find_id() {
        swipe = view.findViewById(R.id.swipe);
        _list = view.findViewById(R.id.list);
        blank_tv = view.findViewById(R.id.blank_tv);
        search_view = view.findViewById(R.id.search_view);
        _list.setLayoutManager(new LinearLayoutManager(baseActivity));
    }

    ArrayList<Constant_model> dates_list;

    @SuppressLint("HandlerLeak")
    public void update_list() {
        String url;
        url = All_Api.get_DateTimeLine_api + Static_values.user_id + "&client_id=" + Static_values.client_id;
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
                            dates_list = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Constant_model[].class)));
                            set_data(dates_list);
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

    private void setList_Data(String txt) {
        if (dates_list == null)
            return;
        ArrayList<Constant_model> slctdates = new ArrayList<>();
        for (Constant_model date : dates_list) {
            if (date.getDate().toLowerCase().startsWith(txt)) {
                slctdates.add(date);
            }
        }
        set_data(slctdates);
    }

    private void set_data(ArrayList<Constant_model> dates_list) {
        if (dates_list != null && dates_list.size() > 0) {
            blank_tv.setVisibility(View.GONE);
            _list.setVisibility(VISIBLE);
            if (visitedDatesAdapter != null) {
                visitedDatesAdapter.update_list(dates_list);
            } else {
                visitedDatesAdapter = new VisitedDatesAdapter(baseActivity,dates_list);
                _list.setAdapter(visitedDatesAdapter);
            }
        } else {
            blank_tv.setVisibility(VISIBLE);
            _list.setVisibility(View.GONE);
        }

    }

}
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.recycler_fast_scroller.IndexFastScrollRecyclerView;
import app.com.arresto.arresto_connect.ui.adapters.InspectionUserAdapter;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;

public class UsersList extends Base_Fragment {
    IndexFastScrollRecyclerView user_list;
    View view;
    TextView blank_tv;
    InspectionUserAdapter userAdapter;
    EditText search_view;
    SwipeRefreshLayout swipe;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.inspection_users_list, parent, false);
            setupUI(view, getActivity());
            find_id();
            update_list(Static_values.user_id);
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
                        set_users(groupUsers);
                    } else if (newText != null) {
                        setList_Data(newText.toLowerCase());
                    }
                }
            });
        }
        return view;
    }

    private void setList_Data(String txt) {
        if (groupUsers == null)
            return;
        ArrayList<GroupUsers> slctusers = new ArrayList<>();
        for (GroupUsers user : groupUsers) {
            if (user.getUacc_username().toLowerCase().startsWith(txt)) {
                slctusers.add(user);
            }
        }
        set_users(slctusers);
    }

    private void find_id() {
        swipe = view.findViewById(R.id.swipe);
        user_list = view.findViewById(R.id.user_list);
        blank_tv = view.findViewById(R.id.blank_tv);
        search_view = view.findViewById(R.id.search_view);
        search_view.setHint(getResString("lbl_search"));
        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        user_list.setLayoutManager(layoutManager);
        user_list.setIndexTextSize(12);
        user_list.setIndexBarColor("#33334c");
        user_list.setIndexBarCornerRadius(0);
        user_list.setIndexBarTransparentValue((float) 0.4);
        user_list.setIndexbarMargin(0);
        user_list.setIndexbarWidth(40);
        user_list.setPreviewPadding(0);
        user_list.setIndexBarTextColor("#FFFFFF");

        user_list.setPreviewTextSize(60);
        user_list.setPreviewColor("#33334c");
        user_list.setPreviewTextColor("#FFFFFF");
        user_list.setPreviewTransparentValue(0.6f);

        user_list.setIndexBarVisibility(true);

        user_list.setIndexBarStrokeVisibility(true);
        user_list.setIndexBarStrokeWidth(1);
        user_list.setIndexBarStrokeColor("#000000");

        user_list.setIndexbarHighLightTextColor("#33334c");
        user_list.setIndexBarHighLightTextVisibility(true);

//        Objects.requireNonNull(user_list.getLayoutManager()).scrollToPosition(0);
    }


    ArrayList<GroupUsers> groupUsers;

    @SuppressLint("HandlerLeak")
    public void update_list(String user_id) {
        String url;
        url = All_Api.get_child_users + Static_values.client_id + "&user_id=" + user_id;
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
                            groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data").toString(), GroupUsers[].class)));
                            Collections.sort(groupUsers, new Comparator<GroupUsers>() {
                                public int compare(GroupUsers obj1, GroupUsers obj2) {
                                    return obj1.getUacc_username().compareToIgnoreCase(obj2.getUacc_username());
                                }
                            });
                            set_users(groupUsers);
                        } else {
                            user_list.setIndexBarVisibility(false);
                            user_list.setVisibility(View.GONE);
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

    private void set_users(ArrayList<GroupUsers> groupUsers) {
        if (groupUsers != null && groupUsers.size() > 0) {
            user_list.setIndexBarVisibility(true);
            user_list.setVisibility(VISIBLE);
            if (userAdapter != null) {
                userAdapter.update_list(groupUsers);
            } else {
                userAdapter = new InspectionUserAdapter(baseActivity, groupUsers);
                user_list.setAdapter(userAdapter);
            }
        } else {
            blank_tv.setVisibility(VISIBLE);
            user_list.setVisibility(View.GONE);
            user_list.setIndexBarVisibility(false);
        }

    }

}

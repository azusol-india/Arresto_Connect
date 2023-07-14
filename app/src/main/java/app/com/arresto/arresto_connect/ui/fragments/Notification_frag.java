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

package app.com.arresto.arresto_connect.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Message_model;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.firebase_services.Config;
import app.com.arresto.arresto_connect.third_party.firebase_services.NotificationUtils;
import app.com.arresto.arresto_connect.ui.adapters.Notifications_Adapter;

import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class Notification_frag extends Fragment {
    View view;
    Data_daowload data_daowload;
    ArrayList<String> msg_db_id;
    TextView blank_tv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.notification_frag_lay, container, false);
            data_daowload = new Data_daowload(getActivity());

            find_allId();
            get_message_data();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            msg_list.setLayoutManager(linearLayoutManager);

            handle_notification();
        }
        return view;
    }

    private void handle_notification() {
        int no_noti = NotificationUtils.count_pnding_Notific();
        Config.count = NotificationUtils.count_pnding_Notific();
        if (no_noti > 0) {
            NotificationUtils.setBadge(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver), new IntentFilter("new_message"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("BroadcastReceiver ", " is- run" );
            Bundle extras = intent.getExtras();
            listMessages.add(0, new Message_model(extras.getString("message"), extras.getString("status"), extras.getString("time")));
            msg_db_id.add(0, extras.getString("id"));
            notifications_adapter.notifyItemInserted(0);
            msg_list.smoothScrollToPosition(0);
            handle_notification();
        }
    };

    ArrayList<Message_model> listMessages;
    Notifications_Adapter notifications_adapter;

    public void get_message_data() {
        listMessages = new ArrayList<>();
        msg_db_id = new ArrayList<>();
        String inspect_query = "SELECT id,user_id,message,status, message_time AS message_time FROM notification_tbl WHERE user_id='" + Static_values.user_id + "' ORDER BY message_time DESC";
        data_daowload.open();
        Cursor c = data_daowload.getSingle_Rowdata(inspect_query);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                msg_db_id.add(c.getString(0));
                Log.e("conversation_img ", c.getString(1) + " is- run " + c.getString(4));
                listMessages.add(new Message_model(c.getString(2), c.getString(3), c.getString(4)));
            } while (c.moveToNext());
        }

        if (listMessages.size() > 0) {
            update_list();
        } else {
            fetch_data_online();
        }
        // make unread message 0
    }


    public void update_list() {
        notifications_adapter = new Notifications_Adapter(getActivity(), this, listMessages);
        msg_list.setAdapter(notifications_adapter);
        if (listMessages.size() > 0) {
            msg_list.setVisibility(View.VISIBLE);
            blank_tv.setVisibility(View.GONE);
        } else {
            msg_list.setVisibility(View.GONE);
            blank_tv.setVisibility(View.VISIBLE);
        }
    }

    public void update_db_msg(int pos, String action) {
        data_daowload.open();
        data_daowload.change_msg_status(msg_db_id.get(pos), action);
        data_daowload.close();
        get_message_data();
    }

    RecyclerView msg_list;

    private void find_allId() {
        msg_list = view.findViewById(R.id.msg_list);
        blank_tv = view.findViewById(R.id.blank_tv);
    }

    void fetch_data_online() {
        final Data_daowload dataBase = new Data_daowload(getActivity());
        String url = All_Api.getNotification + client_id + "&user_id=" + user_id;
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status_code = jsonObject.getString("status_code");
                    if (status_code.equals("200")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        dataBase.open();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            object.put("type", "inspection");
                            dataBase.insert_notification(user_id, object.toString(), "approved", object.getString("timestamp"));
                            listMessages.add(new Message_model(object.toString(), "approved", object.getString("timestamp")));
                        }
                        dataBase.close();
                        Collections.sort(listMessages, new Comparator<Message_model>() {
                            public int compare(Message_model obj1, Message_model obj2) {
                                return obj2.getTimeStamp().compareToIgnoreCase(obj1.getTimeStamp());
                            }
                        });
                        update_list();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });

    }
}

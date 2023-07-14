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

package app.com.arresto.arresto_connect.constants;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.data.models.Advt_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.getAdvt;

public class ConstantMethods {

    public static void get_adv_data(final Handler handler) {
        NetworkRequest network_request = new NetworkRequest();
        String url = getAdvt + client_id;
        network_request.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
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
                            String data = jsonObject.getString("data");
                            DataHolder_Model.getInstance().setAdvt_model(AppUtils.getGson().fromJson(data, Advt_Model.class));
                            if (handler != null) {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
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

    public static void find_pageVideo(Activity activity, String pageName) {
        BaseActivity baseActivity = (BaseActivity) activity;
        if (baseActivity.help_videos != null) {
            int index = baseActivity.help_videos.indexOf(pageName);
            if (index > -1) {
                baseActivity.page_link = DataHolder_Model.getInstance().getAdvt_model().getVideos().get(index).getVideo_url();
                baseActivity.video_btn.setVisibility(View.VISIBLE);
            } else {
                baseActivity.page_link = "";
                baseActivity.video_btn.setVisibility(View.GONE);
            }
        }
    }

}

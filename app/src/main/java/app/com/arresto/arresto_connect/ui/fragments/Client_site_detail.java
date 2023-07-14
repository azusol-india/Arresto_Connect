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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.network.All_Api;

public class Client_site_detail extends Fragment {
    View view;
    ImageView inspctr_img;
    TextView insp_phn, ins_nam;
    String ins_name, phon, insp_detail, image_url;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.client_site_detail, container, false);

            findAllIds(view);
            if (getArguments() != null) {
                insp_detail = getArguments().getString("inspctr_dtal");
                try {
                    JSONObject jsonObject = new JSONObject(insp_detail);
                    ins_name = (jsonObject.getString("upro_first_name") + " " + jsonObject.getString("upro_last_name"));
                    image_url = jsonObject.getString("upro_image");
                    image_url = All_Api.Host + image_url;
                    phon = jsonObject.getString("upro_phone");
                    setdata();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return view;
        } else {
            return view;
        }

    }

    private void setdata() {
        ins_nam.setText(ins_name);
        insp_phn.setText(phon);
        AppUtils.load_image(image_url, inspctr_img);
    }

    private void findAllIds(View view) {
        inspctr_img = (ImageView) view.findViewById(R.id.ins_img);
        ins_nam = (TextView) view.findViewById(R.id.ins_nam);
        insp_phn = (TextView) view.findViewById(R.id.insp_phn);
    }


}

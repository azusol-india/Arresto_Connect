package app.com.arresto.arresto_connect.ui.fragments;

import static app.com.arresto.Flavor_Id.FlavourInfo.unit_name;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.ClientInfo;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment;

public class Dealer_detailFragment extends Base_Fragment {

    View view;
    static Client_model client_model;
    static String page_type;

    public static Dealer_detailFragment newInstance(Client_model data, String type) {
        Dealer_detailFragment asmCustomPage = new Dealer_detailFragment();
        client_model = data;
        page_type = type;
        return asmCustomPage;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.dealer_detail_fragment, parent, false);
            findAllIds(view);
            if (!page_type.equals("contact"))
                setData(client_model);
            else {
//                name_tv.setVisibility(View.GONE);
                contact_lay.setVisibility(View.GONE);
                call_btn.setVisibility(View.GONE);
                distance_tv.setVisibility(View.GONE);
                sport_lay.setVisibility(View.VISIBLE);
                web_link_lay.setVisibility(View.VISIBLE);
                logo_img.setVisibility(View.VISIBLE);
                fetch_data(All_Api.client_info + client_id);
            }


        }
        return view;
    }

    MaterialButton call_btn;
    private TextView phone_tv, distance_tv, web_tv, name_tv, email_tv, address_tv, contact_tv, support_tv, web_link_tv;
    private LinearLayout direction_btn, contact_lay, sport_lay, web_link_lay;
    ImageView logo_img;

    public void findAllIds(View view) {
        logo_img = view.findViewById(R.id.logo_img);
        distance_tv = view.findViewById(R.id.distance_tv);
        direction_btn = view.findViewById(R.id.direction_btn);
        phone_tv = view.findViewById(R.id.phone_tv);
        web_tv = view.findViewById(R.id.web_tv);
        name_tv = view.findViewById(R.id.name_tv);
        email_tv = view.findViewById(R.id.email_tv);
        address_tv = view.findViewById(R.id.address_tv);
        contact_tv = view.findViewById(R.id.contact_tv);
        contact_lay = view.findViewById(R.id.contact_lay);
        sport_lay = view.findViewById(R.id.sport_lay);
        support_tv = view.findViewById(R.id.support_tv);
        web_link_tv = view.findViewById(R.id.web_link_tv);
        web_link_lay = view.findViewById(R.id.web_link_lay);
        call_btn = view.findViewById(R.id.call_btn);
    }

    String EMAIL_PATTERN = "([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)";

    private void setData(Object data) {
        if (!logo_url.equals("")) {
            printLog("logo link == " + logo_url);
            load_image_file(logo_url, logo_img);
        }
        String address = "";
        if (data instanceof ClientInfo) {
            ClientInfo clientInfo = (ClientInfo) data;
            name_tv.setText(getResString("lbl_contact_us"));
            phone_tv.setText(clientInfo.getCustomerPhone());
            web_tv.setText(clientInfo.getCustomerSite());
            web_link_tv.setText(clientInfo.getWeb_link());
            email_tv.setText(clientInfo.getCustomerEmail());
//            support_tv.setText("support@arresto.in");
            support_tv.setText(clientInfo.getCustomerReplyEmail());
            if (clientInfo.getCustomerAddressNew() != null) {
                address = address + clientInfo.getCustomerAddressNew().getLine1();
                address = address + ",\n" + clientInfo.getCustomerAddressNew().getLine2();
                address = address + ",\n" + clientInfo.getCustomerAddressNew().getState();
                address = address + ",\n" + clientInfo.getCustomerAddressNew().getPin();
                address = address.replaceAll(EMAIL_PATTERN, "");
                if (email_tv.getText().equals("")) {
                    email_tv.setText(clientInfo.getCustomerAddressNew().getContact_email());
                }
//                address = clientInfo.getCustomerAddress().replaceAll(EMAIL_PATTERN, "");
            }
            if (!client_id.equals("947"))
                address_tv.setText(address);
        } else if (data instanceof Client_model) {
            final Client_model client_model = (Client_model) data;
            distance_tv.setText(EC_Base_Fragment.round(client_model.getDistance(), 2) + unit_name);
            name_tv.setText(client_model.getClientName());
            phone_tv.setText(client_model.getClientContactNo());
            web_tv.setText(client_model.getClient_website());
            email_tv.setText(client_model.getClientContactPersonEmail());
            contact_tv.setText(client_model.getClientContactPerson());
            address = client_model.getClientAddress();
            if (client_model.getClientDistrict() != null && !client_model.getClientDistrict().equals(""))
                address = address + ",\n" + client_model.getClientDistrict();
            if (client_model.getClientCircle() != null && !client_model.getClientCircle().equals(""))
                address = address + ",\n" + client_model.getClientCircle();

            if (!client_id.equals("947"))
                address_tv.setText(address);
            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", client_model.getClientContactNo(), null));
                    startActivity(intent);
                }
            });
        }


        final String finalAddress = address;
        direction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + finalAddress;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                getActivity().startActivity(intent);
            }
        });

    }

    public void fetch_data(String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    Object json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(response);
                        ClientInfo clientInfo = AppUtils.getGson().fromJson(object.getString("data"), ClientInfo.class);
                        setData(clientInfo);
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

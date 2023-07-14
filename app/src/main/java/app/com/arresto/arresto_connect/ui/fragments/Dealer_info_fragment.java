package app.com.arresto.arresto_connect.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.Dealer_Adapter;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;

import static app.com.arresto.Flavor_Id.FlavourInfo.distance_unit;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;

public class Dealer_info_fragment extends BottomSheetDialogFragment {
    ArrayList<Client_model> clientModels;
    private ACProgressFlower progressDialog;

    public Dealer_info_fragment(ArrayList<Client_model> clientModels) {
        this.clientModels = clientModels;
    }

    BaseActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        progressDialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.YELLOW)
                .text(getResString("lbl_wait_st")).textSize(16).textMarginTop(5)
                .petalThickness(2)
                .sizeRatio((float) 0.22)
                .fadeColor(Color.WHITE).build();
        tempClients = new ArrayList<>();
        coder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        if (clientModels != null && clientModels.size() > 0) {
            calculate_distance();
        }
        Log.e("clients ", " is " + clientModels);
    }

    View view;
    RecyclerView dealer_list;
    Dealer_Adapter dealerAdapter;
    TextView current_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.client_dealer_layout, container, false);
            current_tv = view.findViewById(R.id.current_tv);
            getCurrentAddress();
            dealer_list = view.findViewById(R.id.dealer_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            dealer_list.setLayoutManager(layoutManager);
            dealerAdapter = new Dealer_Adapter(this, tempClients);
            dealer_list.setAdapter(dealerAdapter);
        }
        return view;
    }

    ArrayList<Client_model> tempClients;

    @SuppressLint("StaticFieldLeak")
    public void calculate_distance() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressDialog != null)
                    progressDialog.show();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                tempClients = new ArrayList<>();
                for (int i = 0; i < clientModels.size(); i++) {
                    Client_model client_model = clientModels.get(i);
                    if (client_model.getClient_latlong() != null && !client_model.getClient_latlong().getLat().equals("") && !client_model.getClient_latlong().getLat().equals("")) {
                        Client_model.Latlong client_loc = client_model.getClient_latlong();
                        Location endPoint = new Location("end");
                        endPoint.setLatitude(Double.parseDouble(client_loc.getLat()));
                        endPoint.setLongitude(Double.parseDouble(client_loc.getLng()));
                        client_model.setDistance(distanceTo(endPoint));
                        if (!client_model.getClient_range().equals("")) {
                            int range = Integer.parseInt(client_model.getClient_range());
                            if (range >= client_model.getDistance())
                                tempClients.add(client_model);
                        } else
                            tempClients.add(client_model);
                    } else if (!client_model.getClientAddress().equals("")) {
                        String address = client_model.getClientAddress();
                        if (client_model.getClientDistrict() != null && !client_model.getClientDistrict().equals(""))
                            address = address + " " + client_model.getClientDistrict();
                        if (client_model.getClientCircle() != null && !client_model.getClientCircle().equals(""))
                            address = address + " " + client_model.getClientCircle();
                        double distance = getDistanceFromAddress(address);
                        if (distance > 0) {
                            client_model.setDistance(distance);
                            if (!client_model.getClient_range().equals("")) {
                                int range = Integer.parseInt(client_model.getClient_range());
                                if (range >= client_model.getDistance())
                                    tempClients.add(client_model);
                            } else
                                tempClients.add(client_model);
                        }
                    }
                    Log.e("tempClients loop ", "latlong= " + tempClients);
                }
                Collections.sort(tempClients, new Comparator<Client_model>() {
                    @Override
                    public int compare(Client_model c1, Client_model c2) {
                        return Double.compare(c1.getDistance(), c2.getDistance());
                    }
                });

                return tempClients.size();
            }

            @Override
            protected void onPostExecute(Integer size) {
                super.onPostExecute(size);
                Log.e("size ", "latlong= " + size);
                Log.e("tempClients ", "latlong= " + tempClients);
                if (progressDialog != null)
                    progressDialog.dismiss();
                updateList();
            }
        }.execute();
    }

    private void updateList() {
        if (tempClients.size() > 0)
            dealerAdapter.update_list(tempClients);
        else {
            show_snak(getActivity(), "No Dealer found near you.");
            this.dismiss();
        }
    }




    public void getCurrentAddress() {
         String current_address = "";
         String current_state = "";
         String current_city = "";
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(activity.curr_lat, activity.curr_lng, 1);
            if (addresses != null && addresses.size() > 0) {
                current_state = addresses.get(0).getAdminArea();
                current_city = addresses.get(0).getSubAdminArea();
                current_address = addresses.get(0).getAddressLine(0);
                Log.e("current_address", "address " + addresses.get(0));
//                Log.e("state local", "state " + current_state);
//                Log.e("city local", "city " + current_city);
                current_tv.setText(current_address + " " + current_city + " " + current_state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Geocoder coder;

    public double getDistanceFromAddress(String strAddress) {
        LatLng p1;
        try {
            List<Address> address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() < 1) {
                return -1;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("strAddress ", "latlong= " + p1);
            Location endPoint = new Location("end");
            endPoint.setLatitude(location.getLatitude());
            endPoint.setLongitude(location.getLongitude());
            return distanceTo(endPoint);
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e("IOException ", "is " + ex.getMessage());
            return -1;
        }
    }

    public double distanceTo(Location endPoint) {
        return (activity.curr_location.distanceTo(endPoint) / 1000) *distance_unit + 3;
    }

}

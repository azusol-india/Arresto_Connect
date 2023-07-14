package app.com.arresto.arresto_connect.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppLocationService;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.data.models.UserTrackModel;

public class SitesMapFragment extends Base_Fragment implements OnMapReadyCallback, LocationListener {
    View view;
    MapView mMapView;
    ArrayList<MarkerOptions> markerPoints;
    double curr_lat;
    double curr_lng;
    Location curr_location;
    private GoogleMap googleMap;
    AsyncTask asyncTask;
    ArrayList<Site_Model> sites;
    ArrayList<UserTrackModel> tracks;
    int zoomUnit = 7;

    public static SitesMapFragment newInstance(ArrayList<Site_Model> sites) {
        SitesMapFragment sitesMapFragment = new SitesMapFragment();
        Bundle args = new Bundle();
        args.putString("sites", AppUtils.getGson().toJson(sites));
        sitesMapFragment.setArguments(args);
        return sitesMapFragment;
    }

    public static SitesMapFragment newTrackInstance(ArrayList<UserTrackModel> tracks) {
        SitesMapFragment sitesMapFragment = new SitesMapFragment();
        Bundle args = new Bundle();
        args.putString("track", AppUtils.getGson().toJson(tracks));
        sitesMapFragment.setArguments(args);
        return sitesMapFragment;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.sites_map_fragment, parent, false);
            if (getArguments() != null) {
                if (getArguments().containsKey("sites")) {
                    sites = new ArrayList<>(Arrays.asList(baseActivity.getGson().fromJson(getArguments().getString("sites"), Site_Model[].class)));
                } else if (getArguments().containsKey("track")) {
                    zoomUnit = 12;
                    tracks = new ArrayList<>(Arrays.asList(baseActivity.getGson().fromJson(getArguments().getString("track"), UserTrackModel[].class)));
                }
            }
            mMapView = view.findViewById(R.id.mapView1);
            mMapView.onCreate(savedInstanceState);
            markerPoints = new ArrayList<>();
            map_load();
//            asyncTask = new AsyncCaller().execute();
            return view;
        } else {

            return view;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public void onStop() {
        super.onStop();
        if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
            asyncTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    public void map_load() {
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);
//        googleMap = mMapView.getMap();
        new AppLocationService(baseActivity, new AppLocationService.OnLocationChange() {
            @Override
            public void locationChange(Location location, double latitude, double longitude) {
                curr_location = location;
                curr_lat = latitude;
                curr_lng = longitude;
            }
        });

    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            baseActivity.showDialog();

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (sites != null)
                get_lat_long(sites);
            else if (tracks != null)
                addPolyline();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            add_marker();
//            baseActivity.hideDialog();
        }

    }

    //    @Override
    public void get_lat_long(ArrayList<Site_Model> site_models) {
        if (site_models.size() > 0) {
            for (int i = 0; i < site_models.size(); i++) {
                Site_Model siteModel = site_models.get(i);
                String S_lat = siteModel.getSite_lattitude();
                if (S_lat == null || S_lat.equals("") || S_lat.equals("0") || !S_lat.matches("^[0-9]*\\.?[0-9]*$")) {
                    Geocoder coder = new Geocoder(getContext());
                    ArrayList<Address> adresses;
                    try {
                        adresses = (ArrayList<Address>) coder.getFromLocationName(siteModel.getSite_address() + " " + siteModel.getSite_city(), 1);
                        for (Address add : adresses) {
                            if (!add.equals("null")) {
                                double longitude = add.getLongitude();
                                double latitude = add.getLatitude();
                                LatLng point = new LatLng(latitude, longitude);
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(point);
                                marker.title(siteModel.getSite_id());
                                markerPoints.add(marker);
                                Log.e("lat long add for", "  is  " + point);
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                add_marker1(marker);
                            } else {
                                Log.e("lat long not found for", siteModel.getSite_address());
                            }
                        }
//            Log.e("search lat long", full_address.get(i) + addres_point);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    double latitude = Double.parseDouble(siteModel.getSite_lattitude());
                    double longitude = Double.parseDouble(siteModel.getSite_longitude());
                    LatLng point = new LatLng(latitude, longitude);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(point);
                    marker.title(siteModel.getSite_id());
                    markerPoints.add(marker);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    add_marker1(marker);
                }
            }
        }
        baseActivity.hideDialog();
        add_marker();
    }

    public void addPolyline() {
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions lineOptions = new PolylineOptions();
        for (int i = 0; i < tracks.size(); i++) {
            UserTrackModel trackModel = tracks.get(i);
            double latitude = Double.parseDouble(trackModel.getLatitute());
            double longitude = Double.parseDouble(trackModel.getLongitute());
            LatLng point = new LatLng(latitude, longitude);
            points.add(point);

//            add_marker1(marker);
        }
        if (points.size() > 1) {
            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            if (googleMap != null)
                baseActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        googleMap.addPolyline(lineOptions);
                    }
                });
        }

    }

    public void add_marker1(MarkerOptions marker) {
        if (googleMap != null)
            baseActivity.runOnUiThread(new Runnable() {
                public void run() {
                    googleMap.addMarker(marker);
                }
            });
    }

    public void add_marker() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (MarkerOptions marker : markerPoints) {
//           marker.snippet("someDesc");
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    googleMap.addMarker(marker);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        curr_lat = location.getLatitude();
        // Getting longitude of the current location
        curr_lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        map_load();
        onMapReady(googleMap);
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        asyncTask = new AsyncCaller().execute();
        if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        // create marker
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(curr_lat, curr_lng)).title("current location");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(curr_lat, curr_lng)).zoom(zoomUnit).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

}

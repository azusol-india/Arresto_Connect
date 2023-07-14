package app.com.arresto.arresto_connect.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppLocationService;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.interfaces.OnLocationSelectListener;

public class MapFragment extends Base_Fragment implements OnMapReadyCallback {
    View view;
    MapView mMapView;
    ArrayList<MarkerOptions> markerPoints;
    double curr_lat, curr_lng;
    LatLng slctd_Latlng;
    Location curr_location;
    private GoogleMap googleMap;
    int zoomUnit = 15;
    TextView search_tv;
    OnLocationSelectListener onLocationListener;

    public void setFragmentCallback(OnLocationSelectListener callback) {
        this.onLocationListener = callback;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.sites_map_fragment, parent, false);
            find_id();
            mMapView.onCreate(savedInstanceState);
            markerPoints = new ArrayList<>();
            map_load();
            search_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectLocation(new OnPlaceSelectListener() {
                        @Override
                        public void OnPlaceSelect(Place place) {
                            if (googleMap != null) {
                                moveCamera(place.getLatLng());
                                search_tv.setText(place.getAddress());
                            }
                        }
                    });
                }
            });

            return view;
        } else {

            return view;
        }
    }

    MaterialButton ok_btn;

    private void find_id() {
        mMapView = view.findViewById(R.id.mapView1);
        search_tv = view.findViewById(R.id.search_tv);
        ok_btn = view.findViewById(R.id.ok_btn);
        ok_btn.setVisibility(View.VISIBLE);
        ((ViewGroup) search_tv.getParent()).setVisibility(View.VISIBLE);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationListener.OnLocationSelect(slctd_Latlng);
                baseActivity.onBackPressed();
            }
        });
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
        mMapView.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    public void map_load() {
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);
        new AppLocationService(baseActivity, new AppLocationService.OnLocationChange() {
            @Override
            public void locationChange(Location location, double latitude, double longitude) {
                curr_location = location;
                curr_lat = latitude;
                curr_lng = longitude;
                printLog("AppLocationService Latlng :" + location);
            }
        });

    }

    Marker marker;

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        slctd_Latlng = new LatLng(curr_lat, curr_lng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(slctd_Latlng).title("current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = googleMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(slctd_Latlng).zoom(zoomUnit).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moveCamera(latLng);
            }
        });
        placeMyLocationButton();
    }

    public void moveCamera(LatLng latLng) {
        slctd_Latlng = latLng;
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoomUnit);
        googleMap.animateCamera(location);
        String marker_title = "Lat : " + latLng.latitude + " , " + "Long : " + latLng.longitude;
        marker.setPosition(latLng);
        marker.setTitle(marker_title);
    }

    public void placeMyLocationButton() {
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 50, 50);

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LatLng curr_latlng = new LatLng(curr_lat, curr_lng);
                moveCamera(curr_latlng);
                search_tv.setText(AppUtils.getResString("lbl_search_location"));
                //TODO: Any custom actions
                return true;
            }
        });
    }

}

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

package app.com.arresto.arresto_connect.ui.modules.inspection;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppLocationService;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Detail_Table;
import app.com.arresto.arresto_connect.databinding.InspectionFragmentBinding;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getDisplaySize;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.meterDistanceBetweenPoints;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;

public class InspectionFragment extends Base_Fragment implements OnMapReadyCallback, View.OnClickListener {
    public boolean fullmap;
    public String sub_site_id, site_id;
    LinearLayout inspFrag, hide_view;
    RelativeLayout show_view;
    ImageView imageView;
    ScrollView scrolScrn;
    String[] separated_assetcode;

    int fx_lc = 0;
    ImageView transparentImageView;
    TextView clientEdt, siteEdt, sub_site_edt, assest_edt, inspected_edt, po_edt, project_edt, sms_edt, continueBtn, reportEdt, date_txt, address_txt, lat_txt, lng_txt, fix_loc;
    EditText batch_edt, serial_edt;
    String curr_date, client_name, master_id, site, report_no, date, input_method, rfid, uin, barcode, asset, asset_code, size, po_no, batch_no,
            serial_no, approved_by, project_no, sms_no, server_lat, server_longi, image_url, inspected_status, approved_status;
    String inputvalue, inputtype;
    int height, width;
    double curr_lat;
    double curr_lng;
    MapView mMapView;
    GoogleMap mgoogleMap;
    MarkerOptions marker;
    ArrayList<LatLng> markerPoints;
    View view;
    ArrayList<String> latlng_check = new ArrayList<>(Arrays.asList("0", "1"));

    InspectionFragmentBinding binding;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.inspection_fragment, parent, false);
            view = binding.getRoot();
//            input_typeModel = new Input_TypeModel(this);
//            binding.setInputType(input_typeModel);
            setupUI(view, getActivity());
            markerPoints = new ArrayList<>();
            if (getArguments() != null) {
                client_name = getArguments().getString("client_name");
                site = getArguments().getString("site");
                master_id = getArguments().getString("mdata_id");
                site_id = getArguments().getString("site_id");
                report_no = getArguments().getString("report_no");
                date = getArguments().getString("sms");
                sub_site_id = getArguments().getString("sub_site_id");
                input_method = getArguments().getString("input_method");
                rfid = getArguments().getString("rfid");
                uin = getArguments().getString("uin");
                barcode = getArguments().getString("barcode");
                asset = getArguments().getString("asset");
                asset_code = getArguments().getString("asset_code");
                size = getArguments().getString("size");
                po_no = getArguments().getString("po_no");
                batch_no = getArguments().getString("batch_no");
                serial_no = getArguments().getString("serial_no");
                approved_by = getArguments().getString("asset");
                project_no = getArguments().getString("job_no");
                sms_no = getArguments().getString("sms");
                image_url = getArguments().getString("image_url");
                server_lat = getArguments().getString("latitude");
                server_longi = getArguments().getString("longitude");
                inspected_status = getArguments().getString("inspected_status");
                approved_status = getArguments().getString("approved_status");
            }

            InspectionListItems.selectedPosition.clear();

            separated_assetcode = asset_code.split("####");

            height = getDisplaySize(getActivity()).heightPixels;
            width = getDisplaySize(getActivity()).widthPixels;
            allIds(view);
            myListener();
            settext();

            mMapView = view.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            marker = new MarkerOptions();
            map_load();

            transparentImageView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            scrolScrn.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;
                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            scrolScrn.requestDisallowInterceptTouchEvent(false);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            scrolScrn.requestDisallowInterceptTouchEvent(true);
                            return false;
                        default:
                            return true;
                    }
                }
            });


        } else {

            return view;
        }
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fix_loc:
                fx_lc = 1;
                Toast.makeText(getActivity(), getResString("lbl_fix_loc_msg"), Toast.LENGTH_LONG).show();
                break;
            case R.id.continue_btn:
                if (Profile_Model.getInstance().getConfig() == null || Profile_Model.getInstance().getConfig().getKare_geofencing_radious().equals("") || selectedMasterData_model.getProduct_geo_fancing().equals("no") || server_lat.equals("") || server_lat.equalsIgnoreCase("NA")) {
                    send_data2();
                } else if (meterDistanceBetweenPoints(curr_lat, curr_lng, server_lat, server_longi) < Integer.parseInt(Profile_Model.getInstance().getConfig().getKare_geofencing_radious())
//                } else if (meterDistanceBetweenPoints(28.761675, 76.725882, server_lat, server_longi) < Integer.parseInt(Profile_Model.getInstance().getConfig().getKare_geofencing_radious())
                        || (server_lat.equals("0") && server_longi.equals("0"))) {
                    send_data2();
                } else {
                    Toast.makeText(getContext(),"lat:"+curr_lat+",long:"+curr_lng+"\nDistance meter:"+meterDistanceBetweenPoints(curr_lat, curr_lng, server_lat, server_longi),Toast.LENGTH_SHORT).show();
                    show_snak(getActivity(), "You seem to be out of location.");
                }
                break;
        }
    }

    public void send_data2() {
        if (Static_values.selected_Site_model != null) {
            inputtype = Static_values.selected_Site_model.getInput_type();
            inputvalue = Static_values.selected_Site_model.getInput_value();
        }
        save_data();

//        else if (inputtype.equals("RFID") && inputvalue.equals("")) {
//            Toast.makeText(getActivity(), getResString("lbl_scn_rfid_msg"), Toast.LENGTH_LONG).show();
//        } else if (inputtype.equals("Barcode") && inputvalue.equals("")) {
//            Toast.makeText(getActivity(), getResString("lbl_scn_barcode_msg"), Toast.LENGTH_LONG).show();
//        } else if (inputtype.equals("UIN") && inputvalue.equals("")) {
//            Toast.makeText(getActivity(), getResString("lbl_entr_uin_msg"), Toast.LENGTH_LONG).show();
//        } else {
//            save_data();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "inspection process");
    }

    public void save_data() {
        Inspection_Detail_Table.Inspection_Detail_Dao detail_dao = AppController.getInstance().getDatabase().getInspection_Detail_Dao();
        Inspection_Detail_Table detail_table = new Inspection_Detail_Table();
        detail_table.set_inspection_details(Static_values.user_id, Static_values.unique_id, reportEdt.getText().toString(), site_id, sub_site_id, project_edt.getText().toString(),
                sms_edt.getText().toString(), "" + date_txt.getTag(), assest_edt.getText().toString(), po_edt.getText().toString(), batch_edt.getText().toString(),
                serial_edt.getText().toString(), address_txt.getText().toString(),lat_txt.getText().toString(), lng_txt.getText().toString(), Static_values.user_id,
                inputtype, inputvalue, master_id);
        long id = detail_dao.insert(detail_table);
        if (id > 0) {
            InspectionListItems inspectionListItems = new InspectionListItems();
            Bundle bundle = new Bundle();
            bundle.putStringArray("asset_code", separated_assetcode);
            inspectionListItems.setArguments(bundle);
            LoadFragment.replace(inspectionListItems, getActivity(), getResString("lbl_inspection"));
        } else {
            show_snak(baseActivity, getResString("lbl_try_again_msg"));
        }
    }

    public void allIds(View view) {
        imageView = view.findViewById(R.id.image1);
        inspFrag = view.findViewById(R.id.insp_frag);
        scrolScrn = view.findViewById(R.id.scroll_scrn);
        continueBtn = view.findViewById(R.id.continue_btn);
        address_txt = view.findViewById(R.id.address_txt);
        lat_txt = view.findViewById(R.id.lat_txt);
        lng_txt = view.findViewById(R.id.lng_txt);
        clientEdt = view.findViewById(R.id.client_edt);
        siteEdt = view.findViewById(R.id.site_edt);
        reportEdt = view.findViewById(R.id.report_edt);
        date_txt = view.findViewById(R.id.date_txt);
        sub_site_edt = view.findViewById(R.id.sub_site_edt);
        assest_edt = view.findViewById(R.id.assest_edt);
        po_edt = view.findViewById(R.id.po_edt);
        batch_edt = view.findViewById(R.id.batch_edt);
        serial_edt = view.findViewById(R.id.serial_edt);
        inspected_edt = view.findViewById(R.id.inspected_edt);
        project_edt = view.findViewById(R.id.project_edt);
        sms_edt = view.findViewById(R.id.sms_edt);
        fix_loc = view.findViewById(R.id.fix_loc);

        transparentImageView = view.findViewById(R.id.transparent_image);
        hide_view = view.findViewById(R.id.hide_view);
        show_view = view.findViewById(R.id.map_layout);

    }

    public void myListener() {
        continueBtn.setOnClickListener(this);
        fix_loc.setOnClickListener(this);
    }

    public void settext() {
        AppUtils.load_image(image_url, imageView);

        clientEdt.setText(client_name);
        if (site.trim().length() > 2)
            siteEdt.setText(site);

        if (report_no.equals("")) {
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            if (!site_id.equals(""))
                reportEdt.setText(site_id + "_" + year + "-0001");
            else
                reportEdt.setText(master_id + "_" + year + "-0001");
        } else {
            String[] n1 = report_no.split("-");
            final DecimalFormat decimalFormat = new DecimalFormat("0000");
            int in_val = Integer.parseInt(n1[1]);
            in_val++;
            String new_reportno = n1[0] + "-" + decimalFormat.format(in_val);
            Log.e("report increment", "" + new_reportno);
            reportEdt.setText(new_reportno);
        }
        Calendar c = Calendar.getInstance();
        curr_date = baseActivity.Date_Format().format(c.getTime());
        date_txt.setText(curr_date);
        date_txt.setTag(baseActivity.server_date_format.format(c.getTime()));
        sub_site_edt.setText(sub_site_id);

        assest_edt.setText(asset);
        po_edt.setText(po_no);
        batch_edt.setText(batch_no);
        if (!batch_no.equals("")) {
            batch_edt.setEnabled(false);
        }
        serial_edt.setText(serial_no);
        if (!serial_no.equals("")) {
            serial_edt.setEnabled(false);
        }
        inspected_edt.setText(Profile_Model.getInstance().getUpro_first_name() + " " + Profile_Model.getInstance().getUpro_last_name());
        project_edt.setText(project_no);
        sms_edt.setText(sms_no);

        if (!selectedMasterData_model.getProduct_geo_fancing().equals("no")) {
            if (server_lat != null && !server_lat.equals("") && !server_lat.equals("0")) {
                fix_loc.setVisibility(View.GONE);
            } else {
                fix_loc.setVisibility(View.VISIBLE);
            }
        } else {
            fix_loc.setVisibility(View.GONE);
        }
        address_txt.setText(selectedMasterData_model.getMdata_location());
//        baseActivity.getCurrentAddress(new ObjectListener() {
//            @Override
//            public void onResponse(Object obj) {
//                address_txt.setText(obj.toString());
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });

    }

    public void on_mapClick() {
        if (!fullmap) {
            hide_view.setVisibility(View.GONE);
            show_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height - 50));
            show_view.setPadding(0, 0, 0, 0);
            fullmap = true;
        } else {
            hide_view.setVisibility(View.VISIBLE);
            show_view.setLayoutParams(new LinearLayout.LayoutParams(width - 30, 350));
            show_view.setPadding(30, 0, 0, 35);
            fullmap = false;
        }
    }

    public void map_load() {
        mMapView.onResume();
        mMapView.getMapAsync(this);
        new AppLocationService(baseActivity, new AppLocationService.OnLocationChange() {
            @Override
            public void locationChange(Location location, double latitude, double longitude) {
                curr_lat = latitude;
                curr_lng = longitude;
                if (fx_lc != 1) {
                    if (latlng_check.contains(server_lat) && latlng_check.contains(server_longi) || server_lat.equals("") || server_lat.equalsIgnoreCase("NA") || server_longi.equals("") || server_longi.equalsIgnoreCase("NA")) {
                        lat_txt.setText(String.valueOf(curr_lat));
                        lng_txt.setText(String.valueOf(curr_lng));
                    } else {
                        lat_txt.setText(server_lat);
                        lng_txt.setText(server_longi);
                    }
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(AppController.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AppController.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
//         create marker
        marker.position(new LatLng(curr_lat, curr_lng)).title("current location");
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(marker);
        if (server_lat != null && !server_lat.equals("") && !server_lat.equals("0")) {
            MarkerOptions site_marker = new MarkerOptions();
            site_marker.position(new LatLng(Float.parseFloat(server_lat), Float.parseFloat(server_longi))).title(site);
            site_marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(site_marker);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(curr_lat, curr_lng)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mgoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (!fullmap) {
                    on_mapClick();
                }
            }
        });

    }


}



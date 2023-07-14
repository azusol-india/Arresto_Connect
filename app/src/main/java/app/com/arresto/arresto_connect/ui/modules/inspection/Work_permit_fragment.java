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

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.inspection_tables.WorkPermitTable;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class Work_permit_fragment extends Base_Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    View view;
    ImageView camera_smile, imageview_smile;
    RadioGroup harnss_rg,
            traning1_rg, situation1_rg, equipment1_rg, medical1_rg, medical2_rg, misclncs1_rg;
    String slctd_harnss,
            slctd_traning1, slctd_situation1, slctd_equipment1, slctd_medical1, slctd_medical2, slctd_misclncs1;
    ScrollView main_wp_scrl;
    int mYear, mMonth, mDay;
    String curr_date, client_name, site, report_no, date, input_method, rfid, uin, sub_site_id, site_id, barcode, asset, asset_code, size, po_no, batch_no,
            serial_no, approved_by, project_no, sms_no, image_url, inspected_status, approved_status, workPermit_number;
    TextView clientEdt, company_id_wedt, siteidEdt, site_adrres_edt, assest_edt, continueBtn, date_txt, wrkprmt_txt, prmt_dtfrm_txt, prmt_vlid_txt;
    EditText batch_wedt, serial_wedt, cl_wrkprmt_edt;
    String image_path = "";
    Site_Model site_model;
    MasterData_model masterData_model;
    public String image_directory = directory + "inspection/" + unique_id + "/";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.work_prmit_fragment, parent, false);
            AppUtils.setupUI(view, getActivity());
            site_model = Static_values.selected_Site_model;
            masterData_model = Static_values.selectedMasterData_model;
            if (masterData_model != null) {
                client_name = site_model.getClient_name();
                site = site_model.getSite_address();
                site_id = site_model.getSiteID_id();
                report_no = site_model.getReport_no();
                sub_site_id = site_model.getSite_id();
//                input_method =site_model.get;
                rfid = masterData_model.getMdata_rfid();
                uin = masterData_model.getMdata_uin();
                barcode = masterData_model.getMdata_barcode();
                asset = site_model.getMdata_item_series();
                asset_code = masterData_model.getAssetCodes();
                po_no = masterData_model.getMdata_po();
                batch_no = masterData_model.getMdata_batch();
                serial_no = masterData_model.getMdata_serial();
                project_no = site_model.getSite_jobcard();
                sms_no = site_model.getSite_sms();
                image_url = site_model.getImagepath();
                inspected_status = site_model.getInspected_status();
                approved_status = site_model.getApproved_status();
                workPermit_number = site_model.getWorkPermit_number();
            }
            find_id(view);
            setListner();
            setText();
            return view;
        } else {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "inspection process");
    }

    private void find_id(View view) {
        main_wp_scrl = view.findViewById(R.id.main_wp_scrl);
        camera_smile = view.findViewById(R.id.camera_smile);
        imageview_smile = view.findViewById(R.id.imageview_smile);
        clientEdt = view.findViewById(R.id.client_wedt);
        company_id_wedt = view.findViewById(R.id.company_id_wedt);
        siteidEdt = view.findViewById(R.id.site_wedt);
        site_adrres_edt = view.findViewById(R.id.sub_site_wedt);
        assest_edt = view.findViewById(R.id.assest_wedt);
        wrkprmt_txt = view.findViewById(R.id.wrkprmt_edt);
        cl_wrkprmt_edt = view.findViewById(R.id.cl_wrkprmt_edt);
        date_txt = view.findViewById(R.id.date_txt);
        continueBtn = view.findViewById(R.id.continue_wbtn);

        serial_wedt = view.findViewById(R.id.serial_wedt);
        batch_wedt = view.findViewById(R.id.batch_wedt);
        prmt_dtfrm_txt = view.findViewById(R.id.prmt_sdate_txt);
        prmt_vlid_txt = view.findViewById(R.id.prmt_vlid_txt);

        harnss_rg = view.findViewById(R.id.harness_radioBtn);

        traning1_rg = view.findViewById(R.id.traning1_rg);

        situation1_rg = view.findViewById(R.id.situation1_rg);

        equipment1_rg = view.findViewById(R.id.equipment1_rg);

        medical1_rg = view.findViewById(R.id.medical1_rg);
        medical2_rg = view.findViewById(R.id.medical2_rg);
        misclncs1_rg = view.findViewById(R.id.misclncs1_rg);
        prmt_vlid_txt.setText(baseActivity.Date_Format().format(new Date()));
        if (client_id.equals("931")) {
            ((ViewGroup) company_id_wedt.getParent()).setVisibility(View.VISIBLE);
            ((ViewGroup) cl_wrkprmt_edt.getParent()).setVisibility(View.GONE);
        }
    }

    private void setText() {
        clientEdt.setText(client_name);
        company_id_wedt.setText("" + masterData_model.getMdata_company_id());
        siteidEdt.setText(sub_site_id);
        site_adrres_edt.setText(site);
        assest_edt.setText(asset);
        wrkprmt_txt.setText(workPermit_number);
        serial_wedt.setText(serial_no);
        batch_wedt.setText(batch_no);

        Calendar c = Calendar.getInstance();
        curr_date = baseActivity.Date_Format().format(c.getTime());
        date_txt.setText(curr_date);
        date_txt.setTag(baseActivity.server_date_format.format(c.getTime()));
        prmt_dtfrm_txt.setText(curr_date);
    }

    private void setListner() {

        camera_smile.setOnClickListener(this);

        continueBtn.setOnClickListener(this);
//        prmt_dtfrm_txt.setOnClickListener(this);
        prmt_vlid_txt.setOnClickListener(this);

        harnss_rg.setOnCheckedChangeListener(this);
        traning1_rg.setOnCheckedChangeListener(this);

        situation1_rg.setOnCheckedChangeListener(this);
        equipment1_rg.setOnCheckedChangeListener(this);

        medical1_rg.setOnCheckedChangeListener(this);
        medical2_rg.setOnCheckedChangeListener(this);
        misclncs1_rg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_wbtn:
                gonull_focus();
                break;
//            case R.id.prmt_sdate_txt: show_Date_piker("from_dt");  break;
            case R.id.prmt_vlid_txt:
                show_Date_piker();
                break;
            case R.id.camera_smile:
                if (!baseActivity.isStoragePermissionGranted())
                    return;
                String imageName = "selfie_" + System.currentTimeMillis() + ".jpg";
                chooseImage(image_directory, imageName, new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        image_path = path;
                        load_image_file(path, imageview_smile);
                        imageview_smile.setVisibility(View.VISIBLE);
                    }
                });

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.harness_radioBtn:
                slctd_harnss = ((RadioButton) view.findViewById(harnss_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                  Log.e("harnss_rg"," value "+slctd_harnss);
                break;
//            case R.id.helmet_btn:
//                slctd_helmet = ((RadioButton) view.findViewById(helmet_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                  Log.e("slctd_helmet"," value "+slctd_helmet);
//                break;
//            case R.id.safty_btn:
//                slctd_safty = ((RadioButton) view.findViewById(safty_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                  Log.e("slctd_safty"," value "+slctd_safty);
//                break;
//            case R.id.gloves_btn:
//                slctd_gloves = ((RadioButton) view.findViewById(gloves_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                  Log.e("slctd_gloves"," value "+slctd_gloves);
//                break;
//            case R.id.goggle_btn:
//                slctd_goggle = ((RadioButton) view.findViewById(goggle_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                //   Log.e("slctd_goggle"," value "+slctd_goggle);
//                break;
//            case R.id.wrk_position_rg:
//                slctd_wrk_position = ((RadioButton) view.findViewById(wrk_position_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                 Log.e("slctd_wrk_position"," value "+slctd_wrk_position);
//                break;
//            case R.id.ppe_lst_rg:
//                slctd_ppe_lst = ((RadioButton) view.findViewById(ppe_lst_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                 Log.e("slctd_ppe_lst"," value "+slctd_ppe_lst);
//                break;
            case R.id.traning1_rg:
                slctd_traning1 = ((RadioButton) view.findViewById(traning1_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_traning1"," value "+slctd_traning1);
                break;

            case R.id.situation1_rg:
                slctd_situation1 = ((RadioButton) view.findViewById(situation1_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_situation1"," value "+slctd_situation1);
                break;
//            case R.id.situation2_rg:
//                slctd_situation2 = ((RadioButton) view.findViewById(situation2_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                Log.e("slctd_situation2"," value "+slctd_situation2);
//                break;

            case R.id.equipment1_rg:
                slctd_equipment1 = ((RadioButton) view.findViewById(equipment1_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_equipment1"," value "+slctd_equipment1);
                break;

            case R.id.medical1_rg:
                slctd_medical1 = ((RadioButton) view.findViewById(medical1_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_medical1"," value "+slctd_medical1);
                break;
            case R.id.medical2_rg:
                slctd_medical2 = ((RadioButton) view.findViewById(medical2_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_medical2"," value "+slctd_medical2);
                break;
//            case R.id.medical3_rg:
//                slctd_medical3 = ((RadioButton) view.findViewById(medical3_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                Log.e("slctd_medical3"," value "+slctd_medical3);
//                break;

            case R.id.misclncs1_rg:
                slctd_misclncs1 = ((RadioButton) view.findViewById(misclncs1_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
//                Log.e("slctd_misclncs1"," value "+slctd_misclncs1);
                break;
//            case R.id.misclncs2_rg:
//                slctd_misclncs2 = ((RadioButton) view.findViewById(misclncs2_rg.getCheckedRadioButtonId())).getText().toString().toLowerCase();
////                Log.e("slctd_misclncs2"," value "+slctd_misclncs2);
//                break;
        }
    }

    public void gonull_focus() {
        if (imageview_smile.getVisibility() == View.GONE || imageview_smile.getVisibility() == View.INVISIBLE || image_path.equals("") || !new File(image_path).exists()) {
            Toast.makeText(getActivity().getApplicationContext(), getResString("lbl_pls_takePicture"), Toast.LENGTH_SHORT).show();
        } else if (batch_wedt.getText().toString().equals("")) {
            focusOnView(batch_wedt);
        } else if (serial_wedt.getText().toString().equals("")) {
            focusOnView(serial_wedt);
        } else if (prmt_vlid_txt.getText().toString().equals("")) {
            focusOnView(prmt_vlid_txt);
        } else if (slctd_harnss == null) {
            focusOnView(harnss_rg);
//        }  else if (slctd_helmet == null) {
//            focusOnView(helmet_rg);
//        } else if (slctd_safty == null) {
//            focusOnView(safty_rg);
//        } else if (slctd_gloves == null) {
//            focusOnView(gloves_rg);
//        } else if (slctd_goggle == null) {
//            focusOnView(goggle_rg);
//        } else if (slctd_wrk_position == null) {
//            focusOnView(wrk_position_rg);
//        } else if (slctd_ppe_lst == null) {
//            focusOnView(ppe_lst_rg);
        } else if (slctd_traning1 == null) {
            focusOnView(traning1_rg);
        } else if (slctd_situation1 == null) {
            focusOnView(situation1_rg);
//        } else if (slctd_situation2 == null) {
//            focusOnView(situation2_rg);
        } else if (slctd_equipment1 == null) {
            focusOnView(equipment1_rg);
        } else if (slctd_medical1 == null) {
            focusOnView(medical1_rg);
        } else if (slctd_medical2 == null) {
            focusOnView(medical2_rg);
//        } else if (slctd_medical3 == null) {
//            focusOnView(medical3_rg);
        } else if (slctd_misclncs1 == null) {
            focusOnView(misclncs1_rg);
//        } else if (slctd_misclncs2 == null) {
//            focusOnView(misclncs2_rg);
        } else {
            save_data();
        }
    }

    public void save_data() {
        WorkPermitTable.WorkPermit_Dao workPermitDao = AppController.getInstance().getDatabase().getWorkPermit_Dao();
        WorkPermitTable permitTable = new WorkPermitTable();
        permitTable.set_workPermit(user_id, client_id, unique_id, client_name, "" + date_txt.getTag(), workPermit_number, cl_wrkprmt_edt.getText().toString(), sub_site_id, prmt_dtfrm_txt.getText().toString(), prmt_vlid_txt.getText().toString()
                , site, String.valueOf(baseActivity.curr_lat), String.valueOf(baseActivity.curr_lng), asset, batch_wedt.getText().toString(), serial_wedt.getText().toString(), slctd_harnss
                , slctd_traning1, slctd_situation1, slctd_equipment1, slctd_medical1, slctd_medical2, slctd_misclncs1, image_path, baseActivity.Date_Format().format(new Date()));
        long id = workPermitDao.insert(permitTable);
        if (id > 0) {
            load_fragment();
        } else {
            show_snak(baseActivity, getResString("lbl_try_again_msg"));
        }
    }

    public void show_Date_piker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                c.set(year, month, day);
                String date = baseActivity.Date_Format().format(c.getTime());
                prmt_vlid_txt.setText(date);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, mYear, mMonth, mDay);
        View view = getActivity().getLayoutInflater().inflate(R.layout.date_ptitle_lay, null);
        dialog.setCustomTitle(view);
        dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        dialog.show();
    }

    private void focusOnView(View target_view) {
        Toast.makeText(getActivity().getApplicationContext(), getResString("lbl_field_mndtry"), Toast.LENGTH_SHORT).show();
        target_view.getParent().requestChildFocus(target_view, target_view);
    }

    public void load_fragment() {
        InspectionFragment inspectionFragment = new InspectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("client_name", client_name);
        bundle.putString("job_no", project_no);
        bundle.putString("site", site);
        bundle.putString("mdata_id", masterData_model.getMdata_id());
        bundle.putString("site_id", site_id);
        bundle.putString("sms", sms_no);
        bundle.putString("sub_site_id", sub_site_id);
        bundle.putString("input_method", input_method);
        bundle.putString("rfid", rfid);
        bundle.putString("uin", uin);
        bundle.putString("barcode", barcode);
        bundle.putString("asset", asset);
        bundle.putString("asset_code", asset_code);
        bundle.putString("po_no", po_no);
        bundle.putString("batch_no", batch_wedt.getText().toString());
        bundle.putString("serial_no", serial_wedt.getText().toString());
        bundle.putString("image_url", image_url);
        bundle.putString("report_no", report_no);
//        bundle.putString("latitude", String.valueOf(lat));
//        bundle.putString("longitude", String.valueOf(longi));
        bundle.putString("latitude", site_model.getSite_lattitude());
        bundle.putString("longitude", site_model.getSite_longitude());
        bundle.putString("approved_status", approved_status);
        bundle.putString("inspected_status", inspected_status);
        inspectionFragment.setArguments(bundle);
        LoadFragment.replace(inspectionFragment, getActivity(), getResString("lbl_work_permit"));
//        getActivity().getSupportFragmentManager().beginTransaction().replace(HomeActivity.fragContainer, inspectionFragment).addToBackStack(null).commit();
    }

}

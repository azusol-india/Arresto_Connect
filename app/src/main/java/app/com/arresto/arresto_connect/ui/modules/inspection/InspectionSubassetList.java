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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.inspection_tables.Asset_Positions_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Table;
import app.com.arresto.arresto_connect.ui.adapters.InspectionSubassetAdepter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.inspection.thermal.ThermalImageProcessing;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class InspectionSubassetList extends Base_Fragment implements View.OnClickListener {
    public static ArrayList<Integer> subasset_selectedPosition = new ArrayList<>();
    ListView listView;
    MaterialButton continueBtn;
    String[] nitem;
    ArrayList<String> sub_assets, sub_assets_imgs;
    String component_sub_assets, asset;
    int component_pos;
    List<Integer> inspected_Subasset_pos;
    View view;
    InspectionSubassetAdepter subassetAdepter;
    Inspection_Table.Inspection_Asset_Dao inspectionAssetDao;
    boolean isThermal;
    public String inspection_id;

    public View FragmentView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.inspection_list_items, container, false);
        inspectionAssetDao = AppController.getInstance().getDatabase().getInspection_Asset_dao();
        if (getArguments() != null) {
            component_pos = getArguments().getInt("component_pos");
            asset = getArguments().getString("asset");
            inspection_id = getArguments().getString("inspection_id", "");
            component_sub_assets = getArguments().getString("component_sub_assets");
            isThermal = getArguments().getBoolean("isThermal");
        }
        find_id(view);
//        for database
        inspected_Subasset_pos = new ArrayList<>();
        sub_assets = new ArrayList<>();
        sub_assets_imgs = new ArrayList<>();
        inspected_Subasset_pos = inspectionAssetDao.getInspected_subAsset_Positions(user_id, unique_id, asset, component_pos);
        nitem = component_sub_assets.split("##");
        for (String item : nitem) {
            String[] nw_itm = item.split("#");
            sub_assets.add(nw_itm[0]);
            if (nw_itm.length > 1)
                sub_assets_imgs.add(nw_itm[1]);
            else sub_assets_imgs.add("");
        }
        Log.e("sub_assets ", " " + sub_assets);
        Log.e("sub_assets_imgs ", " " + sub_assets_imgs);
        subassetAdepter = new InspectionSubassetAdepter(getActivity(), asset, sub_assets, sub_assets_imgs, inspected_Subasset_pos, component_pos, status, inspection_id);
        listView.setAdapter(subassetAdepter);
        check_alert();
        return view;
    }

    TextView pass_btn, fail_btn;

    private void find_id(View view) {
        pass_btn = view.findViewById(R.id.pass_btn);
        fail_btn = view.findViewById(R.id.fail_btn);
        view.findViewById(R.id.disclaimer_txt).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btn_section).setVisibility(View.VISIBLE);
        listView = view.findViewById(R.id.listViewInsp);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_lay, listView, false);
        continueBtn = footerView.findViewById(R.id.continue_btn_inlist);
//        if (Fl_CLIENT_ID.equals("")) {
        if (isThermal) {
            RelativeLayout thermo_btn = footerView.findViewById(R.id.thermo_btn);
            footerView.findViewById(R.id.view_line).setVisibility(View.VISIBLE);
            thermo_btn.setVisibility(View.VISIBLE);
            thermo_btn.setOnClickListener(this);
        }
//        }
        listView.addFooterView(footerView);
        pass_btn.setOnClickListener(this);
        fail_btn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);
    }

    String status = "";
    public String image_dir = directory + "inspection/" + unique_id + "/thermal/";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pass_btn:
                status = "Pass";
                if (subassetAdepter != null) {
                    subassetAdepter.updateStatus(status);
                }
                break;
            case R.id.fail_btn:
                status = "Fail";
                if (subassetAdepter != null) {
                    subassetAdepter.updateStatus(status);
                }

                break;
            case R.id.thermo_btn:
                chooseThermographyImage(image_dir, new Base_Fragment.OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        if (!path.equals("")) {
                            synchronized (view) {
                                Intent camera = new Intent(getActivity(), ThermalImageProcessing.class);
                                camera.putExtra("imagePath", path);
                                camera.putExtra("sub_assets", sub_assets);
                                camera.putExtra("image_dir", image_dir);
                                camera.putExtra("asset_code", asset);
                                camera.putExtra("uin", selectedMasterData_model.getMdata_uin());
                                camera.putExtra("master_id", selectedMasterData_model.getMdata_id());
                                camera.putExtra("unique_id", unique_id);
                                startActivity(camera);
                            }
                        } else {
                            show_snak(baseActivity, "Please select or capture an image.");
                        }
                    }
                });
                break;
            case R.id.continue_btn_inlist:
                if (!inspection_id.equals("")) {
                    getActivity().onBackPressed();
                } else {
                    if (status.equals("")) {
                        if (subasset_selectedPosition.size() == sub_assets.size()) {
                            AppController.getInstance().getDatabase().getAsset_position_dao().insert(new Asset_Positions_Table(user_id, Static_values.unique_id, component_pos));
                            if (!InspectionListItems.selectedPosition.contains(component_pos)) {
                                InspectionListItems.selectedPosition.add(component_pos);
                            }
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getResString("lbl_inspect_msg"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        for (int i = 0; i < sub_assets.size(); i++) {
                            if (!subasset_selectedPosition.contains(i)) {
                                Inspection_Table inspection_table = new Inspection_Table();
                                inspection_table.set_inspection_Asset(user_id, Static_values.unique_id, asset, sub_assets.get(i), "", "", "", "", status, "", component_pos, i);
                                inspectionAssetDao.insert(inspection_table);
                            }
                        }
                        if (!InspectionListItems.selectedPosition.contains(component_pos)) {
                            InspectionListItems.selectedPosition.add(component_pos);
                        }
                        getActivity().onBackPressed();
//                    create_RemarkDialog();
                    }
                }
                break;
        }
    }

    public void create_RemarkDialog() {
        final Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        builder.setContentView(R.layout.dialog_uin);
        TextView cancel_btn = builder.findViewById(R.id.cncl_btn);
        TextView ok_btn = builder.findViewById(R.id.ok_btn);
        final EditText edt_dialog = builder.findViewById(R.id.edt_dialog);
        builder.findViewById(R.id.header).setVisibility(View.VISIBLE);
        builder.findViewById(R.id.ex_tv).setVisibility(View.GONE);
        builder.findViewById(R.id.logo_img).setVisibility(View.GONE);

        edt_dialog.setHint("Remark");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark = edt_dialog.getText().toString();
                for (int i = 0; i < sub_assets.size(); i++) {
                    if (!subasset_selectedPosition.contains(i)) {
                        Inspection_Table inspection_table = new Inspection_Table();
                        inspection_table.set_inspection_Asset(user_id, Static_values.unique_id, asset, sub_assets.get(i), "", "", "", "", status, remark, component_pos, i);
                        inspectionAssetDao.insert(inspection_table);
                    }
                }
                if (!InspectionListItems.selectedPosition.contains(component_pos)) {
                    InspectionListItems.selectedPosition.add(component_pos);
                }
                builder.dismiss();
                getActivity().onBackPressed();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }

    private void check_alert() {
        if (!mPrefrence.getBoolean("subasset_alert")) {
            mPrefrence.saveBoolean("subasset_alert", true);
            final Dialog dialog = new Dialog(getActivity(), R.style.theme_dialog);
            dialog.setContentView(R.layout.dialog_alert);
            final TextView heading_tv = dialog.findViewById(R.id.heading_tv);
            final TextView msg_tv = dialog.findViewById(R.id.msg_tv);
            MaterialButton ok_btn = dialog.findViewById(R.id.ok_btn);
            MaterialButton delete_btn = dialog.findViewById(R.id.delete_btn);
            delete_btn.setVisibility(View.GONE);
            ok_btn.setVisibility(View.VISIBLE);
//        heading_tv.setText(getResString("Alert"));
            msg_tv.setText(getResString("lbl_allpass_allfail"));
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }
}

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Table;
import app.com.arresto.arresto_connect.ui.adapters.InspectionListAdptr;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectorSignature;
import app.com.arresto.arresto_connect.ui.modules.inspection.Spares_Quantity_Fragemnt;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class InspectionListItems extends Fragment {
    public static ArrayList<Integer> selectedPosition = new ArrayList<>();
    ListView listView;
    MaterialButton continueBtn;
    String[] seprated_asset_data;
    List<Integer> dbasset_code;
    ArrayList<String> nitem, no_of_lines, asset_code1, asset_code, uom, triple_hash, asset_image_url, final_asset_urls;
    public static boolean isSpare;
    public static String spare_code;
    public String inspection_id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView:InspectionListItems");
        View view = inflater.inflate(R.layout.inspection_list_items, container, false);
//        for db
        isSpare = false;
        spare_code = "";
        dbasset_code = new ArrayList<>();
        dbasset_code = AppController.getInstance().getDatabase().getAsset_position_dao().getInspected_Asset_Positions(user_id, unique_id);


        if (getArguments() != null) {
            seprated_asset_data = getArguments().getStringArray("asset_code");
            inspection_id = getArguments().getString("inspection_id", "");
        }

        listView = view.findViewById(R.id.listViewInsp);
        nitem = new ArrayList<>();
        asset_code = new ArrayList<>();
        asset_code1 = new ArrayList<>();
        no_of_lines = new ArrayList<>();
        asset_image_url = new ArrayList<>();
        final_asset_urls = new ArrayList<>();

        uom = new ArrayList<>();
        triple_hash = new ArrayList<>();

        if (seprated_asset_data[0].length() < 3) {
            alert_box("Assets code not Define.\nPlease Contact to Administration for Missing data.");
        } else {
            for (int a = 0; a < seprated_asset_data.length; a++) {
                String[] line = seprated_asset_data[a].split("###");
                if (line.length > 1) {
                    Log.e("asset_code1  ", a + " " + line[0]);
                    Log.e("asset_image_url  ", " " + line[1]);
                    asset_code1.add(line[0]);
                    asset_image_url.add(line[1]);
                }
                //            asset_image_url.add("http://karam.in/kare_demo//uploads/images/assets/1470141393.gif");
            }
            for (int y = 0; y < asset_code1.size(); y++) {
                String[] line = asset_code1.get(y).split("##");
                triple_hash.add(line[0]);
                uom.add(line[1]);
            }
            for (int k = 0; k < triple_hash.size(); k++) {
                String[] line = triple_hash.get(k).split("#");
                asset_code.add(line[0]);
                no_of_lines.add(line[1]);
            }
            for (int i = 0; i < asset_code1.size(); i++) {
                if (Static_values.selected_Site_model.getSite_id() != null && !Static_values.selected_Site_model.getSite_id().equals("")) {
                    if (no_of_lines.get(i).equals("NO") || no_of_lines.get(i).equals("0")) {
                        Log.e("there are no line for", " " + asset_code.get(i));
                    } else {
                        int val = Integer.parseInt(no_of_lines.get(i));
                        if (val > 0 && !uom.get(i).equalsIgnoreCase("mtr")) {
                            for (int j = 0; j < val; j++) {
                                nitem.add(asset_code.get(i));
                                final_asset_urls.add(asset_image_url.get(i));
                            }
                        } else {
                            nitem.add(asset_code.get(i));
                            final_asset_urls.add(asset_image_url.get(i));
                        }
                    }
                } else {
                    nitem.add(asset_code.get(i));
                    final_asset_urls.add(asset_image_url.get(i));
                }
            }
        }

        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_lay, listView, false);

        listView.setAdapter(new InspectionListAdptr(getActivity(), nitem, dbasset_code, final_asset_urls, inspection_id));
        listView.addFooterView(footerView);

        continueBtn = footerView.findViewById(R.id.continue_btn_inlist);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!inspection_id.equals("")) {
                    getActivity().onBackPressed();
                } else {
                    if (selectedPosition.size() == nitem.size()) {
                        if (isSpare)
                            LoadFragment.replace(Spares_Quantity_Fragemnt.newInstance(spare_code), getActivity(), getResString("lbl_select_spares"));
                        else
                            LoadFragment.replace(InspectorSignature.newInstance("inspection"), getActivity(), getResString("lbl_inspector_remark"));
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getResString("lbl_inspect_msg"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        checkIsSpare();
        return view;
    }

    //    getResString("lbl_remove_and_repair")
    private void checkIsSpare() {
        Inspection_Table.Inspection_Asset_Dao inspectionAssetDao = AppController.getInstance().getDatabase().getInspection_Asset_dao();
        List<Inspection_Table> allInspected_asset = inspectionAssetDao.getAllInspected_Asset(user_id, unique_id);
        for (Inspection_Table inspectAsset : allInspected_asset) {
            String subAsset = inspectAsset.getSubAsset().toLowerCase();
            String asset = inspectAsset.getAsset().toLowerCase();
            if (subAsset != null && (subAsset.startsWith("pn9000") || subAsset.startsWith("pn-9000") || subAsset.startsWith("pn7000") ||
                    subAsset.startsWith("pn-7000")) && inspectAsset.getResult().contains(getResString("lbl_remove_and_repair"))) {
                isSpare = true;
                spare_code = inspectAsset.getSubAsset();
                break;
            } else if (asset != null && (asset.startsWith("pn9000") || asset.startsWith("pn-9000") || asset.startsWith("pn7000") ||
                    asset.startsWith("pn-7000")) && inspectAsset.getResult().contains(getResString("lbl_remove_and_repair"))) {
                isSpare = true;
                spare_code = inspectAsset.getAsset();
                break;
            }
        }
    }


    public void alert_box(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//        alertDialog.setTitle("Booking Confirmation Message");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}

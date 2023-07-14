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

package app.com.arresto.arresto_connect.ui.adapters;

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionAssetFragment;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionAssetFragment_V1;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionSubassetList;

public class InspectionSubassetAdepter extends ArrayAdapter<String> {
    private Activity activity;
    private ArrayList<String> pass_fail_imagepath = new ArrayList<>();
    private ArrayList<String> string;
    private int component_pos;
    private String main_asset;
    private List<Integer> value_indb;
    private ArrayList<String> sub_assets_imgs;
    private String description, uom, observation, result, inspection;
    private Data_daowload data_daowload;
    String status,inspection_id;

    public InspectionSubassetAdepter(Activity activity, String main_asset, ArrayList<String> string, ArrayList<String> sub_assets_imgs, List<Integer> value_indb,
                                     int component_pos, String status, String inspection_id) {
        super(activity, R.layout.inspection_list_adptr, string);
        this.activity = activity;
        this.status = status;
        this.main_asset = main_asset;
        this.string = string;
        this.sub_assets_imgs = sub_assets_imgs;
        this.value_indb = value_indb;
        this.component_pos = component_pos;
        this.inspection_id = inspection_id;
        data_daowload = new Data_daowload(activity);
    }

    public void updateStatus(String status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.inspection_list_adptr, null, true);
        final TextView textView = view.findViewById(R.id.text_insplist);
        final RelativeLayout compView = view.findViewById(R.id.insp_rel_lay);
        final ImageView imageView = view.findViewById(R.id.asset_img);
        final ImageView check_img = view.findViewById(R.id.check_img);
        final ImageView indicator_img = view.findViewById(R.id.indicator_img);

        textView.setText(string.get(position));
        if (InspectionSubassetList.subasset_selectedPosition.contains(position) || value_indb.contains(position)) {
//            compView.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
            if (!InspectionSubassetList.subasset_selectedPosition.contains(position)) {
                InspectionSubassetList.subasset_selectedPosition.add(position);
            }
            check_img.setVisibility(View.VISIBLE);
            check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.inspected));
            ImageViewCompat.setImageTintList(indicator_img, ColorStateList.valueOf(getResColor(R.color.yellow_bg)));
        } else {
            check_img.setVisibility(View.GONE);
//            compView.setBackgroundColor(getResColor(R.color.app_text));
            if (status.equalsIgnoreCase("pass")) {
                check_img.setVisibility(View.VISIBLE);
                check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.approved));
                ImageViewCompat.setImageTintList(indicator_img, ColorStateList.valueOf(getResColor(R.color.app_green)));
            } else if (status.equalsIgnoreCase("fail")) {
                check_img.setVisibility(View.VISIBLE);
                check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.rejected));
                ImageViewCompat.setImageTintList(indicator_img, ColorStateList.valueOf(getResColor(R.color.app_error)));
            }
        }

        String url = sub_assets_imgs.get(position);

        if (!url.equals(Host)) {
            url = url.replace(" ", "%20");

            AppUtils.load_image(url, imageView);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InspectionSubassetList.subasset_selectedPosition.contains(position) || value_indb.contains(position)) {
                    show_snak(activity, getResString("lbl_subalrdy_ins_msg"));
                } else {
                    if (isNetworkAvailable(activity)) {
                        if(!string.get(position).equals(""))
                        myService1(string.get(position), position);
                    } else {
                        select_mdata(string.get(position), position);
                    }
                }
            }

        });
        return view;
    }

    private void myService1(final String assetcode, final int pos1) {
        String url = All_Api.subasset_service + assetcode.trim() + "?client_id=" + client_id;
        url = url.replace(" ", "%20");
        NetworkRequest networkRequest = new NetworkRequest(activity);
        networkRequest.make_get_request(All_Api.subasset_service + assetcode + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("status_code")) {
                            if (jsonObject.getString("status_code").equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jresponse = data.getJSONObject(0);
                                    description = jresponse.getString("sub_assets_description");
                                    uom = jresponse.getString("sub_assets_uom");
                                    inspection = jresponse.getString("sub_assets_inspection");
                                    result = jresponse.getString("sub_assets_result");
                                    observation = jresponse.getString("sub_assets_observation");
                                    pass_fail_imagepath.add(jresponse.getString("pass_imagepath"));
                                    pass_fail_imagepath.add(jresponse.getString("fail_imagepath"));
                                    pass_fail_imagepath.add(jresponse.getString("repair_imagepath"));

                                    set_bundule(pos1);
                                }
                            } else {
                                show_snak(activity, "" + jsonObject.getString("message"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
            }
        });

    }

    private void select_mdata(String assetcode, int position) {
        data_daowload.open();
//            Log.e("this is a subasset ",""+assetcode) ;
        String subquery = "select code,sub_description,sub_uom,sub_inspection,sub_result," +
                "sub_observation,sub_pass_imagepath,sub_fail_imagepath,sub_repair_imagepath from subasset where code='" + assetcode + "'";
        Cursor c1 = data_daowload.getSingle_Rowdata(subquery);
        if (c1.getCount() > 0) {
//                Log.e("this is a subasset ",""+assetcode) ;
            c1.moveToFirst();
            do {
                DisplayContact(c1);
            } while (c1.moveToNext());
            set_bundule(position);
        }
        data_daowload.close();
    }

    private void DisplayContact(Cursor c) {
        // TODO Auto-generated method stub

//        String code = c.getString(0);
        description = c.getString(1);
        uom = c.getString(2);
        inspection = c.getString(3);
        result = c.getString(4);
        observation = c.getString(5);
        pass_fail_imagepath.add(c.getString(6));
        pass_fail_imagepath.add(c.getString(7));
        pass_fail_imagepath.add(c.getString(8));

    }

    private void set_bundule(int position) {
        Fragment inspectionAbImage;
        if (client_id.equals("419") || Fl_CLIENT_ID.equals("419"))
            inspectionAbImage = new InspectionAssetFragment_V1();
        else
            inspectionAbImage = new InspectionAssetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("description", description);
        bundle.putString("uom", uom);
        bundle.putString("component_sub_assets", string.get(position));
        bundle.putString("inspection", inspection);
        bundle.putString("result", result);
        bundle.putString("asset", main_asset);
        bundle.putString("observation", observation);
        bundle.putString("inspection_id", inspection_id);
        bundle.putStringArrayList("pass_fail_imagepath", pass_fail_imagepath);
        bundle.putInt("position", position);
        bundle.putInt("component_pos", component_pos);
        inspectionAbImage.setArguments(bundle);

//        File dir = new File(directory + "inspection_image/"); // Delete data when zip created last subasset inspection
//        if (dir.exists()) {
//            deleteRecursive(dir);
//        }
        LoadFragment.replace(inspectionAbImage, activity, getResString("lbl_inspection"));
    }


}

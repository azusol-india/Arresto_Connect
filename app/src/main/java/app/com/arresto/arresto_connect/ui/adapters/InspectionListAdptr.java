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

import androidx.appcompat.app.AppCompatActivity;
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
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionSubassetList;

public class InspectionListAdptr extends ArrayAdapter<String> {
    private AppCompatActivity activity;
    private ArrayList<String> assets, assetimage_url, pass_fail_imagepath = new ArrayList<>();
    private List<Integer> inspctd_ast;
    private String description, uom, observation, result, inspection, component_sub_assets, inspection_id;
    private Data_daowload data_daowload;

    public InspectionListAdptr(Activity activity, ArrayList<String> string, List<Integer> inspctd_ast, ArrayList<String> image_urls, String inspection_id) {
        super(activity, R.layout.inspection_list_adptr, string);
        this.activity = (AppCompatActivity) activity;
        this.assets = string;
        this.inspctd_ast = inspctd_ast;
        this.assetimage_url = image_urls;
        this.inspection_id = inspection_id;
        data_daowload = new Data_daowload(activity);
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.inspection_list_adptr, null, true);
        final TextView textView = view.findViewById(R.id.text_insplist);
        final ImageView imageView = view.findViewById(R.id.asset_img);
        final RelativeLayout compView = view.findViewById(R.id.insp_rel_lay);
        final ImageView indicator_img = view.findViewById(R.id.indicator_img);

        textView.setText(assets.get(position));
        if (InspectionListItems.selectedPosition.contains(position) || inspctd_ast.contains(position)) {
//            compView.setBackgroundColor(getResColor(R.color.app_green));
            ImageViewCompat.setImageTintList(indicator_img, ColorStateList.valueOf(getResColor(R.color.app_green)));
            if (!InspectionListItems.selectedPosition.contains(position)) {
                InspectionListItems.selectedPosition.add(position);
            }
        } else {
            ImageViewCompat.setImageTintList(indicator_img, ColorStateList.valueOf(getResColor(R.color.transparent)));
//            compView.setBackgroundColor(getResColor(R.color.app_text));
        }

        if (!assetimage_url.get(position).equals("NO")) {
            AppUtils.load_image(assetimage_url.get(position), imageView);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InspectionListItems.selectedPosition.contains(position) || inspctd_ast.contains(position)) {
                    show_snak(activity, getResString("lbl_alrdy_ins_msg"));
                } else {

                    if (isNetworkAvailable(activity)) {
                        myService(assets.get(position), position);
                    } else {
                        select_mdata(assets.get(position), position);
                    }
                }
            }

        });
        return view;
    }

    private void myService(final String assetcode, final int pos) {
        String url = All_Api.components_service + assetcode + "?client_id=" + client_id;
        url = url.replace(" ", "%20");
        url = url.replace("&", "Andamp");
        NetworkRequest networkRequest = new NetworkRequest(activity);
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
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
//                                for (int i = 0; i < data.length(); i++) {
                                JSONObject jresponse = data.getJSONObject(0);
                                description = jresponse.getString("component_description");
                                component_sub_assets = jresponse.getString("component_sub_assets");
                                uom = jresponse.getString("component_uom");
                                inspection = jresponse.getString("component_inspectiontype");
                                result = jresponse.getString("component_expectedresult");
                                observation = jresponse.getString("component_observation");
                                pass_fail_imagepath.add(jresponse.getString("component_pass_imagepath"));
                                pass_fail_imagepath.add(jresponse.getString("component_fail_imagepath"));
                                pass_fail_imagepath.add(jresponse.getString("component_repair_imagepath"));
                                set_bundule("asset", assetcode, pos);
                            }
                        } else {
                            if (!assetcode.equals(""))
                                myService1(assetcode, pos);
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

    private void myService1(final String assetcode1, final int pos1) {
        NetworkRequest networkRequest = new NetworkRequest(activity);
        networkRequest.make_get_request(All_Api.subasset_service + assetcode1 + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
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
                                    JSONObject jresponse = data.getJSONObject(i);
                                    description = jresponse.getString("sub_assets_description");
                                    uom = jresponse.getString("sub_assets_uom");
                                    inspection = jresponse.getString("sub_assets_inspection");
                                    result = jresponse.getString("sub_assets_result");
                                    observation = jresponse.getString("sub_assets_observation");
                                    pass_fail_imagepath.add(jresponse.getString("pass_imagepath"));
                                    pass_fail_imagepath.add(jresponse.getString("fail_imagepath"));
                                    pass_fail_imagepath.add(jresponse.getString("repair_imagepath"));
//                        pass_fail_imagepath .add("http://karam.in/kare_demo/uploads/images/featured/pass/pass1470141522.png");
//                        pass_fail_imagepath .add("http://karam.in/kare_demo/uploads/images/featured/fail/fail1470141522.png");
//                        pass_fail_imagepath .add("http://karam.in/kare_demo/uploads/images/featured/repair/repair1470141522.jpg");
                                }
                                set_bundule("subasset", assetcode1, pos1);
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
        String query = "select code,component_description,component_sub_assets,uom,inspection_type,expected_result," +
                "observation,pass_imagepath,fail_imagepath,repair_imagepath from components where code='" + assetcode + "'";
        Cursor c = data_daowload.getSingle_Rowdata(query);
//        Log.e("Cursor", "     " + c.getCount());

        if (c.getCount() > 0) {
//            Log.e("this is a asset ", "" + assetcode);
            c.moveToFirst();
            do {
                DisplayContact(c, "asset");
            } while (c.moveToNext());
            set_bundule("asset", assetcode, position);
        } else {
//            Log.e("this is a subasset ", "" + assetcode);
            String subquery = "select code,sub_description,sub_uom,sub_inspection,sub_result," +
                    "sub_observation,sub_pass_imagepath,sub_fail_imagepath,sub_repair_imagepath from subasset where code='" + assetcode + "'";
            Cursor c1 = data_daowload.getSingle_Rowdata(subquery);
//            Log.e("sub Cursor ", "     " + c1.getCount());
            if (c1.getCount() > 0) {
//                Log.e("this is a subasset ", "" + assetcode);
                c1.moveToFirst();
                do {
                    DisplayContact(c1, "subasset");
                } while (c1.moveToNext());
                set_bundule("subasset", assetcode, position);
            }
            data_daowload.close();
        }
    }

    private void DisplayContact(Cursor c, String type) {
        // TODO Auto-generated method stub
        if (type.equals("asset")) {
//            String code = c.getString(0);
            description = c.getString(1);
            component_sub_assets = c.getString(2);
            uom = c.getString(3);
            inspection = c.getString(4);
            result = c.getString(5);
            observation = c.getString(6);
            pass_fail_imagepath.add(c.getString(7));
            pass_fail_imagepath.add(c.getString(8));
            pass_fail_imagepath.add(c.getString(9));
        } else {
//            String code = c.getString(0);
            description = c.getString(1);
            uom = c.getString(2);
            inspection = c.getString(3);
            result = c.getString(4);
            observation = c.getString(5);
            pass_fail_imagepath.add(c.getString(6));
            pass_fail_imagepath.add(c.getString(7));
            pass_fail_imagepath.add(c.getString(8));
        }
    }

    private void set_bundule(String type, String assetcode, int position) {
        Fragment inspectionAbImage;
        if (client_id.equals("419") || Fl_CLIENT_ID.equals("419"))
            inspectionAbImage = new InspectionAssetFragment_V1();
        else
            inspectionAbImage = new InspectionAssetFragment();
        Bundle bundle = new Bundle();
        if (type.equals("asset")) {
            bundle.putString("description", description);
            bundle.putString("uom", uom);
            bundle.putString("component_sub_assets", component_sub_assets);
            bundle.putString("inspection", inspection);
            bundle.putString("result", result);
            bundle.putString("asset", assetcode);
            bundle.putString("observation", observation);
            bundle.putString("inspection_id", inspection_id);
            bundle.putStringArrayList("pass_fail_imagepath", pass_fail_imagepath);
            bundle.putInt("component_pos", position);
//            File dir = new File(directory + "inspection_image/");
//            if (dir.exists()) {
//                deleteRecursive(dir);
//            }
            if (component_sub_assets.equals("0") || component_sub_assets.equals("")) {
                inspectionAbImage.setArguments(bundle);
                LoadFragment.replace(inspectionAbImage, activity, getResString("lbl_inspection"));
            } else {
                InspectionSubassetList.subasset_selectedPosition.clear();
                if (assets.size() == 1 && (client_id.equals("419") || Fl_CLIENT_ID.equals("419")))
                    bundle.putBoolean("isThermal", true);
                else
                    bundle.putBoolean("isThermal", false);
                InspectionSubassetList inspectionSubassetList = new InspectionSubassetList();
                inspectionSubassetList.setArguments(bundle);
                LoadFragment.replace(inspectionSubassetList, activity, "" + assetcode);
            }
        } else {
            bundle.putString("description", description);
            bundle.putString("component_sub_assets", "0");
            bundle.putString("uom", uom);
            bundle.putString("inspection", inspection);
            bundle.putString("result", result);
            bundle.putString("asset", assetcode);
            bundle.putString("observation", observation);
            bundle.putStringArrayList("pass_fail_imagepath", pass_fail_imagepath);
            bundle.putInt("component_pos", position);
            inspectionAbImage.setArguments(bundle);
//            File dir = new File(directory + "inspection_image/"); // Delete data when zip created last asset inspection
//            if (dir.exists())
//                deleteRecursive(dir);
            LoadFragment.replace(inspectionAbImage, activity, getResString("lbl_inspection"));
        }
    }

}

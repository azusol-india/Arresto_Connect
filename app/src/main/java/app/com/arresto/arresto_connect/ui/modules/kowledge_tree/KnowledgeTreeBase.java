package app.com.arresto.arresto_connect.ui.modules.kowledge_tree;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.Connected_ClientsModel;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.AutoComplete_Adapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

public abstract class KnowledgeTreeBase extends Base_Fragment {
    AutoCompleteTextView srch_prdct;
    AutoComplete_Adapter search_adapter;
    ImageView search_btn;
    ArrayList data_search;
    public static Connected_ClientsModel connected_clientsModel;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        data_search = DataHolder_Model.getInstance().getKnowledge_treeProducts();
        return BaseView(inflater, parent, savedInstanceState);
    }

    public abstract View BaseView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    public void setup_searchview() {
        search_adapter = new AutoComplete_Adapter(getActivity(), data_search);
        srch_prdct.setAdapter(search_adapter);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!srch_prdct.getText().toString().equals(""))
                    get_search_data(srch_prdct.getText().toString());
            }
        });
        srch_prdct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!srch_prdct.getText().toString().equals(""))
                        get_search_data(srch_prdct.getText().toString());
                    //do Whatever you Want to do
                }
                return true;
            }
        });
        srch_prdct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("onItemClick is run", " now " + adapterView.getItemAtPosition(i).toString());
                final Object item = adapterView.getItemAtPosition(i);

                Log.e("data filter", " is " + item);

                if (item instanceof Category_Model) {
                    Category_Model category = (Category_Model) item;
                    Karam_Infonetperent_frg karam_infonetperent_frg = new Karam_Infonetperent_frg();
                    Bundle bundle = new Bundle();
                    bundle.putString("slctdcat_id", category.getId());
                    bundle.putString("slctdunic_name", category.getCat_name());
                    bundle.putString("slctdprnt_id", category.getCat_parentid());
                    karam_infonetperent_frg.setArguments(bundle);
                    LoadFragment.replace(karam_infonetperent_frg, getActivity(), category.getCat_name());
                } else if (item instanceof Product_model) {
                    Product_model product = (Product_model) item;
                    ArrayList products = new ArrayList();
                    products.add(product);
                    Karam_infonet_product_frg karam_infonet_product_frgm = new Karam_infonet_product_frg();
                    Bundle bundle = new Bundle();
                    bundle.putString("products", new Gson().toJson(products));
                    bundle.putInt("pos", 0);
                    karam_infonet_product_frgm.setArguments(bundle);
                    LoadFragment.replace(karam_infonet_product_frgm, getActivity(), getResString("lbl_product_details"));
                }
            }
        });
    }

    public void get_search_data(String text) {
        String url;
        if (connected_clientsModel != null)
            url =connected_clientsModel.getApi_server()+ "/api_controller/searchProduct?item=" + text + "&client_id=" + connected_clientsModel.getClient_id() + "&date=" + System.currentTimeMillis();
        else
            url = All_Api.search_asset + text + "&client_id=" + client_id + "&date=" + System.currentTimeMillis();

        url = url.replace(" ", "%20");
        NetworkRequest networkRequest = new NetworkRequest(getActivity());
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        Log.e("response", "   " + response);
                        ArrayList<String> cat_id = new ArrayList<>(), cat_name = new ArrayList<>(), cat_parentid = new ArrayList<>(), cat_uniqueName = new ArrayList<>(),
                                cat_image = new ArrayList<>(), cat_status = new ArrayList<>();

                        if (msg_code.equals("200")) {
                                String searchItem=jsonObject.getJSONObject("data").getString("searchItem");
                            Object json1 = new JSONTokener(searchItem).nextValue();
                            String product_code="",image_url="",type="";
                            if (json1 instanceof JSONObject) {
                                JSONObject object = new JSONObject(searchItem);
                                 product_code = object.getString("item_code");
                                 image_url = object.getString("item_imagepath");
                                 type = get_type(object.getString("type"));
                            }else if (json1 instanceof JSONArray){
                                JSONObject object = new JSONArray(searchItem).getJSONObject(0);
                                 product_code = object.getString("item_code");
                                 image_url = object.getString("item_imagepath");
                                 type = get_type(object.getString("type"));
                            }

                            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("searchCategories");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                cat_id.add(jsonObject1.getString("id"));
                                cat_parentid.add(jsonObject1.getString("cat_parentid"));
                                cat_name.add(jsonObject1.getString("cat_name"));
                                cat_uniqueName.add(jsonObject1.getString("cat_uniqueName"));
                                cat_image.add(jsonObject1.getString("cat_image"));
                                cat_status.add(jsonObject1.getString("cat_status"));
                            }

                            Karam_Infonetperent_frg karam_infonetperent_frg = new Karam_Infonetperent_frg();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("slctdcat_id", cat_id);
                            bundle.putStringArrayList("slctdunic_name", cat_name);
                            bundle.putStringArrayList("slctdprnt_id", cat_parentid);
                            bundle.putStringArrayList("cat_uniqueName", cat_uniqueName);
                            bundle.putStringArrayList("cat_image", cat_image);
                            bundle.putStringArrayList("cat_status", cat_status);
                            bundle.putString("page_type", "filtered");
                            bundle.putString("product_code", product_code);
                            bundle.putString("product_image", image_url);
                            bundle.putString("product_type", type);
                            karam_infonetperent_frg.setArguments(bundle);
                            LoadFragment.replace(karam_infonetperent_frg, baseActivity, getResString("lbl_knowledge_tree"));
                        } else show_snak(getActivity(), jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " get_search_data " + message);
                show_snak(getActivity(), "No data Found");
            }
        });
    }


    public String get_type(String text) {
        switch (text) {
            case "product":
                return "asset_series";
            case "component":
                return "assets";
            case "subcomponent":
                return "sub_assets";
            default:
                return "";
        }
    }

}

package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.interfaces.OnItemClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class AddAssetPager extends Base_Fragment {
    View view;

    public static AddAssetPager newInstance() {
        AddAssetPager fragmentFirst = new AddAssetPager();
//        Bundle args = new Bundle();
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
//        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    ScrollPager scrollPager = new ScrollPager() {
        @Override
        public void scrollTo(int posotion) {
            _pager.setCurrentItem(posotion, true);
        }
    };

    public static Component_model selected_asset;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.addassetpager_layout, parent, false);
            setupUI(view, getActivity());
            findAllIds(view);
            fetch_data(All_Api.getAllAssets + client_id+ "&user_id=" + user_id);
        }
//        else {
//            if (filter_data != null)
//                set_fragment();
//        }
        return view;
    }

    ViewPager _pager;
    PagerAdapter myPagerAdapter;

    public void findAllIds(View view) {
        _pager = view.findViewById(R.id._pager);
        _pager.setOffscreenPageLimit(4);
    }

    List<String> titles = new ArrayList<>(Arrays.asList(
            getResString("lbl_add_asset_details"),
            getResString("lbl_add_asset_parameters"),
            getResString("lbl_add_frequency"),
            getResString("lbl_add_standards_certificates")));

    public void set_fragment() {
        myPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), titles);
        _pager.setAdapter(myPagerAdapter);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        List<String> Titles;

        public PagerAdapter(FragmentManager supportFragmentManager, List<String> mTitles) {
            super(supportFragmentManager);
            Titles = mTitles;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                default:
                    AddAssetPage1 page1 = AddAssetPage1.newInstance(titles.get(pos));
                    page1.setscrolleListner(scrollPager);
                    return page1;
                case 1:
                    AddAssetPage2 page2 = AddAssetPage2.newInstance(titles.get(pos));
                    page2.setscrolleListner(scrollPager);
                    return page2;
                case 2:
                    AddAssetPage3 page3 = AddAssetPage3.newInstance(titles.get(pos));
                    page3.setscrolleListner(scrollPager);
                    return page3;
                case 3:
                    AddAssetPage4 page4 = AddAssetPage4.newInstance(titles.get(pos));
                    page4.setscrolleListner(scrollPager);
                    return page4;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles.get(position);
        }

        @Override
        public int getCount() {
            return Titles.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    private ArrayList<Constant_model> assets = new ArrayList<>();

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        assets = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                        Collections.sort(assets, new Comparator<Constant_model>() {
                            public int compare(Constant_model obj1, Constant_model obj2) {
                                return obj1.getName().compareToIgnoreCase(obj2.getName());
                            }
                        });
                        chooseAssetDialog(assets);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    private void chooseAssetDialog(ArrayList<Constant_model> assets) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select Asset");
        if (assets != null && assets.size() > 0) {
            OnItemClickListener onItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position, View v, Object data) {
                    Constant_model slctdAsset = (Constant_model) data;
                    if (slctdAsset != null) {
                        get_Component_data(All_Api.components_service + slctdAsset.getId() + "?client_id=" + client_id);
                    }
                    dialog.dismiss();
                }
            };
            Log.e("data length ", " is " + assets.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), assets, onItemClickListener);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    set_fragment();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1)
//                        setSelection(ad.lastSelected);
                        dialog.cancel();
                }
            });
            srch_prdct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!srch_prdct.getText().toString().equals("")) {
                        filter(editable.toString().toLowerCase(), assets, ad);
                    }
                }
            });

        }
        dialog.show();

    }

    void filter(String filter_txt, ArrayList<Constant_model> assets, CustomRecyclerAdapter ad) {
        List<Constant_model> temp = new ArrayList();
        for (Constant_model d : assets) {
            if (d.getName().toLowerCase().contains(filter_txt)) {
                temp.add(d);
            }
        }
        ad.UpdateData(temp);
    }

    private void get_Component_data(String components_service) {
        NetworkRequest.getComponents(getActivity(), components_service, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    selected_asset = DataHolder_Model.getInstance().getComponent_models().get(0);
                }
                set_fragment();
                // process incoming messages here
                // this will run in the thread, which instantiates it
            }
        });
    }

    public interface ScrollPager {
        void scrollTo(int posotion);
    }
}

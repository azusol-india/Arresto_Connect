package app.com.arresto.arresto_connect.ui.modules.inspection;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.InspectedSiteAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Main_Fragment;

public class Recently_Inspected_page extends Base_Fragment {
    View view;


    RecyclerView sites_list;
    MaterialButton blank_btn;
    String listType = "inspection";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.recently_inspected_page, parent, false);
            sites_list = view.findViewById(R.id.sites_list);
            blank_btn = view.findViewById(R.id.blank_btn);
            if (this.getTag() != null && this.getTag().equals(getResString("lbl_recently_maintained"))) {
                listType = "periodic";
                blank_btn.setText("Start maintenance for new asset");
            }
            ArrayList<Site_Model> site_models;
            sites_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            if (listType.equals("inspection"))
                site_models = new ArrayList<>(Arrays.asList(new Gson().fromJson(AppController.getInstance().getDatabase().getSites_data_Dao().getInspectedSites_data(user_id).toString(), Site_Model[].class)));
            else
                site_models = new ArrayList<>(Arrays.asList(new Gson().fromJson(AppController.getInstance().getDatabase().getSites_data_Dao().getPdmSites_data(user_id).toString(), Site_Model[].class)));

            if (site_models != null && site_models.size() > 0) {
                InspectedSiteAdapter inspectedSiteAdapter = new InspectedSiteAdapter(baseActivity, site_models, listType);
                sites_list.setAdapter(inspectedSiteAdapter);
            } else {
                blank_btn.setVisibility(View.VISIBLE);
                blank_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Main_Fragment main_fragment = new Main_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("page_type", listType);
                        main_fragment.setArguments(bundle);
                        ((HomeActivity) baseActivity).clear_all_fragment();
                        LoadFragment.add(main_fragment, baseActivity, getResString("lbl_scan_txt"));
                    }
                });
            }
        }
        return view;
    }
}

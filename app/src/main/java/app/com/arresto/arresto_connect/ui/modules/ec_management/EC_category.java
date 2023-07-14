/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.ui.adapters.EC_category_Adapter;

import static app.com.arresto.arresto_connect.constants.Static_values.nested_catgrs;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;

public class EC_category extends Fragment {
    private View view;
    private RecyclerView select_systems_list;
    private EC_category_Adapter selected_category_adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ec_home, container, false);
            findAllIds(view);
            if (getArguments() != null) {
                String slctdcat_id = getArguments().getString("slctdcat_id");
                String slctd_boq = getArguments().getString("slctd_boq");

                category_models = DataHolder_Model.getInstance().getCategory_models();
                get_childs(slctdcat_id, slctd_boq);
            } else {

            }

        } else {
            if (nested_catgrs.size() > 0)
                nested_catgrs.remove(0);
            if (selected_category_adapter != null && selected_category_adapter.getItemCount() > 0)
                selected_category_adapter.notifyDataSetChanged();
        }
        return view;
    }

    TextView back_main_btn;

    public void findAllIds(View view) {
        select_systems_list = view.findViewById(R.id.select_systems_list);
        back_main_btn = view.findViewById(R.id.back_main_btn);
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() >4) {
            back_main_btn.setVisibility(View.VISIBLE);
        }
        back_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category_Model last_index = nested_catgrs.get(nested_catgrs.size() - 1);
                Category_Model sec_last_index = nested_catgrs.get(nested_catgrs.size() - 2);
                nested_catgrs.clear();
                nested_catgrs.add(sec_last_index);
                nested_catgrs.add(last_index);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                int index = 3;
                if (index > 0)
                    fragmentManager.popBackStack(index, 0);
            }
        });
    }

    ArrayList<Category_Model> category_models;


    private void get_childs(final String parent, String boq_id) {
        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String treeData) {
                boolean nodeOk = treeData.contains(parent);
                return nodeOk ? 0 : 1;
            }
        };
        TreeNode treeRoot = treeNodes.get(parent);
        if (treeRoot != null) {
            TreeNode found = treeRoot.findTreeNode(searchCriteria);
            Log.e("is leaf ", " " + found.isLeaf());
            if (!found.isLeaf()) {
                List<TreeNode> childs = found.getChildrens();
                category_models = new ArrayList<>();
                for (TreeNode treeNode : childs) {
                    Category_Model child_model = new Gson().fromJson(new Gson().toJson(treeNode.getData()), Category_Model.class);
                    category_models.add(child_model);
                }
                if (!boq_id.equals("0"))
                    set_adapters(category_models, boq_id);
            }
        }
    }

    public void set_adapters(ArrayList<Category_Model> categories, String boq_id) {
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        select_systems_list.setLayoutManager(gridLayoutManager1);
        select_systems_list.setPadding(3, 6, 3, 6);

        selected_category_adapter = new EC_category_Adapter(getActivity(), categories, boq_id);
        select_systems_list.setAdapter(selected_category_adapter);
    }

}

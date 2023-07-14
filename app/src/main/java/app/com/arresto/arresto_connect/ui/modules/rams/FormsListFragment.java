package app.com.arresto.arresto_connect.ui.modules.rams;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class FormsListFragment extends Base_Fragment {
    View view;
    RecyclerView forms_list;
    FormsAdapter projectDateAdapter;

    public static FormsListFragment newInstance(String project_id, ArrayList<CustomForm_Model> forms) {
        FormsListFragment fragment = new FormsListFragment();
        Bundle args = new Bundle();
        args.putString("project_id", project_id);
        args.putString("forms", AppUtils.getGson().toJson(forms));
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<CustomForm_Model> submitted_forms, project_forms;
    ArrayList<String> sbmtd_frms_ids;
    String project_id;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, parent, false);
            find_id();
            submitted_forms = new ArrayList<>();
            sbmtd_frms_ids = new ArrayList<>();
            forms_list.setAdapter(projectDateAdapter);
            if (getArguments() != null) {
                submitted_forms = new ArrayList<>(Arrays.asList(baseActivity.getGson().fromJson(getArguments().getString("forms"), CustomForm_Model[].class)));
                project_id = getArguments().getString("project_id");
            }
            filter_forms();
        }
        return view;
    }

    public void filter_forms() {
        project_forms = new ArrayList<>();
        ArrayList<CustomForm_Model> all_forms = DataHolder_Model.getInstance().getCustomViews_models();
        for (CustomForm_Model customForm_model : all_forms) {
            if (Project_listAdapter.slctd_project.getUp_forms().contains(customForm_model.getCf_id())) {
                project_forms.add(customForm_model);
            }
        }
        for (CustomForm_Model form : submitted_forms) {
            sbmtd_frms_ids.add(form.getForm_id());
        }

        if (projectDateAdapter != null) {
            projectDateAdapter.notifyDataSetChanged();
        } else {
            projectDateAdapter = new FormsAdapter(this);
            forms_list.setAdapter(projectDateAdapter);
        }
    }

    private void find_id() {
        view.findViewById(R.id.ad_project_btn).setVisibility(View.GONE);
        forms_list = view.findViewById(R.id.projects_list);
        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        forms_list.setLayoutManager(layoutManager);
        forms_list.setPadding(3, 5, 5, DpToPixel(50));
        forms_list.setClipToPadding(false);
        forms_list.getLayoutManager().setMeasurementCacheEnabled(false);
    }


    private class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.ViewHolder> {
        private Fragment fragment;

        public FormsAdapter(Fragment fragment) {
            this.fragment = fragment;
        }

        public int getItemCount() {
            return project_forms.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final CustomForm_Model model = project_forms.get(position);
            holder.name_tv.setText(model.getForm_name());
            if (sbmtd_frms_ids.contains(model.getCf_id())) {
                holder.btn_tv.setBackgroundColor(getResColor(R.color.app_green));
            } else {
                holder.btn_tv.setBackgroundColor(getResColor(R.color.app_error));
            }
            holder.btn_tv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AsmCustomPage asmCustomPage;
                    if (sbmtd_frms_ids.contains(model.getCf_id())) {
                        CustomForm_Model submtd_form = submitted_forms.get(sbmtd_frms_ids.indexOf(model.getCf_id()));
                        asmCustomPage = AsmCustomPage.newInstance(model, submtd_form, submtd_form.getCf_id());
                        loadFragment(asmCustomPage, model.getForm_name());
                    } else {
                        get_lastSubmitted_forms_data(model);
                    }

                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name_tv, btn_tv;

            public ViewHolder(View v) {
                super(v);
                name_tv = v.findViewById(R.id.name_tv);
                btn_tv = v.findViewById(R.id.btn_tv);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public void get_lastSubmitted_forms_data(CustomForm_Model form) {
        if (DataHolder_Model.getInstance().getCustomViewsData_models() == null) {
            NetworkRequest.getCustom_Data(getActivity(), All_Api.get_custom_forms_data + user_id + "&client_id=" + client_id + "&project_id=" + project_id + "&time=" + System.currentTimeMillis(), "data", new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.e("return obj", "" + msg.obj);
//                    if (msg.obj != null && (msg.obj.toString()).equals("200")) {
//                    }
                    getDataIndex(form);
                }
            });
        } else {
            getDataIndex(form);
        }

    }

    private void getDataIndex(CustomForm_Model form) {
//        ArrayList<String> submited_forms_id = new ArrayList<>();
        if (DataHolder_Model.getInstance().getCustomViewsData_models() != null) {
            for (CustomForm_Model item : DataHolder_Model.getInstance().getCustomViewsData_models()) {
                if (item.getForm_id().equals(form.getCf_id())) {
                    AsmCustomPage asmCustomPage = AsmCustomPage.newInstance(form, item, "");
                    loadFragment(asmCustomPage, form.getForm_name());
                    return;
                }
//                submited_forms_id.add(item.getForm_id());
            }
        }
        AsmCustomPage asmCustomPage = AsmCustomPage.newInstance(form, null, "");
        loadFragment(asmCustomPage, form.getForm_name());

    }


    public void loadFragment(AsmCustomPage asmCustomPage, String name) {
        LoadFragment.replace(asmCustomPage, baseActivity, name);
        asmCustomPage.setUpdate_Event(new AsmCustomPage.Update_formEvent() {
            @Override
            public void OnUpdate() {
                baseActivity.onBackPressed();
            }
        });
    }
}

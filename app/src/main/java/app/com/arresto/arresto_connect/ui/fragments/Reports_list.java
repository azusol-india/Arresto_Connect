/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.OnLoadMoreListener;
import app.com.arresto.arresto_connect.data.models.Reports_model;
import app.com.arresto.arresto_connect.ui.adapters.Report_listAdapter;

/**
 * Created by AZUSOL-PC-02 on 5/13/2019.
 */
public class Reports_list extends Base_Fragment {

    View view;
    RecyclerView listView;
    EditText searchView;
    String url;
    Report_listAdapter report_listAdapter;
    String type;

    List<Reports_model> list_data;
    List<Reports_model> sub_list;
    SwipeRefreshLayout swipe;
    onRefreshListner listner;

    public static Reports_list newInstance(String page, ArrayList<Reports_model> data, onRefreshListner refreshListner) {
        Reports_list fragmentFirst = new Reports_list();
        fragmentFirst.listner = refreshListner;
        Bundle args = new Bundle();
        args.putString("type", page);
        args.putSerializable("data", data);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
        list_data = (List<Reports_model>) getArguments().getSerializable("data");
        sub_list = new ArrayList<>();
        if (type.equals("pdm_report")) {
            Collections.sort(list_data, new Comparator<Reports_model>() {
                public int compare(Reports_model obj1, Reports_model obj2) {
                    return obj2.getCreated_date().compareToIgnoreCase(obj1.getCreated_date());
                }
            });
        }

    }

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;


    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.report_list_fragment, parent, false);
            find_Allid();
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            setUpList();
            listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount >= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }

                }
            });

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            listView.setHasFixedSize(true);
            final Handler handler = new Handler();
            onLoadMoreListener = new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add null , so the adapter will check view_type and show progress bar at bottom

                    report_listAdapter.get_data().add(null);
                    report_listAdapter.notifyItemInserted(report_listAdapter.get_data().size() - 1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //   remove progress item
                            if (report_listAdapter.get_data().size() > 0) {
                                report_listAdapter.get_data().remove(report_listAdapter.get_data().size() - 1);
                                report_listAdapter.notifyItemRemoved(report_listAdapter.get_data().size());
                            }
                            int start = report_listAdapter.get_data().size();
                            List<Reports_model> next_10 = get_next_10(start);
                            if (next_10 != null) {
                                report_listAdapter.get_data().addAll(next_10);
                                report_listAdapter.notifyItemInserted(report_listAdapter.get_data().size());
                            }
                            loading = false;
                            //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                        }
                    }, 2000);

                }
            };

            swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listner.onRefresh();
                    if (swipe.isRefreshing())
                        swipe.setRefreshing(false);
                }
            });
            return view;
        } else {
            return view;
        }
    }

    private void setUpList() {
        if (list_data != null && list_data.size() > 0) {
            sub_list.addAll(get_next_10(0));
            report_listAdapter = new Report_listAdapter(this, sub_list, type);
            listView.setAdapter(report_listAdapter);
        }
    }


    private void find_Allid() {
        listView = view.findViewById(R.id.suggestion_list);
        swipe = view.findViewById(R.id.swipe);
        searchView = view.findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                if (TextUtils.isEmpty(newText)) {
                    report_listAdapter = new Report_listAdapter(Reports_list.this, list_data, type);
                    listView.setAdapter(report_listAdapter);
                } else {
                    filter_data(newText.toLowerCase());
                }
            }
        });
    }


    ArrayList<Reports_model> slct_list_data;

    public void filter_data(String txt) {
        slct_list_data = new ArrayList<>();
        for (int i = 0; i < list_data.size(); i++) {
            if (list_data.get(i) != null) {
                Reports_model report = list_data.get(i);
                if (report.getReport_no().toLowerCase().contains(txt) || report.getSite_id().toLowerCase().contains(txt)
                        || report.getProduct_code().toLowerCase().contains(txt) || report.getApproved_status().toLowerCase().contains(txt)) {
                    slct_list_data.add(list_data.get(i));
                }
            }
        }
        if (report_listAdapter != null) {
            report_listAdapter.addData(slct_list_data);
        } else {
            report_listAdapter = new Report_listAdapter(this, slct_list_data, type);
            listView.setAdapter(report_listAdapter);
        }
    }

    public List<Reports_model> get_next_10(int current_size) {
        if (list_data.size() >= (current_size + 10)) {
            return list_data.subList(current_size, current_size + 10);
        } else if (list_data.size() > current_size) {
            return list_data.subList(current_size, list_data.size());
        } else
            return null;
    }

    public interface onRefreshListner {
        void onRefresh();
    }
}
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

package app.com.arresto.arresto_connect.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

import app.com.arresto.arresto_connect.data.models.Dashbrd_Search;
import app.com.arresto.arresto_connect.R;

public class Asset_history_frag extends Fragment {
    View view;
    HorizontalScrollView horizontalView1, horizontalView;
    TableLayout table_1;
    TextView ro_ht1, ro_ht2, ro_ht3, ro_ht4, ro_ht5, ro_ht6, ro_ht7, ro_ht8, ro_ht9, ro_ht10;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.asset_history_frag, container, false);
            setRetainInstance(true);
            findViewId(view);
//            setdata();
//            setupSearchView();
//            dashbrd_searches = DataHolder_Model.getInstance().get_Search_data();
//            set_data(dashbrd_searches);

            horizontalView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                public void onScrollChanged() {
                    horizontalView1.scrollTo(horizontalView.getScrollX(), 0);
                }
            });
            return view;
        } else {
            return view;
        }
    }

    private void findViewId(View view) {
        horizontalView = view.findViewById(R.id.horizontalView);
        horizontalView1 = view.findViewById(R.id.horizontalView1);
        table_1 = view.findViewById(R.id.table_1);

        ro_ht1 = view.findViewById(R.id.ro_ht1);
        ro_ht2 = view.findViewById(R.id.ro_ht2);
        ro_ht3 = view.findViewById(R.id.ro_ht3);
        ro_ht4 = view.findViewById(R.id.ro_ht4);
        ro_ht5 = view.findViewById(R.id.ro_ht5);
        ro_ht6 = view.findViewById(R.id.ro_ht6);
        ro_ht7 = view.findViewById(R.id.ro_ht7);
        ro_ht8 = view.findViewById(R.id.ro_ht8);
        ro_ht9 = view.findViewById(R.id.ro_ht9);
        ro_ht10 = view.findViewById(R.id.ro_ht10);
    }

    int a, b, c, d, e, f, g, h, i, k;

    public void set_data(List<Dashbrd_Search> dashbrd_searches) {
        table_1.removeAllViews();
        ro_ht6.measure(0, 0);
        ro_ht7.measure(0, 0);
        ro_ht8.measure(0, 0);
        for (int j = 0; j < dashbrd_searches.size(); j++) {
            Dashbrd_Search dashbrd_search = dashbrd_searches.get(j);

            TableRow row = new TableRow(getActivity());

            TextView tvDebt0 = newTextView();
            tvDebt0.setText("" + (j + 1));

            TextView tvDebt1 = newTextView();
            tvDebt1.setText(dashbrd_search.getClient_name());

            TextView tvDebt2 = newTextView();
            tvDebt2.setText(dashbrd_search.getAsset());

            TextView tvDebt3 = newTextView();
            tvDebt3.setText(dashbrd_search.getAsset_series());

            TextView tvDebt4 = newTextView();
            tvDebt4.setText(dashbrd_search.getReport_no());

            TextView tvDebt5 = newTextView();
            tvDebt5.setMinWidth(ro_ht6.getMeasuredWidth() + 20);
            tvDebt5.setText(dashbrd_search.getSite_id());

            TextView tvDebt6 = newTextView();
            tvDebt6.setMinWidth(ro_ht7.getMeasuredWidth() + 20);
            tvDebt6.setText(dashbrd_search.getJob_card());

            TextView tvDebt7 = newTextView();
            tvDebt7.setMinWidth(ro_ht8.getMeasuredWidth() + 20);
            tvDebt7.setText(dashbrd_search.getSms());

            TextView tvDebt8 = newTextView();
            tvDebt8.setText(dashbrd_search.getStatus());

            TextView tvDebt9 = newTextView();
            tvDebt9.setText(dashbrd_search.getTime());

            row.addView(tvDebt0);
            row.addView(tvDebt1);
            row.addView(tvDebt2);
            row.addView(tvDebt3);
            row.addView(tvDebt4);
            row.addView(tvDebt5);
            row.addView(tvDebt6);
            row.addView(tvDebt7);
            row.addView(tvDebt8);
            row.addView(tvDebt9);


            table_1.addView(row);

            tvDebt0.measure(0, 0);
            tvDebt1.measure(0, 0);
            tvDebt2.measure(0, 0);
            tvDebt3.measure(0, 0);
            tvDebt4.measure(0, 0);
            tvDebt5.measure(0, 0);
            tvDebt6.measure(0, 0);
            tvDebt7.measure(0, 0);
            tvDebt8.measure(0, 0);
            tvDebt9.measure(0, 0);

            if (tvDebt0.getMeasuredWidth() > a) {
                a = tvDebt0.getMeasuredWidth();
                ro_ht1.setWidth(a);
            }
            if (tvDebt1.getMeasuredWidth() > b) {
                b = tvDebt1.getMeasuredWidth();
                ro_ht2.setWidth(b);
            }
            if (tvDebt2.getMeasuredWidth() > c) {
                c = tvDebt2.getMeasuredWidth();
                ro_ht3.setWidth(c);
            }
            if (tvDebt3.getMeasuredWidth() > d) {
                d = tvDebt3.getMeasuredWidth();
                ro_ht4.setWidth(d);
            }
            if (tvDebt4.getMeasuredWidth() > e) {
                e = tvDebt4.getMeasuredWidth();
                ro_ht5.setWidth(e);
            }
            if (tvDebt5.getMeasuredWidth() > f) {
                f = tvDebt5.getMeasuredWidth();
                ro_ht6.setWidth(f);
            }
            if (tvDebt6.getMeasuredWidth() > g) {
                g = tvDebt6.getMeasuredWidth();
                ro_ht7.setWidth(g);
            }
            if (tvDebt7.getMeasuredWidth() > h) {
                h = tvDebt7.getMeasuredWidth();
                ro_ht8.setWidth(h);
            }
            if (tvDebt8.getMeasuredWidth() > i) {
                i = tvDebt8.getMeasuredWidth();
                ro_ht9.setWidth(i);
            }
            if (tvDebt9.getMeasuredWidth() > k) {
                k = tvDebt9.getMeasuredWidth();
                ro_ht10.setWidth(k);
            }

        }
    }

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        if (TextUtils.isEmpty(newText)) {
//            set_data(dashbrd_searches);
//        } else {
//            setListData(newText);
//        }
//        return true;
//    }
//
//    List<Dashbrd_Search> selectd_data;
//
//    private void setListData(String newText) {
//        selectd_data = new LinkedList<>();
//        for (int j = 0; j < dashbrd_searches.size(); j++) {
//            Dashbrd_Search dashbrd_search = dashbrd_searches.get(j);
//            if (dashbrd_search != null) {
//                if (dashbrd_search.getClient_name().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getAsset_id().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getAsset_series().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getSms().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getJob_card().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getSite_id().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getReport_no().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getStatus().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//                else if (dashbrd_search.getTime().toLowerCase().contains(newText.toLowerCase()))
//                    selectd_data.add(dashbrd_search);
//            }
//        }
//        set_data(selectd_data);
//    }

    TextView newTextView() {
        TextView new_tv = new TextView(getActivity());
        new_tv.setGravity(Gravity.CENTER_HORIZONTAL);
        new_tv.setBackgroundResource(R.drawable.table_divider1);
        new_tv.setPadding(30, 10, 30, 10);
        return new_tv;
    }
}

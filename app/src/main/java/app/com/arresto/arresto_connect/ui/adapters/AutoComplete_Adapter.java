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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.data.models.Product_model;

public class AutoComplete_Adapter extends ArrayAdapter implements Filterable {

    private ArrayList fullList;
    private ArrayList<Integer> filterd_id;
    private ArrayList mOriginalValues;
    private ArrayFilter mFilter;

    public AutoComplete_Adapter(Context context, ArrayList objects) {
        super(context, android.R.layout.simple_list_item_1, android.R.id.text1);
        fullList =  objects;
        mOriginalValues = new ArrayList<>(fullList);
        filterd_id = new ArrayList<>();

    }

    public void addData(ArrayList objects) {
        fullList = objects;
        mOriginalValues = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public Object getItem(int position) {
        return fullList.get(position);
    }

    public int getId(int position) {
        return filterd_id.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        filterd_id.add(position);
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<String> list = new ArrayList(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList values = mOriginalValues;
                ArrayList newValues = new ArrayList<>();
                filterd_id = new ArrayList<>();

                for (int i = 0; i < values.size(); i++) {
                    try {
                        Object item = values.get(i);
                        String description = "";
//                        name = item.toString();
                        if (values.get(i) instanceof Product_model) {
                            description = ((Product_model) values.get(i)).getProduct_description();
                        }
                        if (item.toString().toLowerCase().contains(prefixString) || description.toLowerCase().contains(prefixString)) {
                            newValues.add(item);
                            filterd_id.add(i);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", " is " + e.getMessage());
                    }

                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                fullList = (ArrayList<String>) results.values;
            } else {
                fullList = new ArrayList();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}

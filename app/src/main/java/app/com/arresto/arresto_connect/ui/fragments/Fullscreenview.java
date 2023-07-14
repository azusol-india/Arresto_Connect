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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.adapters.ViewPagerAdapter;

public class Fullscreenview extends Fragment {

    ViewPagerAdapter pagerAdapter;
    ViewPager pager;
    int current_pos;
    ArrayList<String> imagurl;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fullimage_view, container, false);
        pager =  rootView.findViewById(R.id.view_pager);
        if (getArguments() != null){
            imagurl = getArguments().getStringArrayList("imgurl");
            current_pos = getArguments().getInt("pos", -1);
        }
        Log.e("web url", " is " + imagurl);
        pagerAdapter = new ViewPagerAdapter(getActivity(), imagurl);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(current_pos);

        return rootView;
    }
}
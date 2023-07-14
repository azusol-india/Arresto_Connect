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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.adapters.Gallery_Adapter;

public class Gallery_Fragment extends Fragment {

    ArrayList<String> images_arr;

    public static Gallery_Fragment newInstance(ArrayList<String> images) {
        Gallery_Fragment gallary_fragment = new Gallery_Fragment();
        Bundle args = new Bundle();
        args.putStringArrayList("images", images);
        gallary_fragment.setArguments(args);
        return gallary_fragment;
    }

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallary_fragment, container, false);
        if (getArguments() != null) {
            images_arr = getArguments().getStringArrayList("images");
            Log.e("images_arr","is "+images_arr);
            RecyclerView recyclerView = view.findViewById(R.id.imagegallery);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(layoutManager);
            Gallery_Adapter adapter = new Gallery_Adapter(getActivity(), images_arr);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

}

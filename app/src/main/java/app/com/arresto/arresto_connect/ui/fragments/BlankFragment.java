package app.com.arresto.arresto_connect.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.arresto.arresto_connect.R;

public class BlankFragment extends Base_Fragment {
    View view;
    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.blank_fragment, parent, false);
        }
        return view;
    }
}

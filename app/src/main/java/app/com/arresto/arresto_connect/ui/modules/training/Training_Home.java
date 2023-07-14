package app.com.arresto.arresto_connect.ui.modules.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_infonet_frg;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

public class Training_Home extends Base_Fragment implements View.OnClickListener {
    View view;

    RelativeLayout kt_btn, webinar_btn, learn_btn, activity_btn;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.training_home, parent, false);
            kt_btn = view.findViewById(R.id.kt_btn);
            webinar_btn = view.findViewById(R.id.webinar_btn);
            learn_btn = view.findViewById(R.id.learn_btn);
            activity_btn = view.findViewById(R.id.activity_btn);
            kt_btn.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kt_btn:
                LoadFragment.replace(new Karam_infonet_frg(), homeActivity, getResString("lbl_knowledge_tree"));
                break;
        }
    }
}

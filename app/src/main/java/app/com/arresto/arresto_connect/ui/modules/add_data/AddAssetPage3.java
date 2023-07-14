package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.ui.modules.add_data.AddAssetPager.selected_asset;

public class AddAssetPage3 extends Base_Fragment implements View.OnClickListener {
    View view;
    AddAssetModel assetModel;

    String heading;

    public static AddAssetPage3 newInstance(String heading) {
        AddAssetPage3 fragmentFirst = new AddAssetPage3();
        Bundle args = new Bundle();
        args.putString("heading", heading);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_asset_page3, parent, false);
            assetModel = AddAssetModel.getInstance();
            if (getArguments() != null) {
                heading = getArguments().getString("heading");
            }
            all_Ids();
            setdata();
        }
        return view;
    }

    MaterialButton back_btn, continue_btn;
    TextView heading_tv;
    private EditText ins_freqM_edt, ins_freqH_edt, lifeM_edt, lifeH_edt, pdm_freq_edt;
    Spinner kstatus_spinr;
    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };


    private void all_Ids() {
        back_btn = view.findViewById(R.id.back_btn);
        continue_btn = view.findViewById(R.id.continue_btn);
        back_btn.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        heading_tv = view.findViewById(R.id.heading_tv);
        ins_freqM_edt = view.findViewById(R.id.ins_freqM_edt);
        ins_freqH_edt = view.findViewById(R.id.ins_freqH_edt);
        lifeM_edt = view.findViewById(R.id.lifeM_edt);
        lifeH_edt = view.findViewById(R.id.lifeH_edt);
        pdm_freq_edt = view.findViewById(R.id.pdm_freq_edt);
        kstatus_spinr = view.findViewById(R.id.kstatus_spinr);
        kstatus_spinr.setSelection(1);
        heading_tv.setText(heading);
        new CustomTextWatcher(ins_freqH_edt, textWatcher);
        new CustomTextWatcher(ins_freqM_edt, textWatcher);
        new CustomTextWatcher(ins_freqH_edt, textWatcher);
        new CustomTextWatcher(lifeM_edt, textWatcher);
        new CustomTextWatcher(lifeH_edt, textWatcher);
        new CustomTextWatcher(pdm_freq_edt, textWatcher);
    }

    private void setdata() {
        if (selected_asset != null) {
            ins_freqH_edt.setText(selected_asset.getComponent_frequency_hours());
            ins_freqM_edt.setText(selected_asset.getComponent_frequency_asset());
            lifeM_edt.setText(selected_asset.getComponent_lifespan_month());
            lifeH_edt.setText(selected_asset.getComponent_lifespan_hours());
            pdm_freq_edt.setText(selected_asset.getComponent_pdm_frequency());
        }

        if (assetModel.getAsset_code() != null) {
            ins_freqM_edt.setText(assetModel.getFrequency_asset());
            lifeM_edt.setText(assetModel.getLifespan_month());
            lifeH_edt.setText(assetModel.getLifespan_hours());
            pdm_freq_edt.setText(assetModel.getPdm_frequency());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                scrollPager.scrollTo(1);
                break;
            case R.id.continue_btn:
                if (isEmpty(ins_freqM_edt) || isEmpty(pdm_freq_edt) || isEmpty(lifeH_edt) || isEmpty(lifeM_edt)) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                assetModel.setFrequency_asset(ins_freqM_edt.getText().toString());
                assetModel.setFrequency_hours(ins_freqH_edt.getText().toString());
                assetModel.setPdm_frequency(pdm_freq_edt.getText().toString());
                assetModel.setLifespan_hours(lifeH_edt.getText().toString());
                assetModel.setLifespan_month(lifeM_edt.getText().toString());
                scrollPager.scrollTo(3);
                break;
        }
    }

    AddAssetPager.ScrollPager scrollPager;

    public void setscrolleListner(AddAssetPager.ScrollPager scrollPager) {
        this.scrollPager = scrollPager;
    }
}

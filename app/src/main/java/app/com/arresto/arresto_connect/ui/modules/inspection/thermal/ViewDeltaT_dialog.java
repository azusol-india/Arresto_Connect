package app.com.arresto.arresto_connect.ui.modules.inspection.thermal;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.ThermalSubassetModel;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class ViewDeltaT_dialog extends DialogFragment {

    public static String TAG = "FullView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    ThermalImageProcessing activity;
    String heading;
    ArrayList<ThermalSubassetModel> dataObjects;

    public static void newInstance(BaseActivity activity, String heading, ArrayList<ThermalSubassetModel> dataObjects) {
        ViewDeltaT_dialog fullScreenDialog = new ViewDeltaT_dialog();
        Bundle args = new Bundle();
//        args.putParcelableArrayList("dataObjects", dataObjects);
        args.putString("heading", heading);
        fullScreenDialog.setArguments(args);
        fullScreenDialog.show(activity.getSupportFragmentManager().beginTransaction(), PointCreatorDialog.TAG);
//        return fullScreenDialog;
    }

    public static void newInstance(BaseActivity activity, String type) {
        ViewDeltaT_dialog fullScreenDialog = new ViewDeltaT_dialog();
        Bundle args = new Bundle();
//        args.putParcelableArrayList("dataObjects", dataObjects);
        args.putString("type", type);
        fullScreenDialog.setArguments(args);
        fullScreenDialog.show(activity.getSupportFragmentManager().beginTransaction(), PointCreatorDialog.TAG);
//        return fullScreenDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ThermalImageProcessing) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.view_delta_t_dialog, container, false);
//        dataObjects = getArguments().getParcelableArrayList("dataObjects");
        String type = getArguments().getString("type");
        if (type.equals("ambient")) {
            heading = getResString("lbl_delta_in_ambient");
            dataObjects = activity.ambientModels;
        } else {
            heading = getResString("lbl_delta_in_similar");
            dataObjects = activity.similarSubassetModels;
        }

        ((TextView) view.findViewById(R.id.header)).setText(heading);
        RecyclerView t_list = view.findViewById(R.id.t_list);

        t_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        DeltaT_Adapter deltaT_adapter = new DeltaT_Adapter(activity, dataObjects);
        t_list.setAdapter(deltaT_adapter);
        ImageView cancel_btn = view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDeltaT_dialog.this.dismiss();
            }
        });
        activity.printLog(AppUtils.getGson().toJson(dataObjects));
//        save_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imageView.dots.size() > 1) {
//                    Canvas bitmapCanvas = new Canvas(bitmap);
//                    imageView.drawEvent(bitmapCanvas, true);
//                    mListener.OnPointCreated(bitmap, imageView.dots);
//                    dismiss();
//                } else
//                    AppUtils.show_snak(getActivity(), "Please create at least two temperature points.");
//            }
//        });
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        activity.refreshViewCount();
    }
}

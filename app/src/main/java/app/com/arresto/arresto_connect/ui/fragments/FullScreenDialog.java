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

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.TouchImageView;

public class FullScreenDialog extends DialogFragment {

    public static String TAG = "FullView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    String image_url;
    Bitmap bitmap;

    public static void newInstance(AppCompatActivity appCompatActivity, String type) {
        FullScreenDialog fullScreenDialog = new FullScreenDialog();
        Bundle args = new Bundle();
        args.putString("url", type);
        fullScreenDialog.setArguments(args);
        fullScreenDialog.show(appCompatActivity.getSupportFragmentManager().beginTransaction(), FullScreenDialog.TAG);
//        return fullScreenDialog;
    }

    public static void newInstance(AppCompatActivity appCompatActivity, Bitmap bitmap) {
        FullScreenDialog fullScreenDialog = new FullScreenDialog();
        Bundle args = new Bundle();
        args.putParcelable("bitmap", bitmap);
        fullScreenDialog.setArguments(args);
        fullScreenDialog.show(appCompatActivity.getSupportFragmentManager().beginTransaction(), FullScreenDialog.TAG);
//        return fullScreenDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.full_image_dialog, container, false);
        final TouchImageView imageView = view.findViewById(R.id.imageView);
        ImageView cancel_btn = view.findViewById(R.id.cancel_btn);

        if (getArguments() != null) {
            if (getArguments().containsKey("url")) {
                image_url = getArguments().getString("url");
                AppUtils.load_image_cache(image_url, imageView);
            } else if (getArguments().containsKey("bitmap")) {
                bitmap = getArguments().getParcelable("bitmap");
                imageView.setImageBitmap(bitmap);
            }
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog.this.dismiss();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}


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

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.FullScreenDialog;
import app.com.arresto.arresto_connect.ui.fragments.Fullscreenview;

public class Recycler_adapter extends RecyclerView.Adapter<Recycler_adapter.ViewHolder> {
    static int width;
    ArrayList imgurls;
    BaseActivity activity;

    public Recycler_adapter(BaseActivity activity, ArrayList imag) {
        this.activity = activity;
        this.imgurls = imag;
        this.width = AppUtils.getDisplayWidth(activity);
    }

    public void notifyData(ArrayList imag) {
        this.imgurls = imag;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (imgurls != null)
            return imgurls.size();
        else
            return 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_images, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.imageView.setImageResource(imgurls[position]);
        if (imgurls.get(position) instanceof String) {
            final String url3 = (String) imgurls.get(position);
            holder.imageView.setTag(url3);
            AppUtils.load_image(url3, holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Fullscreenview fullscreenview = new Fullscreenview();
                    Log.e("imageurl", "" + imgurls);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("imgurl", imgurls);
                    bundle.putInt("pos", position);
                    fullscreenview.setArguments(bundle);
                    if (!imgurls.get(0).equals("0") || !imgurls.get(1).equals("0") || !imgurls.get(2).equals("0")) {
                        activity.getSupportFragmentManager().beginTransaction().replace(activity.fragContainer, fullscreenview).addToBackStack(null).commit();
                    }
                }
            });
        } else if (imgurls.get(position) instanceof Bitmap) {
            final Bitmap bitmap = (Bitmap) imgurls.get(position);
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FullScreenDialog.newInstance(activity, bitmap);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.rimg);
//            if (SplashActivity.istab){
//                imageView.getLayoutParams().width = width / 3;
//            } else {
            imageView.getLayoutParams().width = width;
//            }
        }
    }


}

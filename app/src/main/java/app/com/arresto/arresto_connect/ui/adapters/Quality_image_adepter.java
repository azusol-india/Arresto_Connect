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

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Fullscreenview;

public class Quality_image_adepter extends RecyclerView.Adapter<Quality_image_adepter.ViewHolder> {
    private BaseActivity context;
    private ArrayList imgPic;


    public Quality_image_adepter(BaseActivity c, ArrayList thePic) {
        context =  c;
        this.imgPic = thePic;
    }

    public void add_file(ArrayList thePic) {
        this.imgPic = thePic;
    }

    public long getItemId(int position) {
        return position;
    }

    public void add_item(ArrayList thePic) {
        imgPic = thePic;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (imgPic != null)
            return imgPic.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, rmv_img;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.rimg);
            rmv_img = v.findViewById(R.id.rmv_img);
        }
    }

    //---returns an ImageView view---
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quality_adepter_items, parent, false);
        return new ViewHolder(v);
    }

//    private Map<String, Bitmap> bitmapMap = Collections.synchronizedMap(new HashMap<String, Bitmap>());

    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Object item = imgPic.get(position);
        if (item instanceof Uri) {
            holder.imageView.setImageURI((Uri) item);
            holder.rmv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgPic.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imgPic.size());
                }
            });
        } else {
            final String path = (String) imgPic.get(position);
            AppUtils.load_image_file(path, holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Fullscreenview fullscreenview = new Fullscreenview();
                    Log.e("imageurl", "" + imgPic);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("imgurl", imgPic);
                    bundle.putInt("pos", position);
                    fullscreenview.setArguments(bundle);
                    context.getSupportFragmentManager().beginTransaction().replace(context.fragContainer, fullscreenview).addToBackStack(null).commit();
                }
            });
            holder.rmv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(path, position);
                }
            });
        }
    }

    // ---to remove a list position ---
    private void removeAt(String path, int pos) {
        File file = new File(path);
        if (file.exists())
            file.getAbsoluteFile().delete();
        imgPic.remove(path);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, imgPic.size());
    }
}
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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Periodic_model;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.MultiImageView;
import app.com.arresto.arresto_connect.ui.fragments.Fullscreenview;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;
import app.com.arresto.arresto_connect.ui.fragments.Video_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.extractYTId;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;


public class Periodic_Viewpager extends RecyclerView.Adapter<Periodic_Viewpager.ViewHolder> {

    Activity activity;
    private Periodic_model periodic_model;

    public Periodic_Viewpager(Activity activity, Periodic_model periodic_model) {
        this.activity = activity;
        this.periodic_model = periodic_model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.periodic_viewpager_item, viewGroup, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new ViewHolder(v);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        switch (position) {
            case 0:
                viewHolder.web_view.setVisibility(View.GONE);
                viewHolder.video_thumbnail.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                ArrayList<Periodic_model.Media_array> images = periodic_model.getPdm_images();
                final ArrayList<String> strings = new ArrayList<>();
                for (Periodic_model.Media_array array : images)
                    strings.add(array.getFile_url());

                Log.e("image urls ", " is " + strings);
                if (strings.size() > 0)
//                AppUtils.load_image(activity,strings.get(0),viewHolder.imageView);
                    viewHolder.imageView.setMedias(strings);
                viewHolder.imageView.setOnMediaClickListener(new MultiImageView.OnMediaClickListener() {
                    @Override
                    public void onMediaClick(View view, int index) {
                        Fullscreenview fullscreenview = new Fullscreenview();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("imgurl", strings);
                        bundle.putInt("pos", index);
                        fullscreenview.setArguments(bundle);
                        LoadFragment.replace(fullscreenview,  activity, "");
                    }
                });
                break;
            case 1:
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.video_thumbnail.setVisibility(View.GONE);
                viewHolder.web_view.setVisibility(View.VISIBLE);
                viewHolder.web_view.getSettings().setJavaScriptEnabled(true);
                viewHolder.web_view.getSettings().setBuiltInZoomControls(true);
                final ArrayList<Periodic_model.Media_array> docs = periodic_model.getPdm_documents();
                if (docs.size() > 0)
                    viewHolder.web_view.loadUrl(docs.get(0).getFile_url());

                viewHolder.web_view.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            return false;
                        }

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Report_webview report_webview = new Report_webview();
                            Bundle bundle = new Bundle();
                            bundle.putString("url",  docs.get(0).getFile_url());
                            report_webview.setArguments(bundle);
                            LoadFragment.replace(report_webview,  activity, "");

                        }

                        return false;
                    }
                });


                break;
            case 2:
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.web_view.setVisibility(View.GONE);
                viewHolder.video_thumbnail.setVisibility(View.VISIBLE);
                final String video_id = extractYTId(periodic_model.getPdm_media());
//                final String video_id=extractYTId("https://www.youtube.com/watch?v=TTpe2lOGF4g");
//                https://img.youtube.com/vi/mazwN7hhXQ4/0.jpg //add video_id and load on glide
                Log.e("video_id ", " is " + video_id);
//                String decodded_str=Base64.decode(activity.getResources().getString(R.string.lbl_google_maps_key), Base64.DEFAULT);
                viewHolder.video_thumbnail.initialize(AppUtils.getResString("lbl_google_maps_key"), new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                        youTubeThumbnailLoader.setVideo(video_id);
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                youTubeThumbnailLoader.release();
                            }

                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                            }
                        });
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });

                viewHolder.video_thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!video_id.equalsIgnoreCase("")) {
                            Intent intent = new Intent(activity, Video_Fragment.class);
                            intent.putExtra("video_id", video_id);
                            activity.startActivity(intent);
                        } else {
                            show_snak(activity,"Video not available");
                        }
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MultiImageView imageView;
        public WebView web_view;
        public YouTubeThumbnailView video_thumbnail;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image_view);
            web_view = v.findViewById(R.id.web_view);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
//            imageView.getLayoutParams().width = width;
//            imageView.getLayoutParams().width = width;
        }
    }

}

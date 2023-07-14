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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.ui.fragments.FullScreenDialog;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;

import static app.com.arresto.arresto_connect.constants.AppUtils.share_file;

public class ViewPagerAdapter extends PagerAdapter {
    Activity context;
    WebView webView;
    ACProgressFlower progressDialog;
    private ArrayList<String> img_url;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Activity context, ArrayList<String> img_url) {
        this.context = context;
        this.img_url = img_url;
        layoutInflater = LayoutInflater.from(context);
        progressDialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
//                .bgColor(Color.BLACK)
                .text("Loading...").textSize(16).textMarginTop(5)
                .petalThickness(2)
                .sizeRatio((float) 0.22)
                .fadeColor(Color.YELLOW).build();
    }

    public void update(ArrayList<String> img_url) {
        this.img_url = img_url;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (img_url == null)
            return 0;
        else
            return img_url.size();
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.pager_layout, container, false);
        final ImageView imageView = view.findViewById(R.id.image_show);
        final ImageView share_btn = view.findViewById(R.id.share_btn);
        img_url.set(position, img_url.get(position).replaceAll(" ", "%20"));
        final String url3 = img_url.get(position);
        webView = view.findViewById(R.id.webView1);


        Log.e("file extension", " is " + url3);
        if (url3.contains(".pdf") || url3.contains(".xls") || url3.contains(".xlsx") || url3.contains(".doc")) {
            imageView.setVisibility(View.GONE);
            share_btn.setVisibility(View.VISIBLE);
            progressDialog.show();
            load_webView(webView, url3);
            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share_file(context, url3, System.currentTimeMillis() + "");

                }
            });
        } else {
            webView.setVisibility(View.GONE);
            AppUtils.load_image_cache(url3, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenDialog.newInstance((AppCompatActivity) context, url3);
                }
            });
        }

        container.addView(view);
        view.setTag("view" + position);
        return view;
    }


    void load_webView(final WebView webView, final String weburl) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.invalidate();
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {
            boolean checkhasOnPageStarted = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                checkhasOnPageStarted = true;
//                webView.loadUrl("javascript:(function() { document.querySelector('[role=toolbar]').remove();})()");
            }

            public void onPageFinished(WebView view, String url) {
                if (checkhasOnPageStarted) {
                    progressDialog.dismiss();
                    webView.loadUrl("javascript:(function() { document.querySelector('[role=toolbar]').remove();})()");
//                    webView.loadUrl(removePdfTopIcon);
                } else {
                    load_webView(webView, weburl);
                }
            }
        });

        webView.loadUrl("https://docs.google.com/viewer?url=" + Uri.encode(weburl));
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Uri.encode(weburl));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

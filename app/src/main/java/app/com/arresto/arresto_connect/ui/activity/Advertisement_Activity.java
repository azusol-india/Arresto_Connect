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

package app.com.arresto.arresto_connect.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;

import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;

public class Advertisement_Activity extends Activity {
    ProgressBar progressButton1;
    TextView progress_tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement_activity);
        ImageView Advt_img = findViewById(R.id.add_view);
        progressButton1 = findViewById(R.id.pin_progress_1);
        progress_tv = findViewById(R.id.progress_tv);
        progressButton1.setProgress(0);
        findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_for_next();
//                Intent intent = new Intent(Advertisement_Activity.this, PlayerActivity.class);
//                intent.putExtra("url", "http://arresto.in/web/wp-content/uploads/2019/03/Kare-App-Animation_V3.mp4");
//                startActivity(intent);
            }
        });

        if (DataHolder_Model.getInstance().getAdvt_model() != null && DataHolder_Model.getInstance().getAdvt_model().getAdvt_image() != null && !(DataHolder_Model.getInstance().getAdvt_model().getAdvt_image().length() < 10)) {
            AppUtils.load_image_file(DataHolder_Model.getInstance().getAdvt_model().getAdvt_image(), Advt_img);
            Advt_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = DataHolder_Model.getInstance().getAdvt_model().getAdvt_url();
                    if (url != null && !url.equals("")) {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://" + url;
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                }
            });

            new CountDownTimer(5000, 50) {
                public void onTick(long millisUntilFinished) {
                    progressButton1.setProgress((int) (millisUntilFinished / 50));
                    progress_tv.setText(""+((int) (millisUntilFinished / 1100)+1));
                }

                public void onFinish() {
                    go_for_next();
                }

            }.start();

        } else {
            go_for_next();
        }
    }
    public void go_for_next() {
        if (isLogin()) {
//            Intent intent = new Intent(Advertisement_Activity.this, TestActivity.class);
            Intent intent = new Intent(Advertisement_Activity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(Advertisement_Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

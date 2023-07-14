
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

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;

import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;
import static app.com.arresto.arresto_connect.constants.AppUtils.isTablet;


public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Uri targetUri;

    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;
    boolean isFirstTime = true;
    private SeekBar progressBar;
    private Button fullScreen, mute_btn;
    private Button btnPayPause;
    private TextView timeForMediaPlayer;
    RelativeLayout root_view;
    ProgressBar progressdialog;

    ImageView close_btn;

    @Override
    protected void onStart() {
        super.onStart();
//        if (isTablet(this)) {
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    int height, width;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlayout);

        width = AppUtils.getDisplaySize(this).widthPixels;
        height = AppUtils.getDisplaySize(this).heightPixels;

        targetUri = Uri.parse(getIntent().getExtras().getString("url"));
        initialize();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(16, 14);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setScreenOnWhilePlaying(true);

        surfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final View view = ((ViewGroup) btnPayPause.getParent());
                if (view.isShown()) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    final Handler handler = new Handler();
                    view.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(10000);
                            } catch (Exception e) {
                            }
                            handler.post(new Runnable() {
                                public void run() {
                                    view.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).start();
                }
                return false;
            }
        });


        btnPayPause.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                play_video();
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progressBar.setSecondaryProgress(percent);
            }

        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressdialog.setVisibility(View.INVISIBLE);
                new Thread(runnable).start();
                final Handler handler = new Handler();
                ((ViewGroup) btnPayPause.getParent()).setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                // Set the ShowcaseView's visibility back on the main UI Thread
                                ((ViewGroup) btnPayPause.getParent()).setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }).start();
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    int timeToSet = (mediaPlayer.getDuration() * progress) / 100;
                    mediaPlayer.seekTo(timeToSet);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        fullScreen.setOnClickListener(new ShowcaseView.OnClickListener() {
//            @Override
//            public void onClick(ShowcaseView v) {
//                setFullScreen();
//            }
//        });

        setFullScreen();


        setVolumeControl(mediaPlayer);

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        progressdialog.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        progressdialog.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_for_next();
            }
        });
    }

    public void go_for_next() {
        if (isLogin()) {
            Intent intent = new Intent(PlayerActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(PlayerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    boolean mVolumePlaying = true;

    private void setVolumeControl(final MediaPlayer mp) {

        mute_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp != null) {
                    if (mVolumePlaying) {
                        mute_btn.setBackgroundResource(R.drawable.mute);
                        mp.setVolume(0F, 0F);
                    } else {
                        mute_btn.setBackgroundResource(R.drawable.unmute);
                        mp.setVolume(1F, 1F);
                    }
                    mVolumePlaying = !mVolumePlaying;
                }
            }
        });
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);

                    int currentTime = mediaPlayer.getCurrentPosition();
                    if (mediaPlayer.getDuration() > 0) {
                        int percent = (100 * currentTime) / mediaPlayer.getDuration();
                        progressBar.setProgress(percent);
                        final String timeToShow = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(currentTime),
                                TimeUnit.MILLISECONDS.toMinutes(currentTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentTime)),
                                TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime)));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeForMediaPlayer.setText(timeToShow);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
//                    mediaPlayer.reset();
//                    progressBar.setProgress(runtime);
                    break;
                }
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        pausing = true;
        mediaPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.reset();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        progressdialog.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressdialog.setVisibility(View.VISIBLE);
                play_video();
            }
        }, 1000);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    private void initialize() {
        timeForMediaPlayer = findViewById(R.id.timeForMediaPlayer);
        progressBar = findViewById(R.id.progressBar);
        progressdialog = findViewById(R.id.progressdialog);
        mute_btn = findViewById(R.id.mute_btn);
        fullScreen = findViewById(R.id.fullScreen);
        btnPayPause = findViewById(R.id.play);
        surfaceView = findViewById(R.id.surfaceview);
        root_view = findViewById(R.id.root_view);
        close_btn = findViewById(R.id.close_btn);

        if (isTablet(this)) {
            fullScreen.setVisibility(View.GONE);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            params.width = width;
            params.height = (int) (height / 2.5);
        }
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    boolean mFullScreen;

    public void setFullScreen() {
        if (!mFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            params.width = height;
            params.height = width;
            params.setMargins(0, 0, 0, 0);
            mFullScreen = !mFullScreen;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            int height = root_view.getHeight();//get height Frame Container video
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            params.width = width;
            params.height = (int) (height / 1.5);
            params.setMargins(0, 0, 0, 0);
            //set icon is small screen
            mFullScreen = !mFullScreen;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mFullScreen) {
            super.onBackPressed();
        } else {
            setFullScreen();
        }
    }

    public void play_video() {
        if (pausing) {
            pausing = false;
            mediaPlayer.start();
            btnPayPause.setBackgroundResource(R.drawable.pause_small);
        } else {
            if (isFirstTime) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(surfaceHolder);
                new Player().execute(targetUri);
                isFirstTime = false;
                btnPayPause.setBackgroundResource(R.drawable.pause_small);
            } else {
                pausing = true;
                mediaPlayer.pause();
                btnPayPause.setBackgroundResource(R.drawable.play_small);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Player extends AsyncTask<Uri, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Uri... uris) {
            boolean prepared;
            try {
                mediaPlayer.setDataSource(PlayerActivity.this, uris[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pausing = false;
//                        btn.setBackgroundResource(R.drawable.button_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progressdialog.getVisibility() == View.VISIBLE) {
                progressdialog.setVisibility(View.GONE);
            }
            mediaPlayer.start();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog.setVisibility(View.VISIBLE);

        }
    }

}

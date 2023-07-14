package app.com.arresto.arresto_connect.ui.modules.inspection.thermal;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.flir.thermalsdk.image.Point;
import com.google.android.gms.common.internal.ResourceUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.third_party.flir_thermal.Dot;
import app.com.arresto.arresto_connect.third_party.flir_thermal.DrawableDotImageView;
import app.com.arresto.arresto_connect.third_party.flir_thermal.OnPointCreatedListener;

public class PointCreatorDialog extends DialogFragment {

    public static String TAG = "FullView";
    OnPointCreatedListener mListener;
    SeekBar seekBar;
    TextView spot_heading;
    CheckBox check_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    Bitmap bitmap;
    static ArrayList<Point> intialPoint;

    public static void newInstance(AppCompatActivity appCompatActivity, Bitmap bitmap, ArrayList<Point> intialPoints) {
        PointCreatorDialog fullScreenDialog = new PointCreatorDialog();
        intialPoint = intialPoints;
        Bundle args = new Bundle();
        args.putParcelable("imageBitmap", bitmap);
        fullScreenDialog.setArguments(args);
        fullScreenDialog.show(appCompatActivity.getSupportFragmentManager().beginTransaction(), PointCreatorDialog.TAG);
//        return fullScreenDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnPointCreatedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName() + " must implement OnCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.point_creator_dialog, container, false);
        bitmap = getArguments().getParcelable("imageBitmap");
        final DrawableDotImageView imageView = view.findViewById(R.id.imageView);
        final MaterialButton save_btn = view.findViewById(R.id.save_btn);
        if (bitmap != null) {
            imageView.setBitmapWidth(bitmap.getWidth(), intialPoint, PointCreatorDialog.this);
            imageView.setImageBitmap(bitmap);
        }
        spot_heading = view.findViewById(R.id.spot_heading);
        check_btn = view.findViewById(R.id.check_btn);
        ImageView cancel_btn = view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointCreatorDialog.this.dismiss();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.dots.size() > 1) {
                    Canvas bitmapCanvas = new Canvas(bitmap);
                    imageView.drawEvent(bitmapCanvas, true);
                    mListener.OnPointCreated(bitmap, imageView.dots);
                    dismiss();
                } else
                    AppUtils.show_snak(getActivity(), "Please create at least two temperature points.");
            }
        });

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setThumb(getCurrentThumb(seekBar.getProgress() * 5));
        seekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seek_progress_bg));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progrs = seekBar.getProgress() * 5;
                if (progrs<1){
                    progrs=1;
                }
                seekBar.setThumb(getCurrentThumb(progrs));
                if (selectedDot != null && !check_btn.isChecked())
                    selectedDot.radius = progrs;
                else
                    imageView.setRadius(progrs);
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        check_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spot_heading.setText("Spot size");
                    selectedDot=null;
                }
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

    Dot selectedDot;

    public void onDotSelect(Dot dot) {
        if (!check_btn.isChecked()) {
            selectedDot = dot;
            spot_heading.setText("Spot " + dot.getDotName() + " size");
            seekBar.setThumb(getCurrentThumb((int) (dot.getRadius())));
            seekBar.setProgress((int) (dot.getRadius()/5));
        }else  Toast.makeText(getContext(), "You selected change for all spots. Please unselect it first", Toast.LENGTH_LONG).show();
    }

    public Drawable getCurrentThumb(int currentProgress) {
        Bitmap writableBitmap = AppUtils.drawable2Bitmap(getActivity().getResources().getDrawable(R.drawable.seek_thumb));
        writableBitmap = addText(writableBitmap, currentProgress);
        return AppUtils.bitmap2Drawable(writableBitmap, getActivity());
    }

    public Bitmap addText(Bitmap src, int currentProgress) {
        Bitmap.Config bitmapConfig = src.getConfig();
        if (bitmapConfig == null) bitmapConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = src.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(35);
        Rect rectangle = new Rect();
        paint.getTextBounds(
                String.valueOf(currentProgress),
                0, // start
                String.valueOf(currentProgress).length(),
                rectangle
        );
        canvas.drawText(
                String.valueOf(currentProgress),
                canvas.getWidth() / 2.0f,
                canvas.getHeight() / 2.0f + Math.abs(rectangle.height()) / 2.0f, // y
                paint // Paint
        );
        return bitmap;
    }
}

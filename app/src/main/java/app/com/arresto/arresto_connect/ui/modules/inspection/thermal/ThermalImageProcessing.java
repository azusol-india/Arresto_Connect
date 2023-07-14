package app.com.arresto.arresto_connect.ui.modules.inspection.thermal;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.flir.thermalsdk.androidsdk.ThermalSdkAndroid;
import com.flir.thermalsdk.androidsdk.image.BitmapAndroid;
import com.flir.thermalsdk.image.ImageFactory;
import com.flir.thermalsdk.image.Point;
import com.flir.thermalsdk.image.Scale;
import com.flir.thermalsdk.image.ThermalImageFile;
import com.flir.thermalsdk.image.measurements.MeasurementCircle;
import com.flir.thermalsdk.log.ThermalLog;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.custom_views.switch_lib.SwitchTrackTextDrawable;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Observation_Model;
import app.com.arresto.arresto_connect.data.models.SubAssetModel;
import app.com.arresto.arresto_connect.data.models.ThermalSubassetModel;
import app.com.arresto.arresto_connect.database.inspection_tables.ThermalAsset_Table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.flir_thermal.Dot;
import app.com.arresto.arresto_connect.third_party.flir_thermal.OnPointCreatedListener;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.Recycler_adapter;

public class ThermalImageProcessing extends BaseActivity implements OnPointCreatedListener {
    private static final String TAG = "ThermalImageProcessing";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.thermal_image_processing;
    }

    ThermalImageFile thermalImageFile;
    private RecyclerViewPager recyclerView;
    private ArrayList allImages;
    private ArrayList<Constant_model> fetched_temprature;
    String image_dir, asset_code, master_id, unique_id, uin;
    String thermalImagePath;
    String temp_unit = "C";
    String thermal_id, inspection_id;
    boolean isReplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThermalLog.LogLevel enableLoggingInDebug = BuildConfig.DEBUG ? ThermalLog.LogLevel.DEBUG : ThermalLog.LogLevel.NONE;
        ThermalSdkAndroid.init(getApplicationContext(), enableLoggingInDebug);
        setupUiViews();
        if (getIntent().getExtras() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle.containsKey("inspection_id")) {
                thermal_id = dataBundle.getString("thermal_id");
                inspection_id = dataBundle.getString("inspection_id");
                isReplace = dataBundle.getBoolean("isReplace", false);
                fetchData(thermal_id);
                image_dir = directory;
            } else {
                all_subAsset = dataBundle.getStringArrayList("sub_assets");
                asset_code = dataBundle.getString("asset_code", "");
                master_id = dataBundle.getString("master_id", "");
                unique_id = dataBundle.getString("unique_id", "");
                uin = dataBundle.getString("uin", "");
                if (all_subAsset != null)
                    all_subAsset.add(0, getResString("lbl_pl_slct_msg"));
            }
            if (dataBundle.containsKey("imagePath")) {
                thermalImagePath = dataBundle.getString("imagePath");
                image_dir = dataBundle.getString("image_dir");
                thermalImageFile = openIncludedImage(thermalImagePath);
            }
        }
        checkDir(image_dir);
        if (thermalImageFile != null) {
            Bitmap flirBitmap = BitmapAndroid.createBitmap(thermalImageFile.getImage()).getBitMap();
            if (flirBitmap != null) {
//                allImages.add(BitmapAndroid.createBitmap(thermalImageFile.getImage()).getBitMap()); //new Object because it's modified later
                printLog("flirBitmap X=" + flirBitmap.getWidth() + "Y=" + flirBitmap.getHeight());
                convertScaleBitmap(thermalImageFile);
                ArrayList<Point> intialPoints = new ArrayList<>();
                printLog("thermalImage height ===" + thermalImageFile.getHeight() + "  thermalImage width ===" + thermalImageFile.getWidth());
                printLog("Image height ===" + thermalImageFile.getImage().getHeight() + "   Image width ===" + thermalImageFile.getImage().getWidth());
                double h_ratio = (double) thermalImageFile.getImage().getHeight() / (double) thermalImageFile.getHeight();
                double w_ratio = (double) thermalImageFile.getImage().getWidth() / (double) thermalImageFile.getWidth();

                printLog("width ratio ===" + w_ratio + "   height ratio ===" + h_ratio);
                Point hotSpot = thermalImageFile.getStatistics().hotSpot;
                Point coldSpot = thermalImageFile.getStatistics().coldSpot;
                hotSpot = new Point((int) (hotSpot.x * h_ratio), (int) (hotSpot.y * w_ratio));
                coldSpot = new Point((int) (coldSpot.x * h_ratio), (int) (coldSpot.y * w_ratio));
                intialPoints.add(hotSpot);
                intialPoints.add(coldSpot);
                printLog("intialPoints===" + intialPoints);
                PointCreatorDialog.newInstance(ThermalImageProcessing.this, flirBitmap, intialPoints);
            }
        }
    }

    private TextView spotValue, emissivity_edt;
    LinearLayout ambient_layer, similar_subasset_layer;
    Recycler_adapter imageAdapter;
    ArrayList<String> img_ch_txt = new ArrayList<>(Arrays.asList("Thermal Image", "Pointed Image", "Visual Image"));
    RadioGroup radioGroup;
    TextView imege_ch_txt;
    TextView view_ambient_btn, view_similar_btn;
    DialogSpinner ambient_temp_spinr, severity_spinr;
    Switch _switch;
    DialogSpinner subasset_spnr, temperature_spnr, obser_spinr, excerpt_spnr, result_spnr, subasset1_spnr, subasset2_spnr, temperature1_spnr, temperature2_spnr, obser2_spinr, excerpt2_spnr, result2_spnr;
    EditText delta_edit, similar_delta_edit, remark_edt, remark2_edt, wind_speed_edt, wind_direction_edt, precipitation_edt, air_temperature_edt, humidity_edt, camera_model_edt;

    private void setupUiViews() {
        _switch = findViewById(R.id.btn_accessible);
        view_ambient_btn = findViewById(R.id.view_ambient_btn);
        view_similar_btn = findViewById(R.id.view_similar_btn);
        ambient_temp_spinr = findViewById(R.id.ambient_temp_spinr);
        severity_spinr = findViewById(R.id.severity_spinr);
        spotValue = findViewById(R.id.spot_value);
        emissivity_edt = findViewById(R.id.emissivit_edt);
        recyclerView = findViewById(R.id.recycler);
        radioGroup = findViewById(R.id.radioGroup);
        imege_ch_txt = findViewById(R.id.imege_ch_txt);

        wind_speed_edt = findViewById(R.id.wind_speed_edt);
        wind_direction_edt = findViewById(R.id.wind_direction_edt);
        precipitation_edt = findViewById(R.id.precipitation_edt);
        air_temperature_edt = findViewById(R.id.air_temperature_edt);
        humidity_edt = findViewById(R.id.humidity_edt);
        camera_model_edt = findViewById(R.id.camera_model_edt);

        ambient_layer = findViewById(R.id.ambient_layer);
        subasset_spnr = findViewById(R.id.subasset_spnr);
        temperature_spnr = findViewById(R.id.temperature_spnr);
        obser_spinr = findViewById(R.id.obser_spinr);
        excerpt_spnr = findViewById(R.id.excerpt_spnr);
        result_spnr = findViewById(R.id.result_spnr);
        delta_edit = findViewById(R.id.delta_edit);
        remark_edt = findViewById(R.id.remark_edt);

        similar_subasset_layer = findViewById(R.id.similar_subasset_layer);
        subasset1_spnr = findViewById(R.id.subasset1_spnr);
        subasset2_spnr = findViewById(R.id.subasset2_spnr);
        temperature1_spnr = findViewById(R.id.temperature1_spnr);
        temperature2_spnr = findViewById(R.id.temperature2_spnr);
        obser2_spinr = findViewById(R.id.obser2_spinr);
        excerpt2_spnr = findViewById(R.id.excerpt2_spnr);
        result2_spnr = findViewById(R.id.result2_spnr);
        similar_delta_edit = findViewById(R.id.similar_delta_edit);
        remark2_edt = findViewById(R.id.remark2_edt);

        setListeners();
    }

    public void setListeners() {
        allImages = new ArrayList<>();
        ambientModels = new ArrayList<>();
        similarSubassetModels = new ArrayList<>();
        fetched_temprature = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new Recycler_adapter(this, allImages);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            public void OnPageChanged(int i, int i1) {
                radioGroup.check(radioGroup.getChildAt(i1).getId());
                imege_ch_txt.setText(img_ch_txt.get(i1));
            }
        });

        _switch.setTrackDrawable(new SwitchTrackTextDrawable(this, R.string.lbl_celsius, R.string.lbl_fahrenheit));
        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    temp_unit = "F";
                    refreshTemperature();
                } else {
                    temp_unit = "C";
                    refreshTemperature();
                }
            }
        });
        severity_array = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.severity_array)));
        severity_array.add(0, getResString("lbl_pl_slct_msg"));
        severity_spinr.setItems(severity_array, "");

        subasset_spnr.setOnItemSelectedListener(onItemSelectedListener);
        subasset1_spnr.setOnItemSelectedListener(onItemSelectedListener);
        subasset2_spnr.setOnItemSelectedListener(onItemSelectedListener);
        ambient_temp_spinr.setOnItemSelectedListener(onItemSelectedListener);
        temperature_spnr.setOnItemSelectedListener(onItemSelectedListener);
        temperature1_spnr.setOnItemSelectedListener(onItemSelectedListener);
        temperature2_spnr.setOnItemSelectedListener(onItemSelectedListener);

        na_adapter = new ArrayAdapter<>(this, R.layout.spiner_tv, new ArrayList<>(Arrays.asList("NA")));
    }

    List<String> severity_array;

    public void refreshTemperature() {
        if (fetched_temprature != null && fetched_temprature.size() > 1) {
            String txt = "";
            for (int i = 1; i < fetched_temprature.size(); i++) {
                Constant_model item = fetched_temprature.get(i);
                txt = txt + "<br>" + getString(R.string.spot_value_text, item.getName(), convertTemperature(item.getTemp()), (char) 186 + temp_unit);
            }
            Spanned txt1=Html.fromHtml(txt);
            spotValue.setText(txt1);
        }
        if (ambient_layer.getVisibility() == View.VISIBLE) {
            if (!delta_edit.getText().toString().equals(""))
                delta_edit.setText(convertTemperature(delta_edit.getText().toString()));
        }
        if (similar_subasset_layer.getVisibility() == View.VISIBLE) {
            if (!similar_delta_edit.getText().toString().equals(""))
                similar_delta_edit.setText(convertTemperature(similar_delta_edit.getText().toString()));
        }

    }


    public void refreshViewCount() {
        printLog("Thermal onResume");
        if (ambientModels.size() > 0) {
            view_ambient_btn.setText(getResString("lbl_view") + " (" + ambientModels.size() + ")");
            view_ambient_btn.setVisibility(View.VISIBLE);
        }
        if (similarSubassetModels.size() > 0) {
            view_similar_btn.setText(getResString("lbl_view") + " (" + similarSubassetModels.size() + ")");
            view_similar_btn.setVisibility(View.VISIBLE);
        }
    }

    public void refreshAmbientView() {
        if (all_subAsset != null)
            subasset_spnr.setItems(all_subAsset, "");
        temperature_spnr.setItems(fetched_temprature, "");
        obser_spinr.setAdapter(na_adapter);
        excerpt_spnr.setAdapter(na_adapter);
        result_spnr.setAdapter(na_adapter);
        delta_edit.setText("");
        remark_edt.setText("");
    }

    public void refreshSimilarView() {
        if (all_subAsset != null) {
            subasset1_spnr.setItems(all_subAsset, "");
            subasset2_spnr.setItems(all_subAsset, "");
        }
        temperature1_spnr.setItems(fetched_temprature, "");
        temperature2_spnr.setItems(fetched_temprature, "");
        obser2_spinr.setAdapter(na_adapter);
        excerpt2_spnr.setAdapter(na_adapter);
        result2_spnr.setAdapter(na_adapter);
        similar_delta_edit.setText("");
        remark2_edt.setText("");
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.subasset_spnr:
                    if (position > 0 && recentAmbientObject != null) {
                        recentAmbientObject.setSubAssetName(all_subAsset.get(position));
                        fetchSubassetData(recentAmbientObject, obser_spinr, excerpt_spnr, result_spnr);
                    }
                    break;
                case R.id.subasset1_spnr:
                    if (position > 0 && recentSimilarObject != null) {
                        recentSimilarObject.setSubAssetName(all_subAsset.get(position));
                    }
                    break;
                case R.id.subasset2_spnr:
                    if (position > 0 && recentSimilarObject != null) {
                        recentSimilarObject.setSubAsset2Name(all_subAsset.get(position));
                        fetchSubassetData(recentSimilarObject, obser2_spinr, excerpt2_spnr, result2_spnr);
                    }
                    break;
                case R.id.ambient_temp_spinr:
                    delta_edit.setText(convertTemperature(getDeltaT(position, temperature_spnr)));
                    break;

                case R.id.temperature_spnr:
                    String deta_t = getDeltaT(position, ambient_temp_spinr);
                    delta_edit.setText(convertTemperature(deta_t));
                    if (recentAmbientObject != null) {
                        recentAmbientObject.setDelta_t(deta_t);
                        if (position > 0)
                            recentAmbientObject.setTemperature(fetched_temprature.get(position));
                        else recentAmbientObject.setTemperature(null);
                    }
                    break;
                case R.id.temperature1_spnr:
                    String deta_t1 = getDeltaT(position, temperature2_spnr);
                    similar_delta_edit.setText(convertTemperature(deta_t1));
                    if (recentSimilarObject != null) {
                        recentSimilarObject.setDelta_t(deta_t1);
                        if (position > 0)
                            recentSimilarObject.setTemperature(fetched_temprature.get(position));
                        else recentSimilarObject.setTemperature(null);
                    }
                    break;
                case R.id.temperature2_spnr:
                    String deta_t2 = getDeltaT(position, temperature1_spnr);
                    similar_delta_edit.setText(convertTemperature(deta_t2));
                    if (recentSimilarObject != null) {
                        recentSimilarObject.setDelta_t(deta_t2);
                        if (position > 0)
                            recentSimilarObject.setTemperature2(fetched_temprature.get(position));
                        else recentSimilarObject.setTemperature2(null);
                    }
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private String getDeltaT(int selectedPosition, DialogSpinner compareSpinner) {
        String delta_t = "";
        if (selectedPosition > 0 && compareSpinner.getSelectedItemPosition() > 0) {
            double _t = Math.abs(Double.parseDouble(fetched_temprature.get(selectedPosition).getTemp()) - Double.parseDouble(fetched_temprature.get(compareSpinner.getSelectedItemPosition()).getTemp()));
            delta_t = "" + round(_t, 2);
        }
        return delta_t;
    }

    ThermalSubassetModel recentAmbientObject, recentSimilarObject;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_ambient_btn:
                if (subasset_spnr.getSelectedItemPosition() < 1 || temperature_spnr.getSelectedItemPosition() < 1) {
                    show_snak(this, getResString("lbl_field_mndtry"));
                    return;
                }
                if (!obser_spinr.getSelectedItem().toString().equalsIgnoreCase(getResString("lbl_ok_st")) && (excerpt_spnr.getSelectedItemPosition() < 1 || result_spnr.getSelectedItemPosition() < 1)) {
                    show_snak(this, getResString("lbl_field_mndtry"));
                    return;
                }
                recentAmbientObject.setRemark(remark_edt.getText().toString());
                ThermalSubassetModel newObject = recentAmbientObject.clone();
                if (newObject == null)
                    return;
                ambientModels.add(newObject);
                ambient_layer.setVisibility(View.GONE);
                ((ViewGroup) view_ambient_btn.getParent()).setVisibility(View.VISIBLE);
                if (ambientModels.size() > 0) {
                    view_ambient_btn.setVisibility(View.VISIBLE);
                    view_ambient_btn.setText(getResString("lbl_view") + " (" + ambientModels.size() + ")");
                }
                recentAmbientObject = null;
                obser_spinr.setOnItemSelectedListener(null);
                excerpt_spnr.setOnItemSelectedListener(null);
                result_spnr.setOnItemSelectedListener(null);
                refreshAmbientView();
                break;
            case R.id.add_ambient_btn:
                ambient_layer.setVisibility(View.VISIBLE);
                ((ViewGroup) view_ambient_btn.getParent()).setVisibility(View.GONE);
                recentAmbientObject = new ThermalSubassetModel();
                recentAmbientObject.setObservation(new Observation_Model());
                break;
            case R.id.add_similar_btn:
                similar_subasset_layer.setVisibility(View.VISIBLE);
                ((ViewGroup) view_similar_btn.getParent()).setVisibility(View.GONE);
                recentSimilarObject = new ThermalSubassetModel();
                recentSimilarObject.setObservation(new Observation_Model());
                break;
            case R.id.save_similar_btn:
                if (subasset1_spnr.getSelectedItemPosition() < 1 || temperature1_spnr.getSelectedItemPosition() < 1 || subasset2_spnr.getSelectedItemPosition() < 1 || temperature2_spnr.getSelectedItemPosition() < 1) {
                    show_snak(this, getResString("lbl_field_mndtry"));
                    return;
                }
                if (!obser2_spinr.getSelectedItem().toString().equalsIgnoreCase(getResString("lbl_ok_st")) && (excerpt2_spnr.getSelectedItemPosition() < 1 || result2_spnr.getSelectedItemPosition() < 1)) {
                    show_snak(this, getResString("lbl_field_mndtry"));
                    return;
                }
                recentSimilarObject.setRemark(remark2_edt.getText().toString());
                ThermalSubassetModel newObject1 = recentSimilarObject.clone();
                if (newObject1 == null)
                    return;
                similarSubassetModels.add(newObject1);
                similar_subasset_layer.setVisibility(View.GONE);
                ((ViewGroup) view_similar_btn.getParent()).setVisibility(View.VISIBLE);
                if (similarSubassetModels.size() > 0) {
                    view_similar_btn.setVisibility(View.VISIBLE);
                    view_similar_btn.setText(getResString("lbl_view") + " (" + similarSubassetModels.size() + ")");
                }
                recentSimilarObject = null;
                obser2_spinr.setOnItemSelectedListener(null);
                excerpt2_spnr.setOnItemSelectedListener(null);
                result2_spnr.setOnItemSelectedListener(null);
                refreshSimilarView();
                break;
            case R.id.view_ambient_btn:
                if (ambientModels.size() > 0) {
                    ViewDeltaT_dialog.newInstance(ThermalImageProcessing.this, "ambient");
                }
                break;

            case R.id.view_similar_btn:
                if (similarSubassetModels.size() > 0) {
                    ViewDeltaT_dialog.newInstance(ThermalImageProcessing.this, "similar");
                }
                break;

            case R.id.cntnu_btn:
                if (!isEmpty1(wind_speed_edt) && !isEmpty1(wind_direction_edt) && !isEmpty1(precipitation_edt)
                        && !isEmpty1(air_temperature_edt) && !isEmpty1(humidity_edt) && !isEmpty1(camera_model_edt)
                        && ambient_temp_spinr.getSelectedItemPosition() > 0 && severity_spinr.getSelectedItemPosition() > 0) {
                    if (ambientModels.size() < 1 || similarSubassetModels.size() < 1) {
                        show_snak(this, "Please select at least single ∆T parameters");
                        return;
                    }

                    JSONObject finalJson = new JSONObject();
                    try {
                        finalJson.put("client_id", client_id)
                                .put("user_id", user_id)
                                .put("asset_code", asset_code)
                                .put("master_id", master_id)
                                .put("uin", uin)
                                .put("wind_speed", wind_speed_edt.getText().toString())
                                .put("wind_direction", wind_direction_edt.getText().toString())
                                .put("precipitation", precipitation_edt.getText().toString())
                                .put("air_temperature", air_temperature_edt.getText().toString())
                                .put("humidity", humidity_edt.getText().toString())
                                .put("camera_model", camera_model_edt.getText().toString())
                                .put("emissivity", emissivity_edt.getText().toString())
                                .put("ambient_temperature", new JSONObject(AppUtils.getGson().toJson(ambient_temp_spinr.getSelectedItem())))
                                .put("severity", severity_spinr.getSelectedItem().toString())
                                .put("all_temperatures", new JSONArray(AppUtils.getGson().toJson(fetched_temprature)))
                                .put("ambient_subassets", new JSONArray(AppUtils.getGson().toJson(ambientModels)))
                                .put("similar_subassets", new JSONArray(AppUtils.getGson().toJson(similarSubassetModels)))
                                .put("temperature_unit", temp_unit);

                        if (thermalImagePath != null && !thermalImagePath.equals("")) {
                            String fileName = thermalImagePath.substring(thermalImagePath.lastIndexOf('/') + 1);
                            String thermal_imagepath = image_dir + fileName;
                            String actual_imagepath = image_dir + "actual_" + System.currentTimeMillis() + ".jpg";
                            String marked_imagepath = image_dir + "marked_" + System.currentTimeMillis() + ".jpg";
                            String scale_imagepath = image_dir + "scale_" + System.currentTimeMillis() + ".jpg";
                            save_Image((Bitmap) allImages.get(0), thermal_imagepath);
                            save_Image((Bitmap) allImages.get(1), marked_imagepath);
                            save_Image((Bitmap) allImages.get(2), actual_imagepath);
                            if (scalImage != null)
                                save_Image(scalImage, scale_imagepath);
                            finalJson.put("actual_image", actual_imagepath);
                            finalJson.put("thermal_image", thermal_imagepath);
                            finalJson.put("marked_image", marked_imagepath);
                            finalJson.put("scale_image", scale_imagepath);
                        }

                        if (thermal_id == null || thermal_id.equals("")) {
                            ThermalAsset_Table.ThermalAsset_Dao thermalAsset_dao = AppController.getInstance().getDatabase().getThermalAsset_dao();
                            ThermalAsset_Table thermal_table = new ThermalAsset_Table();
                            thermal_table.setUser_id(user_id);
                            thermal_table.setUnique_id(unique_id);
                            thermal_table.setAsset(asset_code);
                            thermal_table.setJsonData(finalJson.toString());
                            long id = thermalAsset_dao.insert(thermal_table);
                            finish();
                        } else {
                            finalJson.put("thermal_id", thermal_id);
                            finalJson.put("inspection_id", inspection_id);
                            updateThermal(finalJson);
                        }
                        printLog("final json = " + finalJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    show_snak(this, getResString("lbl_field_mndtry"));
                }
                break;
        }
    }

    private void updateThermal(JSONObject finalJson) {
        String url;
        if (isReplace) {
            url = All_Api.postThermal_service;
            try {
                finalJson.put("return_id", inspection_id);
                finalJson.put("actual_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(finalJson.getString("actual_image")));
                finalJson.put("thermal_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(finalJson.getString("thermal_image")));
                finalJson.put("marked_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(finalJson.getString("marked_image")));
                finalJson.put("scale_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(finalJson.getString("scale_image")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            url = All_Api.updateThermal_data;
        new NetworkRequest(this).make_contentpost_request(url, finalJson, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    show_snak(ThermalImageProcessing.this, jsonObject.getString("message"));
                    if (jsonObject.getString("status_code").equals("200")) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    public void setUpTempraturePoints(ArrayList<Dot> points) {
        synchronized (recyclerView) {
            Bitmap visualBitmap = BitmapAndroid.createBitmap(thermalImageFile.getFusion().getPhoto()).getBitMap();
            allImages.add(visualBitmap);
            imageAdapter.notifyData(allImages);
            radioGroup.check(radioGroup.getChildAt(0).getId());
            imege_ch_txt.setText(img_ch_txt.get(0));
            showImageData(points);
        }
    }

    private void showImageData(ArrayList<Dot> points) {
        fetched_temprature = new ArrayList<>();
        Constant_model temp_model = new Constant_model();
        temp_model.setName(getResString("lbl_pl_slct_msg"));
        temp_model.setTemp("");
        fetched_temprature.add(temp_model);

        if (!thermalImageFile.getMeasurements().getCircles().isEmpty())
            thermalImageFile.getMeasurements().getCircles().addAll(thermalImageFile.getMeasurements().getCircles());
        camera_model_edt.setText("" + thermalImageFile.getCameraInformation().model);
        float xRatio = (float) thermalImageFile.getWidth() / ((Bitmap) allImages.get(0)).getWidth();
       try {
           if (points != null && points.size() > 0) {
               for (Dot dot : points) {
                   int radius = (int) (xRatio * dot.getRadius());
                   Log.d(TAG, "image width:"+thermalImageFile.getWidth());
                   Log.d(TAG, "image height:"+thermalImageFile.getHeight());
                   Log.d(TAG, "image x/2:"+thermalImageFile.getWidth()/2);
                   Log.d(TAG, "image y/2:"+thermalImageFile.getHeight()/2);
                   Log.d(TAG, "image x/3:"+thermalImageFile.getWidth()/3);
                   Log.d(TAG, "image y/3:"+thermalImageFile.getHeight()/3);
                   Log.d(TAG, "image x/4:"+thermalImageFile.getWidth()/4);
                   Log.d(TAG, "image y/4:"+thermalImageFile.getHeight()/4);
                   Log.d(TAG, "image x/100:"+thermalImageFile.getWidth()/100);
                   Log.d(TAG, "image y/100:"+thermalImageFile.getHeight()/100);
                   Log.d(TAG, "dot x:"+dot.getIntX());
                   Log.d(TAG, "dot y:"+dot.getIntY());
                   Log.d(TAG, "dotbmp x:"+dot.getIntBitmapX());
                   Log.d(TAG, "dotbmp y:"+dot.getIntBitmapY());
//                   thermalImageFile.getMeasurements().addCircle(dot.getIntX(),dot.getIntY(), radius);
                   thermalImageFile.getMeasurements().addCircle(thermalImageFile.getWidth() / 2, thermalImageFile.getHeight() / 2, radius);
//                   thermalImageFile.getMeasurements().addCircle(thermalImageFile.getWidth() / 2, thermalImageFile.getHeight() / 2, radius);
               }
           }
       }catch (Exception e){
           Log.d(TAG, "addCircles: "+e.getMessage());return;
       }
        try {
            if (!thermalImageFile.getMeasurements().getCircles().isEmpty()) {
                List<MeasurementCircle> cilrcles = thermalImageFile.getMeasurements().getCircles();
                for (int i = 0; i < cilrcles.size(); i++) {
                    MeasurementCircle cilrcle = cilrcles.get(i);
                    String maxValue = String.valueOf(round(cilrcle.getMaxValue().asCelsius().value, 2));
                    String minValue = String.valueOf(round(cilrcle.getMinValue().asCelsius().value, 2));
                    temp_model = new Constant_model();
                    if (points.get(i).dotName.equals("T1")) {
                        temp_model.setName(points.get(i).dotName + "<sup><small>H</small></sup>");
                        temp_model.setTemp(maxValue);
                        fetched_temprature.add(temp_model);
                    } else if (points.get(i).dotName.equals("T2")) {
                        temp_model.setName(points.get(i).dotName + "<sup><small>C</small></sup>");
                        temp_model.setTemp(minValue);
                        fetched_temprature.add(temp_model);
                    } else {
                        temp_model.setName(points.get(i).dotName + "<sup><small>Max</small></sup>");
                        temp_model.setTemp(maxValue);
                        fetched_temprature.add(temp_model);

                        temp_model = new Constant_model();
                        temp_model.setName(points.get(i).dotName + "<sup><small>Min</small></sup>");
                        temp_model.setTemp(minValue);
                        fetched_temprature.add(temp_model);
                    }
                }
                String emissivity = String.valueOf(round(thermalImageFile.getImageParameters().getEmissivity(), 2));
                String humidity = String.valueOf(round(thermalImageFile.getImageParameters().getRelativeHumidity(), 2));
                String air_temp = String.valueOf(round(thermalImageFile.getImageParameters().getAtmosphericTemperature().asCelsius().value, 2));
//            emissivit_edt.setText(getString(R.string.lbl_emissivity, emissivity));
                emissivity_edt.setText(emissivity);
                humidity_edt.setText(humidity);
                air_temperature_edt.setText(air_temp);
            }
            ambient_temp_spinr.setItems(fetched_temprature, "");
            refreshTemperature();
            refreshAmbientView();
            refreshSimilarView();
        }catch (Exception e){
            Log.d(TAG, "fetch circles: "+e.getMessage());
        }

    }

    public static double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Open the included FLIR image file
     *
     * @param imagePath
     * @return a IR file or NULL if failed to open file
     */
    public ThermalImageFile openIncludedImage(String imagePath) {
        ThermalImageFile thermalImageFile = null;
        try {
            thermalImageFile = (ThermalImageFile) ImageFactory.createImage(imagePath);
        } catch (IOException e) {
            ThermalLog.e(TAG, "failed to open IR file, exception:" + e);
            AppUtils.show_snak(this, "Selected image is not a thermography image.");
            finish();
        }
        return thermalImageFile;
    }

    @Override
    public void OnPointCreated(Bitmap finalBitmap, ArrayList<Dot> points) {
        if (finalBitmap != null) {
            allImages.add(finalBitmap);
        }
        setUpTempraturePoints(points);
    }


    ArrayList<ThermalSubassetModel> ambientModels, similarSubassetModels;
    ArrayList<String> action_taken = new ArrayList<>(Arrays.asList(getResString("lbl_pl_slct_msg"), getResString("lbl_remove_and_repair"), getResString("lbl_done"), getResString("lbl_ok_st")
            , getResString("lbl_no_action_taken"), getResString("lbl_remove_and_replace")));
    ArrayAdapter<String> na_adapter;
    ArrayList<String> all_subAsset;

    @SuppressLint("HandlerLeak")
    private void fetchSubassetData(ThermalSubassetModel thermalSubassetModel, DialogSpinner obser_spinr, DialogSpinner excerpt_spnr, DialogSpinner result_spnr) {
        ArrayList<Constant_model> observations = new ArrayList<>();
        Constant_model constant_model = new Constant_model();
        constant_model.setId("");
        constant_model.setName(getResString("lbl_pl_slct_msg"));
        observations.add(constant_model);
        String subAsset = "" + thermalSubassetModel.getSubAssetName();
        if (thermalSubassetModel.getSubAsset2Name() != null)
            subAsset = thermalSubassetModel.getSubAsset2Name();
        new NetworkRequest(this).make_get_request(All_Api.subasset_service + subAsset + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("status_code")) {
                            if (jsonObject.getString("status_code").equals("200")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                ArrayList<SubAssetModel> subAssetModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), SubAssetModel[].class)));
                                if (subAssetModels.size() > 0) {
                                    String[] observation_arr = subAssetModels.get(0).getSubAssetsObservation().split("##");
                                    if (subAssetModels.get(0).getSubAssetsObservation().length() > 0) {
                                        for (String anObservation : observation_arr) {
                                            if (anObservation.contains("#")) {
                                                Constant_model item = new Constant_model();
                                                String[] n1 = anObservation.split("#");
                                                if (n1.length > 1) {
                                                    item.setId(n1[0]);
                                                    item.setName(n1[1]);
                                                    observations.add(item);
                                                }
                                            }
                                        }
                                    }
                                    setUpObservation(observations, thermalSubassetModel, obser_spinr, excerpt_spnr, result_spnr);
                                }
                            } else {
                                show_snak(ThermalImageProcessing.this, "" + jsonObject.getString("message"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    public void setUpObservation(ArrayList<Constant_model> all_observations, ThermalSubassetModel thermalSubassetModel, DialogSpinner obser_spinr, DialogSpinner excerpt_spnr
            , DialogSpinner result_spnr) {
        obser_spinr.setItems(all_observations, "");
        result_spnr.setItems(action_taken, "");
        obser_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = obser_spinr.getSelectedItem().toString();
                if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg")) && !new_slcted_itm.equalsIgnoreCase("Na")) {
                    thermalSubassetModel.getObservation().setObservation(new_slcted_itm);
                    thermalSubassetModel.getObservation().setObservation_id(all_observations.get(position).getId());
                }
                if (position > 0) {
                    if (new_slcted_itm.equalsIgnoreCase(getResString("lbl_ok_st"))) {
                        excerpt_spnr.setEnabled(false);
                        result_spnr.setEnabled(false);
                        excerpt_spnr.setAdapter(na_adapter);
                        result_spnr.setAdapter(na_adapter);
                    } else {
                        excerpt_spnr.setEnabled(true);
                        result_spnr.setEnabled(true);
                        result_spnr.setItems(action_taken, "");
                        if (AppUtils.isNetworkAvailable(ThermalImageProcessing.this)) {
                            get_action_prposed_from_api(thermalSubassetModel.getObservation().getObservation_id(), excerpt_spnr);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        excerpt_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = excerpt_spnr.getSelectedItem().toString();
                if (!new_slcted_itm.equalsIgnoreCase("Na")) {
                    Constant_model item = (Constant_model) parent.getSelectedItem();
                    new_slcted_itm = item.toString();
                    if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg"))) {
                        thermalSubassetModel.getObservation().setAction_proposed(new_slcted_itm);
                        thermalSubassetModel.getObservation().setAction_proposed_id(item.getId());
                    }
                } else {
                    thermalSubassetModel.getObservation().setAction_proposed(new_slcted_itm);
                    thermalSubassetModel.getObservation().setAction_proposed_id("");
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        result_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String new_slcted_itm = result_spnr.getSelectedItem().toString();
                if (!new_slcted_itm.equals(getResString("lbl_pl_slct_msg"))) {
                    thermalSubassetModel.getObservation().setResult(new_slcted_itm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void get_action_prposed_from_api(String obser_id, final DialogSpinner exrpt_spiner) {
        String url = All_Api.actionProposed_service + obser_id;
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("success").equals("No data Found")) {
                        Log.e("string object", "running");
                    } else {
                        ArrayList excerpt = new ArrayList<>();
                        Constant_model constant_model = new Constant_model();
                        constant_model.setId("");
                        constant_model.setName(getResString("lbl_pl_slct_msg"));
                        excerpt.add(constant_model);
                        excerpt.addAll(Arrays.asList(AppUtils.getGson().fromJson(object.getString("success"), Constant_model[].class)));
                        ((ViewGroup) exrpt_spiner.getParent()).setVisibility(View.VISIBLE);
                        exrpt_spiner.setItems(excerpt, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
            }
        });

    }

    //  Edit Code here

    private void fetchData(String thermal_id) {
        String url = All_Api.getThermal_Api + client_id + "&thermal_id=" + thermal_id + "&inspection_id=" + inspection_id;
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        JSONObject data = object.getJSONObject("data");
                        setUpEditData(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    private void setUpEditData(JSONObject data) throws JSONException {
        asset_code = data.getString("asset_code");
        get_Component_data(All_Api.components_service + asset_code + "?client_id=" + client_id);
        master_id = data.getString("mdata_id");
        uin = data.getString("uin");
        temp_unit = data.getString("temperature_unit");
        if (thermalImagePath == null || thermalImagePath.equals("")) {
            allImages.add(data.getString("thermal_image"));
            allImages.add(data.getString("marked_image"));
            allImages.add(data.getString("actual_image"));
        }
        wind_speed_edt.setText(data.getString("wind_speed"));
        wind_direction_edt.setText(data.getString("wind_direction"));
        precipitation_edt.setText(data.getString("precipitation"));
        air_temperature_edt.setText(data.getString("air_temperature"));
        humidity_edt.setText(data.getString("humidity"));
        camera_model_edt.setText(data.getString("camera_model"));
        emissivity_edt.setText(data.getString("emissivity"));
        severity_spinr.setLastSelected(severity_array.indexOf(data.getString("severity")));
        fetched_temprature = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data.getString("all_temperatures"), Constant_model[].class)));

        ambient_temp_spinr.setItems(fetched_temprature, "");
        Constant_model slectedAmbient = AppUtils.getGson().fromJson(data.getString("ambient_temperature"), Constant_model.class);
        ambient_temp_spinr.setLastSelected(findIndex(fetched_temprature, slectedAmbient.getName()));

        if (!data.getString("ambient_subassets").equals(""))
            ambientModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data.getString("ambient_subassets"), ThermalSubassetModel[].class)));
        if (!data.getString("similar_subassets").equals(""))
            similarSubassetModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data.getString("similar_subassets"), ThermalSubassetModel[].class)));
        refreshTemperature();
        refreshAmbientView();
        refreshSimilarView();
        refreshViewCount();
        imageAdapter.notifyData(allImages);
        radioGroup.check(radioGroup.getChildAt(0).getId());
    }


    @SuppressLint("HandlerLeak")
    private void get_Component_data(String components_service) {
        all_subAsset = new ArrayList<>();
        all_subAsset.add(0, getResString("lbl_pl_slct_msg"));
        NetworkRequest.getComponents(this, components_service, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    Component_model selected_asset = DataHolder_Model.getInstance().getComponent_models().get(0);
                    String subAssets_txt = selected_asset.getComponent_sub_assets();
                    if (subAssets_txt != null && !subAssets_txt.equals("0") && !subAssets_txt.equals("")) {
                        final List<String> sub_ast_name = Arrays.asList(subAssets_txt.split("##"));
                        for (int j = 0; j < sub_ast_name.size(); j++) {
                            String[] row_data = sub_ast_name.get(j).split("#");
                            if (row_data[0] != null && !row_data[0].equals(""))
                                all_subAsset.add(row_data[0]);
                        }
                        if (all_subAsset != null) {
                            subasset_spnr.setItems(all_subAsset, "");
                            subasset1_spnr.setItems(all_subAsset, "");
                            subasset2_spnr.setItems(all_subAsset, "");
                        }
                    }
                }
            }
        });
    }

    private int findIndex(ArrayList<Constant_model> list, String string) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return index;

    }

    Bitmap scalImage;

    public void convertScaleBitmap(ThermalImageFile thermalImageFile) {
//        allImages.add(BitmapAndroid.createBitmap(thermalImageFile.getImage()).getBitMap());
        Bitmap thermal_bitmap = BitmapAndroid.createBitmap(thermalImageFile.getImage()).getBitMap();
        Scale scale = thermalImageFile.getScale();
        scalImage = BitmapAndroid.createBitmap(scale.getFixedFullRangeScaleImage()).getBitMap();
        putOverlay(thermal_bitmap, scalImage, thermalImageFile.getStatistics().min.asCelsius().value,
                thermalImageFile.getStatistics().max.asCelsius().value);
        allImages.add(0, thermal_bitmap);
        printLog("scale max= " + thermalImageFile.getScale().getRangeMax().asCelsius());
        printLog("scale min= " + thermalImageFile.getScale().getRangeMin().asCelsius());
    }

    public void putOverlay(Bitmap bitmap, Bitmap overlay, double minTemp, double maxTemp) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        int top = bitmap.getHeight() / 4;
        canvas.drawBitmap(overlay, 20, top, paint);
        float scale = getResources().getDisplayMetrics().density;
        Paint tvPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tvPaint.setColor(Color.WHITE);
        tvPaint.setTextSize((int) (12 * scale));
        canvas.drawText("" + round(maxTemp, 1), 10, top - 15, tvPaint);
        canvas.drawText("" + round(minTemp, 1), 10, top * 3 + 35, tvPaint);

    }

    public String convertTemperature(String temp) {
        if (temp.equals("") || !_switch.isChecked())
            return temp;
        Double newTemp = Double.parseDouble(temp);
        newTemp = 32 + (newTemp * 9 / 5);
        return "" + round(newTemp, 2);
    }
}

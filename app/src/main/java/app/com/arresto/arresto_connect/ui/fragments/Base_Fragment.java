package app.com.arresto.arresto_connect.ui.fragments;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getRealPathFromURI;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CameraActivity;
import app.com.arresto.arresto_connect.constants.RingtonePlayer;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.custom_scan.DecoderActivity;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.modules.inspection.thermal.ThermalCameraActivity;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.DfuService;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;

public abstract class Base_Fragment extends Fragment {
    public BaseActivity baseActivity;
    public ACProgressFlower progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        progressDialog = new ACProgressFlower.Builder(baseActivity)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.YELLOW)
//                .bgColor(Color.BLACK)
                .text(getResString("lbl_wait_st")).textSize(16).textMarginTop(5)
                .petalThickness(2)
                .sizeRatio((float) 0.22)
                .fadeColor(Color.WHITE).build();
        progressDialog.setCancelable(false);

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        return FragmentView(inflater, parent, savedInstanseState);
    }

    public abstract View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);


    public void show_progress() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void hide_progress() {
        if (progressDialog != null && !baseActivity.isFinishing())
            progressDialog.cancel();
    }

    public void show_Date_piker(final TextView tv) {
        final Calendar c = Calendar.getInstance();
        if (!tv.getText().toString().equals("")) {
            try {
                Date d = baseActivity.Date_Format().parse(tv.getText().toString());
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                tv.setText(baseActivity.Date_Format().format(c.getTime()));
                tv.setTag(baseActivity.server_date_format.format(c.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, mYear, mMonth, mDay);
        dialog.show();
    }

    BarcodeListener barcodeListener;
    OCRListener ocrListener;
    OnImageCapture onImageCapture;
    OnPlaceSelectListener placeSelectListener;
    int BARCODE_REQUEST = 10001;
    int OCR_REQUEST = 10002;
    int IMAGE_REQUEST = 10003;
    int IMAGE_GALLERY_REQUEST = 10004;
    int PLACE_LOCATION_REQUEST = 10008;
    int THERMAL_IMAGE_REQUEST = 10009;
    int THERMAL_GALLERY_REQUEST = 10010;
    String imageDirectory = Static_values.directory;
    String fileName = "";

    public void selectLocation(OnPlaceSelectListener placeSelectListener) {
        this.placeSelectListener = placeSelectListener;
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getActivity());
        startActivityForResult(intent, PLACE_LOCATION_REQUEST);
    }

    public void scan_rfid(NFC_Listner.Read_interface read_interface) {
        baseActivity.Read_intent(read_interface);
    }


    public void scan_barcode(BarcodeListener barcodeListener) {
        if (!baseActivity.isStoragePermissionGranted())
            return;
        this.barcodeListener = barcodeListener;
        Intent intent = new Intent(getActivity(), DecoderActivity.class);
        startActivityForResult(intent, BARCODE_REQUEST);
    }

    public void scan_Ocr(OCRListener ocrlistner) {
        if (!baseActivity.isStoragePermissionGranted())
            return;
        this.ocrListener = ocrlistner;
        baseActivity.checkDir(imageDirectory);
        String file_name = System.currentTimeMillis() + ".jpg";
        imagePath = imageDirectory + file_name;
        startCamera(OCR_REQUEST, file_name, false, true, false);
    }

    public void chooseThermographyImage(String path, final OnImageCapture onImageCapture) {
        if (!baseActivity.isStoragePermissionGranted())
            return;
        imageDirectory = path;
        this.onImageCapture = onImageCapture;
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_choose_image);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        final TextView capture_tv = dialog.findViewById(R.id.capture_tv);
        capture_tv.setText("Take Thermal Photo");
        final TextView choose_tv = dialog.findViewById(R.id.choose_tv);
        choose_tv.setText("Choose Thermal Photo");
        final MaterialButton cancel_btn = dialog.findViewById(R.id.cancel_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        capture_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseActivity.isSupportThermalSensor()) {
                    capture_Thermal_Image(onImageCapture);
                    dialog.dismiss();
                } else {
                    Toast.makeText(baseActivity, "Temperature Sensor Not Detected", Toast.LENGTH_LONG).show();
                }
            }
        });
        choose_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, THERMAL_GALLERY_REQUEST);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void capture_Thermal_Image(OnImageCapture onImageCapture) {
        this.onImageCapture = onImageCapture;
        baseActivity.checkDir(imageDirectory);
        if (fileName.equals("")) {
            fileName = System.currentTimeMillis() + ".jpg";
            imagePath = imageDirectory + fileName;
        }
        startCamera(THERMAL_IMAGE_REQUEST, fileName, true, false, true);
    }


    public void chooseImage(String path, String file_Name, final OnImageCapture onImageCapture) {
        imageDirectory = path;
        fileName = file_Name;
        imagePath = imageDirectory + fileName;
        chooseImage(onImageCapture);
    }


    public void chooseImage(final OnImageCapture onImageCapture) {
        if (!baseActivity.isStoragePermissionGranted())
            return;
        this.onImageCapture = onImageCapture;
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_choose_image);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        final TextView capture_tv = dialog.findViewById(R.id.capture_tv);
        final TextView choose_tv = dialog.findViewById(R.id.choose_tv);
        final MaterialButton cancel_btn = dialog.findViewById(R.id.cancel_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        capture_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture_image(onImageCapture);
                dialog.dismiss();
            }
        });
        choose_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void capture_image(OnImageCapture onImageCapture) {
        this.onImageCapture = onImageCapture;
        baseActivity.checkDir(imageDirectory);
        if (fileName.equals("")) {
            fileName = System.currentTimeMillis() + ".jpg";
            imagePath = imageDirectory + fileName;
        }
        startCamera(IMAGE_REQUEST, fileName, true, false, false);
    }


    public void startCamera(int request_code, String name, boolean isTime, boolean isScan, boolean isThermal) {
        Intent camera = new Intent();
        if (isThermal)
            camera.setClass(getActivity(), ThermalCameraActivity.class);
        else
            camera.setClass(getActivity(), CameraActivity.class);
        camera.putExtra("name", name);
        camera.putExtra("path", imageDirectory);
        camera.putExtra("istime", isTime);
        camera.putExtra("isScan", isScan);
        startActivityForResult(camera, request_code);
    }

    public String imagePath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OCR_REQUEST) {
                runObjectDetection(BitmapFactory.decodeFile(imagePath));
//                new File(imagePath).delete();
            } else if (requestCode == BARCODE_REQUEST && data != null) {
                String scaned_data = data.getDataString();
                if (scaned_data != null) {
                    scaned_data = scaned_data.replaceAll("amp;", "");
                    barcodeListener.onScanned(scaned_data);
                } else {
                    show_snak(getActivity(), getResString("lbl_try_again_msg"));
                }
            } else if (requestCode == IMAGE_REQUEST || requestCode == THERMAL_IMAGE_REQUEST) {
                onImageCapture.onCapture(imagePath);
            } else if (requestCode == IMAGE_GALLERY_REQUEST || requestCode == THERMAL_GALLERY_REQUEST) {
                show_progress();
                Uri selectedImage = data.getData();
                imagePath = getRealPathFromURI(getActivity(), selectedImage);
                onImageCapture.onCapture(imagePath);
                hide_progress();
            } else if (requestCode == PLACE_LOCATION_REQUEST) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                placeSelectListener.OnPlaceSelect(place);
                Log.e("searched", " current_latLng " + place);
            } else {
                show_snak(getActivity(), getResString("lbl_try_again_msg"));
            }

        }
    }

    public void runObjectDetection(Bitmap bitmap) {
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(FirebaseVisionImage.fromBitmap(bitmap))
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.e("text scan", "" + firebaseVisionText.getText());
                        Log.e("text scan blocks", "" + firebaseVisionText.getTextBlocks());
                        List<String> textWords = new ArrayList<>();
                        for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
                            for (FirebaseVisionText.Line line : textBlock.getLines()) {
                                textWords.add(line.getText().toLowerCase());
                            }
                        }
//                        if (textWords.size() > 0)
//                            showForChooseOCR(textWords, R.string.lbl_select_batch, true);
                        String getbach = "", getserial = "", uid = "";
                        for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
                            String regexStr = "^[0-9]*$";
//                            String rStr = "^[a-zA-Z ]+$";

                            for (FirebaseVisionText.Line line : textBlock.getLines()) {
                                String txt = line.getText().toLowerCase();
                                if (txt.contains("serial") && txt.contains("batch")) {
                                    String[] batch_serial = txt.split("serial");
                                    String batch = format_scanText(batch_serial[0]);
                                    String serial = format_scanText(batch_serial[1]);
                                    if (batch.trim().matches(regexStr) && batch.length() <= 8 && batch.length() >= 6 && getbach.equals("")) {
                                        getbach = batch;
                                    }
                                    if (serial.trim().matches(regexStr) && serial.length() == 4 && getserial.equals("")) {
                                        getserial = serial;
                                    }
                                } else if (txt.contains("serial") || txt.contains("batch")) {
                                    txt = format_scanText(txt);
                                } else if (txt.toLowerCase().contains("uid") || (txt.trim().matches(regexStr) && txt.length() >= 10)) {
                                    txt = format_scanText(txt.toLowerCase());
                                }

                                if (txt.trim().matches(regexStr) && txt.length() >= 10 && txt.length() <= 12 && uid.equals("")) {
                                    uid = txt;
                                } else if (txt.trim().matches(regexStr) && txt.length() == 4 && getserial.equals("")) {
                                    getserial = txt;
                                } else if (txt.trim().matches(regexStr) && txt.length() <= 8 && txt.length() >= 6 && getbach.equals("")) {
                                    getbach = txt;
                                }

                                if (!getbach.equals("") && !getserial.equals("")) {
                                    getbach = getbach.replaceFirst("^0+(?!$)", "");
                                    getserial = getserial.replaceFirst("^0+(?!$)", "");
                                    ocrListener.onScanned(getbach, getserial);
                                    Toast.makeText(getActivity(), getResString("lbl_serial_no_st") + " = " + getserial + "\n" + getResString("lbl_batch_no_st") + " = " + getbach, Toast.LENGTH_LONG).show();
                                    return;
                                } else if (!uid.equals("")) {
//                                    uid = uid.replaceFirst("^0+(?!$)", "");
                                    ocrListener.onUIDScanned(uid);
                                    Toast.makeText(getActivity(), "UID = " + uid, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                        if (getbach.equals("") || getserial.equals("")) {
                            if (textWords.size() > 0)
                                showForChooseOCR(textWords, R.string.lbl_select_batch, true);
                            else
                                show_snak(getActivity(), getResString("lbl_try_again_msg"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        show_snak(getActivity(), getResString("lbl_try_again_msg"));
                        Log.e("text error", "" + e.getMessage());
                    }
                });
    }

    String batch;

    private void showForChooseOCR(List<String> textWords, int title, boolean isBatch) {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        dialog.setTitle(title);
        TextView header = dialog.findViewById(R.id.header);
        header.setText(title);
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        CustomRecyclerAdapter ad = new CustomRecyclerAdapter(baseActivity, textWords);
        lvLangs.setAdapter(ad);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = textWords.get(ad.lastSelected);
                if (isBatch) {
                    batch = t;
                    textWords.remove(ad.lastSelected);
                    showForChooseOCR(textWords, R.string.lbl_select_serial, false);
                } else {
                    t = t.replaceFirst("^0+(?!$)", ""); //remove first 0 digits
                    ocrListener.onScanned(batch, t);
                }
                dialog.dismiss();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    String format_scanText(String input) {
        input = input.replaceAll("[^a-zA-Z0-9]", "");
        input = input.replace("serialno", "");
        input = input.replace("batchno", "");
        input = input.replace("serial", "");
        input = input.replace("batch", "");
        input = input.replace("no", "");
        input = input.replace("uid", "");
        return input;
    }

    public boolean isEmpty(TextView etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            if (etText.getParent().getParent() instanceof TextInputLayout) {
                ((TextInputLayout) etText.getParent().getParent()).setError("This Field is Mandatory");
            } else
                etText.setError("This Field is Mandatory");
            return true;
        }
    }

    public Dialog show_Rfid_Dialog() {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.alert_dialog_layout);
        final TextView title_tv = dialog.findViewById(R.id.title_tv);
        final TextView message_tv = dialog.findViewById(R.id.message_tv);
        Button but_yes = dialog.findViewById(R.id.but_yes);
        Button but_no = dialog.findViewById(R.id.but_no);
        but_yes.setVisibility(VISIBLE);
        title_tv.setText("Waiting!");
        message_tv.setText("Waiting to connect RFID tag");

        but_yes.setText(getResString("lbl_cncl_st"));
        but_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                baseActivity.write_interface = null;
                baseActivity.text_toWrite = null;
            }
        });
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent e) {
                return baseActivity.dispatchKeyEvent(e);
            }

        });
        return dialog;
    }

    public String getCountryCode(String countryName) {
        // Get all country codes in a string array.
        String[] isoCountryCodes = Locale.getISOCountries();
        Map<String, String> countryMap = new HashMap<>();
        Locale locale;
        String name;
        // Iterate through all country codes:
        for (String code : isoCountryCodes) {
            // Create a locale using each country code
            locale = new Locale("", code);
            // Get country name for each code.
            name = locale.getDisplayCountry();
            // Map all country names and codes in key - value pairs.
            countryMap.put(name, code);
        }
        String contryId = countryMap.get(countryName);
        String contryDialCode = null;
        String[] arrContryCode = this.getResources().getStringArray(R.array.DialingCountryCode);
        for (int i = 0; i < arrContryCode.length; i++) {
            String[] arrDial = arrContryCode[i].split(",");
            if (arrDial[1].trim().equals(contryId.trim())) {
                contryDialCode = arrDial[0];
                break;
            }
        }
        return contryDialCode;
    }


    public interface BarcodeListener {
        void onScanned(String scaned_text);
    }

    public interface OCRListener {
        void onScanned(String batch, String serial);

        void onUIDScanned(String uid);
    }

    public interface OnImageCapture {
        void onCapture(String path);
    }

    public interface OnPlaceSelectListener {
        void OnPlaceSelect(Place place);
    }

    //     Knowledgetree get_category
    public ArrayList get_childs(final String parent) {
        ArrayList data_list = new ArrayList<>();
        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String treeData) {
                boolean nodeOk = treeData.contains(parent);
                return nodeOk ? 0 : 1;
            }
        };
        TreeNode treeRoot = treeNodes.get(parent);
        if (treeRoot != null) {
            TreeNode found = treeRoot.findTreeNode(searchCriteria);
            Log.e("is leaf ", " " + found.isLeaf());
            if (!found.isLeaf()) {
                List<TreeNode> childs = found.getChildrens();
                for (TreeNode treeNode : childs) {
                    Category_Model child_model = new Gson().fromJson(new Gson().toJson(treeNode.getData()), Category_Model.class);
                    data_list.add(child_model);
                }
            }
        }
        return data_list;
    }

    public String toCamelCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

    public void rotate_image(final ImageView imageView, final int angle_frm, final int angle_to) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageView, "rotation", angle_frm, angle_to);
        rotate.setDuration(500);
        rotate.start();
    }

    public void visible(final View view) {
        view.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(VISIBLE);
                    }
                });
    }

    public void Gone(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public void printLog(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("baseActivity", msg);
    }


    public int mFileType;
    public static final int SELECT_FILE_REQ = 1;
    public static final int SELECT_INIT_FILE_REQ = 2;
    public String mFilePath;
    public Uri mFileStreamUri;
    public String mInitFilePath;
    public Uri mInitFileStreamUri;

    public void openFileChooser() {
        mFileType = DfuService.TYPE_AUTO;

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mFileType == DfuService.TYPE_AUTO ? DfuService.MIME_TYPE_ZIP : DfuService.MIME_TYPE_OCTET_STREAM);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(baseActivity.getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    public void getSensorData(String device_name, ObjectListener objectListener) {
        if (isNetworkAvailable(baseActivity)) {
            String url = All_Api.getSensorVibrations + client_id + "&sensor_id=" + device_name;
            url = url.replace(" ", "%20");
            new NetworkRequest().make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("getSensorData", " response run");
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                FallCountModel fallCountModel = AppUtils.getGson().fromJson(jsonObject.getString("data"), FallCountModel.class);
                                ArrayList<String> containingThresholds = new ArrayList<>();
                                if (fallCountModel != null && fallCountModel.getFirmware_info().getThresholds() != null) {
                                    for (Constant_model threshold : fallCountModel.getFirmware_info().getThresholds()) {
                                        containingThresholds.add(threshold.getId());
                                    }
                                }
                                fallCountModel.getFirmware_info().setContainingThresholds(containingThresholds);
                                objectListener.onResponse(fallCountModel);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "" + error);
                }
            });
        } else {
//            show_snak(activity, getResString("lbl_network_alert"));
        }
    }

    Vibrator vibrator;
    RingtonePlayer ringtonePlayer;
    AlertDialog connectionAlert;

    public void startNotification() {
        Log.d("BaseFragment", "startNotification()");
        connectionAlert = connection_alert();
        connectionAlert.show();
        if (ringtonePlayer == null)
            ringtonePlayer = new RingtonePlayer(baseActivity);
        ringtonePlayer.play(true);
        vibrator = (Vibrator) baseActivity.getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }
    }

    public void stopNotification() {
        Log.e("BaseFragment", "stopNotification()");
        if (connectionAlert != null)
            connectionAlert.cancel();
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    public AlertDialog connection_alert() {
        return new AlertDialog.Builder(baseActivity)
                .setIcon(R.drawable.error)
                .setTitle("Danger!")
                .setMessage("Hook is Disconnected!")
                .setPositiveButton(getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopNotification();
                    }
                }).create();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(mMessageReceiver);
        if (ringtonePlayer != null) {
            ringtonePlayer.releaseMedia();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(mMessageReceiver, new IntentFilter("sensor_alert"));
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            Log.e("onReceive"," BroadcastReceiver run");
            if (status.toLowerCase().contains("hd")) {
                startNotification();
            }else if(status.toLowerCase().contains("hc")){
                stopNotification();
            }
        }
    };

}

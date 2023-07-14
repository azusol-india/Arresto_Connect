package app.com.arresto.arresto_connect.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.Quality_image_adepter;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;

public class FeedBack_Fragment extends Dialog {
    BaseActivity context;
    Base_Fragment fragment;

    public FeedBack_Fragment(Base_Fragment fragment) {
        super(fragment.baseActivity, R.style.theme_dialog);
        this.context = fragment.baseActivity;
        this.fragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback_fragment);
        find_all_id();
        sbmt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rmrk_edt.getText().toString().equals(""))
                    show_snak(context, getResString("lbl_please_enter_feedback"));
                else
                    makeJsonRequest(All_Api.feedBackUrl);
            }
        });

        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.chooseImage(new Base_Fragment.OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        images.add(path);
                        image_adepter.add_item(images);
                    }
                });
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }
    //    @Override
//    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//        if (view == null) {
//            view = inflater.inflate(R.layout.feedback_fragment, parent, false);
//            find_all_id(view);
//
//            sbmt_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (rmrk_edt.getText().toString().equals(""))
//                        show_snak(getActivity(), getResString("lbl_please_enter_feedback"));
//                    else
//                        makeJsonRequest(All_Api.feedBackUrl);
//                }
//            });
//
//            add_image_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//                }
//            });
//        }
//        return view;
//    }

    EditText rmrk_edt;
    MaterialButton sbmt_btn;
    FloatingActionButton add_image_btn;
    RecyclerView img_list;
    Quality_image_adepter image_adepter;
    ArrayList images;
    ImageView close_btn;

    private void find_all_id() {
        rmrk_edt = findViewById(R.id.rmrk_edt);
        img_list = findViewById(R.id.img_list);
        add_image_btn = findViewById(R.id.add_image_btn);
        sbmt_btn = findViewById(R.id.sbmt_btn);
        close_btn = findViewById(R.id.close_btn);

        img_list.setLayoutManager(new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false));
        img_list.setPadding(3, 6, 3, 6);

        images = new ArrayList<>();
        image_adepter = new Quality_image_adepter(context, images);
        img_list.setAdapter(image_adepter);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        rmrk_edt.setHint(getResString("lbl_please_enter_feedback"));
    }


    private void makeJsonRequest(String url) {
        final JSONObject params2 = new JSONObject();
        try {
            params2.put("user_id", Static_values.user_id);
            params2.put("client_id", Static_values.client_id);
            params2.put("message", "" + rmrk_edt.getText().toString());
            JSONArray image_json = new JSONArray();
            for (int i = 0; i < images.size(); i++) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(""+images.get(i));
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                image_json.put("data:" + filetype + ";base64," + AppUtils.Image_toBase64(""+images.get(i)));


//                Uri image_uri = (Uri) images.get(i);
//                final MimeTypeMap mime = MimeTypeMap.getSingleton();
//                String extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(image_uri));
//                String filetype = null;
//                if (extension != null) {
//                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                }
//                String image_string = uriToBase64(image_uri);
//                if (!image_string.equals(""))
//                    image_json.put("data:" + filetype + ";base64," + image_string);
            }
            params2.put("feedback_file", image_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("data is", "" + params2.toString());
        NetworkRequest network_request = new NetworkRequest(context);
        network_request.make_contentpost_request(url, params2, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        show_snak(context, jsonObject1.getString("message"));
                        if (jsonObject1.getString("status_code").equals("200")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    context.onBackPressed();
                                }
                            }, 2000);
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
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // When an Image is picked
//        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
//            if (data.getClipData() != null) {
//                ClipData mClipData = data.getClipData();
//                for (int i = 0; i < mClipData.getItemCount(); i++) {
//                    ClipData.Item item = mClipData.getItemAt(i);
//                    Uri uri = item.getUri();
//                    if (uri != null)
//                        images.add(uri);
//                }
//            } else if (data.getData() != null) {
//                Uri mImageUri = data.getData();
//                images.add(mImageUri);
//            }
//            image_adepter.add_item(images);
//        } else {
//            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
//        }
//
//    }

    private String uriToBase64(Uri imageUri) {
        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            final Bitmap bm = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}

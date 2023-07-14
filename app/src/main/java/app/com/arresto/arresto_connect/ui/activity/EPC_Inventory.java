package app.com.arresto.arresto_connect.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.epc_lib.ScanUtil;
import cn.pda.serialport.Tools;

public class EPC_Inventory extends BaseActivity {

    UHFRManager mUhfrManager;
    private ScanUtil instance;

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT == 29) {
            instance = ScanUtil.getInstance(this);
            instance.disableScanKey("134");
        }
        mUhfrManager = UHFRManager.getInstance();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.epc_inventory;
    }

    TextView scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scan_btn = findViewById(R.id.scan_btn);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRead();
            }
        });
    }

    private Handler handler1 = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String epc = msg.getData().getString("data");
                    String rssi = msg.getData().getString("rssi");

                    if (epc == null || epc.length() == 0) {
                        return;
                    }

                    showToast(epc + "\n" + rssi);

                    break;
                case 1980:
//                    String countString = tvRunCount.getText().toString();
//                    if (countString.equals("") || countString == null) {
//                        tvRunCount.setText(String.valueOf(1));
//                    } else {
//                        int previousCount = Integer.valueOf(countString);
//                        int nowCount = previousCount + 1;
//                        tvRunCount.setText(String.valueOf(nowCount));
//                    }
                    break;
            }
        }
    };

    public void isRead() {
        if (mUhfrManager == null) {
            showToast("connection_failed");
            return;
        }
        mUhfrManager.asyncStartReading();
        handler1.postDelayed(runnable_MainActivity, 0);
    }


    private Runnable runnable_MainActivity = new Runnable() {
        @Override
        public void run() {
            List<Reader.TAGINFO> list1;
            List<Reader.TAGINFO> list2;
            List<Reader.TAGINFO> list3;
            list1 = mUhfrManager.tagInventoryRealTime();
            list2 = mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
            list3 =mUhfrManager.tagInventoryByTimer((short) 50);
            String data;
            handler1.sendEmptyMessage(1980);
            if (list2 != null && list2.size() > 0) {
                Log.e("TGA", list2.size() + "");
                for (Reader.TAGINFO tfs : list2) {
//                    byte[] epcdata = tfs.EpcId;
//                    if (isTid){
                        data = Tools.Bytes2HexString(tfs.EmbededData, tfs.EmbededDatalen);
//                    }else {
//                    data = Tools.Bytes2HexString(epcdata, epcdata.length);
//                    }
                    int rssi = tfs.RSSI;
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("data", data);
                    b.putString("rssi", rssi + "");
                    msg.setData(b);
                    handler1.sendMessage(msg);
                }
            }
            handler1.postDelayed(runnable_MainActivity, 0);
        }
    };
    Toast toast;
    private void showToast(String info) {
        if (toast == null) toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }
}

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

package app.com.arresto.arresto_connect.constants;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;


public class Scan_RFID {
    BaseActivity activity;
    public NfcAdapter mNfcAdapter;
    Intent intent;
    public boolean isNFCSupport;
    String[][] techList = new String[][]{{Ndef.class.getName()}, {NdefFormatable.class.getName()}};
    String[][] techListsArray = new String[][]{new String[]{MifareUltralight.class.getName(), Ndef.class.getName(), NfcA.class.getName()},
            new String[]{NdefFormatable.class.getName(), MifareClassic.class.getName(), Ndef.class.getName(), NfcA.class.getName()}};

    IntentFilter[] intentFiltersArray;
    PendingIntent pendingIntent;

    public Scan_RFID(BaseActivity context) {
        activity = context;
        init();
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
            if (mNfcAdapter != null) {
//                enableForegroundDispatch();
                isNFCSupport = true;
            } else
                isNFCSupport = false;
        } else {
//            enableForegroundDispatch();
            isNFCSupport = true;
        }
    }

    private void init() {

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndef.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        ndef.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndef.addDataType("text/plain");    /* Handles all MIME based dispatches. You should specify only the ones that you need. */
//            ndef.addDataScheme("https");
//            filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef};


        Intent nfcIntent = new Intent(activity, activity.getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(activity, 0, nfcIntent,
                    0 | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(activity, 0, nfcIntent,
                    0);
        }


    }

    public void enableForegroundDispatch() {
        if (mNfcAdapter != null) {
//        mNfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techList);
            mNfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
            activity.printLog("enableForegroundDispatch runn");
        }
    }

    public void disable_adapter() {
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(activity);
    }

    public void set_intent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public boolean isSupport() {
        return isNFCSupport;
    }

    public String get_RfidTAG() {
        checkNfcEnabled();
        if (mNfcAdapter == null && activity.mUhfrManager == null) {
            AppUtils.show_snak(activity, "Your Device Does Not support NFC.");
            return "";
        } else if (intent == null) {
            AppUtils.show_snak(activity, "Please check if your phone is NFC enabled, please locate the position of NFC reader in your Phone and bring the tag near the reader");
        }

//   code to read serial number  data
//        if (intent != null) {
//            if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
//                Log.e("tag scan ", "byte array=   " + intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
//                String s = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
//                Log.e("tag scan ", "NFC Tag is   " + s);
//                mNfcAdapter.disableForegroundDispatch(activity);
//                return s;
//            }
//        }

//   code to read payload  data
        if (intent != null) {
            NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
            NdefRecord record = msgs[0].getRecords()[0];
            byte[] payload = record.getPayload();
//               String payloadString = new String(payload);
            if (payload.length > 0) {
                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                int langCodeLen = payload[0] & 0063;
                String s = "";
                try {
                    s = new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1, textEncoding);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    activity.printLog("UnsupportedEncodingException=" + e.getMessage());
                }
                return s;
            }
//            return payloadString;
        }
        return "";
    }

    NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }

        } else {
            Log.e("tag", "Unknown intent.");
        }
        return msgs;
    }


    public boolean checkNfcEnabled() {
        if (mNfcAdapter != null) {
            Boolean nfcEnabled = mNfcAdapter.isEnabled();
            if (!nfcEnabled) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.lbl_text_warning_nfc_is_off))
                        .setMessage(activity.getString(R.string.lbl_text_turn_on_nfc))
                        .setCancelable(false)
                        .setPositiveButton(activity.getString(R.string.lbl_text_update_settings),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(AppUtils.getResString("lbl_cncl_st"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }

            return nfcEnabled;
        }
        return false;
    }


//    Code  to write text on RFID Tag

    public String write_text(String text) {
        if (intent != null) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null && ((NdefMessage) rawMsgs[0]).getRecords()[0].getPayload().length > 0) {
                return "Tag already written";
            }
            writeRepeat = 0;
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            WriteResponse wr = writeTag(getTagAsNdef(text), detectedTag);
            String message = (wr.getStatus() == 1 ? "Success: " : "Failed: ") + wr.getMessage();
            return message;
        } else {
            return "Failed: This tag is not writable";
        }
    }

    int writeRepeat = 0; //used when writing error found , repeat writing 3 times

    public WriteResponse writeTag(NdefMessage message, Tag tag) {
        String mess;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        mess = "Formatted tag and wrote message";
                        return new WriteResponse(1, mess);
                    } catch (IOException e) {
                        mess = "Failed to format tag." + " error=" + e.getMessage();
                        activity.printLog(mess);
                        if (writeRepeat < 3) {
                            writeRepeat++;
                            return writeTag(message, tag);
                        } else
                            return new WriteResponse(0, mess);
                    }
                } else {
                    mess = "Tag doesn't support NDEF.";
                    return new WriteResponse(0, mess);
                }
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResponse(0, "Tag is read-only");
                }
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    mess = "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.";
                    return new WriteResponse(0, mess);
                }
                ndef.writeNdefMessage(message);
                mess = "Wrote message to Pre-Formatted tag.";
                return new WriteResponse(1, mess);

            }
        } catch (Exception e) {
            mess = "Failed to write tag" + " error=" + e.getMessage();
            activity.printLog(mess);
            if (writeRepeat < 3) {
                writeRepeat++;
                return writeTag(message, tag);
            } else
                return new WriteResponse(0, mess);
        }
    }


    private class WriteResponse {
        int status;
        String message;

        WriteResponse(int Status, String Message) {
            this.status = Status;
            this.message = Message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    private NdefMessage getTagAsNdef(String txt) {
        try {
            NdefRecord record;
            // Get UTF-8 byte
            if (txt.contains("http")) {
                Uri uri = Uri.parse(txt);
                record = NdefRecord.createUri(uri);
                return new NdefMessage(record);
            } else {
                byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
                byte[] text = txt.getBytes("UTF-8"); // Content in UTF-8
                int langSize = lang.length;
                int textLength = text.length;
                ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
                payload.write((byte) (langSize & 0x1F));
                payload.write(lang, 0, langSize);
                payload.write(text, 0, textLength);
                record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
                return new NdefMessage(new NdefRecord[]{record});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

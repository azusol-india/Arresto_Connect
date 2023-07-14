package app.com.arresto.arresto_connect.appcontroller;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class NFCManager {
    BaseActivity activity;
    private NfcAdapter nfcAdpt;

    public NFCManager(BaseActivity activity) {
        this.activity = activity;
    }

    public void verifyNFC() throws NFCNotSupported, NFCNotEnabled {
        nfcAdpt = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdpt == null)
            throw new NFCNotSupported();
        if (!nfcAdpt.isEnabled())
            throw new NFCNotEnabled();
    }

    public void enableDispatch() {
        Intent nfcIntent = new Intent(activity, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, nfcIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter[] intentFiltersArray = new IntentFilter[]{};
        String[][] techList = new String[][]{{Ndef.class.getName()}, {NdefFormatable.class.getName()}};
        nfcAdpt.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techList);
//        nfcAdpt.enableReaderMode (activity);
    }

    public void disableDispatch() {
        nfcAdpt.disableForegroundDispatch(activity);
    }

    public static class NFCNotSupported extends Exception {
        public NFCNotSupported() {
            super();
        }
    }

    public static class NFCNotEnabled extends Exception {

        public NFCNotEnabled() {
            super();
        }
    }


    public void writeTag(Tag tag, NdefMessage message) {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                } else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NdefMessage createUriMessage(String content, String type) {
        NdefRecord record = NdefRecord.createUri(type + content);
        NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
        return msg;
    }

    public NdefMessage createTextMessage(String content) {
        try {
            // Get UTF-8 byte
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8"); // Content in UTF-8
            int langSize = lang.length;
            int textLength = text.length;
            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public NdefMessage createExternalMessage(String content) {
        NdefRecord externalRecord = NdefRecord.createExternal("com.survivingwithandroid", "data", content.getBytes());
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{externalRecord});
        return ndefMessage;
    }
}
package app.com.arresto.arresto_connect.constants;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class CustomTextWatcher implements TextWatcher {
    TextView mEditText;
    OnTextChange listner;

    public CustomTextWatcher(TextView e, OnTextChange listner) {
        mEditText = e;
        this.listner = listner;
        mEditText.addTextChangedListener(this);
    }

    public CustomTextWatcher(EditText e, OnTextChange listner) {
        mEditText = e;
        this.listner = listner;
        mEditText.addTextChangedListener(this);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        if (mEditText.hasFocus())
            listner.afterChange(mEditText, s.toString());
    }

    public interface OnTextChange {
        void afterChange(TextView view, String text);
    }
}
package binaryblitz.gethelpapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Utils;

public class PhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_layout);
        UsPhoneNumberFormatter addLineNumberFormatter = new UsPhoneNumberFormatter(
                new WeakReference<>(((EditText) findViewById(R.id.edittext1))));
        ((EditText) findViewById(R.id.edittext1)).addTextChangedListener(
                addLineNumberFormatter
                //new PrefixEntryWatcher("+7", ((EditText) findViewById(R.id.edittext1)))
        );

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isConnected(PhoneActivity.this)) {
                    Snackbar.make(findViewById(R.id.main), "Отсутствует интернет подключение.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(PhoneActivity.this, CodeActivity.class);

                String phone = ((EditText) findViewById(R.id.edittext1)).getText().toString();
                phone = phone.replace("(", "");
                phone = phone.replace(")", "");
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class UsPhoneNumberFormatter implements TextWatcher {
        //This TextWatcher sub-class formats entered numbers as 1 (123) 456-7890
        private boolean mFormatting; // this is a flag which prevents the
        // stack(onTextChanged)
        private boolean clearFlag;
        private int mLastStartLocation;
        private String mLastBeforeText;
        private WeakReference<EditText> mWeakEditText;

        public UsPhoneNumberFormatter(WeakReference<EditText> weakEditText) {
            this.mWeakEditText = weakEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (after == 0 && s.toString().equals("1 ")) {
                clearFlag = true;
            }
            mLastStartLocation = start;
            mLastBeforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO: Do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Make sure to ignore calls to afterTextChanged caused by the work
            // done below
            if (!mFormatting) {
                mFormatting = true;
                int curPos = mLastStartLocation;
                String beforeValue = mLastBeforeText;
                String currentValue = s.toString();
                String formattedValue = formatUsNumber(s);
                if (currentValue.length() > beforeValue.length()) {
                    int setCusorPos = formattedValue.length()
                            - (beforeValue.length() - curPos);
                    mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
                } else {
                    int setCusorPos = formattedValue.length()
                            - (currentValue.length() - curPos);
                    if(setCusorPos > 0 && !Character.isDigit(formattedValue.charAt(setCusorPos -1))){
                        setCusorPos--;
                    }
                    mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
                }
                mFormatting = false;
            }
        }

        private String formatUsNumber(Editable text) {
            StringBuilder formattedString = new StringBuilder();
            // Remove everything except digits
            int p = 0;
            while (p < text.length()) {
                char ch = text.charAt(p);
                if (!Character.isDigit(ch)) {
                    text.delete(p, p + 1);
                } else {
                    p++;
                }
            }
            // Now only digits are remaining
            String allDigitString = text.toString();

            int totalDigitCount = allDigitString.length();

            if (totalDigitCount == 0
                    || (totalDigitCount > 11 && !allDigitString.startsWith("7"))
                    || totalDigitCount > 12) {
                // May be the total length of input length is greater than the
                // expected value so we'll remove all formatting
                text.clear();
                text.append(allDigitString);
                return allDigitString;
            }
            int alreadyPlacedDigitCount = 0;
            // Only '1' is remaining and user pressed backspace and so we clear
            // the edit text.
            if (allDigitString.equals("7") && clearFlag) {
                text.clear();
                clearFlag = false;
                return "+7 ";
            }
            if (allDigitString.startsWith("7")) {
                formattedString.append("+7 ");
                alreadyPlacedDigitCount++;
            }
            // The first 3 numbers beyond '1' must be enclosed in brackets "()"
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append("("
                        + allDigitString.substring(alreadyPlacedDigitCount,
                        alreadyPlacedDigitCount + 3) + ") ");
                alreadyPlacedDigitCount += 3;
            }
            // There must be a '-' inserted after the next 3 numbers
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append(allDigitString.substring(
                        alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3)
                        + "-");
                alreadyPlacedDigitCount += 3;
            }
            // All the required formatting is done so we'll just copy the
            // remaining digits.
            if (totalDigitCount > alreadyPlacedDigitCount) {
                formattedString.append(allDigitString
                        .substring(alreadyPlacedDigitCount));
            }

            text.clear();
           // text.append("+7 ");
            text.append(formattedString.toString());
            return formattedString.toString();
        }

    }
}
/*======================================================================*
 DTS, Inc.
 5220 Las Virgenes Road
 Calabasas, CA 91302  USA

 CONFIDENTIAL: CONTAINS CONFIDENTIAL PROPRIETARY INFORMATION OWNED BY
 DTS, INC. AND/OR ITS AFFILIATES ("DTS"), INCLUDING BUT NOT LIMITED TO
 TRADE SECRETS, KNOW-HOW, TECHNICAL AND BUSINESS INFORMATION. USE,
 DISCLOSURE OR DISTRIBUTION OF THE SOFTWARE IN ANY FORM IS LIMITED TO
 SPECIFICALLY AUTHORIZED LICENSEES OF DTS.  ANY UNAUTHORIZED
 DISCLOSURE IS A VIOLATION OF STATE, FEDERAL, AND INTERNATIONAL LAWS.
 BOTH CIVIL AND CRIMINAL PENALTIES APPLY.

 DO NOT DUPLICATE. COPYRIGHT 2015, DTS, INC. ALL RIGHTS RESERVED.
 UNAUTHORIZED DUPLICATION IS A VIOLATION OF STATE, FEDERAL AND
 INTERNATIONAL LAWS.

 ALGORITHMS, DATA STRUCTURES AND METHODS CONTAINED IN THIS SOFTWARE
 MAY BE PROTECTED BY ONE OR MORE PATENTS OR PATENT APPLICATIONS.
 UNLESS OTHERWISE PROVIDED UNDER THE TERMS OF A FULLY-EXECUTED WRITTEN
 AGREEMENT BY AND BETWEEN THE RECIPIENT HEREOF AND DTS, THE FOLLOWING
 TERMS SHALL APPLY TO ANY USE OF THE SOFTWARE (THE "PRODUCT") AND, AS
 APPLICABLE, ANY RELATED DOCUMENTATION:  (i) ANY USE OF THE PRODUCT
 AND ANY RELATED DOCUMENTATION IS AT THE RECIPIENT'S SOLE RISK:
 (ii) THE PRODUCT AND ANY RELATED DOCUMENTATION ARE PROVIDED "AS IS"
 AND WITHOUT WARRANTY OF ANY KIND AND DTS EXPRESSLY DISCLAIMS ALL
 WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE, REGARDLESS OF WHETHER DTS KNOWS OR HAS REASON TO KNOW OF THE
 USER'S PARTICULAR NEEDS; (iii) DTS DOES NOT WARRANT THAT THE PRODUCT
 OR ANY RELATED DOCUMENTATION WILL MEET USER'S REQUIREMENTS, OR THAT
 DEFECTS IN THE PRODUCT OR ANY RELATED DOCUMENTATION WILL BE
 CORRECTED; (iv) DTS DOES NOT WARRANT THAT THE OPERATION OF ANY
 HARDWARE OR SOFTWARE ASSOCIATED WITH THIS DOCUMENT WILL BE
 UNINTERRUPTED OR ERROR-FREE; AND (v) UNDER NO CIRCUMSTANCES,
 INCLUDING NEGLIGENCE, SHALL DTS OR THE DIRECTORS, OFFICERS, EMPLOYEES,
 OR AGENTS OF DTS, BE LIABLE TO USER FOR ANY INCIDENTAL, INDIRECT,
 SPECIAL, OR CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO
 DAMAGES FOR LOSS OF BUSINESS PROFITS, BUSINESS INTERRUPTION, AND LOSS
 OF BUSINESS INFORMATION) ARISING OUT OF THE USE, MISUSE, OR INABILITY
 TO USE THE PRODUCT OR ANY RELATED DOCUMENTATION.
*======================================================================*/

package com.dts.dtsaudioprocessing.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.util.SharedPrefManager;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    static final String WELCOME_SCREEN_DISPLAYED_TAG = "WelcomeScreenDisplayed";
    static final String FIRST_RUN_TAG = "FirstRun";
    static final String EULA_ACCEPT_TAG = "EulaPrivacyPolicyAccepted";

    private Button mContinueButton;
    private CheckBox mAgreeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        //Set the product title
        TextView titleTextView = (TextView) findViewById(R.id.agreement_title);
        titleTextView.setText(Html.fromHtml(getString(R.string.welcome_dts_title)));

        // Set texts to be clickable
        SpannableString ss = new SpannableString(getString(R.string.agreement_check));
        ClickableSpan licenseSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // Start document activity to show license agreement
                openDocumentActivity(DocumentActivity.DOCUMENT_EULA);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openDocumentActivity(DocumentActivity.DOCUMENT_PRIVACY_POLICY);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        // [SpannableString]
        // The following sets the texts at their exact location to be clickable.
        // Therefore, if the text in R.string is changed, then the below values must also be updated
        ss.setSpan(licenseSpan, 15, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(privacySpan, 49, 64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView checkText = (TextView) findViewById(R.id.agreement_checkText);
        checkText.setText(ss);
        checkText.setMovementMethod(LinkMovementMethod.getInstance());
        checkText.setHighlightColor(Color.TRANSPARENT);

        // Set onClickListener for the button
        mContinueButton = (Button) findViewById(R.id.agreement_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that the user has checked "I agree"
                if (mAgreeCheckBox.isChecked()) {
                    // Remember user's accept
                    SharedPrefManager.putBoolean(WelcomeActivity.this, EULA_ACCEPT_TAG, true);
                    SharedPrefManager.putBoolean(WelcomeActivity.this, WELCOME_SCREEN_DISPLAYED_TAG, true);

                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Log.d(TAG, "User has not checked the checkBox yet. Ignoring");
                }
            }
        });

        mAgreeCheckBox = (CheckBox) findViewById(R.id.agreement_checkBox);
        mAgreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setContinueButtonColor(isChecked);
            }
        });
        //mAgreeCheckBox.getBackground().setColorFilter(getColor(this, R.color.dtsGray7), PorterDuff.Mode.MULTIPLY);

        setContinueButtonColor(mAgreeCheckBox.isChecked());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openDocumentActivity(int documentType) {
        Intent intent = new Intent(WelcomeActivity.this, DocumentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DocumentActivity.DOCUMENT_TYPE, documentType);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setContinueButtonColor(boolean isChecked) {
        if (isChecked) {
            // If check box is checked, set button color to theme color
            mContinueButton.getBackground().setColorFilter(getColor(this, R.color.dtsOrange), PorterDuff.Mode.MULTIPLY);
            mContinueButton.setTextColor(getColor(this, R.color.dtsWhite));
        } else {
            // Otherwise, remove tint
            mContinueButton.getBackground().setColorFilter(null);
            mContinueButton.setTextColor(getColor(this, R.color.dtsGray9));
        }
    }

    /**
     * Convenient function to get the color resource for different versions of Android
     *
     * @param context
     * @param id
     * @return
     */
    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}

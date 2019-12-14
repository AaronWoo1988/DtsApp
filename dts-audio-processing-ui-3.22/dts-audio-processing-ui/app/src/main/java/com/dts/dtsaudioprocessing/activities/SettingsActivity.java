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

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dts.dtssdk.DtsManager;
import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.util.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    // Views:
    private Switch mSwtSettingsUsageTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Status bar:
        setStatusBarColor();

        // Tool bar:
        Toolbar tbSettings = (Toolbar) findViewById(R.id.tb_settings);
        setSupportActionBar(tbSettings);

        // Action bar:
        ActionBar abSettings = getSupportActionBar();
        assert abSettings != null;
        abSettings.setDisplayHomeAsUpEnabled(true);

        // Interactive views:
        mSwtSettingsUsageTracking = (Switch) findViewById(R.id.swt_settings_usage_tracking);
        mSwtSettingsUsageTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCreate==mSwtSettingsUsageTracking==onCheckedChanged(). isChecked(" + isChecked + ")");

                // Save value:
                SharedPrefManager.putBoolean(SettingsActivity.this, WelcomeActivity.EULA_ACCEPT_TAG, isChecked);

                refreshUiWithUsageTracking(isChecked);

                // Enable/disable Analytics:
                DtsManager.getInstance().setMetricsEnabled(isChecked);
            }
        });

        boolean userAcceptedAgreement = SharedPrefManager.getBoolean(this, WelcomeActivity.EULA_ACCEPT_TAG, false);
        mSwtSettingsUsageTracking.setChecked(userAcceptedAgreement);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();

        refreshUi();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();
    }

    @TargetApi(21)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.dtsOrange));
        } else {
            Log.d(TAG, "==setStatusBarColor: This does not work for API Level 20 or lower.");
        }
    }

    private void refreshUi() {
        boolean userAcceptedAgreement = SharedPrefManager.getBoolean(this, WelcomeActivity.EULA_ACCEPT_TAG, false);
        refreshUiWithUsageTracking(userAcceptedAgreement);
    }

    private void refreshUiWithUsageTracking(boolean isEnabled) {
        // Customize on/off colors:
        int colorId = (isEnabled) ? R.color.dtsWhite : R.color.dtsGray8;

        mSwtSettingsUsageTracking.getThumbDrawable().setColorFilter(ContextCompat.getColor(this, colorId), PorterDuff.Mode.MULTIPLY);
        mSwtSettingsUsageTracking.getTrackDrawable().setColorFilter(ContextCompat.getColor(this, colorId), PorterDuff.Mode.MULTIPLY);
    }
}

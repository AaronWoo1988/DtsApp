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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dts.dtssdk.DtsManager;
import com.dts.dtsaudioprocessing.R;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Status bar:
        setStatusBarColor();

        // Tool bar:
        Toolbar tbAbout = (Toolbar) findViewById(R.id.tb_about);
        setSupportActionBar(tbAbout);

        // Action bar:
        ActionBar abAbout = getSupportActionBar();
        assert abAbout != null;
        abAbout.setDisplayHomeAsUpEnabled(true);

        // Interactive views:
        Button btnAboutDtsWebAddress = (Button) findViewById(R.id.btn_about_dts_web_address);
        assert btnAboutDtsWebAddress != null;
        btnAboutDtsWebAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate==mBtnWebAddress==onClick()");
                
                openWeb("http://www.dts.com");
            }
        });

        LinearLayout llAboutFacebook = (LinearLayout) findViewById(R.id.ll_about_facebook);
        assert llAboutFacebook != null;
        llAboutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate==llAboutFacebook==onClick()");
                
                openWeb("https://www.facebook.com/DTS.Inc");
            }
        });

        LinearLayout llAboutTwitter = (LinearLayout) findViewById(R.id.ll_about_twitter);
        assert llAboutTwitter != null;
        llAboutTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate==llAboutTwitter==onClick()");
                
                openWeb("https://twitter.com/dts_inc");
            }
        });

        // DTS UI version
        TextView txtAboutDtsUiVersion = (TextView) findViewById(R.id.txt_about_dts_ui_version);
        assert txtAboutDtsUiVersion != null;

        String strDtsUiVersion = "Version " + DtsManager.getInstance().getUiSdkVersion();
        txtAboutDtsUiVersion.setText(strDtsUiVersion);

        // EULA text
        TextView eulaText = (TextView) findViewById(R.id.txt_about_eula);
        assert eulaText != null;
        eulaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocumentActivity(DocumentActivity.DOCUMENT_EULA);
            }
        });

        // Privacy policy text
        TextView ppText = (TextView) findViewById(R.id.txt_about_pp);
        assert ppText != null;
        ppText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocumentActivity(DocumentActivity.DOCUMENT_PRIVACY_POLICY);
            }
        });
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

    /**
     * Convenient function to open web via starting a new intent with a URL
     *
     * @param url of the web page to open
     */
    private void openWeb(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.w(TAG, "Opening intent for URL " + url + " failed. Device may not have a browser installed/enabled");
            e.printStackTrace();
        }
    }

    private void openDocumentActivity(int documentType) {
        Intent intent = new Intent(AboutActivity.this, DocumentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DocumentActivity.DOCUMENT_TYPE, documentType);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

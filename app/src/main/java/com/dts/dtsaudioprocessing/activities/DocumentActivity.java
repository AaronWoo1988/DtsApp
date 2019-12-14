package com.dts.dtsaudioprocessing.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dts.dtsaudioprocessing.R;

public class DocumentActivity extends AppCompatActivity {
    private static final String TAG = DocumentActivity.class.getSimpleName();

    public static final String DOCUMENT_TYPE = "DocumentType";
    public static final int DOCUMENT_EULA = 1;
    public static final int DOCUMENT_PRIVACY_POLICY = 2;

    private static final String EULA = "file:///android_asset/dts_eula.html";
    private static final String PRIVACY_POLICY = "file:///android_asset/dts_privacy_policy.htm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        // Status bar:
        setStatusBarColor();

        // Tool bar:
        Toolbar tbAbout = (Toolbar) findViewById(R.id.tb_document);
        setSupportActionBar(tbAbout);

        // Action bar:
        android.support.v7.app.ActionBar abAbout = getSupportActionBar();
        assert abAbout != null;
        abAbout.setDisplayHomeAsUpEnabled(true);

        int document;
        String uri;

        // Get the attached bundle to know which document to show
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            document = bundle.getInt(DOCUMENT_TYPE);
        } else {
            document = DOCUMENT_EULA;
        }

        // Set different titles depending on which document to show
        switch (document) {
            case DOCUMENT_PRIVACY_POLICY:
                uri = PRIVACY_POLICY;
                setTitle(R.string.agreement_privacypolicy);
                break;
            case DOCUMENT_EULA:
            default:
                uri = EULA;
                setTitle(R.string.agreement_license);
                break;
        }

        WebView webView = (WebView) findViewById(R.id.document_webView);
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("http://")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else if (EULA.equals(url)) {
                    // Update title
                    setTitle(R.string.agreement_license);
                } else {
                    Log.d(TAG, "Unrecognized hyperlink clicked: " + url);
                }
                return false;
            }
        });
        webView.loadUrl(uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back icon in action bar clicked; close activity
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}

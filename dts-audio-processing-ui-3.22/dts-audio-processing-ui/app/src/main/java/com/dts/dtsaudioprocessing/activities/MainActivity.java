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
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dts.dtssdk.DtsFeature;
import com.dts.dtssdk.DtsFeatureChecker;
import com.dts.dtssdk.DtsIntents;
import com.dts.dtssdk.DtsManager;
import com.dts.dtssdk.DtsSystemStatusChecker;
import com.dts.dtssdk.accessory.StereoPreference;
import com.dts.dtssdk.DtsVersions;
import com.dts.dtssdk.accessory.ContentMode;
import com.dts.dtssdk.callback.OnCompleteCallback;
import com.dts.dtssdk.callback.QueryCallback;
import com.dts.dtssdk.result.DtsResult;
import com.dts.dtssdk.result.LicenseResultCode;
import com.dts.dtssdk.util.AudioRoute;
import com.dts.dtssdk.util.ContentModeType;
import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.util.FeatureManager;
import com.dts.dtsaudioprocessing.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TRANSITION_DURATION_MILLIS = 500;

    // Tool bar:
    private ImageView mImgMainDtsAudioProcessingPower;
    private boolean mDtsAudioProcessingEnabled = true;

    // Power-off:
    private View mVwMainDtsAudioProcessingOffOverlay;
    private ImageView mImgMainDtsArrow;
    private TextView mTxtMainDtsAudioProcessingOffMessage;


    // Interactive views:
    private RelativeLayout mRlMainTopLevel;

    // --- Content Modes:
    private LinearLayout mLlMainSoundModes;

    //-- Audio Settings :
    private LinearLayout mMainAudioSettings;

    private ImageView mImgMainMusicMode;
    private TextView mTxtMainMusicMode;

    private ImageView mImgMainMoviesMode;
    private TextView mTxtMainMoviesMode;

    private ImageView mImgMainGamesMode;
    private TextView mTxtMainGamesMode;

    private RelativeLayout mRlMainBassBoost;
    private RelativeLayout mRlMainVocalBoost;
    private RelativeLayout mRlMainTrebleBoost;
    private LinearLayout mLlMainStereoSound;
    private LinearLayout mLlMainGraphicEq;
    private RelativeLayout rlMainDtsAudioProcessingEnabled;

    private ImageView mImgMainBassBoost, mImgMainVocalBoost, mImgMainTrebleBoost;
    private TextView mTxtMainBassBoost, mTxtMainVocalBoost, mTxtMainTrebleBoost;

    private TextView mTxtMainStereoSoundSub;
    private TextView mTxtMainStereoSound;
    private TextView mTxtMainGraphicEq;
    private TextView mTxtMainGraphicEqSub;
    private ImageView mImgMainGraphicEq;
    private ImageView mImgMainStereoSound;

    private boolean mRunningForTheFirstTime;
    private ContentMode mCurrentContentMode;
    private AudioRoute mActiveRoute;

    private boolean mServiceUnresponsive = false;
    private ArrayList<ContentMode> mContentModeList;

    private boolean mResetInProgress;
    private boolean setDefaultContentMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        boolean welcomeActivityStarted = startWelcomeActivityIfNeeded();
        if (welcomeActivityStarted) {
            finish();
            return;
        }

        //Initialise DTS
        //TODO Add a splash screen to wait till init is done.
        AsyncTask initTask = new DtsInitializeTask(this);
        initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setContentView(R.layout.activity_main2);

        // Status bar:
        setStatusBarColor();

        // Tool bar:
        Toolbar tbMain = (Toolbar) findViewById(R.id.tb_main);
        assert tbMain != null;
        setSupportActionBar(tbMain);

        // Action bar:
        ActionBar abMain = getSupportActionBar();
        assert abMain != null;
        abMain.setTitle(null);

        tbMain.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));

        rlMainDtsAudioProcessingEnabled = (RelativeLayout) tbMain.findViewById(R.id.rl_main_dts_audio_processing_enabled);
        mImgMainDtsAudioProcessingPower = (ImageView) findViewById(R.id.img_main_dts_audio_processing_power);

        // Power-off:
        mVwMainDtsAudioProcessingOffOverlay = findViewById(R.id.vw_main_dts_audio_processing_off_overlay);
        mImgMainDtsArrow = findViewById(R.id.img_main_dts_arrow);
        mTxtMainDtsAudioProcessingOffMessage = findViewById(R.id.txt_main_suggest_dts_audio_processing);

        // Interactive views:
        mRlMainTopLevel = (RelativeLayout) findViewById(R.id.rl_main_top_level);

        // --- Content Modes:
        mLlMainSoundModes = (LinearLayout) findViewById(R.id.ll_main_sound_modes);

        // --- Audio Modes:
        mMainAudioSettings = (LinearLayout) findViewById(R.id.rl_main_audio_settings);

        mImgMainMusicMode = (ImageView) findViewById(R.id.img_main_music_mode);
        mTxtMainMusicMode = (TextView) findViewById(R.id.txt_main_music_mode);

        mImgMainMoviesMode = (ImageView) findViewById(R.id.img_main_movies_mode);
        mTxtMainMoviesMode = (TextView) findViewById(R.id.txt_main_movies_mode);

        mImgMainGamesMode = (ImageView) findViewById(R.id.img_main_games_mode);
        mTxtMainGamesMode = (TextView) findViewById(R.id.txt_main_games_mode);

        mRlMainBassBoost = (RelativeLayout) findViewById(R.id.rl_main_bass_boost);
        mRlMainVocalBoost = (RelativeLayout) findViewById(R.id.rl_main_vocal_boost);
        mRlMainTrebleBoost = (RelativeLayout) findViewById(R.id.rl_main_treble_boost);
        mLlMainStereoSound = (LinearLayout) findViewById(R.id.ll_main_stereo_sound);
        mLlMainGraphicEq = (LinearLayout) findViewById(R.id.ll_main_graphic_eq);

        mImgMainBassBoost = (ImageView) findViewById(R.id.img_main_bass_boost);
        mImgMainVocalBoost = (ImageView) findViewById(R.id.img_main_vocal_boost);
        mImgMainTrebleBoost = (ImageView) findViewById(R.id.img_main_treble_boost);

        mTxtMainBassBoost = (TextView) findViewById(R.id.txt_main_bass_boost);
        mTxtMainVocalBoost = (TextView) findViewById(R.id.txt_main_vocal_boost);
        mTxtMainTrebleBoost = (TextView) findViewById(R.id.txt_main_treble_boost);

        mImgMainStereoSound = (ImageView) findViewById(R.id.img_main_stereo_sound);
        mTxtMainStereoSound = (TextView) findViewById(R.id.txt_main_stereo_sound);
        mTxtMainStereoSoundSub = (TextView) findViewById(R.id.txt_main_stereo_sound_sub);

        mImgMainGraphicEq = (ImageView) findViewById(R.id.img_main_graphic_eq);
        mTxtMainGraphicEq = (TextView) findViewById(R.id.txt_main_graphic_eq);
        mTxtMainGraphicEqSub = (TextView) findViewById(R.id.txt_main_graphic_eq_sub);

        mDtsAudioProcessingEnabled = DtsManager.getInstance().getDtsEnabled().getData();
    } // end of onCreate()

    private void checkFeatureAvailability() {

        if (!FeatureManager.hasBassBoost()) {
            mRlMainBassBoost.setVisibility(View.GONE);
        }
        if (!FeatureManager.hasTrebleBoost()) {
            mRlMainTrebleBoost.setVisibility(View.GONE);
        }
        if (!FeatureManager.hasDialogEnhancement()) {
            mRlMainVocalBoost.setVisibility(View.GONE);
        }

        refreshGeqUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        // Register global broadcast receiver for audio route change events
        IntentFilter sdkChangeFilter = new IntentFilter();
        sdkChangeFilter.addAction(DtsIntents.INTENT_AUDIO_ROUTE_CHANGED);
        // And register to listen to enable state changes. In case a third party app changes the state using the
        // broadcast API.
        sdkChangeFilter.addAction(DtsIntents.INTENT_STATUS_CHANGED_DTS_STATE);
        DtsManager.getInstance().setBroadcastStateChangeEnabled(this, true); // Make sure we enable the broadcasting of enable state, by the SDK.
        registerReceiver(this.mSDKBroadcastReceiver, sdkChangeFilter);

        refreshUiWithDtsAudioProcessingEnabled(mDtsAudioProcessingEnabled);
        //Check if DTS is to be enabled
        if (FeatureManager.hasDts()) {

            rlMainDtsAudioProcessingEnabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDtsAudioProcessingEnabled = !mDtsAudioProcessingEnabled;

                    DtsResult result = DtsManager.getInstance().setDtsEnabled(MainActivity.this, mDtsAudioProcessingEnabled);
                    if (!result.isResultOk()) {
                        handleError("rlMainDtsAudioProcessingEnabled.onClick(): setDtsEnabled()", result);
                        Toast.makeText(MainActivity.this, "Setting DTS Audio Processing enabled has failed: " + result.getResultCode() + " | " + result.getResultMessage(), Toast.LENGTH_SHORT).show();
                    }

                    refreshUiWithDtsAudioProcessingEnabled(mDtsAudioProcessingEnabled);
                }
            });
        } else {
            rlMainDtsAudioProcessingEnabled.setVisibility(View.GONE);
        }

        mContentModeList = new ArrayList<ContentMode>();

        // Set UI feature availability as per the feature availability
        checkFeatureAvailability();

        //Retrieve available content modes from SDK
        getAllContentModes();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();

        mActiveRoute = DtsManager.getInstance().getAudioRoute();

        mRunningForTheFirstTime = SharedPrefManager.getBoolean(this, WelcomeActivity.FIRST_RUN_TAG, true);
        if (mRunningForTheFirstTime) {
            SharedPrefManager.putBoolean(this, WelcomeActivity.FIRST_RUN_TAG, false);
        }

        DtsManager.getInstance().getContentMode(this, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {

                if (dtsResult.isResultOk()) {

                    ContentMode contentMode = (ContentMode) dtsResult.getData();
                    if (contentMode != null) {
                        mCurrentContentMode = contentMode;
                        setContentMode(mCurrentContentMode);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(this.mSDKBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.item_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.item_about_dts_sound:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_reset:
                Log.d(TAG, "Resetting to OEM default!");

                if (mDtsAudioProcessingEnabled) {
                    showResetDialog();
                } else {
                    String message = getResources().getString(R.string.msg_dap_disabled);
                    Log.d(TAG, message);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void showResetDialog() {

        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.DialogTheme);

            // Set title and message
            AlertDialog.Builder builder = dialogBuilder
                    .setTitle(R.string.reset_defaults_title)
                    .setMessage(R.string.reset_default_message)
                    .setCancelable(false)
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            resetAllPersonalSettings();
                        }
                    });

            // Create dialog and show
            dialogBuilder.create().show();
        } catch (Exception e) {
            // Showing this dialog may fail (throws Exception) if this function is called after the
            // activity has already been destroyed.
            // See: http://stackoverflow.com/questions/5934050/check-whether-activity-is-active/8963867#8963867
            // http://stackoverflow.com/questions/3256152/how-to-check-whether-an-activity-is-running-or-not?lq=1
            // As such, to prevent the app from crashing, try displaying the dialog. If that fails,
            // then don't display anything
            Log.d(TAG, "Attempted to display dialog, but activity may be destroyed already.");
        }
    }

    private void resetAllPersonalSettings() {

        Log.d(TAG, "User confirmed reset user");
        if (!mResetInProgress) {
            new OemResetTask().execute();
        } else {
            Log.d(TAG, "OEM reset is already in progress. Suppressing");
        }

    }

    private class OemResetTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mResetInProgress = true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            resetToOemDefault();
            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            mResetInProgress = false;
            Snackbar.make(mRlMainTopLevel, getString(R.string.reset_complete_preferences), Snackbar.LENGTH_LONG).show();
        }
    }

    public void resetContentModes() {
        if (mContentModeList == null || mContentModeList.isEmpty()) {
            return;
        }

        for (ContentMode cm : mContentModeList) {
            if (cm.getType() != null) {
                if (cm.getType() == ContentModeType.ATTACHED_2 || cm.getType() == ContentModeType.ATTACHED_3 || cm.getType() == ContentModeType.ATTACHED_4) {
                    if (cm.getType() == ContentModeType.ATTACHED_2) {
                        mCurrentContentMode = cm;
                    }
                    resetContentMode(cm);
                }
            }
        }
        if (!setDefaultContentMode) {
            resetContentMode(mCurrentContentMode);
            setDefaultContentMode = true;
        }
    }

    private void setGeqDefaultValues() {
        List<Integer> geqValues = new ArrayList<>(5);

        // Update last-set gain array
        for (int index = 0; index < 5; ++index)
            geqValues.add(0);

        // Use API to set all GEQ
        DtsResult result = DtsManager.getInstance().setAllGEQ5Gains(geqValues);

        if (!result.isResultOk()) {
            Log.e(TAG, "Resetting GEQ failed with: " + result.getResultCode() + " | " + result.getResultMessage());
        } else {
            Log.d(TAG, "Successfully reset GEQ");
        }
    }

    private void resetToOemDefault() {
        resetContentModes();
        resetAudioLevelSettings();

        //Forcefully set GEQ to be enabled before resetting GEQ to defaults
        if ((DtsFeatureChecker.getInstance().hasFeature(DtsFeature.GEQ) || DtsFeatureChecker.getInstance().hasFeature(DtsFeature.GEQ_5)) && !DtsManager.getInstance().getGEQEnabled().getData()) {
            DtsResult geqEnableResult = DtsManager.getInstance().setGEQEnabled(true);
            if (!geqEnableResult.isResultOk()) {
                Log.e(TAG, "Enabling GEQ failed with: " + geqEnableResult.getResultCode() + " | " + geqEnableResult.getResultMessage());
            }
        }

        setGeqDefaultValues();

        Log.d(TAG, "Reset to OEM default tunings finished");
    }

    private void resetAudioLevelSettings() {
        // Reset audio level settings
        DtsResult result = DtsManager.getInstance().resetAudioLevelSettings();
        if (!result.isResultOk()) {
            Log.e(TAG, "Reset audio level settings failed: " + result.getResultCode() + " | " + result.getResultMessage());
        } else {
            Log.d(TAG, "Successfully reset audio level settings ");
        }
    }

    public void onContentModeRelativeLayoutClicked(View view) {

        ContentModeType newContentModeType;
        int viewId = view.getId();
        switch (viewId) {
            case R.id.rl_main_music_mode:
                newContentModeType = ContentModeType.ATTACHED_2;
                break;
            case R.id.rl_main_movies_mode:
                newContentModeType = ContentModeType.ATTACHED_3;
                break;
            case R.id.rl_main_games_mode:
                newContentModeType = ContentModeType.ATTACHED_4;
                break;
            default:
                newContentModeType = ContentModeType.UNKNOWN;
        }

        if (newContentModeType != ContentModeType.UNKNOWN) {
            // Set new Content Mode:
            if (mCurrentContentMode != null)
                animateContentModeChange(mCurrentContentMode.getType(), newContentModeType);

            for (ContentMode cm : mContentModeList) {
                if (cm.getType().equals(newContentModeType)) {
                    mCurrentContentMode = cm;
                    setContentMode(cm);
                    break;
                }
            }
        }
    }

    public void onAdvancedAudioLayoutClicked(View view) {
        if (mDtsAudioProcessingEnabled)
            startActivity(new Intent(this, AdvancedAudioActivity.class));
        else {
            Log.d(TAG, "DTS Sound is disabled");
        }
    }

    public void onAudioSettingsClicked(View view) {

        int viewId = view.getId();
        switch (viewId) {
            case R.id.rl_main_bass_boost:
                if (!FeatureManager.hasBassBoost()) {
                    // disable
                    setEnabledAll(mRlMainBassBoost, false);
                    return;

                }
                enableBassBoost(!getBassBoostEnabled(), mCurrentContentMode);

                break;
            case R.id.rl_main_vocal_boost:
                if (!FeatureManager.hasDialogEnhancement()) {
                    // disable
                    setEnabledAll(mRlMainVocalBoost, false);
                    return;
                }
                enableDialogEnhancement(!getDialogEnhancementEnabled(), mCurrentContentMode);

                break;
            case R.id.rl_main_treble_boost:
                if (!FeatureManager.hasTrebleBoost()) {
                    // disable
                    setEnabledAll(mRlMainTrebleBoost, false);
                    return;
                }
                enableTrebleBoost(!getTrebleBoostEnabled());

                break;

            case R.id.ll_main_graphic_eq:

                if (mDtsAudioProcessingEnabled) {
                    Intent intent = new Intent(this, AdvancedAudioActivity.class);
                    intent.putExtra(AdvancedAudioActivity.ADVANCED_AUDIO_KEY, AdvancedAudioActivity.GEQ_FLAG);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "DTS Sound is disabled");
                }

                break;

            case R.id.ll_main_stereo_sound:

                if (mDtsAudioProcessingEnabled) {
                    Intent intent = new Intent(this, AdvancedAudioActivity.class);
                    intent.putExtra(AdvancedAudioActivity.ADVANCED_AUDIO_KEY, AdvancedAudioActivity.STEREO_FLAG);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "DTS Sound is disabled");
                }

                break;

        }

    }

    /**
     * Convenient function to get the Bass boost enabled state.
     *
     * @return Bass boost is enabled/disabled
     */
    private boolean getBassBoostEnabled() {
        if (!FeatureManager.hasBassBoost()) {
            Log.w(TAG, "Bass Boost feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Bass Boost enabled state
        DtsResult<Boolean> resultBassBoostMode = DtsManager.getInstance().getBassBoostEnabled(mActiveRoute, mCurrentContentMode);
        if (!resultBassBoostMode.isResultOk()) {
            // getBassBoostEnabled() call returned error
            return false;
        }
        Log.d(TAG, "Bass Boost enabled = " + resultBassBoostMode.getData());
        return resultBassBoostMode.getData();
    }

    /**
     * Convenient function to get the Treble boost enabled state.
     *
     * @return Treble boost is enabled/disabled
     */
    private boolean getTrebleBoostEnabled() {
        if (!FeatureManager.hasTrebleBoost()) {
            Log.w(TAG, "Treble Boost feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Treble Boost enabled state
        DtsResult<Boolean> resultTrebleBoostMode = DtsManager.getInstance().getTrebleBoostEnabled(mActiveRoute, mCurrentContentMode);
        if (!resultTrebleBoostMode.isResultOk()) {
            // getTrebleBoostEnabled() call returned error
            return false;
        }
        return resultTrebleBoostMode.getData();
    }

    /**
     * Convenient function to get the Dialog enhancement enabled state.
     *
     * @return Dialog enhancement is enabled/disabled
     */
    private boolean getDialogEnhancementEnabled() {
        if (!FeatureManager.hasDialogEnhancement()) {
            Log.w(TAG, "Dialog enhancement feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Dialog Enhancement enabled state
        DtsResult<Boolean> resultDialogEnhancementMode = DtsManager.getInstance().getDialogEnhancementEnabled(mActiveRoute, mCurrentContentMode);
        if (!resultDialogEnhancementMode.isResultOk()) {
            // getDialogEnhancementEnabled() call returned error
            return false;
        }
        return resultDialogEnhancementMode.getData();
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
        // Refresh DTS Sound Version
        // =========================
        // Call license check API to ensure that the license is valid and installed,
        // and that DTS service is alive and responsive
        DtsResult<LicenseResultCode> resultLicense = DtsSystemStatusChecker.getInstance().getDtsLicenseExists();

        if (!resultLicense.isResultOk()) {
            // License check API failed

            // Check if the error was due to service not responding
            if (resultLicense.getResultCode() == DtsResult.ResultCode.SERVICE_NOT_PRESENT) {
                // If so, show the warning about unresponsive service
                Log.e(TAG, "Calling license check API failed.");
                Log.e(TAG, "DTS returned error code: " + resultLicense.getResultCode() + ". " + resultLicense.getResultMessage());

                // Also, abort here instead of unnecessarily waiting for a long time for an
                // unresponsive service to respond
                mServiceUnresponsive = true;
                return;
            } else {
                Log.e(TAG, "Calling license check API failed with result: " + resultLicense.getResultCode());
                return;
            }
        } else if (resultLicense.getData() != LicenseResultCode.OK) {
            // Check if license check failed
            // If so, show the warning about license check fail
            Log.e(TAG, "License check failed.");
            Log.e(TAG, "DTS returned error code: " + resultLicense.getResultCode() + ". " + resultLicense.getResultMessage());
        } else {
            Log.d(TAG, "License check passed. Valid license found on device");
            // Get DTS versions and print them to LogCat
            DtsResult<DtsVersions> resultVersions = DtsManager.getInstance().getAllVersions();
            if (!resultVersions.isResultOk()) {
                Log.e(TAG, "Getting DTS Versions failed with " + resultVersions.getResultCode() + " | " + resultVersions.getResultMessage());
            } else {
                Log.d(TAG, "DTS Versions: ");
                Log.d(TAG, resultVersions.getData().toString());
                String DtsAudioProcessingVersion = resultVersions.getData().getUiSdkVersion();
                Log.v(TAG, "refreshUi(): DTS Sound Version(" + DtsAudioProcessingVersion + ")");
            }

        }
        mServiceUnresponsive = false;

        // Refresh DTS Sound Enabled - related
        // =========================
        DtsResult<Boolean> resultBoolean = DtsManager.getInstance().getDtsEnabled();
        if (!resultBoolean.isResultOk()) {
            handleError("refreshUi: getDtsEnabled()", resultBoolean);
        }
        boolean isDtsAudioProcessingEnabled = resultBoolean.getData();

        Log.v(TAG, "refreshUi(): isDtsAudioProcessingEnabled(" + isDtsAudioProcessingEnabled + ")");

        //Refresh Content mode
        refreshContentMode();

        refreshUiWithDtsAudioProcessingEnabled(isDtsAudioProcessingEnabled);

        refreshAudioLevelSettings();

    }


    // Refreshes views that are related to DTS Sound Enabled:
    private void refreshUiWithDtsAudioProcessingEnabled(boolean enabled) {
        mDtsAudioProcessingEnabled = enabled;

        if (FeatureManager.hasDts()) {
            // Set tint for power button
            setTint(mImgMainDtsAudioProcessingPower, enabled);
        }

        // Whether to enable controls - at the group level:
        setEnabledAll(mLlMainSoundModes, enabled);

        setEnabledAll(mMainAudioSettings, enabled);

        //whether to enable Stereo preference and GEQ

        // Power off
        int powerOffVisibility = enabled ? View.INVISIBLE : View.VISIBLE;
        if (mVwMainDtsAudioProcessingOffOverlay != null)
            mVwMainDtsAudioProcessingOffOverlay.setVisibility(powerOffVisibility);
        if (mImgMainDtsArrow != null)
            mImgMainDtsArrow.setVisibility(powerOffVisibility);
        if (mTxtMainDtsAudioProcessingOffMessage != null)
            mTxtMainDtsAudioProcessingOffMessage.setVisibility(powerOffVisibility);

        refreshGeqUI();
    }

    private void refreshUiWithContentMode(ContentModeType soundMode, boolean changeBackground) {
        // Change background
        if (changeBackground && soundMode != null) {
            switch (soundMode) {
                case ATTACHED_2:
                    mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.dts_bg_music));
                    break;
                case ATTACHED_3:
                    mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.dts_bg_movies));
                    break;
                case ATTACHED_4:
                    mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.dts_bg_games));
                    break;

            }
        }

        // Change the control layout tint
        setTint(mImgMainMusicMode, soundMode == ContentModeType.ATTACHED_2);
        setTint(mTxtMainMusicMode, soundMode == ContentModeType.ATTACHED_2);
        setTint(mImgMainMoviesMode, soundMode == ContentModeType.ATTACHED_3);
        setTint(mTxtMainMoviesMode, soundMode == ContentModeType.ATTACHED_3);
        setTint(mImgMainGamesMode, soundMode == ContentModeType.ATTACHED_4);
        setTint(mTxtMainGamesMode, soundMode == ContentModeType.ATTACHED_4);

        if (soundMode == ContentModeType.UNKNOWN) {
            Log.e(TAG, "refreshUiWithContentMode: Content mode is unknown");
        }
    }

    private void animateContentModeChange(ContentModeType oldContentMode, ContentModeType newContentMode) {
        // Change background transition drawable:
        if (oldContentMode == ContentModeType.ATTACHED_2) {
            if (newContentMode == ContentModeType.ATTACHED_2) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.music_to_music));
            } else if (newContentMode == ContentModeType.ATTACHED_3) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.music_to_movies));
            } else if (newContentMode == ContentModeType.ATTACHED_4) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.music_to_games));
            }
        } else if (oldContentMode == ContentModeType.ATTACHED_3) {
            if (newContentMode == ContentModeType.ATTACHED_2) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.movies_to_music));
            } else if (newContentMode == ContentModeType.ATTACHED_3) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.movies_to_movies));
            } else if (newContentMode == ContentModeType.ATTACHED_4) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.movies_to_games));
            }
        } else if (oldContentMode == ContentModeType.ATTACHED_4) {
            if (newContentMode == ContentModeType.ATTACHED_2) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.games_to_music));
            } else if (newContentMode == ContentModeType.ATTACHED_3) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.games_to_movies));
            } else if (newContentMode == ContentModeType.ATTACHED_4) {
                mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.games_to_games));
            }
        } else if (newContentMode == ContentModeType.ATTACHED_2) {
            mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.music_to_music));
        } else if (newContentMode == ContentModeType.ATTACHED_3) {
            mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.movies_to_movies));
        } else if (newContentMode == ContentModeType.ATTACHED_4) {
            mRlMainTopLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.games_to_games));
        }

        if (mRlMainTopLevel.getBackground() instanceof TransitionDrawable) {

            // Perform background drawable transition:
            TransitionDrawable transition = (TransitionDrawable) mRlMainTopLevel.getBackground();
            transition.startTransition(TRANSITION_DURATION_MILLIS);
        }

        refreshUiWithContentMode(newContentMode, false);
    }

    // Method to start welcome activity if needed:
    private boolean startWelcomeActivityIfNeeded() {
        // Check if welcome activity has already been displayed:
        boolean welcomeScreenDisplayed = SharedPrefManager.getBoolean(this, WelcomeActivity.WELCOME_SCREEN_DISPLAYED_TAG, false);

        if (!welcomeScreenDisplayed) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    // Method to enable/disable all child views of a view:
    private static void setEnabledAll(View v, boolean enabled) {
        v.setEnabled(enabled);
        v.setFocusable(enabled);

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setEnabledAll(vg.getChildAt(i), enabled);
            }
        }
    }

    private AudioRoute getActiveAudioRoute() {
        return DtsManager.getInstance().getAudioRoute();
    }

    private void handleError(String action, DtsResult result) {
        Log.e(TAG, action + " failed: " + result.getResultCode() + " | " + result.getResultMessage());
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will remove any tint.
     * If disabled, will apply gray tint.
     *
     * @param imageView to apply the color tint.
     * @param enabled   to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(ImageView imageView, boolean enabled) {
        int color = ContextCompat.getColor(MainActivity.this, R.color.dtsGray8);

        if (enabled) {
            imageView.setColorFilter(null);
        } else {
            imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will apply white tint.
     * If disabled, will apply gray tint.
     *
     * @param textView to apply the color tint.
     * @param enabled  to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(TextView textView, boolean enabled) {
        int color;

        if (enabled) {
            color = ContextCompat.getColor(MainActivity.this, R.color.dtsWhite);
        } else {
            color = ContextCompat.getColor(MainActivity.this, R.color.dtsGray8);
        }
        textView.setTextColor(color);
    }

    /**
     * Convenient function to set background of layout to indicate the UI is enabled or disabled.
     * If enabled, will apply white border.
     * If disabled, will apply gray border.
     *
     * @param view    to apply the background.
     * @param enabled to indicate if the UI should be enabled or disabled.
     */
    private void setLayoutBackground(View view, boolean enabled) {
        int border;
        if (enabled) {
            border = R.drawable.dts_bg_fill33black_borderwhite;
        } else {
            border = R.drawable.dts_bg_fill33black_bordergray8;
        }

        view.setBackground(getDrawable(border));

    }

    /**
     * Broadcast Receiver to listen to broadcasts sent from the DTS SDK
     */
    private BroadcastReceiver mSDKBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.e(TAG, "Received null intent. Aborting");
                return;
            }

            Log.d(TAG, "Received intent. Action: " + intent.getAction());


            switch (intent.getAction()) {
                case DtsIntents.INTENT_AUDIO_ROUTE_CHANGED:
                    mActiveRoute = (AudioRoute) intent.getSerializableExtra(DtsIntents.EXTRA_AUDIO_ROUTE);
                    Log.d(TAG, "Audio route detected: " + mActiveRoute);
                    refreshUi();
                    break;
                case DtsIntents.INTENT_STATUS_CHANGED_DTS_STATE:
                    boolean enabled = intent.getBooleanExtra(DtsIntents.EXTRA_DTS_STATE, false);
                    Log.d(TAG, "Received EXTRA_DTS_STATE: " + enabled);
                    if (enabled) {
                        if (FeatureManager.hasDts()) {
                            // enable dts switch
                            refreshUiWithDtsAudioProcessingEnabled(true);
                        }
                    } else {
                        if (FeatureManager.hasDts()) {
                            // disable dts switch
                            refreshUiWithDtsAudioProcessingEnabled(false);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * Function to set the content mode for headphones and internal speaker
     *
     * @param newContentMode content mode to be set
     */
    private void setContentMode(final ContentMode newContentMode) {

        if (getActiveAudioRoute() == AudioRoute.UNKNOWN || !mDtsAudioProcessingEnabled) {
            Log.e(TAG, "setContentMode() does not support UNKNOWN audio route. Aborting");
            return;
        }
        DtsManager.getInstance().setContentMode(this, newContentMode, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {
                if (!dtsResult.isResultOk()) {
                    Log.e(TAG, "Selecting content mode returned error: " + dtsResult.getResultCode() + " | " + dtsResult.getResultMessage());
                    return;
                }
                Log.d(TAG, "New content mode " + newContentMode.getName() + " successfully selected: ");
                refreshAudioLevelSettings();
            }
        });
    }

    private Handler defaultContentModeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUi();
        }
    };

    private void resetContentMode(final ContentMode newContentMode) {

        if (getActiveAudioRoute() == AudioRoute.UNKNOWN || !mDtsAudioProcessingEnabled) {
            Log.e(TAG, "resetContentMode() does not support UNKNOWN audio route. Aborting");
            return;
        }
        DtsManager.getInstance().setContentMode(this, newContentMode, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {
                if (!dtsResult.isResultOk()) {
                    Log.e(TAG, "Selecting content mode returned error: " + dtsResult.getResultCode() + " | " + dtsResult.getResultMessage());
                    return;
                }
                Log.d(TAG, "resetContentMode " + newContentMode.getName() + " successfully selected: ");
                enableBassBoost(false, newContentMode);
                enableDialogEnhancement(false, newContentMode);

                if (setDefaultContentMode) {
                    defaultContentModeHandler.sendMessage(Message.obtain());
                    setDefaultContentMode = false;
                }
            }
        });
    }

    /**
     * Function to get the content mode for headphones and internal speaker
     *
     * @return newContentMode content mode to be set
     */
    private void refreshContentMode() {

        if (getActiveAudioRoute() == AudioRoute.UNKNOWN) {
            Log.e(TAG, "refreshContentMode() does not support UNKNOWN audio route. Aborting");
            return;
        }

        DtsManager.getInstance().getContentMode(this, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {
                if (!dtsResult.isResultOk()) {
                    Log.e(TAG, "Getting content mode returned error: " + dtsResult.getResultCode() + " | " + dtsResult.getResultMessage());
                    return;
                }
                ContentMode contentMode = (ContentMode) dtsResult.getData();
                if (contentMode != null) {
                    mCurrentContentMode = contentMode;
                    Log.v(TAG, "currentContentMode(" + mCurrentContentMode.getType() + ")");
                    refreshUiWithContentMode(mCurrentContentMode.getType(), true);
                    refreshAudioLevelSettings();
                } else {
                    // If content mode is null, then set default content mode
                    for (ContentMode cm : mContentModeList) {
                        // Check if only one content mode is available then set it as default
                        if (mContentModeList.size() == 1) {
                            setContentMode(cm);
                            mCurrentContentMode = cm;
                            animateContentModeChange(null, cm.getType());
                            Log.v(TAG, "defaultContentMode(" + mCurrentContentMode.getType() + ")");
                            refreshAudioLevelSettings();
                            break;
                        } else if (mContentModeList.size() > 1) { // If more than one content modes are available then then set the default content mode
                            // priority in order Attached2, Attached3, Attached4 as default

                            ContentModeType defaultType = null;
                            switch (cm.getType()) {
                                case ATTACHED_2: // Music
                                    defaultType = ContentModeType.ATTACHED_2;
                                    break;
                                case ATTACHED_3: // Movies
                                    defaultType = ContentModeType.ATTACHED_3;
                                    break;
                                case ATTACHED_4: // Games
                                    defaultType = ContentModeType.ATTACHED_4;
                                    break;

                            }
                            setContentMode(cm);
                            mCurrentContentMode = cm;
                            animateContentModeChange(null, defaultType);
                            Log.v(TAG, "defaultContentMode(" + mCurrentContentMode.getType() + ")");
                            refreshAudioLevelSettings();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Function to get all the content modes for headphones and internal speaker
     *
     * @return List<ContentMode> content mode to be set
     */
    private void getAllContentModes() {

        DtsManager.getInstance().getAllContentModes(this, new QueryCallback<ContentMode>() {
            @Override
            public void onQueryComplete(DtsResult dtsResult, List<ContentMode> list) {
                if (!dtsResult.isResultOk()) {
                    Log.e(TAG, "Querying for content mode returned error: " + dtsResult.getResultCode() + " | " + dtsResult.getResultMessage());
                    return;
                }
                if (list != null && list.size() > 0) {
                    // Sort content modes list in ascending order based on content mode type in order - Attached0, Attached2, Attached3, Attached4
                    Collections.sort(list, new ContentModeComparator());

                    // Add content modes
                    addContentModes(list);
                    refreshContentMode();
                }
            }
        });
    }

    public void addContentModes(List<ContentMode> contentModeList) {
        if (contentModeList == null || contentModeList.isEmpty()) {
            return;
        }

        for (ContentMode cm : contentModeList) {
            mContentModeList.add(cm);
        }
    }

    /**
     * Async task for initializing DTS and handling the results
     */
    private class DtsInitializeTask extends AsyncTask<Object, Void, DtsResult> {
        private Context mContext;

        public DtsInitializeTask(Context context) {
            mContext = context;
        }

        @Override
        protected DtsResult doInBackground(Object... params) {

            Log.d(TAG, "Initializing DTS");

            long startTime = System.currentTimeMillis();

            /*
             * Initialize DTS
             * Note that this function must be called before any of the DTS system APIs can be used
             * It is safe to call this multiple times, but has no effect for redundant calls
             *
             * In this example, the initialize is called to not just initialize DTS,
             * but to check if the service is installed properly and responsive. initialize() function
             * will return an error code (via DtsResult object) if it detects that the service is
             * unresponsive.
             */
            DtsResult initializeResult = DtsManager.getInstance().initialize(mContext);

            long endTime = System.currentTimeMillis();

            Log.d(TAG, "DTS initialize completed in " + (endTime - startTime) + "ms");

            return initializeResult;
        }

        @Override
        protected void onPostExecute(DtsResult dtsResult) {
            Log.d(TAG, "DTS Initialize finished");
            Log.d(TAG, "Initialize result: " + dtsResult.getResultCode() + " | " + dtsResult.getResultMessage());

            if (dtsResult.isResultOk()) {
                //TODO

            } else {
                //TODO Handle Service errors
                // Initialize was not successful; service is bad
                // Show error message
//                mLoadingText.setVisibility(View.GONE);
//                mErrorLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Convenient function to enable/disable Bass Boost
     *
     * @param enable true if Bass Boost mode is to be enabled
     *               false if Bass Boost is to be disabled
     */
    private void enableBassBoost(boolean enable, ContentMode contentMode) {
        if (!FeatureManager.hasBassBoost()) {
            Log.w(TAG, "Bass Boost feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setBassBoostEnabled(this, mActiveRoute, contentMode, enable);

        if (!result.isResultOk()) {
            Log.e(TAG, result.getResultMessage());
            return;
        }

        setTint(mImgMainBassBoost, enable);
        setTint(mTxtMainBassBoost, enable);
        setLayoutBackground(mRlMainBassBoost, enable);


    }

    /**
     * Convenient function to enable/disable Treble Boost
     *
     * @param enable true if Treble Boost mode is to be enabled
     *               false if Treble Boost is to be disabled
     */
    private void enableTrebleBoost(boolean enable) {
        if (!FeatureManager.hasTrebleBoost()) {
            Log.w(TAG, "Treble Boost feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setTrebleBoostEnabled(this, mActiveRoute, mCurrentContentMode, enable);

        if (!result.isResultOk()) {
            Log.e(TAG, result.getResultMessage());
            return;
        }

        setTint(mImgMainTrebleBoost, enable);
        setTint(mTxtMainTrebleBoost, enable);
        setLayoutBackground(mRlMainTrebleBoost, enable);
    }

    /**
     * Convenient function to enable/disable Vocal Boost
     *
     * @param enable true if Vocal Boost mode is to be enabled
     *               false if Vocal Boost is to be disabled
     */
    private void enableDialogEnhancement(boolean enable, ContentMode contentMode) {
        if (!FeatureManager.hasDialogEnhancement()) {
            Log.w(TAG, "Dialog Enhancement feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setDialogEnhancementEnabled(this, mActiveRoute, contentMode, enable);

        if (!result.isResultOk()) {
            Log.e(TAG, result.getResultMessage());
            return;
        }

        setTint(mImgMainVocalBoost, enable);
        setTint(mTxtMainVocalBoost, enable);
        setLayoutBackground(mRlMainVocalBoost, enable);
    }

    private void refreshAudioLevelSettings() {

        // Refresh bass boost ui
        boolean bassEnabled = getBassBoostEnabled();
        setTint(mImgMainBassBoost, bassEnabled);
        setTint(mTxtMainBassBoost, bassEnabled);
        setLayoutBackground(mRlMainBassBoost, bassEnabled);

        // Refresh dialog enhancement ui
        boolean dialogEnabled = getDialogEnhancementEnabled();
        setTint(mImgMainVocalBoost, dialogEnabled);
        setTint(mTxtMainVocalBoost, dialogEnabled);
        setLayoutBackground(mRlMainVocalBoost, dialogEnabled);

        // Refresh treble boost ui
        boolean trebleEnabled = getTrebleBoostEnabled();
        setTint(mImgMainTrebleBoost, trebleEnabled);
        setTint(mTxtMainTrebleBoost, trebleEnabled);
        setLayoutBackground(mRlMainTrebleBoost, trebleEnabled);

        // Refresh stereo preference ui
        refreshStereoPreferenceUI(getStereoPreference());

        // Refresh graphic equalizer ui
        refreshGeqUI();

    }

    /**
     * Convenient function to get the system's current stereo mode.
     *
     * @return system's current stereo mode
     */
    private StereoPreference getStereoPreference() {
        if (!FeatureManager.hasStereoPreference()) {

            mLlMainStereoSound.setClickable(false);
            mLlMainStereoSound.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLlMainGraphicEq.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            return StereoPreference.UNKNOWN;
        }

        // [API-CALL] Call API to get current stereo mode
        DtsResult<StereoPreference> resultStereoMode = DtsManager.getInstance().getStereoPreference(mActiveRoute, mCurrentContentMode);
        if (!resultStereoMode.isResultOk()) {
            // getStereoMode() call returned error
            Log.e(TAG, "getStereoMode() call returned error - " + resultStereoMode.getResultMessage());
            return StereoPreference.UNKNOWN;
        }
        return resultStereoMode.getData();
    }

    /**
     * Convenient function to refresh the UI based on current stereo mode.
     */
    private void refreshStereoPreferenceUI(StereoPreference mode) {

        boolean stereoPreferenceState = true;
        switch (mode) {
            case WIDE:
                mTxtMainStereoSoundSub.setText(getText(R.string.preference_wide_title));
                mImgMainStereoSound.setImageResource(R.drawable.dts_stereo_pref_wide);
                mImgMainStereoSound.setPadding(5, 5, 5, 5);
                break;
            case FRONT:
                mTxtMainStereoSoundSub.setText(getText(R.string.preference_infront_title));
                mImgMainStereoSound.setImageResource(R.drawable.dts_stereo_pref_infront);
                mImgMainStereoSound.setPadding(0, 15, 0, 0);
                break;
            case TRADITIONAL:
                mTxtMainStereoSoundSub.setText(getText(R.string.preference_traditional_title));
                mImgMainStereoSound.setImageResource(R.drawable.dts_stereo_pref_traditional);
                mImgMainStereoSound.setPadding(0, 0, 0, 0);
                break;
            case UNKNOWN:
                mTxtMainStereoSoundSub.setText(getText(R.string.preference_unknown_title));
                stereoPreferenceState = false;
                setTint(mTxtMainStereoSoundSub, false);
                break;
        }

        setTint(mImgMainStereoSound, stereoPreferenceState);
        setTint(mTxtMainStereoSound, stereoPreferenceState);
        setLayoutBackground(mLlMainStereoSound, stereoPreferenceState);
    }

    /**
     * Convenient function to get equalizer state.
     * If the API returned error message, this function will show AlertDialog and return false
     *
     * @return true if GEQ is enabled
     * false if GEQ is disabled, or returned error
     */
    private boolean isGEQEnabled() {
        if (!FeatureManager.hasGEQ()) {
            mLlMainGraphicEq.setClickable(false);
            mLlMainGraphicEq.setVisibility(View.GONE);
            return false;
        }

        DtsResult<Boolean> resultBool = DtsManager.getInstance().getGEQEnabled();
        if (!resultBool.isResultOk()) {
            // getGEQEnabled() returned error
            // Log error message and show Alert Dialog
            Log.e(TAG, "Getting GEQ enabled failed.");
            Log.e(TAG, "DTS returned error code: " + resultBool.getResultCode() + ". " + resultBool.getResultMessage());

            return false;
        }

        boolean geqEnabled = resultBool.getData();

        return geqEnabled;
    }

    /**
     * Convenient function to refresh the UI based Geq enabled/disabled.
     */
    private void refreshGeqUI() {

        boolean geqEnabled = isGEQEnabled();
        setTint(mImgMainGraphicEq, geqEnabled);
        setTint(mTxtMainGraphicEq, geqEnabled);
        setTint(mTxtMainGraphicEqSub, geqEnabled);
        setLayoutBackground(mLlMainGraphicEq, geqEnabled);

    }

    /**
     * This class is used to sort the list of ContentMode objects
     */
    class ContentModeComparator implements Comparator<ContentMode> {

        @Override
        public int compare(ContentMode cm1, ContentMode cm2) {
            return Integer.valueOf(cm1.getType().ordinal()).compareTo(Integer.valueOf(cm2.getType().ordinal()));
        }
    }
}

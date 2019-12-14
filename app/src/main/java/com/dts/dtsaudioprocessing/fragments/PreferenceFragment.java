/* DTS,INC.
 * 5220 LAS VIRGENES ROAD
 * CALABASAS, CA 91302  USA
 *
 * ©DTS, INC.  ALL RIGHTS RESERVED.
 *
 * THIS SOFTWARE, ANY COMPONENTS THEREOF, AND ANY RELATED DOCUMENTATION (THE “PRODUCT”)
 * CONTAINS CONFIDENTIAL PROPRIETARY INFORMATION OWNED BY DTS, INC. AND/OR ITS
 * AFFILIATES (“DTS”) INCLUDING BUT NOT LIMITED TO TRADE SECRETS, KNOW-HOW,
 * TECHNICAL, AND BUSINESS INFORMATION.  UNLESS OTHERWISE PROVIDED UNDER THE
 * TERMS OF A FULLY-EXECUTED WRITTEN AGREEMENT BY AND BETWEEN THE RECIPIENT
 * HEREOF AND DTS, ALL USE, DUPLICATION, DISCLOSURE, OR DISTRIBUTION OF THE
 * PRODUCT, IN ANY FORM, IS PROHIBITED AND IS A VIOLATION OF STATE, FEDERAL, AND
 * INTERNATIONAL LAWS. THE PRODUCT CONTAINS CONFIDENTIAL, PROPRIETARY TRADE SECRETS,
 * AND IS PROTECTED BY APPLICABLE COPYRIGHT LAW AND/OR PATENT LAW. BOTH CIVIL AND
 * CRIMINAL PENALTIES APPLY.
 *
 * ALGORITHMS, DATA STRUCTURES AND METHODS CONTAINED IN THE PRODUCT MAY BE
 * PROTECTED BY ONE OR MORE PATENTS OR PATENT APPLICATIONS. UNLESS OTHERWISE
 * PROVIDED UNDER THE TERMS OF A FULLY-EXECUTED WRITTEN AGREEMENT BY AND BETWEEN
 * THE RECIPIENT HEREOF AND DTS, THE FOLLOWING TERMS SHALL APPLY TO ANY USE OF
 * THE PRODUCT:  (I) USE OF THE PRODUCT IS AT THE RECIPIENT'S SOLE RISK; (II) THE
 * PRODUCT IS PROVIDED "AS IS" AND WITHOUT WARRANTY OF ANY KIND AND DTS EXPRESSLY
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE,
 * REGARDLESS OF WHETHER DTS KNOWS OR HAS REASON TO KNOW OF THE USER'S PARTICULAR
 * NEEDS; (III) DTS DOES NOT WARRANT THAT THE PRODUCT MEET USER'S REQUIREMENTS,
 * OR THAT ANY ALLEGED DEFECTS IN THE PRODUCT WILL BE CORRECTED; (IV) DTS DOES
 * NOT WARRANT THAT THE OPERATION OF ANY HARDWARE OR SOFTWARE ASSOCIATED WITH THE
 * PRODUCT WILL BE UNINTERRUPTED OR ERROR-FREE; AND (V) UNDER NO CIRCUMSTANCES,
 * INCLUDING NEGLIGENCE, SHALL DTS OR THE DIRECTORS, OFFICERS, EMPLOYEES, OR
 * AGENTS OF DTS, BE LIABLE TO USER FOR ANY INCIDENTAL, INDIRECT, SPECIAL, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO DAMAGES FOR LOSS OF BUSINESS
 * PROFITS, BUSINESS INTERRUPTION, AND LOSS OF BUSINESS INFORMATION) ARISING OUT
 * OF THE USE, MISUSE, OR INABILITY TO USE THE PRODUCT.
 */
package com.dts.dtsaudioprocessing.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dts.dtssdk.DtsIntents;
import com.dts.dtssdk.DtsManager;
import com.dts.dtssdk.accessory.ContentMode;
import com.dts.dtssdk.accessory.StereoPreference;
import com.dts.dtssdk.callback.OnCompleteCallback;
import com.dts.dtssdk.result.DtsResult;
import com.dts.dtssdk.util.AudioRoute;
import com.dts.dtsaudioprocessing.R;

/**
 * Preference fragment for setting headphone sound preferences
 */
public class PreferenceFragment extends DtsFragment {

    private static final String TAG = PreferenceFragment.class.getSimpleName();

    private FragmentActivity mActivity;

    private RelativeLayout mWideLayout;
    private RelativeLayout mInfrontLayout;
    private RelativeLayout mTraditionalLayout;
    private RadioButton mWideButton;
    private RadioButton mInfrontButton;
    private RadioButton mTraditionalButton;
    private ImageView mImgWide;
    private TextView mTxtWide;
    private TextView mTxtWideTitle;
    private ImageView mImgInfront;
    private TextView mTxtInfront;
    private TextView mTxtInfrontTitle;
    private ImageView mImgTraditional;
    private TextView mTxtTraditional;
    private TextView mTxtTraditionalTitle;

    private AudioRoute mActiveRoute = AudioRoute.UNKNOWN;
    private ContentMode mContentMode;

    public PreferenceFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContentMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView()");

        setHasOptionsMenu(true);

        mActivity = getActivity();
        mActivity.setTitle(getText(R.string.preference_title));
        View view = inflater.inflate(R.layout.fragment_preference, container, false);
        ActionBar abGeq = ((AppCompatActivity)getActivity()).getSupportActionBar();
        abGeq.setDisplayHomeAsUpEnabled(true);
        mWideLayout = (RelativeLayout) view.findViewById(R.id.preference_layout_wide);
        mInfrontLayout = (RelativeLayout) view.findViewById(R.id.preference_layout_infront);
        mTraditionalLayout = (RelativeLayout) view.findViewById(R.id.preference_layout_traditional);
        mWideButton = (RadioButton) view.findViewById(R.id.preference_wide_button);
        mInfrontButton = (RadioButton) view.findViewById(R.id.preference_infront_button);
        mTraditionalButton = (RadioButton) view.findViewById(R.id.preference_traditional_button);
        mImgWide=(ImageView)view.findViewById(R.id.preference_wide_icon);
        mTxtWide = (TextView)view.findViewById(R.id.preference_wide_subtitle);
        mTxtWideTitle = (TextView)view.findViewById(R.id.preference_wide_title);
        mImgInfront=(ImageView)view.findViewById(R.id.preference_infront_icon);
        mTxtInfront = (TextView)view.findViewById(R.id.preference_infront_subtitle);
        mTxtInfrontTitle = (TextView)view.findViewById(R.id.preference_infront_title);
        mImgTraditional=(ImageView)view.findViewById(R.id.preference_traditional_icon);
        mTxtTraditional = (TextView)view.findViewById(R.id.preference_traditional_subtitle);
        mTxtTraditionalTitle = (TextView)view.findViewById(R.id.preference_traditional_title);

        mWideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStereoPreference(StereoPreference.WIDE);
            }
        });

        mInfrontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStereoPreference(StereoPreference.FRONT);
            }
        });

        mTraditionalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStereoPreference(StereoPreference.TRADITIONAL);
            }
        });
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");
        // Register a broadcast receiver to listen to any changes to active route
        IntentFilter preferenceFilter = new IntentFilter();
        preferenceFilter.addAction(DtsIntents.INTENT_AUDIO_ROUTE_CHANGED);
        getActivity().registerReceiver(this.mAudioRouteBroadcastReceiver, preferenceFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");
        mActiveRoute = DtsManager.getInstance().getAudioRoute();

        DtsManager.getInstance().getContentMode(mActivity, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {

                if (dtsResult.isResultOk()) {

                    ContentMode contentMode = (ContentMode) dtsResult.getData();
                    if(contentMode != null){
                        mContentMode = contentMode;
                        refreshStereoPreferenceUI(getStereoPreference());
                    }
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

        // Stop Broadcast receiver when stopping this fragment
        getActivity().unregisterReceiver(this.mAudioRouteBroadcastReceiver);
    }

    /**
     * Convenient function to set the system's stereo mode.
     * Also calls refreshStereoPreferenceUI() function to update the UI.
     *
     * @param stereoPreference stereo mode to update the system
     */
    private void setStereoPreference(StereoPreference stereoPreference) {
        // [API-CALL] Call API to set stereo mode
        DtsResult result = DtsManager.getInstance().setStereoPreference(mActiveRoute, mContentMode, stereoPreference);

        if (!result.isResultOk()) {
            showErrorDialog(result);
        }

        refreshStereoPreferenceUI(stereoPreference);
    }

    /**
     * Convenient function to refresh the UI based on current stereo mode.
     */
    private void refreshStereoPreferenceUI(StereoPreference mode) {
        if (!isAdded()) {
            Log.d(TAG, "Fragment is not added to activity. Aborting");
            return;
        }
        // [API-CALL] Read current system state for DTS Enabled and set switch accordingly
        boolean dtsEnabled = isDtsEnabled();
        // For Preference icon, DTS needs to be enabled AND current active route must be
        // either line-out or bluetooth. Set preference icon accordingly
        if (dtsEnabled
                && (mActiveRoute == AudioRoute.LINE_OUT
                || mActiveRoute == AudioRoute.BLUETOOTH || mActiveRoute == AudioRoute.USB)) {
//            mPreferenceIcon.setImageTintList(mEnabledColor);
//            mPreferenceTitle.setTextColor(getResources().getColor(R.color.grey_deep_dark));
        } else {
//            mPreferenceIcon.setImageTintList(mDisabledColor);
//            mPreferenceTitle.setTextColor(getResources().getColor(R.color.grey_light));
        }

        switch (mode) {
            case WIDE:
                mWideButton.setChecked(true);
                mInfrontButton.setChecked(false);
                mTraditionalButton.setChecked(false);
                setTint(mImgWide,true);
                setTint(mTxtWide,true);
                setTintTitle(mTxtWideTitle,true);
                setTint(mImgInfront,false);
                setTint(mTxtInfront,false);
                setTintTitle(mTxtInfrontTitle,false);
                setTint(mImgTraditional,false);
                setTint(mTxtTraditional,false);
                setTintTitle(mTxtTraditionalTitle,false);
                break;
            case FRONT:
                mWideButton.setChecked(false);
                mInfrontButton.setChecked(true);
                mTraditionalButton.setChecked(false);
                setTint(mImgWide,false);
                setTint(mTxtWide,false);
                setTintTitle(mTxtWideTitle,false);
                setTint(mImgInfront,true);
                setTint(mTxtInfront,true);
                setTintTitle(mTxtInfrontTitle,true);
                setTint(mImgTraditional,false);
                setTint(mTxtTraditional,false);
                setTintTitle(mTxtTraditionalTitle,false);

                break;
            case TRADITIONAL:
                mWideButton.setChecked(false);
                mInfrontButton.setChecked(false);
                mTraditionalButton.setChecked(true);
                setTint(mImgWide,false);
                setTint(mTxtWide,false);
                setTintTitle(mTxtWideTitle,false);
                setTint(mImgInfront,false);
                setTint(mTxtInfront,false);
                setTintTitle(mTxtInfrontTitle,false);
                setTint(mImgTraditional,true);
                setTint(mTxtTraditional,true);
                setTintTitle(mTxtTraditionalTitle,true);
                break;
        }
    }

    /**
     * Convenient function to get the system's current stereo mode.
     *
     * @return system's current stereo mode
     */
    private StereoPreference getStereoPreference() {
        // [API-CALL] Call API to get current stereo mode
        DtsResult<StereoPreference> resultStereoMode = DtsManager.getInstance().getStereoPreference(mActiveRoute, mContentMode);
        if (!resultStereoMode.isResultOk()) {
            // getStereoMode() call returned error
            showErrorDialog(resultStereoMode);
            return StereoPreference.UNKNOWN;
        }
        return resultStereoMode.getData();
    }

    /**
     * Broadcast Receiver to listen to changes in active audio route and other broadcasts related
     * to stereo preference.
     */
    private BroadcastReceiver mAudioRouteBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.e(TAG, "Received null intent. Aborting");
                return;
            }

            Log.d(TAG, "Received intent. Action: " + intent.getAction());

            switch (intent.getAction()) {
                case DtsIntents.INTENT_AUDIO_ROUTE_CHANGED:
                    AudioRoute audioRoute = (AudioRoute) intent.getSerializableExtra(DtsIntents.EXTRA_AUDIO_ROUTE);
                    mActiveRoute = audioRoute;
                    refreshStereoPreferenceUI(getStereoPreference());
                    break;
            }
        }
    };
    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will remove any tint.
     * If disabled, will apply gray tint.
     *
     * @param imageView to apply the color tint.
     * @param enabled to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(ImageView imageView, boolean enabled) {
        int color;

        if (enabled) {
            color = ContextCompat.getColor(mActivity, R.color.dtsOrangeAlt2);
            imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        } else {
            color = ContextCompat.getColor(mActivity, R.color.dtsGray8);
            imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will apply white tint.
     * If disabled, will apply gray tint.
     *
     * @param textView to apply the color tint.
     * @param enabled to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(TextView textView, boolean enabled) {
        int color;

        if (enabled) {
            color = ContextCompat.getColor(mActivity, R.color.dtsWhite);
        } else {
            color = ContextCompat.getColor(mActivity, R.color.dtsGray10);
        }
        textView.setTextColor(color);
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will apply white tint.
     * If disabled, will apply gray tint.
     *
     * @param textView to apply the color tint.
     * @param enabled to indicate if the UI should be enabled or disabled.
     */
    private final void setTintTitle(TextView textView, boolean enabled) {
        int color;

        if (enabled) {
            color = ContextCompat.getColor(mActivity, R.color.dtsOrangeAlt2);
        } else {
            color = ContextCompat.getColor(mActivity, R.color.dtsWhite);
        }
        textView.setTextColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back icon in action bar clicked; close activity
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Convenient function to get DTS enabled state.
     * If the API returned error message, this function will show AlertDialog and return false
     *
     * @return true if DTS is enabled
     * false if DTS is disabled, or returned error
     */
    private boolean isDtsEnabled() {
        DtsResult<Boolean> resultBool = DtsManager.getInstance().getDtsEnabled();
        if (!resultBool.isResultOk()) {
            // getDtsEnabled() returned error
            // Log error message and show Alert Dialog
            Log.e(TAG, "Getting DTS enabled failed.");
            Log.e(TAG, "DTS returned error code: " + resultBool.getResultCode() + ". " + resultBool.getResultMessage());
            showErrorDialog(resultBool);

            return false;
        }

        boolean dtsEnabled = resultBool.getData();

        // Update the DTS switch accordingly
        // mDtsSwitch.setChecked(dtsEnabled);

        return dtsEnabled;
    }

    /**
     * Convenient function to get currently selected content mode.
     */
    private void getContentMode(){
        DtsManager.getInstance().getContentMode(mActivity, new OnCompleteCallback() {
            @Override
            public void onComplete(DtsResult dtsResult) {

                if (dtsResult.isResultOk()) {

                    ContentMode contentMode = (ContentMode) dtsResult.getData();
                    if(contentMode != null){
                        mContentMode = contentMode;
                    }
                }
            }
        });
    }
}

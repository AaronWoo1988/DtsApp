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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;

import com.dts.dtssdk.DtsIntents;
import com.dts.dtssdk.DtsManager;
import com.dts.dtssdk.accessory.ContentMode;
import com.dts.dtssdk.callback.OnCompleteCallback;
import com.dts.dtssdk.result.DtsResult;
import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.util.FeatureManager;
import com.dts.dtssdk.util.AudioRoute;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EnhancementsFragment extends DtsFragment {
    private static final String TAG = EnhancementsFragment.class.getSimpleName();

    private FragmentActivity mActivity;

    private TextView enhancements_bassboost_title;
    private TextView enhancements_vocalboost_title;
    private TextView enhancements_trebleboost_title;
    private Switch enhancements_trebleboost_switch;
    private Switch enhancements_vocalboost_switch;
    private Switch enhancements_bassboost_switch;
    private ImageView enhancements_bassboost_icon;
    private ImageView enhancements_vocalboost_icon;
    private ImageView enhancements_trebleboost_icon;

    private OnFragmentInteractionListener mListener;
    private ContentMode mContentMode;
    private AudioRoute mActiveRoute;

    public EnhancementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContentMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_enhancements, container, false);
        enhancements_bassboost_title = (TextView)view.findViewById(R.id.enhancements_bassboost_title);
        enhancements_vocalboost_title = (TextView)view.findViewById(R.id.enhancements_vocalboost_title);
        enhancements_trebleboost_title = (TextView)view.findViewById(R.id.enhancements_trebleboost_title);
        enhancements_trebleboost_switch = (Switch)view.findViewById(R.id.enhancements_trebleboost_switch);
        enhancements_vocalboost_switch = (Switch)view.findViewById(R.id.enhancements_vocalboost_switch);
        enhancements_bassboost_switch = (Switch)view.findViewById(R.id.enhancements_bassboost_switch);
        enhancements_bassboost_icon = (ImageView)view.findViewById(R.id.enhancements_bassboost_icon);
        enhancements_vocalboost_icon = (ImageView)view.findViewById(R.id.enhancements_vocalboost_icon);
        enhancements_trebleboost_icon = (ImageView)view.findViewById(R.id.enhancements_trebleboost_icon);

        // Check if Treble Boost feature is enabled
        RelativeLayout trebleBoostLayout = (RelativeLayout) view.findViewById(R.id.enhancements_layout_trebleboost);
        if(FeatureManager.hasTrebleBoost()) {
            enhancements_trebleboost_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //Enable/Disable Treble Boost mode
                    enableTrebleBoost(isChecked);

                    //Update the icon and title
                    refreshTrebleBoostUI(isChecked);
                }
            });
        } else {
            trebleBoostLayout.setVisibility(View.GONE);
        }

        // Check if Bass Boost feature is enabled
        RelativeLayout bassBoostLayout = (RelativeLayout) view.findViewById(R.id.enhancements_layout_bassboost);
        if(FeatureManager.hasBassBoost()) {

            enhancements_bassboost_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            //Enable/Disable Bass Boost mode
                enableBassBoost(isChecked);

            //Update the icon and title
                refreshBassBoostUI(isChecked);
            }
        });
        } else {
            bassBoostLayout.setVisibility(View.GONE);
        }

        // Check if Vocal Boost / Dialog Enhancement feature is enabled
        RelativeLayout vocalBoostLayout = (RelativeLayout) view.findViewById(R.id.enhancements_layout_vocalboost);
        if(FeatureManager.hasDialogEnhancement()) {
            enhancements_vocalboost_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Enable/Disable Dialog Enhancement mode
                enableDialogEnhancement(isChecked);

                //Update the icon and title
                refreshVocalBoostUI(isChecked);
            }
        });
        } else {
            vocalBoostLayout.setVisibility(View.GONE);
        }

        //If all the enhancement features are disabled,hide the fragment
        if(!FeatureManager.hasTrebleBoost() && !FeatureManager.hasBassBoost() && !FeatureManager.hasDialogEnhancement()) {
            TextView enhancementsTitle = (TextView) view.findViewById(R.id.enhancements_title);
            enhancementsTitle.setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register global broadcast receiver for audio route change events
        IntentFilter sdkChangeFilter = new IntentFilter();
        sdkChangeFilter.addAction(DtsIntents.INTENT_AUDIO_ROUTE_CHANGED);
        mActivity.registerReceiver(this.mSDKBroadcastReceiver, sdkChangeFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        mActiveRoute = DtsManager.getInstance().getAudioRoute();

        //Update the Boost modes UI
        refreshBassBoostUI(getBassBoostEnabled());
        refreshTrebleBoostUI(getTrebleBoostEnabled());
        refreshVocalBoostUI(getDialogEnhancementEnabled());
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.unregisterReceiver(this.mSDKBroadcastReceiver);
    }

    /**
     * Convenient function to refresh UI of Bass Boost enabled state.
     */
    private void refreshBassBoostUI(boolean enabled) {
        if (!isAdded()) {
            Log.d(TAG, "Fragment is not added to activity. Aborting");
            return;
        }
        setTint(enhancements_bassboost_icon,enabled);
        setTint(enhancements_bassboost_title,enabled);
        enhancements_bassboost_switch.setChecked(enabled);
    }

    /**
     * Convenient function to refresh UI of Treble Boost enabled state.
     */
    private void refreshTrebleBoostUI(boolean enabled) {
        if (!isAdded()) {
            Log.d(TAG, "Fragment is not added to activity. Aborting");
            return;
        }

        setTint(enhancements_trebleboost_icon,enabled);
        setTint(enhancements_trebleboost_title,enabled);
        enhancements_trebleboost_switch.setChecked(enabled);
    }

    /**
     * Convenient function to refresh UI of Vocal Boost enabled state.
     */
    private void refreshVocalBoostUI(boolean enabled) {
        if (!isAdded()) {
            Log.d(TAG, "Fragment is not added to activity. Aborting");
            return;
        }

        setTint(enhancements_vocalboost_icon,enabled);
        setTint(enhancements_vocalboost_title,enabled);
        enhancements_vocalboost_switch.setChecked(enabled);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will remove any tint.
     * If disabled, will apply gray tint.
     *
     * @param imageView to apply the color tint.
     * @param enabled to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(ImageView imageView, boolean enabled) {
        int color = ContextCompat.getColor(mActivity, R.color.dtsGray8);

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
     * Convenient function to enable/disable Bass Boost
     *
     * @param enable true if Bass Boost mode is to be enabled
     *               false if Bass Boost is to be disabled
     */
    private void enableBassBoost(boolean enable) {
        if (!FeatureManager.hasBassBoost()) {
            Log.w(TAG,"Bass Boost feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setBassBoostEnabled(mActivity, mActiveRoute, mContentMode, enable);

        if (!result.isResultOk()) {
            showErrorDialog(result);
        }
    }

    /**
     * Convenient function to enable/disable Treble Boost
     *
     * @param enable true if Treble Boost mode is to be enabled
     *               false if Treble Boost is to be disabled
     */
    private void enableTrebleBoost(boolean enable) {
        if (!FeatureManager.hasTrebleBoost()) {
            Log.w(TAG,"Treble Boost feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setTrebleBoostEnabled(mActivity, mActiveRoute, mContentMode, enable);

        if (!result.isResultOk()) {
            showErrorDialog(result);
        }
    }

    /**
     * Convenient function to enable/disable Vocal Boost
     *
     * @param enable true if Vocal Boost mode is to be enabled
     *               false if Vocal Boost is to be disabled
     */
    private void enableDialogEnhancement(boolean enable) {
        if (!FeatureManager.hasDialogEnhancement()) {
            Log.w(TAG,"Dialog Enhancement feature is disabled");
            return;
        }

        DtsResult result = DtsManager.getInstance().setDialogEnhancementEnabled(mActivity, mActiveRoute, mContentMode, enable);

        if (!result.isResultOk()) {
            showErrorDialog(result);
        }
    }

    /**
     * Convenient function to get the Bass boost enabled state.
     *
     * @return Bass boost is enabled/disabled
     */
    private boolean getBassBoostEnabled() {
        if (!FeatureManager.hasBassBoost()) {
            Log.w(TAG,"Bass Boost feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Bass Boost enabled state
        DtsResult<Boolean> resultBassBoostMode = DtsManager.getInstance().getBassBoostEnabled(mActiveRoute, mContentMode);
        if (!resultBassBoostMode.isResultOk()) {
            // getBassBoostEnabled() call returned error
            showErrorDialog(resultBassBoostMode);
            return false;
        }
        Log.d(TAG,"Bass Boost enabled = "+resultBassBoostMode.getData());
        return resultBassBoostMode.getData();
    }

    /**
     * Convenient function to get the Treble boost enabled state.
     *
     * @return Treble boost is enabled/disabled
     */
    private boolean getTrebleBoostEnabled() {
        if (!FeatureManager.hasTrebleBoost()) {
            Log.w(TAG,"Treble Boost feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Treble Boost enabled state
        DtsResult<Boolean> resultTrebleBoostMode = DtsManager.getInstance().getTrebleBoostEnabled(mActiveRoute, mContentMode);
        if (!resultTrebleBoostMode.isResultOk()) {
            // getTrebleBoostEnabled() call returned error
            showErrorDialog(resultTrebleBoostMode);
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
            Log.w(TAG,"Dialog enhancement feature is disabled");
            return false;
        }
        // [API-CALL] Call API to get Dialog Enhancement enabled state
        DtsResult<Boolean> resultDialogEnhancementMode = DtsManager.getInstance().getDialogEnhancementEnabled(mActiveRoute, mContentMode);
        if (!resultDialogEnhancementMode.isResultOk()) {
            // getDialogEnhancementEnabled() call returned error
            showErrorDialog(resultDialogEnhancementMode);
            return false;
        }
        return resultDialogEnhancementMode.getData();
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
                    break;
            }
        }
    };
}

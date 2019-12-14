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
package com.dts.dtsaudioprocessing.fragments;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.dts.dtsaudioprocessing.util.FeatureManager;
import com.dts.dtsaudioprocessing.views.VerticalSeekBar;
import com.dts.dtsaudioprocessing.R;

import com.dts.dtssdk.DtsIntents;
import com.dts.dtssdk.DtsManager;
import com.dts.dtssdk.result.DtsResult;
import com.dts.dtssdk.util.AudioRoute;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GeqFragment extends DtsFragment {
    private static final String TAG = GeqFragment.class.getSimpleName();

    private static final int NUM_GUI_BANDS = 5;

    // GEQ value ranges
    //gain range of GEQ band(from -12 to 12 dB without treble slider)
    private static final int GEQ_GAIN_LOW = -12;
    private static final int GEQ_GAIN_LOW_TREBLE = -10;
    private static final int GEQ_GAIN_HIGH = 12;
    private static final int GEQ_GAIN_HIGH_TREBLE = 10;
    private int mBarUiMin; // Needed for UI progress to system value converstion.
    private int mBarUiMax;

    // Views
    private VerticalSeekBar[] mBars;
    private TextView[] mValues;
    private TextView mGeqTxtGeqTitle;
    private TextView mGeqTxtGeqGainTop;
    private TextView mGeqTxtGeqGainBottom;
    // Moderator
    private EqualizerModerator mEqualizerModerator;
    private SwitchCompat mSwitchActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_geq, container, false);
        setHasOptionsMenu(true);

        getActivity().setTitle(getText(R.string.graphic_equalizer_title));

        mBars = new VerticalSeekBar[NUM_GUI_BANDS];
        mValues = new TextView[NUM_GUI_BANDS];
        ActionBar abGeq = ((AppCompatActivity) getActivity()).getSupportActionBar();
        abGeq.setDisplayHomeAsUpEnabled(true);

        // Interactive views:
        mBars[0] = (VerticalSeekBar) view.findViewById(R.id.equalizer_seekBar1);
        mBars[1] = (VerticalSeekBar) view.findViewById(R.id.equalizer_seekBar2);
        mBars[2] = (VerticalSeekBar) view.findViewById(R.id.equalizer_seekBar3);
        mBars[3] = (VerticalSeekBar) view.findViewById(R.id.equalizer_seekBar4);
        mBars[4] = (VerticalSeekBar) view.findViewById(R.id.equalizer_seekBar5);
        mValues[0] = (TextView) view.findViewById(R.id.equalizer_barValue1);
        mValues[1] = (TextView) view.findViewById(R.id.equalizer_barValue2);
        mValues[2] = (TextView) view.findViewById(R.id.equalizer_barValue3);
        mValues[3] = (TextView) view.findViewById(R.id.equalizer_barValue4);
        mValues[4] = (TextView) view.findViewById(R.id.equalizer_barValue5);
        mGeqTxtGeqTitle = (TextView) view.findViewById(R.id.txt_geq_title);
        mGeqTxtGeqTitle.setVisibility(View.INVISIBLE);
        mGeqTxtGeqGainTop = (TextView) view.findViewById(R.id.txt_geq_gain_top);
        mGeqTxtGeqGainBottom = (TextView) view.findViewById(R.id.txt_geq_gain_bottom);
        // Moderator
        mEqualizerModerator = new EqualizerModerator();

        /**
         * Set onSeekBarChangeListener for all seek bars
         */
        for (int i = 0; i < NUM_GUI_BANDS; i++) {
            final int barIndex = i;
            mBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mValues[barIndex].setText(String.valueOf(toGain(progress)));

                        // When user is dragging the equalizer, use EqualizerModerator to send
                        // setGEQ signals passively (and moderate it)
                        mEqualizerModerator.setGEQPassive(barIndex, toGain(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mValues[barIndex].setText(String.valueOf(toGain(seekBar.getProgress())));
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // onStopTrackingTouch() gets called either when the user taps the equalizer
                    // (as opposed to dragging it) or was dragging the equalizer but stopped touching
                    // it (let go of the fingers from the screen). Use EqualizerModerator to send
                    // setGEQAggressive to set the GEQ instantaneously.
                    mValues[barIndex].setText(String.valueOf(toGain(seekBar.getProgress())));

                    mEqualizerModerator.setGEQAggressive(barIndex, toGain(seekBar.getProgress()));
                }
            });
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.menu_geq, menu);

        MenuItem menuItem = menu.findItem(R.id.menuSwitch);
        mSwitchActionBar = (SwitchCompat) menuItem.getActionView();
        mSwitchActionBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "OnCheckChanged(): mEqualizerSwitch - " + isChecked);

                if (buttonView.isClickable()) {
                    enableEqualizer(isChecked);
                } else {
                    Log.d(TAG, "Equalizer switch is disabled. Ignoring onCheckChanged(): " + isChecked);
                }
            }
        });

        refreshUi(DtsManager.getInstance().getAudioRoute());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back icon in action bar clicked; close activity
                // finish();
                getActivity().onBackPressed();
                return true;

            case R.id.menuRefresh:
                refreshGeq();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Convenient method to refresh GEQ setting when refresh button clicked from menu.
     */
    private void refreshGeq() {

        if (isGEQEnabled()) {
            mEqualizerModerator.getGeqDefaultValues();
            mEqualizerModerator.setGeqValues();
        }
        refreshUi(DtsManager.getInstance().getAudioRoute());
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        // Register global broadcast receiver for audio route change events
        IntentFilter sdkChangeFilter = new IntentFilter();
        sdkChangeFilter.addAction(DtsIntents.INTENT_AUDIO_ROUTE_CHANGED);
        getActivity().registerReceiver(this.mSDKBroadcastReceiver, sdkChangeFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(this.mSDKBroadcastReceiver);
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

    @TargetApi(21)
    private void setStatusBarColor() {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.dtsOrange));
        } else {
            Log.d(TAG, "==setStatusBarColor: This does not work for API Level 20 or lower.");
        }
    }

    void refreshUi(AudioRoute audioRoute) {
        Log.d(TAG, "refreshUi()");

        refreshEqualizerUI(audioRoute);
    }

    @Override
    public void updateState() {
        refreshUi(DtsManager.getInstance().getAudioRoute());
    }

    /**
     * Convenient function to handle DtsResult errors
     *
     * @param action
     * @param result
     */
    private void handleError(String action, DtsResult result) {
        Log.e(TAG, action + " failed: " + result.getResultCode() + " | " + result.getResultMessage());
    }

    /**
     * Convenient function to refresh the UI based on current equalizer setting
     */
    private void refreshEqualizerUI(AudioRoute audioRoute) {

        int barMax;

        //[API-CALL] Read current system state for GEQ enable/disable and set switch,bars accordingly
        refreshGeqBars(isGEQEnabled());

        // GEQ value ranges:
        //Values for GEQ slider range
        //When treble siider is enabled the headroom becomes less
        Log.d(TAG, "hasTrebleBoost feature : " + FeatureManager.hasTrebleBoost());
        if (FeatureManager.hasTrebleBoost()) {
            mBarUiMin = GEQ_GAIN_LOW_TREBLE;
            mBarUiMax = GEQ_GAIN_HIGH_TREBLE;
        } else {
            //Treble boost is disabled
            mBarUiMin = GEQ_GAIN_LOW;
            mBarUiMax = GEQ_GAIN_HIGH;
        }

        barMax = mBarUiMax - mBarUiMin;

        for (int i = 0; i < NUM_GUI_BANDS; i++) {
            mBars[i].setMax(barMax);
        }

        String viewText = "+" + String.valueOf(mBarUiMax);
        mGeqTxtGeqGainTop.setText(viewText);
        viewText = String.valueOf(mBarUiMin);
        mGeqTxtGeqGainBottom.setText(viewText);

        //Update active audio route
       /* if (audioRoute == AudioRoute.INTERNAL_SPEAKERS) {
            mGeqTxtGeqTitle.setText(getResources().getString(R.string.speakers));
        } else if (audioRoute == AudioRoute.LINE_OUT) {
            mGeqTxtGeqTitle.setText(getResources().getString(R.string.headphones));
        } else if (audioRoute == AudioRoute.USB) {
            mGeqTxtGeqTitle.setText(getResources().getString(R.string.usb));
        } else if (audioRoute == AudioRoute.BLUETOOTH) {
            mGeqTxtGeqTitle.setText(getResources().getString(R.string.bluetooth));
        }*/

        // Get the Integer List (list of dB values) for the GEQ and update the TextView and
        // equalizer bar with it.
        List<Integer> gainList = mEqualizerModerator.getGeqValues();

        mBars[0].setProgress(toProgressBarValue(gainList.get(0)));
        mBars[1].setProgress(toProgressBarValue(gainList.get(1)));
        mBars[2].setProgress(toProgressBarValue(gainList.get(2)));
        mBars[3].setProgress(toProgressBarValue(gainList.get(3)));
        mBars[4].setProgress(toProgressBarValue(gainList.get(4)));

        mValues[0].setText(String.valueOf(gainList.get(0)));
        mValues[1].setText(String.valueOf(gainList.get(1)));
        mValues[2].setText(String.valueOf(gainList.get(2)));
        mValues[3].setText(String.valueOf(gainList.get(3)));
        mValues[4].setText(String.valueOf(gainList.get(4)));
    }

    /**
     * Convenient function to change SeekBar's progress to gain value.
     *
     * @param rawProgress progress value of the SeekBar
     * @return int gain value
     */
    private int toGain(int rawProgress) {
        return rawProgress + mBarUiMin;
    }

    /**
     * Convenient function to convert the gain amount to progress bar value
     *
     * @param gain for the equalizer bar
     * @return value to be set to the progress bar (equalizer)
     */
    private int toProgressBarValue(int gain) {
        return gain - mBarUiMin;
    }

    private int getScreenOrientation() {
        return getResources().getConfiguration().orientation;
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

            AudioRoute audioRoute;
            switch (intent.getAction()) {
                case DtsIntents.INTENT_AUDIO_ROUTE_CHANGED:
                    audioRoute = (AudioRoute) intent.getSerializableExtra(DtsIntents.EXTRA_AUDIO_ROUTE);
                    Log.d(TAG, "Audio route detected: " + audioRoute);
                    refreshUi(audioRoute);
                    break;
            }
        }
    };

    /**
     * A class to moderate sending setGEQ requests for the equalizer bar. All setGEQ() (and related)
     * functions should be done through this class so that the number of requests can be moderated
     * appropriately.
     * <p>
     * Calls to setGEQ() must be moderated because Android SeekBar's onProgressChanged() function
     * gets called hundreds of times when a user drags the SeekBar. If DtsManager's setGEQ() is called
     * from onProgressChanged(), the system may be overloaded with too many requests. To avoid flooding
     * the system, this class provides a function to filter out unnecessary/similar requests
     * (passive mode) and therefore reduce the number of calls to DtsManager's setGEQ().
     */
    private class EqualizerModerator {
        Handler mHandler = new Handler();
        int[] mLastSetGain = new int[]{-100, -100, -100, -100, -100};
        HashSet<Integer> mActiveRunnables = new HashSet<>();

        // Amount of time it waits (in milliseconds) before sending setGEQ() request to DtsManager
        private final int WAIT_DURATION_IN_MILLIS = 200;//ms

        /**
         * Sets the GEQ in passive mode. In this mode, the function will wait WAIT_DURATION_IN_MILLIS
         * before sending the setGEQ() request to DtsManager. In that time, if it receives new requests
         * for the same band, it will save the new gain value and discard the old one. After the wait time
         * expires, whatever gain value was called last will be used to send setGEQ() to DtsManager.
         * <p/>
         * For instance, if WAIT_DURATION_IN_MILLIS is set to 200 and this function is called
         * 5 times in 200ms, then only the last setGEQPassive() call will be processed while 4 previous
         * calls will be discarded. This implementation is to ensure that we don't overload the system
         * with too many setGEQ() calls (especially when the user is dragging the equalizer bar).
         *
         * @param band number of the equalizer
         * @param gain dB value to set
         */
        void setGEQPassive(int band, int gain) {
            final int index = band;

            if (mLastSetGain[index] != gain) {
                // Save this gain value as the "last set" gain value
                mLastSetGain[index] = gain;

                // Check if runnable for this index is already running
                if (!mActiveRunnables.contains(index)) {
                    // Runnable for this index is not running. Start new and trigger wait
                    mActiveRunnables.add(index);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            mActiveRunnables.remove(index);

                            DtsManager.getInstance().setGEQ5Gain(index, mLastSetGain[index]);

                            // For passive setting, don't show alert dialog even if setGEQ fails
                        }
                    }, WAIT_DURATION_IN_MILLIS);
                } else {
                    // The runnable for this index is already running. Let the runnable take care
                    // of setting the last set gain value. Do nothing
                }
            }
        }

        /**
         * Sets the GEQ in aggressive mode. In this mode, the setGEQ will be called instantaneously,
         * without waiting for other calls. If the gain value has already been set for the band,
         * this call will be ignored.
         *
         * @param band number of the equalizer
         * @param gain dB value to set
         */
        void setGEQAggressive(int band, int gain) {
            // If the gain value is same as last recorded value (or to-be-recorded by the runnable),
            // ignore, as we don't want to make redundant calls to the system.
            if (mLastSetGain[band] != gain) {
                mLastSetGain[band] = gain;

                // [API-CALL] Set gain for a specific band
                DtsResult result = DtsManager.getInstance().setGEQ5Gain(band, gain);

                if (!result.isResultOk()) {
                    // setGEQGain() returned error. Show error message.
                    handleError("setGEQAggressive()", result);
                }
            }
        }

        private List<Integer> getGeqDefaultValues() {
            // In DTS there are no GEQ presets. Hence, the default values are 0.
            List<Integer> geqDefaultValues = new ArrayList<>(NUM_GUI_BANDS);

            mLastSetGain[0] = 0;
            mLastSetGain[1] = 0;
            mLastSetGain[2] = 0;
            mLastSetGain[3] = 0;
            mLastSetGain[4] = 0;

            return geqDefaultValues;
        }

        private void setGeqValues() {
            List<Integer> geqValues = new ArrayList<>(NUM_GUI_BANDS);

            // Update last-set gain array

            geqValues.add(mLastSetGain[0]);
            geqValues.add(mLastSetGain[1]);
            geqValues.add(mLastSetGain[2]);
            geqValues.add(mLastSetGain[3]);
            geqValues.add(mLastSetGain[4]);

            // Use API to set all GEQ
            DtsResult result = DtsManager.getInstance().setAllGEQ5Gains(geqValues);

            if (!result.isResultOk()) {
                handleError("setGeqValues()", result);
                return;
            }
        }

        private List<Integer> getGeqValues() {
            // Get current GEQ values:
            // Use API to get all GEQ values:
            List<Integer> geqValues;
            DtsResult<List<Integer>> result = DtsManager.getInstance().getAllGEQ5Gains();
            if (!result.isResultOk()) {
                handleError("getGeqValues()", result);
                return new ArrayList<>(NUM_GUI_BANDS);
            } else {
                geqValues = result.getData();
            }

            // Update last-set gain array:
            for (int i = 0; i < NUM_GUI_BANDS; i++) {
                //Ensure GEQ gain values returned from platform are bound within the expected GEQ ranges.
                if (geqValues.get(i) < mBarUiMin) {
                    geqValues.set(i, mBarUiMin);
                    setGEQAggressive(i, mBarUiMin); //  update platform with the new values
                } else if (geqValues.get(i) > mBarUiMax) {
                    geqValues.set(i, mBarUiMax);
                    setGEQAggressive(i, mBarUiMax); //  update platform with the new values
                }
                mLastSetGain[i] = geqValues.get(i);
            }
            return geqValues;
        }
    }

    /**
     * Convenient function to enable/disable equalizer
     *
     * @param enable true if equalizer is to be enabled
     *               false if equalizer is to be disabled
     */
    private void enableEqualizer(boolean enable) {
        DtsResult result = DtsManager.getInstance().setGEQEnabled(enable);

        if (!result.isResultOk()) {
            showErrorDialog(result);
        }

        updateFeatureIcon(isDtsEnabled(), enable);
        refreshGeqBars(enable);
    }

    /**
     * Convenient function to get equalizer state.
     * Also updates the {@link #mSwitchActionBar} state accordingly
     * If the API returned error message, this function will show AlertDialog and return false
     *
     * @return true if GEQ is enabled
     * false if GEQ is disabled, or returned error
     */
    private boolean isGEQEnabled() {
        if (!FeatureManager.hasGEQ()) {
            return false;
        }

        DtsResult<Boolean> resultBool = DtsManager.getInstance().getGEQEnabled();
        if (!resultBool.isResultOk()) {
            // getGEQEnabled() returned error
            // Log error message and show Alert Dialog
            Log.e(TAG, "Getting GEQ enabled failed.");
            Log.e(TAG, "DTS returned error code: " + resultBool.getResultCode() + ". " + resultBool.getResultMessage());
            showErrorDialog(resultBool);

            return false;
        }

        boolean geqEnabled = resultBool.getData();

        // Update the equalizer switch accordingly
        mSwitchActionBar.setChecked(geqEnabled && isDtsEnabled());

        return geqEnabled;
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

        return dtsEnabled;
    }

    /**
     * Convenient function to apply tint to feature (equalizer, headphone, and stereo preference)
     * icons. The color of the icon tints depends on DTS status and currently active route.
     *
     * @param dtsEnabled       whether or not DTS is enabled
     * @param equalizerEnabled whether or not equalizer is enabled
     */
    private void updateFeatureIcon(boolean dtsEnabled, boolean equalizerEnabled) {

        if (dtsEnabled) {
            // Set to previous state before disable
            mSwitchActionBar.setChecked(equalizerEnabled);

        } else {
            // Disable GEQ if DTS is disabled
            // (If GEQ is already disabled, no need to do anything)
            if (equalizerEnabled) {
                mSwitchActionBar.setChecked(false);
            }
        }
    }

    private void refreshGeqBars(boolean enable) {
        //enable/disable the sliders
        for (int i = 0; i < NUM_GUI_BANDS; i++) {
            mBars[i].setEnabled(enable);
            setTint(mBars[i].getThumb(), enable);
        }
    }

    /**
     * Convenient function to set tint to indicate the UI is enabled or disabled.
     * If enabled, will apply dts orange tint.
     * If disabled, will apply gray tint.
     *
     * @param drawable to apply the color tint.
     * @param enabled  to indicate if the UI should be enabled or disabled.
     */
    private final void setTint(Drawable drawable, boolean enabled) {
        int color;

        if (enabled) {
            color = ContextCompat.getColor(getActivity(), R.color.dts_orange);
        } else {
            color = ContextCompat.getColor(getActivity(), R.color.dtsGray10);
        }
        drawable.setTint(color);
    }
}
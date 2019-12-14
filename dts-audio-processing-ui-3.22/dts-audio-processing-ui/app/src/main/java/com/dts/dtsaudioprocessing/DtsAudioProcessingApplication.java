package com.dts.dtsaudioprocessing;

import android.app.Application;
import android.util.Log;

public class DtsAudioProcessingApplication extends Application {
    private static final String TAG = DtsAudioProcessingApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        super.onCreate();
    }
}

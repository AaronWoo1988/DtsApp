package com.dts.dtsaudioprocessing.util;

import android.content.Context;
import android.content.SharedPreferences;

// A simple class to manage a special shared preferences data.
//
// Limitations:
// 1. The shared preferences name is hard-coded to MY_NAME.
// 2. The file creation mode is hard-coded to MY_MODE.
//
public class SharedPrefManager {
    private static final String MY_NAME = "my_shared_preferences";
    private static final int MY_MODE = Context.MODE_PRIVATE;

    // Get boolean:
    public static boolean getBoolean(final Context context, final String key, final boolean defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_NAME, MY_MODE);
        return sharedPref.getBoolean(key, defValue);
    }

    // Put boolean:
    public static void putBoolean(final Context context, final String key, final boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_NAME, MY_MODE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Get String:
    public static String getString(final Context context, final String key, final String defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_NAME, MY_MODE);
        return sharedPref.getString(key, defValue);
    }

    // Put boolean:
    public static void putString(final Context context, final String key, final String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_NAME, MY_MODE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}

package com.teamll.expectlauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerAdapter;

public final class PreferencesUtility {
    private final static String SHOW_APP_TITLE = "show_app_title";

    private final static String APP_ICON_SIZE ="app_icon_size";

    private final static String APP_POSITION = "app_position";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;
    private static Context context;
    private ConnectivityManager connManager = null;

    public PreferencesUtility(final Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }
    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
    public float getAppIconSize() {
       return mPreferences.getFloat(APP_ICON_SIZE,1.0f);
    }
    public void setAppIconSize(float value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        if(value<0f) value = 0;
        editor.putFloat(APP_ICON_SIZE, value);
        editor.apply();
    }
    public boolean isShowAppTitle() {
        return mPreferences.getBoolean(SHOW_APP_TITLE,true);
    }
    public void setShowAppTitle(boolean value) {
        final  SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SHOW_APP_TITLE,value);
        editor.apply();
    }

}

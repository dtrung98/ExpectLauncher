package com.teamll.expectlauncher;

import android.app.Application;

import com.teamll.expectlauncher.util.PreferencesUtility;

public class ExpectLauncher extends Application {
    private static ExpectLauncher sInstance;
    private PreferencesUtility mPreferences;
    public PreferencesUtility getPreferencesUtility() {
        if(mPreferences==null) mPreferences = new PreferencesUtility(getApplicationContext());
        return mPreferences;
    }
    @Override
    public void onCreate() {
        sInstance = this;
        getPreferencesUtility();
        super.onCreate();
    }
    public static ExpectLauncher getInstance() {
        return sInstance;
    }
    public void release() {
        mPreferences = null;
    }

    @Override
    public void onTerminate() {
        release();
        sInstance = null;
        super.onTerminate();
    }
}

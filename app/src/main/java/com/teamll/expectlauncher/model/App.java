package com.teamll.expectlauncher.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.teamll.expectlauncher.ExpectLauncher;

import java.io.File;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class App {

    private Context mContext;
    private ApplicationInfo mInfo;

    private String mAppLabel;
    Drawable mIcon;

    private boolean mMounted;
    private File mApkFile;

    public AppInstance getAppSavedInstance() {
        return mAppInstance;
    }

    public void setAppSavedInstance(AppInstance mAppInstance) {
        this.mAppInstance = mAppInstance;
    }
    public void createNewSavedInstance(int index) {
        if(mAppInstance==null)
        mAppInstance = ExpectLauncher.getInstance().getPreferencesUtility().createAppInstance(this,index);
    }

    private AppInstance mAppInstance;


    public App() {

    }

    public App(Context context, ApplicationInfo info) {
        mContext = context;
        mInfo = info;
        mApkFile = new File(info.sourceDir);
    }

    public ApplicationInfo getAppInfo() {
        return mInfo;
    }

    public String getApplicationPackageName() {
        return getAppInfo().packageName;
    }

    public String getLabel() {
        return mAppLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {

                mIcon = mInfo.loadIcon(mContext.getPackageManager());

                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mContext.getPackageManager());
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }


    void loadLabel(Context context) {
        if (mAppLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mAppLabel = mInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mAppLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }
    public void setLabel(String s) {
        mAppLabel = s;
    }
}

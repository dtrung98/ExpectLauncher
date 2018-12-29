package com.teamll.expectlauncher.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.util.BitmapEditor;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class AppsLoader extends AsyncTaskLoader<ArrayList<App>> {
    private static final String TAG ="AppsLoader";

    private ArrayList<App> mInstalledApps;

    private final PackageManager mPm;
    private PackageIntentReceiver mPackageObserver;

    public AppsLoader(Context context) {
        super(context);

        mPm = context.getPackageManager();
    }

    private int findItem(ArrayList<App> appList, String packageName) {
        int id = -1;

        int count = appList.size();
        for (int index = 0; index < count; index++) {
            if (appList.get(index).getApplicationPackageName().equals(packageName)) {
                id = index;
                break;
            }
        }

        return id;
    }
    @Override
    public ArrayList<App> loadInBackground() {
        long t1 = System.currentTimeMillis(),t2,t3,t4,t5;

        // retrieve the list of installed applications
        List<ApplicationInfo> applicationInfos = mPm.getInstalledApplications(0);

        if (applicationInfos == null) {
            applicationInfos = new ArrayList<>();
        }

        t2 = System.currentTimeMillis();

        final Context context = getContext();

        // get the saved app instances if any
        AppInstance[] appInstances = ExpectLauncher.getInstance().getPreferencesUtility().getSavedAppInstance();

        t3 = System.currentTimeMillis();

        ArrayList<App> appList = new ArrayList<>(applicationInfos.size());
        HashMap<String,App> appMap = new HashMap<>(applicationInfos.size());

        App myApp = null;
        for (ApplicationInfo appInfo :
                applicationInfos) {
            String pkg = appInfo.packageName;
            if(context.getPackageManager().getLaunchIntentForPackage(pkg)!=null) {
                App app = new App(context,appInfo);
                app.loadLabel(context);
                if(pkg.equals(context.getPackageName())) app.setLabel(context.getResources().getString(R.string.preference));
                if(myApp==null&&appInstances.length==0&&pkg.equals(context.getPackageName())) myApp = app;
                else {
                    appList.add(app);
                    appMap.put(app.getApplicationPackageName(),app);
                }
            }
        }

        t4 = System.currentTimeMillis();

        ArrayList<App> returnList;

        // combine appInstance to appList
        // add my launcher to be the first item
        if(appInstances.length==0) {
            // Sort the list
            Collections.sort(appList, ALPHA_COMPARATOR);
           if(myApp!=null)  appList.add(0,myApp);
            for (int i = 0; i < appList.size(); i++) {
                App app = appList.get(i);
                app.createNewSavedInstance(i);
            }
            returnList = appList;
        } else {
            returnList = new ArrayList<>(appList.size());
            // the app instances is exist in map
            for (AppInstance instance: appInstances) {
                App app = appMap.get(instance.getPackageName());
                if (app != null) {
                    app.setAppSavedInstance(instance);
                    returnList.add(app);
                    appList.remove(app);
                    // else the app in instance might be deleted
                }
            }

            // we need to create the new instances for the new added apps
            for (App app :
                    appList) {
                app.createNewSavedInstance(returnList.size());
                returnList.add(app);
            }
        }
        t5 = System.currentTimeMillis();
        Log.d(TAG, "loadInBackgroundV2: t1 "+0);
        Log.d(TAG, "loadInBackgroundV2: t2 "+(t2-t1));
        Log.d(TAG, "loadInBackgroundV2: t3 "+(t3 - t2));
        Log.d(TAG, "loadInBackgroundV2: t4 "+(t4 - t3));
        Log.d(TAG, "loadInBackgroundV2: t5 "+(t5 - t4));
        Log.d(TAG, "loadInBackgroundV2: finish");
        return returnList;
    }

    @Override
    public void deliverResult(ArrayList<App> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }

        ArrayList<App> oldApps = apps;
        mInstalledApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mInstalledApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mInstalledApps);
        }

        // watch for changes in app install and uninstall operation
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }

        if (takeContentChanged() || mInstalledApps == null ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(ArrayList<App> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mInstalledApps != null) {
            onReleaseResources(mInstalledApps);
            mInstalledApps = null;
        }

        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper method to do the cleanup work if needed, for example if we're
     * using Cursor, then we should be closing it here
     *
     * @param apps
     */
    protected void onReleaseResources(ArrayList<App> apps) {
        // do nothing
    }


    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<App> ALPHA_COMPARATOR = new Comparator<App>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(App object1, App object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}

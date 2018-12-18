package com.teamll.expectlauncher.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.AsyncTaskLoader;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.util.BitmapEditor;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class AppsLoader extends AsyncTaskLoader<ArrayList<App>> {
    ArrayList<App> mInstalledApps;

    final PackageManager mPm;
    PackageIntentReceiver mPackageObserver;

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
        SharedPreferences pref = getContext().getSharedPreferences("app-data", Context.MODE_PRIVATE);
        String appString = pref.getString("app-list", "");
        // retrieve the list of installed applications
        List<ApplicationInfo> apps = mPm.getInstalledApplications(0);

        if (apps == null) {
            apps = new ArrayList<ApplicationInfo>();
        }

        final Context context = getContext();

        // create corresponding apps and load their labels
        ArrayList<App> items = new ArrayList<App>(apps.size());
        ArrayList<App> resultItems = new ArrayList<App>();
        for (int i = 0; i < apps.size(); i++) {
            String pkg = apps.get(i).packageName;
            // only apps which are launchable
            if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                App app = new App(context, apps.get(i));
                app.loadLabel(context);
              createAverageColor(app);

              // Rename itself label
              if(app.getApplicationPackageName().equals(context.getPackageName()))
                  app.setLabel(context.getResources().getString(R.string.preference));

             items.add(app);
            }
        }

        // sort the list
        if (appString.equals("")) {
            Collections.sort(items, ALPHA_COMPARATOR);

            // Preference is the first item by default
            for(int i=0;i<items.size();i++) {
                if(items.get(i).getApplicationPackageName().equals(context.getPackageName())) {
                    items.add(1, items.remove(i));
                    break;
                }
            }

            resultItems = items;
        }
        else {
            try {
                JSONArray appsJson = new JSONArray(appString);
                int count = appsJson.length();
                for (int index = 0; index < count; index++) {
                    int id = findItem(items, appsJson.get(index).toString());
                    if (id > -1) {
                        resultItems.add(items.get(id));
                        items.remove(id);
                    }
                }
                count = items.size();
                for (int index = 0; index < count; index++) {
                    resultItems.add(items.get(index));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AppFolder appFolder = new AppFolder(context);
        appFolder.set(resultItems.subList(0,5));
        resultItems.add(appFolder);

        return resultItems;
    }
    private void createAverageColor(App app) {
        if(app.getIcon() instanceof BitmapDrawable) {
             BitmapDrawable bd = (BitmapDrawable) app.getIcon();
            int[] c =  BitmapEditor.getAverageColorRGB(bd.getBitmap());
            // int c2 = Tool.getContrastVersionForColor(Color.rgb(c[0],c[1],c[2]));
            int c2 = Color.rgb(c[0],c[1],c[2]);
            c2 = BitmapEditor.darkenColor(c2);
            app.setDarkenAverageColor(c2);
            // mIcon.setBackgroundColor(c2);
        } else {
            app.setDarkenAverageColor(Color.WHITE);
        }
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

package com.teamll.expectlauncher.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.teamll.expectlauncher.model.App;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static int[] getAverageColor(App app) {
        int[] ret = new int[2];
        if(app.getIcon() instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) app.getIcon();

            int[] dominantNMostColor = BitmapEditor.getDominantAndMostColor(bd.getBitmap());
            int lightValue = BitmapEditor.getLuminance(dominantNMostColor[1]);
            if(lightValue>243||lightValue<17) ret[1] = dominantNMostColor[1];
            else ret[1] = dominantNMostColor[0];

            ret[0] = BitmapEditor.darkenColor(ret[1]);
        } else {
           ret[0] = Color.WHITE;
           ret[1] = Color.TRANSPARENT;
        }
        return ret;
    }

    /**
     * Force hide keyboard if open
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Filtering followers by search char sequence
     * @param list source follower list
     * @param charString searching char sequence
     * @return filtered follower list
     */
    public static ArrayList<App> searchAppsFilter(List<App> list, String charString) {
        ArrayList<App> filteredTempList = new ArrayList<>();
        for (App app : list) {
            if (app != null ) {
                // Filter by user name and user id
                if (containsIgnoreCase(app.getLabel(), charString)
                        || containsIgnoreCase(String.valueOf(app.getApplicationPackageName()), charString)) {
                    filteredTempList.add(app);
                }
            }
        }
        return filteredTempList;
    }

    /**
     * Search if substring has char sequence in source string ignore case
     * @param src source string
     * @param charString substring for searching
     * @return true if has coincidence
     */
    public static boolean containsIgnoreCase(String src, String charString) {
        final int length = charString.length();
        if (length == 0) {
            return true;
        }
        for (int i = src.length() - length; i >= 0; i--) {
            if (src.regionMatches(true, i, charString, 0, length)) {
                return true;
            }
        }
        return false;
    }

    public static int findPackageName(ArrayList<App> appList, String packageName) {
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

    public static String[] getDockAppPackageNames(Activity activity) {
        String[] dockAppPackageNames = null;

        SharedPreferences pref = activity.getSharedPreferences("app-data", Context.MODE_PRIVATE);

        try {
            JSONArray dockAppPackageNamesJson = new JSONArray(pref.getString("dock-app-package-names", "[]"));

            if (dockAppPackageNamesJson.length() == 4) {
                dockAppPackageNames = new String[4];

                for (int index = 0; index < 4; index++) {
                    dockAppPackageNames[index] = dockAppPackageNamesJson.getString(index);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dockAppPackageNames;
    }

    public static void saveDockAppPackageNames(Activity activity, String[] dockAppPackageNames) {
        if (dockAppPackageNames != null) {
            JSONArray dockAppPackageNamesJson = new JSONArray();

            for (String packageName : dockAppPackageNames) {
                dockAppPackageNamesJson.put(packageName);
            }

            SharedPreferences pref = activity.getSharedPreferences("app-data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("dock-app-package-names", dockAppPackageNamesJson.toString());
            editor.commit();
        }
    }

    public static App findApp(ArrayList<App> appsList, String packageName) {
        App app = null;

        for (App tempApp : appsList) {
            if (tempApp.getApplicationPackageName().equals(packageName)) {
                app = tempApp;
                break;
            }
        }

        return app;
    }

//    public static int findPackageName(ArrayList<App> appList, String packageName) {
//        int id = -1;
//
//        int count = appList.size();
//        for (int index = 0; index < count; index++) {
//            if (appList.get(index).getApplicationPackageName().equals(packageName)) {
//                id = index;
//                break;
//            }
//        }
//
//        return id;
//    }
}

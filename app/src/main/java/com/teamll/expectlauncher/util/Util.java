package com.teamll.expectlauncher.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.teamll.expectlauncher.model.App;

import java.util.ArrayList;
import java.util.List;

public class Util {
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

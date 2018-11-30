package com.teamll.expectlauncher.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.teamll.expectlauncher.model.AppDetail;

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
    public static ArrayList<AppDetail> searchAppsFilter(List<AppDetail> list, String charString) {
        ArrayList<AppDetail> filteredTempList = new ArrayList<>();
        for (AppDetail app : list) {
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
}

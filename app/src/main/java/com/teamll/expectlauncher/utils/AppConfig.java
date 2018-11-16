package com.teamll.expectlauncher.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.teamll.expectlauncher.model.Rectangle;

public class AppConfig {
    public enum  APP_ICON_SIZE {
        SMALLER,
        SMALL,
        NORMAL,
        LARGE,
        LARGER
    }
    public enum APP_DRAWER_CONFIG_MODE {
        NORMAL,
        MOVABLE_APP_ICON,
        APP_ICON_EDITOR
    }

    public APP_ICON_SIZE mSizeType;
    public APP_DRAWER_CONFIG_MODE mConfigMode;

}

package com.teamll.expectlauncher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

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
    public IconEditorConfig getIconConfig() {
        IconEditorConfig iec = new IconEditorConfig();
        iec.mShapedType = mPreferences.getInt(IconEditorConfig.SHAPE_TYPE,0);
        iec.mWhiteBackground = mPreferences.getBoolean(IconEditorConfig.WHITE_BACKGROUND,true);
        iec.mPadding = mPreferences.getFloat(IconEditorConfig.PADDING,4.0f/62);
        iec.mAutoTextColor = mPreferences.getBoolean(IconEditorConfig.AUTO_TEXT_COLOR,true);
        iec.mTextColor = mPreferences.getInt(IconEditorConfig.TEXT_COLOR, Color.WHITE);
        iec.mCornerRadius = mPreferences.getFloat(IconEditorConfig.CORNER_RADIUS,6/31.0f);
        return iec;
    }
    public static class IconEditorConfig {

        public static String SHAPE_TYPE ="ic_shape_type";
        public static String WHITE_BACKGROUND = "ic_white_background";
        public static String PADDING = "ic_padding";
        public static String TEXT_COLOR="ic_text_color";
        public static String AUTO_TEXT_COLOR = "ic_auto_text_color";
        public static String CORNER_RADIUS ="ic_corner_radius";

        int mShapedType;
        boolean mWhiteBackground;
        float mPadding; //(0 ->1)
        int mTextColor;
        boolean mAutoTextColor;
        float mCornerRadius; // (0 -> 1)
        private IconEditorConfig() {

        }

        public int getShapedType() {
            return mShapedType;
        }

        public IconEditorConfig setShapedType(int mShapedType) {
            this.mShapedType = mShapedType;
            return this;
        }

        public boolean isWhiteBackground() {
            return mWhiteBackground;
        }

        public void setWhiteBackground(boolean mWhiteBackground) {
            this.mWhiteBackground = mWhiteBackground;
        }

        public float getPadding() {
            return mPadding;
        }

        public IconEditorConfig setPadding(float mPadding) {
            this.mPadding = mPadding;
            return this;
        }
        public IconEditorConfig applyPadding(float mPadding) {
            setPadding(mPadding);
            final SharedPreferences.Editor editor = mPreferences.edit();
            editor.putFloat(IconEditorConfig.PADDING,mPadding);
            editor.apply();
            return this;
        }


        public int getTextColor() {
            return mTextColor;
        }

        public IconEditorConfig setTextColor(int mTextColor) {
            this.mTextColor = mTextColor;
            return this;
        }

        public float getCornerRadius() {
            return mCornerRadius;
        }

        public IconEditorConfig setCornerRadius(float mCornerRadius) {
            this.mCornerRadius = mCornerRadius;
            return this;
        }
        public IconEditorConfig applyAll() {
            final  SharedPreferences.Editor editor = mPreferences.edit();

            editor.putInt(IconEditorConfig.SHAPE_TYPE, mShapedType);
            editor.putBoolean(IconEditorConfig.WHITE_BACKGROUND,mWhiteBackground);
            editor.putFloat(IconEditorConfig.PADDING,mPadding);
            editor.putBoolean(IconEditorConfig.AUTO_TEXT_COLOR,mAutoTextColor);
            editor.putInt(IconEditorConfig.TEXT_COLOR, mTextColor);
            editor.putFloat(IconEditorConfig.CORNER_RADIUS,mCornerRadius);
            editor.apply();
            return this;
        }
    }

}

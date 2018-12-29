package com.teamll.expectlauncher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.system.Os;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppFolder;
import com.teamll.expectlauncher.model.AppInstance;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public final class PreferencesUtility {
    private static final String TAG ="PreferencesUtility";

    private final static String SHOW_APP_TITLE = "show_app_title";

    private final static String APP_ICON_SIZE ="app_icon_size";
    private final static String FONT_TITLE_SIZE = "font_title_size";
    private final static String SAVED_APP_INSTANCES ="saved_app_instances";

    private final static String APP_POSITION = "app_position";


    private SharedPreferences mPreferences;

    private ConnectivityManager connManager = null;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

    public int getFontTitleSize(Context context) {
        return mPreferences.getInt(FONT_TITLE_SIZE,context.getResources().getInteger(R.integer.font_title_size_integer));
    }
    public void setFontTitleSize(int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        if(value<1f) value = 1;
        editor.putInt(FONT_TITLE_SIZE, value);
        editor.apply();
    }
    public AppInstance[] getSavedAppInstance() {
        String data = mPreferences.getString(SAVED_APP_INSTANCES,"");
        if(data==null||data.isEmpty()) return new AppInstance[0];
        AppInstance[] array = new GsonBuilder().create().fromJson(data,AppInstance[].class);
        Arrays.sort(array, (o1, o2) -> o1.getIndex()-o2.getIndex());
        return array;
    }
    public static int findAppInstancesIfAny(AppInstance[] data,String pkg) {
        for (int i = 0; i < data.length; i++) {
            if(pkg.equals(data[i].getPackageName())) return i;
        }
        return -1;
    }
    public static App findAppByInstance(ArrayList<App> data, AppInstance instance){
        int size = data.size();
        int index = instance.getIndex();
        if(index<size&&data.get(index).getApplicationPackageName().equals(instance.getPackageName())) return data.get(index);
        return findByCodeIsIn(data,instance.getPackageName());
    }
    public static App findByCodeIsIn(Collection<App> listCarnet, String codeIsIn) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return listCarnet.stream().filter(carnet -> codeIsIn.equals(carnet.getApplicationPackageName())).findFirst().orElse(null);
        } else {
            for (App app : listCarnet) {
                if (app.getApplicationPackageName().equals(codeIsIn)) return app;
            }
            return null;
        }
    }

        public AppInstance createAppInstance(App app,int index) {
        AppInstance appInstance = new AppInstance();
        appInstance.setIndex(index);
        appInstance.setPackageName(app.getApplicationPackageName());

        int[] color = Util.getAverageColor(app);
        appInstance.setBackground1(color[0]);
        appInstance.setCustomBackground(color[0]);
        appInstance.setBackground2(color[1]);

        appInstance.setPadding(2/31f);

        appInstance.setApps(new ArrayList<>());

        if(app instanceof AppFolder) appInstance.setIsFolder(true);

        return appInstance;
    }
    public void saveAppInstance(ArrayList<App> data) {
        ArrayList<AppInstance> app = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            App a = data.get(i);
            AppInstance instance = a.getAppSavedInstance();
            instance.setIndex(i);
            app.add(a.getAppSavedInstance());
        }
        Type listType = new TypeToken<ArrayList<AppInstance>>() {}.getType();
        String json = new GsonBuilder().create().toJson(app,listType);
      //  Log.d(TAG, "saveAppInstance: "+json);
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SAVED_APP_INSTANCES,json);
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
    public ArrayList<Integer> getWidgetLists()
    {
        ArrayList<Integer> list = new ArrayList<>();
        String data =  mPreferences.getString("widget_list","");
        if(data.isEmpty()) return list;

            try {

                JSONArray appsJson = new JSONArray(data);
                int count = appsJson.length();
                for (int index = 0; index < count; index++) {
                    list.add(appsJson.getInt(index));
                }

            } catch (JSONException e) {
                e.printStackTrace();
        }

        return list;
    }
    public void savedWidgetLists(int[] list) {
        final  SharedPreferences.Editor editor = mPreferences.edit();
        JSONArray appsJson = new JSONArray();
        int count = list.length;
        for (int index = 0; index < count; index++) {
                appsJson.put(list[index]);
        }

        editor.putString("widget_list", appsJson.toString());
        editor.apply();
    }
    public void savedWidgetLists(ArrayList<Integer> list) {
        final  SharedPreferences.Editor editor = mPreferences.edit();
        JSONArray appsJson = new JSONArray();
        int count = list.size();
        for (int index = 0; index < count; index++) {
            appsJson.put(list.get(index));
        }

        editor.putString("widget_list", appsJson.toString());
        editor.apply();
    }

    private IconEditorConfig iec;
    public IconEditorConfig getIconConfig() {
        if(iec !=null) return this.iec;
        iec = new IconEditorConfig();
        iec.mShapedType = mPreferences.getInt(IconEditorConfig.SHAPE_TYPE,2); // 0 mean normal, 1 mean white square, 2 mean color square
        iec.mWhiteBackground = mPreferences.getBoolean(IconEditorConfig.WHITE_BACKGROUND,true);
        iec.mPadding = mPreferences.getFloat(IconEditorConfig.PADDING,4.0f/62);
        iec.mAutoTextColor = mPreferences.getBoolean(IconEditorConfig.AUTO_TEXT_COLOR,true);
        iec.mTextColor = mPreferences.getInt(IconEditorConfig.TEXT_COLOR, 0); // 0 means auto, 1 means white, 2 means black
        iec.mCornerRadius = mPreferences.getFloat(IconEditorConfig.CORNER_RADIUS,6/31.0f);
        return iec;
    }
    public static class IconEditorConfig {

        static String SHAPE_TYPE ="ic_shape_type";
        static String WHITE_BACKGROUND = "ic_white_background";
        static String PADDING = "ic_padding";
        static String TEXT_COLOR="ic_text_color";
        static String AUTO_TEXT_COLOR = "ic_auto_text_color";
        static String CORNER_RADIUS ="ic_corner_radius";

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
            final SharedPreferences.Editor editor = ExpectLauncher.getInstance().getPreferencesUtility().mPreferences.edit();
            editor.putFloat(IconEditorConfig.PADDING,mPadding);
            editor.apply();
            return this;
        }


        public int getTitleColorType() {
            return mTextColor;
        }

        public IconEditorConfig setTitleColorType(int mTextColor) {
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
            final  SharedPreferences.Editor editor = ExpectLauncher.getInstance().getPreferencesUtility().mPreferences.edit();

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

package com.teamll.expectlauncher.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppsLoader;

import java.util.ArrayList;

public abstract class AppLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<App>>  {
    private static final String TAG ="AppLoaderActivity";


    private ArrayList<AppDetailReceiver> listeners = new ArrayList<>();
    private ArrayList<App> appData = new ArrayList<>();
    public void addAppDetailReceiver(AppDetailReceiver receiver) {
        if(!listeners.contains(receiver)) {
            listeners.add(receiver);
           if(appData.size()!=0) receiver.onLoadComplete(appData);
        }
    }
    public void removeAppDetailReceiver(AppDetailReceiver receiver) {
        if(listeners.contains(receiver)) {

            listeners.remove(receiver);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0, null,  this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.launcher_scale_in,R.anim.app_exit);

    }


    @NonNull
    @Override
    public Loader<ArrayList<App>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AppsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<App>> loader, ArrayList<App> appDetails) {
        appData.clear();
        appData.addAll(appDetails);

        for (AppDetailReceiver receiver: listeners) {
            receiver.onLoadComplete(appData);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<App>> loader) {
        appData.clear();
        for (AppDetailReceiver receiver: listeners) {
            receiver.onLoadReset();
        }
        Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show();
    }

    public interface AppDetailReceiver {
        void onLoadComplete(ArrayList<App> data);
        void onLoadReset();
    }
    public static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        public MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
    public void onOpenPreference(View v, App app) {
        Toast.makeText(this,"Preference will be available soon...",Toast.LENGTH_SHORT).show();
    }
    public void openApp(View v, App app) {
        if (app != null) {
            if(app.getApplicationPackageName().equals(getResources().getString(R.string.package_name))) {
                onOpenPreference(v,app);
                return;
            }

            Intent intent =getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());
            if (intent != null) {
                final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bound);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
                myAnim.setInterpolator(interpolator);
               startActivityType2(v,intent);
                v.startAnimation(myAnim);
                 //   root.startAnimation(myAnim);
            }
        }
    }
    public void startActivityType1(View v,Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);

    }
    public static final String KEY_ANIM_START_X = "android:activity.animStartX";

    /**
     * Start Y position of thumbnail animation.
     * @hide
     */
    public static final String KEY_ANIM_START_Y = "android:activity.animStartY";

    /**
     * Initial width of the animation.
     * @hide
     */
    public static final String KEY_ANIM_WIDTH = "android:activity.animWidth";

    /**
     * Initial height of the animation.
     * @hide
     */
    public static final String KEY_ANIM_HEIGHT = "android:activity.animHeight";

    public void startActivityType2(View v,Intent intent) {
        int left = 0, top = 0;
        int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v,left,top,width,height);

        final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bound);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
        myAnim.setInterpolator(interpolator);
        Bundle b= options.toBundle();

        Log.d(TAG, "startActivityType2: startX = "+b.getInt(KEY_ANIM_START_X,-1)+", startY = "+b.getInt(KEY_ANIM_START_Y,-1));

//        b.remove(KEY_ANIM_START_X);
//        b.remove(KEY_ANIM_START_Y);
//        b.putInt(KEY_ANIM_START_X, 700);
//        b.putInt(KEY_ANIM_START_Y, 0);

        startActivity(intent, b);

        v.startAnimation(myAnim);

    }

}

package com.teamll.expectlauncher.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.model.AppsLoader;

import java.util.ArrayList;

public abstract class AppLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<App>>  {
    private ArrayList<AppDetailReceiver> listeners = new ArrayList<>();
    private ArrayList<App> appData = new ArrayList<>();
    public void addAppDetailReceiver(AppDetailReceiver receiver) {
        if(!listeners.contains(receiver)) {
            listeners.add(receiver);
           if(appData.size()!=0) receiver.onLoadComplete(appData);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(0, null,  this);
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
    public void openApp(View v, AppDetail appDetail) {
        if (appDetail != null) {
            Intent intent =getPackageManager().getLaunchIntentForPackage(appDetail.getApplicationPackageName());
            if (intent != null) {
                int left = 0, top = 0;
                int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v,left,top,width,height);
                // getActivity().overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bound);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
                myAnim.setInterpolator(interpolator);
                startActivity(intent, options.toBundle());
                v.startAnimation(myAnim);
                 //   root.startAnimation(myAnim);
            }
        }
    }

}

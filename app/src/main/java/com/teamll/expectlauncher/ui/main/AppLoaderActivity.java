package com.teamll.expectlauncher.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.teamll.expectlauncher.model.App;
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

}

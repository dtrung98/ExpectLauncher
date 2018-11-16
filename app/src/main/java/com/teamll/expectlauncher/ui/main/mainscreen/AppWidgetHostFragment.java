package com.teamll.expectlauncher.ui.main.mainscreen;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.teamll.expectlauncher.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class AppWidgetHostFragment extends Fragment {
    private static final String TAG="AppWidgetHostFragment";
    public static final int REQUEST_1 = 19999;

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;
   public void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_1);
    }
   public void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == REQUEST_1) {
                configureWidget(data);
            }
            else if (requestCode == getResources().getInteger(R.integer.REQUEST_CREATE_APPWIDGET)) {
                createWidget(data);
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent,  getResources().getInteger(R.integer.REQUEST_CREATE_APPWIDGET));
        } else {
            createWidget(data);
        }
    }
    public LinearLayout widgetContainer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        widgetContainer = view.findViewById(R.id.widget_container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppWidgetManager = AppWidgetManager.getInstance(getActivity());
        mAppWidgetHost = new AppWidgetHost(getActivity(), getResources().getInteger(R.integer.APPWIDGET_HOST_ID));

    }

    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(getActivity(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        widgetContainer.addView(hostView);

        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();

    }
    /**
     * Stop listen for updates for our widgets (saving battery).
     */
    @Override
    public void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    /**
     * Removes the widget displayed by this AppWidgetHostView.
     */
    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        widgetContainer.removeView(hostView);
    }

    public void removeWidgetMenuSelected() {
        int childCount = widgetContainer.getChildCount();
        if (childCount > 1) {
                View view = widgetContainer.getChildAt(childCount - 1);
            if (view instanceof AppWidgetHostView) {
                removeWidget((AppWidgetHostView) view);
                Toast.makeText(getActivity(), "Widget removed", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(getActivity(), "No widget popup", Toast.LENGTH_SHORT).show();
    }

}

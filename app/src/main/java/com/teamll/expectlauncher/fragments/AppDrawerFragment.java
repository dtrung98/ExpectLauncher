package com.teamll.expectlauncher.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.adapters.AppListAdapterForRecyclerView;
import com.teamll.expectlauncher.helper.OnStartDragListener;
import com.teamll.expectlauncher.helper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.others.AppModel;
import com.teamll.expectlauncher.others.AppsLoader;
import com.teamll.expectlauncher.others.CustomItemDecoration;

import java.util.ArrayList;

public class AppDrawerFragment extends Fragment implements AppListAdapterForRecyclerView.ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<AppModel>>, OnStartDragListener {
    View rootView;
    Activity activity;
    AppListAdapterForRecyclerView mAdapter;
    RecyclerView appRecyclerView;
    ItemTouchHelper mItemTouchHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.app_list_fragment,container,false);
        appRecyclerView = rootView.findViewById(R.id.appListRecyclerView);
        //  setEmptyText("No Applications");

        setAdapterForRecyclerView();
        setLayoutManager();
        setDraggable();

        return rootView;
    }

    private void setAdapterForRecyclerView() {
        mAdapter = new AppListAdapterForRecyclerView(activity,null,this);
        appRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
    }

    private void setLayoutManager() {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;

        Resources resources = getResources();
        float appWidth = resources.getDimension(R.dimen.app_width);
        float appHeight = resources.getDimension(R.dimen.app_height);

        float minWidthZone = appWidth*1.2f;
        float minHeightZone = appHeight*1.1f;

        int numberColumn = (int) (screenWidth/minWidthZone);
        int numberRow = (int) (screenHeight/minHeightZone);
        float widthMargin = (screenWidth - numberColumn*appWidth)/(numberColumn*2);
        float heightMargin = (screenHeight - numberRow*appHeight)/(numberRow*2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,numberColumn,GridLayoutManager.VERTICAL,false);
        appRecyclerView.setLayoutManager(gridLayoutManager);

       // SnapHelper snapHelper = new LinearSnapHelper();
       // snapHelper.attachToRecyclerView(appRecyclerView);

        CustomItemDecoration itemDecoration = new CustomItemDecoration((int) widthMargin,(int)widthMargin);
        appRecyclerView.addItemDecoration(itemDecoration);
    }

    private void setDraggable() {
        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(appRecyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public static int calculateNoOfColumns(Context context, float appWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
       // float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (displayMetrics.widthPixels / appWidth);
        return noOfColumns;
    }

    @NonNull
    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        return new AppsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        mAdapter.setData(apps);

        if (isResumed()) {
            //setListShown(true);
        } else {
          //  setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onItemClick(View view, AppModel app) {
        if (app != null) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());

            if (intent != null) {
                Activity activity = getActivity();
                int left = 0, top = 0;
                int width = view.getMeasuredWidth(), height = view.getMeasuredHeight();
              //  getActivity().startActivity(intent);
              //  getActivity().overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                ActivityOptions options ;
                     //   ActivityOptions.makeCustomAnimation(activity, R.anim.zoom_in, R.anim.zoom_out);
              //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             //      options = ActivityOptions.makeClipRevealAnimation(view,left,top,width,height);
              //  } else {
                   options = ActivityOptions.makeScaleUpAnimation(view,left,top,width,height);
              //  }
                activity.startActivity(intent, options.toBundle());
                  getActivity().overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
            }
        }
    }

}

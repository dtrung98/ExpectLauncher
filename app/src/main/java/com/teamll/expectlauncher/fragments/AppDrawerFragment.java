package com.teamll.expectlauncher.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.adapters.AppListAdapter;
import com.teamll.expectlauncher.helper.OnStartDragListener;
import com.teamll.expectlauncher.helper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.others.AppModel;
import com.teamll.expectlauncher.others.AppsLoader;
import com.teamll.expectlauncher.others.CustomItemDecoration;
import com.teamll.expectlauncher.ultilities.Tool;

import java.util.ArrayList;

public class AppDrawerFragment extends Fragment implements AppListAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<AppModel>>, OnStartDragListener {
    View rootView;
    Activity activity;
    AppListAdapter mAdapter;
    RecyclerView recyclerView;
    ItemTouchHelper mItemTouchHelper;

    float statusBarHeight  = 0;
    float navigationHeight = 0;
    float oneDp = 0;

    private void setScreenProperties(){

        oneDp = Tool.getOneDps(getActivity());
        statusBarHeight = Tool.getStatusHeight(getResources());
        navigationHeight = Tool.getNavigationHeight(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.app_drawer_fragment,container,false);
        recyclerView = rootView.findViewById(R.id.appListRecyclerView);
        //  setEmptyText("No Applications");
        setScreenProperties();
        setAdapterForRecyclerView();
        setLayoutManager();
        setDraggable();

        return rootView;
    }


    private void setAdapterForRecyclerView() {
        mAdapter = new AppListAdapter(activity,null,this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
    }

    private void setLayoutManager() {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float padding = activity.getResources().getDimension(R.dimen.padding);
        padding = (statusBarHeight>padding) ? statusBarHeight: padding;
        padding = (navigationHeight>padding)? navigationHeight : padding;
        float screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;
       // recyclerView.setPadding((int)padding,(int)padding,(int)padding,(int)padding);
        Resources resources = getResources();
        float appWidth = resources.getDimension(R.dimen.app_width);
        float appHeight = resources.getDimension(R.dimen.app_height);

        float minWidthZone = appWidth*1.4f;
        float minHeightZone = appHeight*1.35f;

        int numberColumn = (int) (screenWidth/minWidthZone);
        int numberRow = (int) (screenHeight/minHeightZone);
        float horizontalMargin = (screenWidth - numberColumn*appWidth)/(numberColumn+1);
        float verticalMargin = (screenHeight - numberRow*appHeight)/(numberRow+1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,numberColumn,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

       // SnapHelper snapHelper = new LinearSnapHelper();
       // snapHelper.attachToRecyclerView(recyclerView);

        CustomItemDecoration itemDecoration = new CustomItemDecoration(screenWidth, screenHeight,numberColumn,numberRow,(int) (verticalMargin*0.9f),(int)(horizontalMargin*0.9f));
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void setDraggable() {
        // create the loader to load the apps list in background
        getLoaderManager().initLoader(0, null, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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

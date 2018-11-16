package com.teamll.expectlauncher.ui.main.appdrawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.bottomsheet.RoundedBottomSheetDialogFragment;
import com.teamll.expectlauncher.ui.widgets.DarkenRoundedBackgroundFrameLayout;
import com.teamll.expectlauncher.ui.widgets.EventWatchableFrameLayout;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.widgets.BoundItemDecoration;
import com.teamll.expectlauncher.utils.Animation;
import com.teamll.expectlauncher.utils.Tool;

import java.util.ArrayList;

public class AppDrawerFragment extends Fragment implements AppDrawerAdapter.ItemClickListener, OnStartDragListener, AppLoaderActivity.AppDetailReceiver, LayoutSwitcher.EventSender, RoundedBottomSheetDialogFragment.BottomSheetListener {
    private static final String TAG="AppDrawerFragment";

    /**
     * FrameLayout cho phép nó phát sinh sự kiện chạm trước tòan bộ view con của nó.
     * Khác với sự kiện chạm bình thường - phát sinh khi KHÔNG CÓ view con nào xử lý.
     *
     **/
    EventWatchableFrameLayout rootView;

    /**
    Activity sở hữu fragment
     */
    Activity activity;
    /**
    Adapter của recyclerView
     */
    public AppDrawerAdapter mAdapter;

    RecyclerView recyclerView;
    public MotionRoundedBitmapFrameLayout recyclerParent;
    /*
    ViewGroup chứa fragment hiện tại
     */
    FrameLayout container;

    ItemTouchHelper mItemTouchHelper;

    float statusBarHeight  = 0;
    float navigationHeight = 0;
    float oneDp = 0;
    float dockHeight;
    float dockMargin;
    Rectangle rect;

    FrameLayout.LayoutParams params;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        activity = getActivity();
        statusBarHeight= Tool.getStatusHeight(activity.getResources());
        navigationHeight = Tool.getNavigationHeight(activity);

        Resources r = getResources();
        dockHeight = r.getDimension(R.dimen.dock_height);
        dockMargin = r.getDimension(R.dimen.dock_margin);
        container = activity.findViewById(R.id.container);
        super.onCreate(savedInstanceState);
       }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_drawer_fragment,container,false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootView = (EventWatchableFrameLayout) view;

        /**
         * Vị trí mặc định của AppDrawer
         */
        int[] size = Tool.getScreenSize(getContext());
        rect = new Rectangle(size[0],size[1]);
        rect.Left = 0;
        rect.Top = rect.Height;
        updateLayout();
        recyclerParent = rootView.findViewById(R.id.parentOfRecycleView);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerParent.getLayoutParams();
        lp.topMargin += (int) statusBarHeight;
        lp.height = (int) (rect.Height - lp.bottomMargin - navigationHeight - dockMargin -dockHeight - lp.topMargin);
        lp.bottomMargin = 0;
        recyclerParent.requestLayout();
        recyclerParent.setBackGroundColor(0xFFEEEEEE);
        recyclerParent.setRoundNumber(1.75f,true);
        Tool.getInstance().AddWallpaperChangedNotifier((Tool.WallpaperChangedNotifier) recyclerParent);

        recyclerView = recyclerParent.findViewById(R.id.recyclerview);

        mAdapter = new AppDrawerAdapter(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setLayoutManager();
     //   setDraggable();


        /**
         * Đăng ký bộ lắng nghe việc load app cho đối tượng này.
         */
        AppLoaderActivity ac = (AppLoaderActivity)getActivity();
        if(ac!=null)
        ac.addAppDetailReceiver(this);
         else throw new NullPointerException("Fragment was created from an activity class that doesn't inherit from AppLoaderActivity class");
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

        BoundItemDecoration itemDecoration = new BoundItemDecoration(screenWidth, screenHeight,numberColumn,numberRow,(int) (verticalMargin*0.9f),(int)(horizontalMargin*0.9f));
        recyclerView.addItemDecoration(itemDecoration);
    }


    private void setDraggable() {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(View view, AppDetail app) {
        openApp(view,app);
    }

    public void updateLayout() {
        if(params==null) {
            params = new FrameLayout.LayoutParams(
            rect.Width,rect.Height);
            params.topMargin = rect.Top;
            params.leftMargin = rect.Left;
            rootView.setLayoutParams(params);
        } else {
            params.leftMargin = rect.Left;
            params.topMargin = rect.Top;
            params.width = rect.Width;
            params.height = rect.Height;
            rootView.requestLayout();
        }
    }

    public void setAppData(ArrayList<App> data) {
        if(mAdapter!=null) mAdapter.setData(data);
    }

    void openApp(View v, AppDetail appDetail) {
        ((AppLoaderActivity)getActivity()).openApp(v,appDetail);
    }



    @Override
    public void onLoadComplete(ArrayList<App> data) {
        setAppData(data);
    }

    @Override
    public void onLoadReset() {
      setAppData(null);
    }

    @Override
    public View getRoot() {
        return rootView;
    }

    @Override
    public View getEventSenderView() {
        return recyclerView;
    }

    @Override
    public boolean onClickButtonInsideBottomSheet(int id) {
        return false;
    }
}

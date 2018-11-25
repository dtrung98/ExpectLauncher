package com.teamll.expectlauncher.ui.main.appdrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.bottomsheet.RoundedBottomSheetDialogFragment;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.widgets.BoundItemDecoration;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.OnRangeChangedListener;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.utils.PreferencesUtility;
import com.teamll.expectlauncher.utils.Tool;

import java.util.ArrayList;

public class AppDrawerFragment extends Fragment implements AppDrawerAdapter.ItemClickListener, OnStartDragListener, AppLoaderActivity.AppDetailReceiver, LayoutSwitcher.EventSender, RoundedBottomSheetDialogFragment.BottomSheetListener, OnRangeChangedListener {
    private static final String TAG="AppDrawerFragment";

    FrameLayout rootView;

    /**
    Activity sở hữu fragment
     */
    Activity activity;
    /**
    Adapter của mRecyclerView
     */
    public AppDrawerAdapter mAdapter;

    public RecyclerView mRecyclerView;
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

    public MotionRoundedBitmapFrameLayout search_bar;
    FrameLayout.LayoutParams params;

    public ImageView search_image_view;
    public Bitmap black_search_icon, white_search_icon;
    public TextView search_text_view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
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
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.app_drawer_fragment,container,false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootView = (FrameLayout) view;

        /**
         * Vị trí mặc định của AppDrawer
         */
        int[] size = Tool.getScreenSize(getContext());
        rect = new Rectangle(size[0],size[1]);
        rect.Left = 0;
        rect.Top = rect.Height;
        updateLayout();
        search_bar = rootView.findViewById(R.id.search_bar);
        recyclerParent = rootView.findViewById(R.id.parentOfRecycleView);

        search_image_view = recyclerParent.findViewById(R.id.search_icon);
        search_text_view = recyclerParent.findViewById(R.id.search_text_view);
        black_search_icon = BitmapFactory.decodeResource(getResources(),R.drawable.search);
        white_search_icon = BitmapFactory.decodeResource(getResources(),R.drawable.search_white);


        FrameLayout.LayoutParams rPParams = (FrameLayout.LayoutParams) recyclerParent.getLayoutParams();
        rPParams.topMargin += (int) statusBarHeight;
        rPParams.height = (int) (rect.Height - rPParams.bottomMargin - navigationHeight /*-dockHeight  - dockMargin */ - rPParams.topMargin);
        rPParams.bottomMargin = 0;
        recyclerParent.requestLayout();
        recyclerParent.setBackGroundColor(0xFFEEEEEE);
        recyclerParent.setRoundNumber(1.75f,true);
        Tool.getInstance().AddWallpaperChangedNotifier( recyclerParent);
        Tool.getInstance().AddWallpaperChangedNotifier(search_bar);
        mRecyclerView = recyclerParent.findViewById(R.id.recyclerview);

        mAdapter = new AppDrawerAdapter(getActivity(),this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setLayoutManager();
        setDraggable();

        /**
         * Đăng ký bộ lắng nghe việc load app cho đối tượng này.
         */
        AppLoaderActivity ac = (AppLoaderActivity)getActivity();
        if(ac!=null)
        ac.addAppDetailReceiver(this);
         else throw new NullPointerException("Fragment was created from an activity class that doesn't inherit from AppLoaderActivity class");

    }

    public void setLayoutManager() {
        Resources resources = getResources();
        float margin = resources.getDimension(R.dimen.app_drawer_margin);
        float padding = resources.getDimension(R.dimen.recycler_view_padding);
        int[] ss = Tool.getScreenSize(getActivity());
        float mRVContentWidth = ss[0] - margin - padding;
        float mRVContentHeight = recyclerParent.getLayoutParams().height
                - resources.getDimension(R.dimen.search_bar_height)
                - 2*resources.getDimension(R.dimen.search_bar_margin)
                - padding;
         float scale = PreferencesUtility.getInstance(getActivity().getApplicationContext()).getAppIconSize();
        float appWidth = resources.getDimension(R.dimen.app_width)*scale;
        float appHeight = resources.getDimension(R.dimen.app_height)*scale;
        if(!PreferencesUtility.getInstance(getActivity().getApplicationContext()).isShowAppTitle())
            appHeight = appWidth;
        float minWidthZone = appWidth*1.4f;
        float minHeightZone = appHeight*1.35f;

        int numberColumn = (int) (mRVContentWidth/minWidthZone);
        int numberRow = (int) (mRVContentHeight/minHeightZone);
        float horizontalMargin = (mRVContentWidth - numberColumn*appWidth)/(numberColumn+1);
        float verticalMargin = (mRVContentHeight - numberRow*appHeight)/(numberRow+1);
        if(numberColumn<1) numberColumn = 1;
        if(numberRow<1) numberRow = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity,numberColumn,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        BoundItemDecoration itemDecoration = new BoundItemDecoration(mRVContentWidth, mRVContentHeight,numberColumn,numberRow,(int) (verticalMargin*0.9f),(int)(horizontalMargin*0.9f));
        mRecyclerView.addItemDecoration(itemDecoration);
    }
    public void setAppIconSize(float scale) {
        PreferencesUtility.getInstance(getActivity().getApplicationContext()).setAppIconSize(scale);
        mAdapter.notifyDataSetChanged();
        setLayoutManager();
    }

    private void setDraggable() {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(View view, AppDetail app) {
        openApp(view,app);
    }

    @Override
    public void onItemLongPressed(View view, AppDetail appDetail) {
        RoundedBottomSheetDialogFragment fragment =  RoundedBottomSheetDialogFragment.newInstance(LayoutSwitcher.MODE.IN_APP_DRAWER);

        fragment.setAppDrawer(this);
        fragment.show(getActivity().getSupportFragmentManager(),
                "song_popup_menu");
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
    void showHideAppTitle(View v) {
       PreferencesUtility pu =  PreferencesUtility.getInstance(getActivity().getApplicationContext());
       boolean isShow = !pu.isShowAppTitle();
       pu.setShowAppTitle(
               isShow
        );
       mAdapter.notifyDataSetChanged();
      updateShowHideTitleButton(v,isShow);
    }
    private void updateShowHideTitleButton(View v,boolean isShow) {
        FloatingActionButton fab = (FloatingActionButton) v;
        TextView tv = ((ViewGroup)v.getParent()).findViewById(R.id.show_title_text);
        if(null==tv) return;
        if(isShow) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.FloatingButtonColor)));
            tv.setText(R.string.visible_app_title);
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            tv.setText(R.string.hidden_app_title);
        }
        setLayoutManager();
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
    public void onClickButtonInsideBottomSheet(View v) {
        switch (v.getId()) {
            case R.id.show_title:
                showHideAppTitle(v);
                return;
            case R.id.position:
                mAdapter.switchMode(AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON);
                return;
            case R.id.app_icon_editor:
                mAdapter.switchMode(AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.APP_ICON_EDITOR);
        }
    }

    @Override
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
        setAppIconSize(leftValue/100);
    }

    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }
}

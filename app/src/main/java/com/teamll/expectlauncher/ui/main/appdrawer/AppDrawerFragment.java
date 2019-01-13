package com.teamll.expectlauncher.ui.main.appdrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.MainActivity;
import com.teamll.expectlauncher.ui.main.bottomsheet.EditAppConfigBottomSheet;
import com.teamll.expectlauncher.ui.main.bottomsheet.MoreSettingBottomSheet;
import com.teamll.expectlauncher.ui.main.bottomsheet.CommonSettingBottomSheet;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.ui.main.setting.DashBoardSetting;
import com.teamll.expectlauncher.ui.widgets.BoundItemDecoration;
import com.teamll.expectlauncher.ui.widgets.DarkenRoundedBackgroundFrameLayout;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.CustomItemTouchHelper;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.ui.widgets.numberpicker.NumberPicker;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.OnRangeChangedListener;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppDrawerFragment extends Fragment implements View.OnClickListener,EditAppConfigBottomSheet.UpdateItemListener,
                                                            AppDrawerAdapter.ItemClickListener,
                                                            OnStartDragListener,
        AppLoaderActivity.AppsReceiver,
                                                           LayoutSwitcher.EventSender,
                                                           CommonSettingBottomSheet.BottomSheetListener,
                                                           OnRangeChangedListener,
                                                            NumberPicker.OnValueChangeListener,
                                                           SearchView.OnQueryTextListener, MoreSettingBottomSheet.AppEditorCallBack,
                                                            Tool.WallpaperChangedNotifier {

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

   @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
   @BindView(R.id.recycler_view_parent) public FrameLayout mRecyclerViewParent;

   /**
    ViewGroup gốc mà fragment được thêm vào
    ContainerView được lấy reference thông qua activity
     */
    FrameLayout mContainerView;

    /**
     * Đối tượng helper xử lý cử chỉ đổi vị trí các ứng dụng
     */
    CustomItemTouchHelper mItemTouchHelper;

    /**
     * Chiều cao của thanh trạng thái
     */
    float statusBarHeight  = 0;

    /**
     * Chiều cao của thanh điều hướng ( nếu có)
     */
    float navigationHeight = 0;

    /**
     * Một DP bằng mấy PX
     */
    float oneDp = 0;

    /**
     * Chiều cao của thanh dock
     * AppDrawer cần biết giá trị này để tính toán vị trí phù hợp khi hiển thị lên màn hình
     */
    float dockHeight;

    /**
     * Viền ngoài của dock
     */
    float dockMargin;

    /**
     * Hình chữ nhật bao phủ App Drawer
     * Left - tương ứng vị trí X của App Drawer
     * Top - Tương tứng Y của App Drawer trên màn hình
     * ...
     */
    Rectangle rect;

    @BindView(R.id.search_back_ground) public FrameLayout mSearchBackGround;
    FrameLayout.LayoutParams params;

    @BindView(R.id.search_view) public SearchView mSearchView;
    private boolean recyclerMoving;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        statusBarHeight= Tool.getStatusHeight(activity.getResources());
        navigationHeight = Tool.getNavigationHeight(activity);

        Resources r = getResources();
        dockHeight = r.getDimension(R.dimen.dock_height);
        dockMargin = r.getDimension(R.dimen.dock_margin);
        mContainerView = activity.findViewById(R.id.container);
       }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_drawer_fragment,container,false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Bind View
        rootView = (FrameLayout) view;
        ButterKnife.bind(this,view);


        /**
         * Vị trí mặc định của AppDrawer
         */
        int[] size = Tool.getScreenSize(getContext());
        rect = new Rectangle(size[0],size[1]);
        rect.Left = 0;
        rect.Top = rect.Height;
        requestLayout();


        FrameLayout.LayoutParams rPParams = (FrameLayout.LayoutParams) mRecyclerViewParent.getLayoutParams();
        rPParams.topMargin += (int) statusBarHeight;
        rPParams.height = (int) (rect.Height - rPParams.bottomMargin - navigationHeight /*-dockHeight  - dockMargin */ - rPParams.topMargin);
        rPParams.bottomMargin = 0;
        mRecyclerViewParent.requestLayout();

        if(mRecyclerViewParent instanceof Tool.WallpaperChangedNotifier)  Tool.getInstance().AddWallpaperChangedNotifier((Tool.WallpaperChangedNotifier) mRecyclerViewParent);
        if(mSearchBackGround instanceof Tool.WallpaperChangedNotifier)  Tool.getInstance().AddWallpaperChangedNotifier((Tool.WallpaperChangedNotifier) mSearchBackGround);

        Tool.getInstance().AddWallpaperChangedNotifier(this);
        mAdapter = new AppDrawerAdapter(getActivity(),this);


        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setLayoutManager();
        initTouchHelper();

        /**
         * Đăng ký bộ lắng nghe việc load app cho đối tượng này.
         */
        ((AppLoaderActivity)getActivity()).addAppsReceiver(this);

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnClickListener(this);
        mSearchView.onActionViewExpanded();
        mSearchView.clearFocus();

        updateSearchViewTheme(true);
    }

    private void updateSearchViewTheme(boolean textColorWhite) {
        Log.d(TAG, "updateSearchViewTheme: "+textColorWhite);
        EditText searchEditText =  mSearchView.findViewById(R.id.search_src_text);
//        ImageView searchButton = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        if (searchEditText != null)
        {

          //  searchEditText.setGravity(Gravity.CENTER);
            if(textColorWhite) {
  //              searchEditText.setHintTextColor(Color.WHITE);
                searchEditText.setTextColor(Color.WHITE);
            } else {
//                searchEditText.setHintTextColor(Color.BLACK);
                searchEditText.setTextColor(Color.BLACK);
            }
        }
//        if(searchButton!=null) {
//            Log.d(TAG, "updateSearchViewTheme search icon detected");
//         //   if(textColorWhite) searchButton.setImageResource(R.drawable.white_search);
//           // else
//                searchButton.setImageResource(R.drawable.black_search);
//        }
        if(mSearchBackGround instanceof DarkenRoundedBackgroundFrameLayout) {
            if(textColorWhite) ((DarkenRoundedBackgroundFrameLayout)mSearchBackGround).setBackGroundColor(Color.BLACK);
            else ((DarkenRoundedBackgroundFrameLayout)mSearchBackGround).setBackGroundColor(Color.WHITE);
        }

    }

    private void initTouchHelper() {
        CustomItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new CustomItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onUpdate() {

        if(mAdapter!=null) mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateItem(int item) {
        if(mAdapter!=null) mAdapter.notifyItemChanged(item);
    }

    /**
     * Hàm này cài đặt GridLayoutManager cho mRecyclerView
     * Tính toán số lượng cột phù hợp để hiển thị vừa vặn độ rộng màn hình
     * Nó cũng tính toán margin giữa các biểu tượng sao cho hiển thị trông cách đều nhau
     * Gọi hàm này mỗi khi kích thước của biểu tượng ứng dụng bị thay đổi
     */
    public void setLayoutManager() {
        Resources resources = getResources();
        float margin = resources.getDimension(R.dimen.app_drawer_margin);
        float padding = resources.getDimension(R.dimen.recycler_view_padding);
        int[] ss = Tool.getScreenSize(getActivity());
        float mRVContentWidth = ss[0] - margin - padding;
        float mRVContentHeight = mRecyclerViewParent.getLayoutParams().height
                - resources.getDimension(R.dimen.recycler_view_margin_top) - resources.getDimension(R.dimen.recycler_view_padding);
         float scale = ExpectLauncher.getInstance().getPreferencesUtility().getAppIconSize();
        float appWidth = resources.getDimension(R.dimen.app_width)*scale;
        float appHeight = resources.getDimension(R.dimen.app_height)*scale;
        if(!ExpectLauncher.getInstance().getPreferencesUtility().isShowAppTitle())
            appHeight = appWidth;
        float minWidthZone = appWidth*1.35f;
        float minHeightZone = appHeight*1.35f;

        int numberColumn = (int) (mRVContentWidth/minWidthZone);
        int numberRow = (int) (mRVContentHeight/minHeightZone);
        float horizontalMargin = (mRVContentWidth - numberColumn*appWidth)/(numberColumn+1);
        float verticalMargin = (mRVContentHeight - numberRow*appHeight)/(numberRow+1);
        if(numberColumn<1) numberColumn = 1;
        if(numberRow<1) numberRow = 1;
        mGridLayoutManager = new GridLayoutManager(activity,numberColumn,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        BoundItemDecoration itemDecoration = new BoundItemDecoration(mRVContentWidth, mRVContentHeight,numberColumn,numberRow,(int) (verticalMargin*0.9f),(int)(horizontalMargin*0.9f));
        mRecyclerView.addItemDecoration(itemDecoration);


    }
    GridLayoutManager mGridLayoutManager;

    /**
     * Hàm thay đổi kích thước biểu tượng ứng dụng
     * Gọi trực tiếp hàm này để thực hiện chức năng phóng to - thu nhỏ biểu tượng ứng dụng
     * @param scale tỉ lệ zoom từ kích thước mặc định ( scale từ 0.5 -> 3 )
     */
    public void setAppIconSize(float scale) {
        ExpectLauncher.getInstance().getPreferencesUtility().setAppIconSize(scale);
        mAdapter.notifyDataSetChanged();
        setLayoutManager();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(View view, App app) {
        openApp(view,app);
    }

    @Override
    public void onItemLongPressed(View view, App app, int index) {
        if(isRecyclerMoving) return;
        mAdaptiveApp = app;
        mAdaptiveIndex = index;

        CommonSettingBottomSheet fragment =  CommonSettingBottomSheet.newInstance(LayoutSwitcher.MODE.IN_APP_DRAWER);

        fragment.setAppDrawer(this);
        fragment.show(getChildFragmentManager(),
                "song_popup_menu");
    }
    PopupMenu mPopupMenu;
    public void dismissMenu() {
        if(null!=mPopupMenu) {
            mPopupMenu.dismiss();
            mPopupMenu = null;
        }
    }

    public boolean isMenuShown() {
        return null!=mPopupMenu;
    }

    public boolean handleIfMenuIsShown() {
        if(false&& isMenuShown()) {
            dismissMenu();
            mAdapter.switchMode(AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON);
            return true;
        }
        return false;
    }
    /**
     * Cập nhật vị trí mới cho App Drawer
     * Dùng nó để di chuyển App Drawer từ dưới lên
     */
    public void requestLayout() {
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

    void openApp(View v, App app) {
        ((AppLoaderActivity)getActivity()).openApp(v, app);
    }

    @Override
    public void showHideAppTitle(View v) {
       PreferencesUtility pu =  ExpectLauncher.getInstance().getPreferencesUtility();
       boolean isShow = !pu.isShowAppTitle();
       pu.setShowAppTitle(
               isShow
        );
       mAdapter.notifyDataSetChanged();
       if(v instanceof FloatingActionButton)
      updateShowHideTitleButton(v,isShow);
        setLayoutManager();
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

    }

    @Override
    public void onLoadComplete(ArrayList<App> data) {
        if(mAdapter!=null) mAdapter.setData(data);
    }

    @Override
    public void onLoadReset() {
    // do nothing
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
                MoreSettingBottomSheet.newInstance(this).show(getActivity().getSupportFragmentManager(),
                        "icon_app_editor_bottom_sheet");
                break;
            case R.id.app_config:
                //mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView,RecyclerView.Mo,mAdaptiveIndex);
              //  mRecyclerView.smoothScrollToPosition(mAdaptiveIndex);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] pos = new int[2];
                        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(mAdaptiveIndex);
                        if(vh ==null) return;
                        View view = vh.itemView;
                      view.getLocationInWindow(pos);
                      int[] pos2 = new int[2];
                      mRecyclerView.getLocationInWindow(pos2);
                 //       Log.d(TAG, "run: recyclerHeight = "+mRecyclerView.getHeight()+", posY = "+pos2[1]+", item posy ="+pos[1]);
                      mRecyclerView.smoothScrollBy(0,pos[1] - pos2[1]-view.getHeight());

                    }
                },500);
                EditAppConfigBottomSheet.newInstance(this,getAdaptiveApp(),mAdaptiveIndex).show(getActivity().getSupportFragmentManager(),"app_config");
                break;
                case R.id.launcher_setting:
                ((MainActivity)getActivity()).presentFragment(new DashBoardSetting());
            break;
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s==null||s.length()==0) {
            if(mAdapter.isInSearchMode()) {
                mAdapter.setInSearchMode(false);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if(!mAdapter.isInSearchMode()) {
                mAdapter.setInSearchMode(true);
                mAdapter.notifyDataSetChanged();
            }
        }
        if(mAdapter.isInSearchMode()) mAdapter.getFilter().filter(s);

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view : mSearchView.onActionViewExpanded();break;
        }
    }
    private App mAdaptiveApp;
    private int mAdaptiveIndex;

    @Override
    public App getAdaptiveApp() {
        return mAdaptiveApp;
    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        boolean isDarkWallpaper =  Tool.getInstance().isDarkWallpaper();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View root = getActivity().findViewById(R.id.root);
            if(root!=null&&isDarkWallpaper)
                root.setSystemUiVisibility(0);
            else if(root!=null)
            root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        updateSearchViewTheme(!isDarkWallpaper);
        onUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAdapter!=null)
            mAdapter.backupApps();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter!=null)
            mAdapter.restoreApps();

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
       if(mAdapter!=null) mAdapter.setFontValue(oldVal,newVal);
    }
    boolean isRecyclerMoving = false;
    public void setRecyclerMoving(boolean recyclerMoving) {
        this.recyclerMoving = recyclerMoving;
    }
}

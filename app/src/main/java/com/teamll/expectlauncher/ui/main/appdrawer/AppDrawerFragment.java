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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.bottomsheet.IconAppEditorBottomSheet;
import com.teamll.expectlauncher.ui.main.bottomsheet.RoundedBottomSheetDialogFragment;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.SimpleItemTouchHelperCallback;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.widgets.BoundItemDecoration;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.OnRangeChangedListener;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppDrawerFragment extends Fragment implements View.OnClickListener,
                                                            AppDrawerAdapter.ItemClickListener,
                                                           OnStartDragListener,
                                                           AppLoaderActivity.AppDetailReceiver,
                                                           LayoutSwitcher.EventSender,
                                                           RoundedBottomSheetDialogFragment.BottomSheetListener,
                                                           OnRangeChangedListener,
                                                           SearchView.OnQueryTextListener, IconAppEditorBottomSheet.AppEditorCallBack,
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

   @BindView(R.id.recyclerview) public RecyclerView mRecyclerView;
   @BindView(R.id.recycler_view_parent) public FrameLayout mRecyclerViewParent;

   /**
    ViewGroup gốc mà fragment được thêm vào
    ContainerView được lấy reference thông qua activity
     */
    FrameLayout mContainerView;

    /**
     * Đối tượng helper xử lý cử chỉ đổi vị trí các ứng dụng
     */
    ItemTouchHelper mItemTouchHelper;

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

        mAdapter = new AppDrawerAdapter(getActivity(),this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setLayoutManager();
        initTouchHelper();

        /**
         * Đăng ký bộ lắng nghe việc load app cho đối tượng này.
         */
        ((AppLoaderActivity)getActivity()).addAppDetailReceiver(this);

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnClickListener(this);
        mSearchView.onActionViewExpanded();
        mSearchView.clearFocus();
        EditText searchEditText =  mSearchView.findViewById(R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setGravity(Gravity.CENTER);
        }

    }

    private void initTouchHelper() {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onUpdate() {
        if(mAdapter!=null) mAdapter.notifyDataSetChanged();
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
                - resources.getDimension(R.dimen.recycler_view_margin_top);
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

    /**
     * Hàm thay đổi kích thước biểu tượng ứng dụng
     * Gọi trực tiếp hàm này để thực hiện chức năng phóng to - thu nhỏ biểu tượng ứng dụng
     * @param scale tỉ lệ zoom từ kích thước mặc định ( scale từ 0.5 -> 3 )
     */
    public void setAppIconSize(float scale) {
        PreferencesUtility.getInstance(getActivity().getApplicationContext()).setAppIconSize(scale);
        mAdapter.notifyDataSetChanged();
        setLayoutManager();
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
        mAdaptiveApp = appDetail;
        RoundedBottomSheetDialogFragment fragment =  RoundedBottomSheetDialogFragment.newInstance(LayoutSwitcher.MODE.IN_APP_DRAWER);

        fragment.setAppDrawer(this);
        fragment.show(getActivity().getSupportFragmentManager(),
                "song_popup_menu");
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

                IconAppEditorBottomSheet.newInstance(this).show(getActivity().getSupportFragmentManager(),
                        "icon_app_editor");
                return;
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
    private AppDetail mAdaptiveApp;

    @Override
    public AppDetail getAdaptiveApp() {
        return mAdaptiveApp;
    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        onUpdate();
    }
}

package com.teamll.expectlauncher.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplicationSetting extends SupportFragment implements AppLoaderActivity.AppsReceiver,ShowHiddenAppAdapter.onItemClick {
    @BindView(R.id.back_button)
    ImageView mBackButton;

    @BindView(R.id.next_reload_app)
    MKLoader mLoading;

    @BindView(R.id.icon_restore_app) View mIconRestore;
    @BindView(R.id.restore_app) View mRestoreApp;
    @BindView(R.id.restore_app_panel) View mRestoreAppPanel;

    private int[] mIConID = {R.id.icon_reload_app,R.id.icon_restore_app};

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerview;

    ShowHiddenAppAdapter mAdapter;

    @BindView(R.id.background_toolbar)
    View mBackgroundToobar;
    @OnClick(R.id.back_button)
    void back() {
        getMainActivity().dismiss();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.application_setting,container,false);
    }

    @OnClick(R.id.reload_app_panel)
    void reloadAppList() {
        if(mLoading!=null)
            mLoading.setVisibility(View.VISIBLE);
        getMainActivity().restartLoader();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        int color = Tool.getSurfaceColor();
        mBackgroundToobar.setBackgroundColor(color);
        for (int id :
                mIConID) {
            ((ImageView)view.findViewById(id)).setColorFilter(color);
        }

        mAdapter = new ShowHiddenAppAdapter();
        mAdapter.setOnItemClick(this);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
      //  refreshData();
    }
    public void refreshData() {
        getMainActivity().receiveAppsNow(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().addAppsReceiver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getMainActivity().removeAppsReceiver(this);
    }

    @Override
    public boolean isWhiteTheme() {
        return false;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        if(mBackButton!=null) {
            ((ViewGroup.MarginLayoutParams) mBackButton.getLayoutParams()).topMargin = value;
            mBackButton.requestLayout();
        }
    }

    @Override
    public void onLoadComplete(ArrayList<App> data) {
        mAdapter.setData(data);
        if(mLoading!=null&&mLoading.getVisibility()==View.VISIBLE) {
            mLoading.setVisibility(View.GONE);
            mLoading.postDelayed(() -> getMainActivity().getNavigationController().popToRootFragment(),300);
        }


    }

    @Override
    public void onLoadReset() {

    }

    @Override
    public void onItemClick(App app, int pos) {
        app.getAppSavedInstance().setHidden(false);
        refreshData();
    }

    @Override
    public void onNoHiddenApp() {
        mRestoreApp.setVisibility(View.GONE);
        mRestoreAppPanel.setVisibility(View.GONE);
        mIconRestore.setVisibility(View.GONE);
    }
}

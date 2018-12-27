package com.teamll.expectlauncher.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;

import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.PresentStyle;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashBoardSetting extends SupportFragment {

    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.title)
    TextView mTitle;
    
    private int[] mIConID = {R.id.icon_dock,R.id.icon_app,R.id.icon_app_drawer,R.id.icon_folders,R.id.icon_about};

    @BindView(R.id.background_toolbar)  View mBackgroundToobar;
    @OnClick(R.id.back_button)
    void back() {
        getMainActivity().dismiss();
    }

    @OnClick(R.id.dock_panel)
    void goToDockSetting() {
        getMainActivity().presentFragment(new DockSetting());
    }
    @OnClick(R.id.app_panel)
    void gotToAppSetting() {
        getMainActivity().presentFragment(new ApplicationSetting());
    }
    @OnClick({R.id.app_drawer,R.id.icon_app_drawer,R.id.next_app_drawer})
    void goToAppDrawerSetting() {
        getMainActivity().presentFragment(new AppDrawerSetting());
    }

    @OnClick({R.id.icon_folders,R.id.folders,R.id.next_folders})
    void goToFoldersSetting() {
        getMainActivity().presentFragment(new FoldersSetting());
    }
    @OnClick(R.id.about_panel)
    void goToAboutSetting() {
        getMainActivity().presentFragment(new AboutSetting());
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        if(mBackButton!=null) {
            ((ViewGroup.MarginLayoutParams) mBackButton.getLayoutParams()).topMargin = value;
            mBackButton.requestLayout();
        }
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.setting_dashboard,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        int color = Tool.getSurfaceColor();
        int heavy = Tool.getHeavyColor();
        mBackgroundToobar.setBackgroundColor(color);
        for (int id :
                mIConID) {
            ((ImageView)view.findViewById(id)).setColorFilter(color);
        }

    }


    @Override
    public boolean isWhiteTheme() {
        return false;
    }
}

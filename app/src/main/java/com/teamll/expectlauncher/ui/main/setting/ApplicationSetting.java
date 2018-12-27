package com.teamll.expectlauncher.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.PresentStyle;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplicationSetting extends SupportFragment {
    @BindView(R.id.back_button)
    ImageView mBackButton;

    private int[] mIConID = {};

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
}

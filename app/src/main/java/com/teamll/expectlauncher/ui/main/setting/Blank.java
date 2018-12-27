package com.teamll.expectlauncher.ui.main.setting;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;

public class Blank extends SupportFragment {
    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.blank,container,false);
    }

    @Override
    public boolean isWhiteTheme() {
        return !Tool.getInstance().isDarkWallpaper();
    }
}

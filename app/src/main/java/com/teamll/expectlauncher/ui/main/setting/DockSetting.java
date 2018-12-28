package com.teamll.expectlauncher.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.MainActivity;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.PresentStyle;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;
import com.teamll.expectlauncher.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DockSetting extends SupportFragment implements AppLoaderActivity.AppDetailReceiver {
    @BindView(R.id.back_button)
    ImageView mBackButton;
    ImageView dockApps[] = new ImageView[4];
    String dockAppPackageNames[] = null;
    private ArrayList<App> mData = new ArrayList<>();

//    @BindView(R.id.dockAppOne) ImageView dockAppOne;
//    @BindView(R.id.dockAppTwo) ImageView dockAppTwo;
//    @BindView(R.id.dockAppThree) ImageView dockAppThree;
//    @BindView(R.id.dockAppFour) ImageView dockAppFour;

//    @OnClick(R.id.text_view)
//    void doSomething() {
//        getMainActivity().presentFragment(new FoldersSetting()); // di toi mot fragment khac
//        getMainActivity().dismiss(); // huy fragment hien tai
//        Toast.makeText(getContext(),"You click textview",Toast.LENGTH_SHORT).show();
//    }

    private int[] mIConID = {};

    @BindView(R.id.background_toolbar)  View mBackgroundToobar;
    @OnClick(R.id.back_button)
    void back() {
        getMainActivity().dismiss();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view =inflater.inflate(R.layout.dock,container,false);

        dockApps[0] = view.findViewById(R.id.dockAppOne);
        dockApps[1] = view.findViewById(R.id.dockAppTwo);
        dockApps[2] = view.findViewById(R.id.dockAppThree);
        dockApps[3] = view.findViewById(R.id.dockAppFour);

        return view;
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
        // dang ky nhan danh sach app
        ((AppLoaderActivity)getActivity()).addAppDetailReceiver(this);


    }

    @Override
    public void onDestroy() {
        // huy di khi huy fragment
        ((AppLoaderActivity)getActivity()).removeAppDetailReceiver(this);
        super.onDestroy();
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
        mData.clear();
        if(data!=null) mData.addAll(data);

        dockAppPackageNames = Util.getDockAppPackageNames(getActivity());

        if (dockAppPackageNames != null) {
            int count = dockAppPackageNames.length;
            for (int index = 0; index < count; index++) {
                loadDockApp(index, dockAppPackageNames[index]);
            }
        }
        else {
            dockAppPackageNames = new String[4];
        }
    }

    @OnClick({R.id.dockAppOne, R.id.dockAppTwo, R.id.dockAppThree, R.id.dockAppFour})
    public void dockAppOneClick(View view) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChooseAppDialog chooseAppDialog = null;
        switch(view.getId()) {
            case R.id.dockAppOne:
                chooseAppDialog = new ChooseAppDialog(this, 0, mData);
                break;
            case R.id.dockAppTwo:
                chooseAppDialog = new ChooseAppDialog(this, 1, mData);
                break;
            case R.id.dockAppThree:
                chooseAppDialog = new ChooseAppDialog(this, 2, mData);
                break;
            case R.id.dockAppFour:
                chooseAppDialog = new ChooseAppDialog(this, 3, mData);
                break;
        }

        chooseAppDialog.show(fm, "choose_app_for_dock");
    }

    private void loadDockApp(int pos, String packageName) {
        App app = Util.findApp(mData, packageName);
        if (app != null) {
            dockApps[pos].setImageDrawable(app.getIcon());
        }
    }

    @Override
    public void onLoadReset() {
    // do nothing
    }

    public void notifyChosenDockApp(int pos, String packageName) {
        dockAppPackageNames[pos] = packageName;
        loadDockApp(pos, packageName);
    }

    @Override
    public void onPause() {
        super.onPause();

        Util.saveDockAppPackageNames(getActivity(), dockAppPackageNames);
        getMainActivity().getMainScreenFragment().updateDock();
    }
}

package com.teamll.expectlauncher.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.activities.MainActivity;

public class MainScreenFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private TextView appDrawerButton;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.homescreen,container,false);
        appDrawerButton = mRootView.findViewById(R.id.app_drawer);
        appDrawerButton.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        mainActivity.openAppDrawer();
    }
}

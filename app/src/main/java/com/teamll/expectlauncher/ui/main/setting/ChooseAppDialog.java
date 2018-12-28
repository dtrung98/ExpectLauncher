package com.teamll.expectlauncher.ui.main.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ChooseAppDialog extends DialogFragment {
    @BindView(R.id.appsList) RecyclerView appsListLayout;
    private int dockPos;
    private ArrayList<App> appsList;
    private DockSetting parent;

    public ChooseAppDialog(DockSetting parent, int dockPos, ArrayList<App> appsList) {
        this.dockPos = dockPos;
        this.parent = parent;
        this.appsList = appsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_app_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getDialog().setTitle("Choose app");
        //appsListLayout = view.findViewById(R.id.appsList);
        appsListLayout.setAdapter(new ChooseAppAdapter(appsList));
        appsListLayout.setLayoutManager(new LinearLayoutManager(getActivity()));
        appsListLayout.addOnItemTouchListener(new ChooseAppTouchListener(getContext(), appsListLayout, new ChooseAppTouchListener.ChooseAppClickListener() {
            @Override
            public void onClick(View view, int position) {
                parent.notifyChosenDockApp(dockPos, appsList.get(position).getApplicationPackageName());

                dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}

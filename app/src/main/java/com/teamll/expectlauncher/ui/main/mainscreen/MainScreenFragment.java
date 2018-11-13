package com.teamll.expectlauncher.ui.main.mainscreen;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.MainActivity;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.utils.Tool;

import java.util.ArrayList;

public class MainScreenFragment extends AppWidgetHostFragment implements View.OnTouchListener, View.OnClickListener, LayoutSwitcher.EventSender,AppLoaderActivity.AppDetailReceiver {
    private static final String TAG="MainScreenFragment";

    private long savedTime;
    private View adaptiveDock;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: ");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                adaptiveDock = v;
                Log.d(TAG, "onTouch: action down");
                savedTime = System.currentTimeMillis();
        }

        return false;
    }

    private View mRootView;
    private TextView appDrawerButton;
    private MotionRoundedBitmapFrameLayout dock;
    private MainActivity activity;
    float statusBarHeight  = 0;
    float navigationHeight = 0;
    float oneDp = 0;
    Rectangle rect;
    FrameLayout.LayoutParams dockParams;
    ImageView dockApp[] = new ImageView[4];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) getActivity();
        statusBarHeight= Tool.getStatusHeight(activity.getResources());
        navigationHeight = Tool.getNavigationHeight(activity);
        rect = new Rectangle();
        int[] size = Tool.getScreenSize(activity);
        rect.setSize(size[0],size[1]);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_screen_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view,savedInstanceState);
        mRootView = view;
    dock = view.findViewById(R.id.dock);

    dockApp[0] =dock.findViewById(R.id.dockApp1);
    dockApp[1] =dock.findViewById(R.id.dockApp2);
    dockApp[2] =dock.findViewById(R.id.dockApp3);
    dockApp[3] =dock.findViewById(R.id.dockApp4);
        for (View v :
                dockApp) {
            v.setOnTouchListener(this);

        }
    dock.setBackGroundColor(Color.WHITE);
    dock.setAlphaBackground(0.35f);
    dock.setRoundNumber(1.75f,true);
    dockParams = (FrameLayout.LayoutParams) dock.getLayoutParams();
    dockParams.topMargin = (int) (rect.Height - navigationHeight - dockParams.bottomMargin - dockParams.height);
    dockParams.bottomMargin = 0;
    dock.requestLayout();

    toggle = view.findViewById(R.id.toggleButton);
    toggleParams = (FrameLayout.LayoutParams) toggle.getLayoutParams();
    toggleParams.topMargin = dockParams.topMargin - toggleParams.height;
    toggle.requestLayout();
    Tool.getInstance().AddWallpaperChangedNotifier(dock);
        AppLoaderActivity ac = (AppLoaderActivity)getActivity();
        if(ac!=null)
            ac.addAppDetailReceiver(this);
        else throw new NullPointerException("Fragment was created from an activity class that doesn't inherit from AppLoaderActivity class");

    }
    private void dockClick(View v) {
        for (int i=0;i<appInDock.size();i++) {
            if (dockApp[i].getId() == v.getId()) {
                openApp(v, appInDock.get(i));
            }
        }
    }
   public FrameLayout.LayoutParams toggleParams ;
    public ImageView toggle;

    @Override
    public void onClick(View v) {
        activity.initScreen();
    }
    public void onUp() {
        if(System.currentTimeMillis() - savedTime <=300) {
           if(adaptiveDock!=null) {
               dockClick(adaptiveDock);
               adaptiveDock = null;
           }
        }
    }

    @Override
    public View getRoot() {
        return null;
    }

    @Override
    public View getEventSenderView() {
        return null;
    }
    public void onLongPress() {
        Toast.makeText(getContext(),"On Long Press",Toast.LENGTH_SHORT).show();
    }
    private ArrayList<App> appInDock = new ArrayList<>();
    private String[] packageDock = {
            "com.android.dialer" ,
            "com.android.messaging" ,
            "com.android.browser",
            "com.android.settings"};
    private boolean isPackageDock(App app) {
        for (String p :
                packageDock) {
            if(p.equals(app.getAppInfo().packageName)) return true;
        }
        return false;
    }
    private ArrayList<App> apps = new ArrayList<>();
    @Override
    public void onLoadComplete(ArrayList<App> data) {
       apps.clear();
        apps.addAll(data);

        appInDock.clear();
        for (App a :
                apps) {
           if(isPackageDock(a)) appInDock.add(a);
        }

        for(int i=0;i<appInDock.size();i++) {
            dockApp[i].setImageDrawable(appInDock.get(i).getIcon());
        }
    }
    void openApp(View v, AppDetail appDetail) {
        ((AppLoaderActivity)getActivity()).openApp(v,appDetail);
    }

    @Override
    public void onLoadReset() {
    }
}

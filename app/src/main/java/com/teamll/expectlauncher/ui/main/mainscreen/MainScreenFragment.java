package com.teamll.expectlauncher.ui.main.mainscreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.MainActivity;
import com.teamll.expectlauncher.ui.widgets.DarkenRoundedBackgroundFrameLayout;
import com.teamll.expectlauncher.utils.Tool;

public class MainScreenFragment extends Fragment implements View.OnClickListener, LayoutSwitcher.EventSender {
    private View mRootView;
    private TextView appDrawerButton;
    private DarkenRoundedBackgroundFrameLayout dock;
    private MainActivity activity;
    float statusBarHeight  = 0;
    float navigationHeight = 0;
    float oneDp = 0;
    Rectangle rect;
    FrameLayout.LayoutParams dockParams;
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
    mRootView = view;
    dock = view.findViewById(R.id.dock);
    dock.setAlphaBackground(0.7f);
    dock.setRoundNumber(1.75f,true);
    dockParams = (FrameLayout.LayoutParams) dock.getLayoutParams();
    dockParams.topMargin = (int) (rect.Height - navigationHeight - dockParams.bottomMargin - dockParams.height);
    dockParams.bottomMargin = 0;
    dock.requestLayout();

    toggle = view.findViewById(R.id.toggleButton);
    toggleParams = (FrameLayout.LayoutParams) toggle.getLayoutParams();
    toggleParams.topMargin = dockParams.topMargin - toggleParams.height;
    toggle.requestLayout();

    }
   public FrameLayout.LayoutParams toggleParams ;
    public ImageView toggle;

    @Override
    public void onClick(View v) {
        activity.initScreen();
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
}

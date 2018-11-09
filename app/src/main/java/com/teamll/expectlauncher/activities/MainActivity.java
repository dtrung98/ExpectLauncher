package com.teamll.expectlauncher.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.adapters.AppListAdapter;
import com.teamll.expectlauncher.config.AppItemConfig;
import com.teamll.expectlauncher.fragments.AppDrawerFragment;
import com.teamll.expectlauncher.fragments.MainScreenFragment;
import com.teamll.expectlauncher.ultilities.Tool;


public class MainActivity extends AppCompatActivity implements Tool.WallpaperChangedNotifier {
    private Toolbar toolbarResizeIcon;
    private AppDrawerFragment appDrawerFragment;

    private final int SMALL_ICON_WIDTH = 45;
    private final int SMALL_ICON_HEIGHT = 45;
    private final int MEDIUM_ICON_WIDTH = 55;
    private final int MEDIUM_ICON_HEIGHT = 55;
    private final int LARGE_ICON_WIDTH = 65;
    private final int LARGE_ICON_HEIGHT = 65;
    @Override
    protected void onResume() {
       super.onResume();
       Tool.getInstance().resumeWallpaperTracking();
    }

    @Override
    protected void onPause() {
      //  InstallShortcutReceiver.enableInstallQueue(InstallShortcutReceiver.FLAG_ACTIVITY_PAUSED);
        super.onPause();
        Tool.getInstance().stopWallpaperTracking();
    }
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.Init(this);
        Tool tool = Tool.getInstance();
        tool.AddWallpaperChangedNotifier(this);
        setContentView(R.layout.main_activity);
        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        imageView = findViewById(R.id.imageView);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainScreenFragment())
                    .commitNow();
            getWindow().getDecorView()
                    .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        }

        toolbarResizeIcon = findViewById(R.id.toolbarResizeIcon);
        setSupportActionBar(toolbarResizeIcon);
        getSupportActionBar().hide();
        //toolbarResizeIcon.setVisibility(View.INVISIBLE);

        appDrawerFragment = new AppDrawerFragment();


    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        imageView.setImageBitmap(original);
    }
    private int mode = 0;
    @Override
    public void onBackPressed() {
        if(mode !=0) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new MainScreenFragment())
            .commitNow();
         mode=1;
}
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) !=
                Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) {
           onBackPressed();
        }
    }

    public void openAppDrawer() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, appDrawerFragment)
                .commitNow();
        mode = 1;
        getSupportActionBar().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return (event.getKeyCode() == KeyEvent.KEYCODE_HOME) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popupmenu_resize_icon, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.small_size:
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).setNewIconSize(SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT);
                appDrawerFragment.recyclerView.removeAllViewsInLayout();
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                break;
            // action with ID action_settings was selected
            case R.id.medium_size:
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).setNewIconSize(MEDIUM_ICON_WIDTH,MEDIUM_ICON_HEIGHT);
                appDrawerFragment.recyclerView.removeAllViewsInLayout();
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                break;
            case R.id.large_size:
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).setNewIconSize(LARGE_ICON_WIDTH,LARGE_ICON_HEIGHT);
                appDrawerFragment.recyclerView.removeAllViewsInLayout();
                ((AppListAdapter)appDrawerFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                break;
            default:
                break;
        }

        return true;
    }
}

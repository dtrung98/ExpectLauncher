package com.teamll.expectlauncher.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.main.mainscreen.MainScreenFragment;
import com.teamll.expectlauncher.utils.Tool;


public class MainActivity extends AppLoaderActivity implements Tool.WallpaperChangedNotifier {
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
            initScreen();
            getWindow().getDecorView()
                    .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);

        }
    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        imageView.setImageBitmap(original);
    }
    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) !=
                Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) {
           onBackPressed();
        }
    }
    private LayoutSwitcher switcher;
    private MainScreenFragment mainScreenFragment;
    private AppDrawerFragment appDrawerFragment;

    public void initScreen() {
        mainScreenFragment = new MainScreenFragment();
        appDrawerFragment = new AppDrawerFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,mainScreenFragment )
                .commitNow();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, appDrawerFragment)
                .commitNow();
     }

    @Override
    protected void onStart() {
        super.onStart();
        if(switcher==null)
            switcher = new LayoutSwitcher(this,mainScreenFragment,appDrawerFragment);

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
}

package com.teamll.expectlauncher.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.main.mainscreen.MainScreenFragment;
import com.teamll.expectlauncher.utils.HomeWatcher;
import com.teamll.expectlauncher.utils.Tool;


public class MainActivity extends AppLoaderActivity implements Tool.WallpaperChangedNotifier,HomeWatcher.OnHomePressedListener {
    private static final String TAG="MainActivity";

    private static final int MY_PERMISSIONS_READ_STORAGE = 1;
    @Override
    protected void onResume() {
       super.onResume();
       inResuming = true;
       Tool.getInstance().resumeWallpaperTracking();
    }

    @Override
    protected void onPause() {
      //  InstallShortcutReceiver.enableInstallQueue(InstallShortcutReceiver.FLAG_ACTIVITY_PAUSED);
        super.onPause();
        inResuming = false;
        Tool.getInstance().stopWallpaperTracking();
    }
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.main_activity);
        Tool.Init(this);
        Tool tool = Tool.getInstance();
        tool.AddWallpaperChangedNotifier(this);

        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        imageView = findViewById(R.id.imageView);

        initScreen();

        if(savedInstanceState!=null)
        getWindow().getDecorView()
                .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        initHomeWatcher();
        GetPermission();

    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        imageView.setImageBitmap(original);
    }
    @Override
    public void onBackPressed() {
        if(switcher!=null) switcher.onBackPressed();
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

    private void GetPermission() {
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= 27&&ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_READ_STORAGE);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else doWorkAfterPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        //   Log.d("Permission Order","Reply Permission'");
        switch (requestCode) {
            case MY_PERMISSIONS_READ_STORAGE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Granted
                        doWorkAfterPermissionGranted();
                    } else finish();
                }
            }
            return;
        }
    }
    HomeWatcher mHomeWatcher;
    private void initHomeWatcher() {
        if(mHomeWatcher!=null) return;
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
    }
    private void doWorkAfterPermissionGranted() {
        Log.d(TAG, "doWorkAfterPermissionGranted: ");
        Tool.getInstance().startWallpaperTracking();
    }
    private boolean inResuming = false;

    @Override
    public void onHomePressed() {
        if(inResuming&&switcher!=null) switcher.onBackPressed();
    }

    @Override
    public void onHomeLongPressed() {

    }
}

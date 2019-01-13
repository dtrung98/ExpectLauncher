package com.teamll.expectlauncher.ui.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.main.mainscreen.MainScreenFragment;
import com.teamll.expectlauncher.ui.main.setting.Blank;
import com.teamll.expectlauncher.ui.main.setting.DashBoardSetting;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.FragmentNavigationController;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.PresentStyle;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.HomeWatcher;
import com.teamll.expectlauncher.util.Tool;


public class MainActivity extends AppLoaderActivity implements Tool.WallpaperChangedNotifier,HomeWatcher.OnHomePressedListener {
    private static final String TAG="MainActivity";

    private static final int MY_PERMISSIONS_READ_STORAGE = 1;

    FragmentNavigationController mNavigationController;
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
       super.onResume();
       inResuming = true;
       Tool.getInstance().resumeWallpaperTracking();
        mHomeWatcher.startWatch();
    }
    public FragmentNavigationController getNavigationController() {
        return mNavigationController;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        inResuming = false;
        mHomeWatcher.stopWatch();
        Tool.getInstance().stopWallpaperTracking();
    }
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
       setContentView(R.layout.main_activity);
        Tool.init(this);
        Tool tool = Tool.getInstance();
        tool.AddWallpaperChangedNotifier(this);
        //Layout Switcher
        switcher = new LayoutSwitcher();

        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        imageView = findViewById(R.id.imageView);



        if(savedInstanceState!=null) {
            getWindow().getDecorView()
                    .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            getFragmentReference();


        } else {
            initScreen();
        }
        initBackStack(savedInstanceState);
        initHomeWatcher();
        GetPermission();

    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        imageView.setImageBitmap(original);
    }
    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(!(isNavigationControllerInit() && dismiss()))
        if(switcher!=null) switcher.onBackPressed();

    }
    private LayoutSwitcher switcher;
    private MainScreenFragment mainScreenFragment;
    private AppDrawerFragment appDrawerFragment;
    public void initScreen() {


        mainScreenFragment = new MainScreenFragment();
        appDrawerFragment = new AppDrawerFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.add(R.id.container, mainScreenFragment,"MSF")
                .add(R.id.container, appDrawerFragment,"ADF")
                .commit();


    }
    public void getFragmentReference() {
        FragmentManager ft = getSupportFragmentManager();
        mainScreenFragment = (MainScreenFragment) ft.findFragmentByTag("MSF");
        appDrawerFragment = (AppDrawerFragment) ft.findFragmentByTag("ADF");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if(null!=switcher)
            switcher.bind(this,mainScreenFragment,appDrawerFragment);

    }



    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");

       // Tool.getInstance().clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        if(null!=switcher&&switcher.isViewAttached()) {
            Log.d(TAG, "switcher : ");
            switcher.detachView();
            switcher = null;
        }

        Tool.getInstance().destroy();
        super.onDestroy();
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

    }
    private void doWorkAfterPermissionGranted() {
        Log.d(TAG, "doWorkAfterPermissionGranted: ");
        Tool.getInstance().startWallpaperTracking();
    }
    private boolean inResuming = false;

    @Override
    public void onHomePressed() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(isNavigationControllerInit() && mNavigationController.getFragmentCount()==2) {
            dismiss();
        } else if(isNavigationControllerInit() && mNavigationController.getFragmentCount()>2)
        {
            while (mNavigationController.getFragmentCount() >= 3) {
                dismiss();
            }
            dismiss();
        } else
        if(inResuming&&switcher!=null) switcher.onBackPressed();
        else {
            // Check if no view has focus:

        }
    }

    @Override
    public void onHomeLongPressed() {

    }

    @Override
    public void onOpenPreference(View v, App app) {
        Log.d(TAG, "onOpenPreference");
       presentFragment(new DashBoardSetting());

    }

    public static int PRESENT_STYLE_DEFAULT = PresentStyle.SLIDE_LEFT;


    private void initBackStack(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        mNavigationController = FragmentNavigationController.navigationController(fm, R.id.fragment_container);
        mNavigationController.setPresentStyle(PRESENT_STYLE_DEFAULT);
        mNavigationController.setDuration(250);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        mNavigationController.presentFragment(new Blank());
       // mNavigationController.presentFragment(new MainFragment());

    }
    private boolean isNavigationControllerInit() {
        return null!= mNavigationController;
    }
    public void presentFragment(SupportFragment fragment) {
        Log.d(TAG, "presentFragment");
        if(isNavigationControllerInit()) {
            Log.d(TAG, "presentFragment: INIT");
//            Random r = new Random();
//            mNavigationController.setPresentStyle(r.nextInt(39)+1); //exclude NONE present style
            mNavigationController.setPresentStyle(fragment.getPresentTransition());

            setTheme(fragment.isWhiteTheme());
            mNavigationController.presentFragment(fragment, true);

        }
    }

    protected void setTheme(boolean white) {
        Log.d(TAG, "setTheme: "+white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View root = findViewById(R.id.root);
            if(root!=null&&!white)
                root.setSystemUiVisibility(0);
            else if(root!=null)
                root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public boolean dismiss() {
        if(isNavigationControllerInit()) {

           boolean b = mNavigationController.dismissFragment();
           setTheme(mNavigationController.getTopFragment().isWhiteTheme());
           return b;
        } return false;
    }

    public void presentFragment(SupportFragment fragment, boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.presentFragment(fragment,animated);
        }
    }
    public boolean dismiss(boolean animated) {
        if(isNavigationControllerInit()) {
            boolean b = mNavigationController.dismissFragment(animated);
            setTheme(mNavigationController.getTopFragment().isWhiteTheme());
        return b;
        }
        return false;
    }

    public MainScreenFragment getMainScreenFragment() {
        return mainScreenFragment;
    }
}

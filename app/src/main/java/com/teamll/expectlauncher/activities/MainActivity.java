package com.teamll.expectlauncher.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.fragments.AppDrawerFragment;
import com.teamll.expectlauncher.ultilities.Tool;


public class MainActivity extends AppCompatActivity implements Tool.WallpaperChangedNotifier {
    @Override
    protected void onResume() {
       super.onResume();
       Tool.getInstance().resumeWallpaperTracking();
    }

    @Override
    protected void onPause() {
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
                    .replace(R.id.container, new AppDrawerFragment())
                    .commitNow();
        }
    }

    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        imageView.setImageBitmap(blur);
    }

    @Override
    public void onBackPressed() {

    }
}

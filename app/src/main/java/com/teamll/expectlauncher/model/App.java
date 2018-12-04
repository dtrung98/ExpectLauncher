package com.teamll.expectlauncher.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.teamll.expectlauncher.util.BitmapEditor;

public class App extends AppDetail {
    public App(Context context, ApplicationInfo info) {
        super(context,info);
    }

//    @Override
//    public BitmapDrawable getIcon() {
//        super.getIcon();
//        BitmapDrawable bmd = new BitmapDrawable(BitmapEditor.CropBitmapTransparency(((BitmapDrawable)super.getIcon()).getBitmap()));
//        mIcon = bmd;
//        return bmd;
//    }
}

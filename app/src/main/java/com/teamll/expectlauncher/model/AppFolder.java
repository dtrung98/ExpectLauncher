package com.teamll.expectlauncher.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.teamll.expectlauncher.R;

import java.util.ArrayList;
import java.util.List;

public class AppFolder extends App {
    private ArrayList<App> mApps = new ArrayList<>();
    private String mTitle="";
    private final Context mContext;
    public AppFolder(Context context) {
        super();
        mContext = context;
        mTitle = context.getResources().getString(R.string.folder);
    }
    public void clear() {
        mApps.clear();
    }

    public void add(App app) {
        if(app!=null&&!mApps.contains(mApps))
            mApps.add(app);
    }

    public void remove(App app) {

    }

    public void set(List<App> apps) {
        mApps.clear();
        if(apps!=null) mApps.addAll(apps);
    }

    public ArrayList<App> getApps() {
        return mApps;
    }

    @Override
    public String getLabel() {
        return mTitle;
    }

    @Override
    public String getApplicationPackageName() {
        return getLabel();
    }

    @Override
    public Drawable getIcon() {
        if(mIcon==null) {
            updateIcon();
        }
        return mIcon;
    }
    public void updateIcon() {
        Bitmap bmp = Bitmap.createBitmap(196,196,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.save();
        for (int i=0;i<mApps.size();i++) {
            canvas.translate(10*i,0);
            Drawable drawable = mApps.get(i).getIcon();
            Rect old = drawable.copyBounds();
            drawable.setBounds(0,0,48,48);
            drawable.draw(canvas);
            drawable.setBounds(old);
        }
        canvas.restore();
        mIcon = new BitmapDrawable(mContext.getResources(),bmp);
        //mIcon = new AppFolderDrawable(mApps);
    }


    public static class AppFolderDrawable extends Drawable {
        private static final String TAG ="AppFolderDrawable";


        ArrayList<App> mData;
        Paint mPaint;
       public AppFolderDrawable(ArrayList<App> data) {
           mData = data;
           mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
           mPaint.setColor(Color.BLUE);
           mPaint.setStyle(Paint.Style.FILL);
           setBounds(0,0,96,96);

       }

        @Override
        public void draw(Canvas canvas) {
            Log.d(TAG, "draw: size = "+mData.size()+", bound = "+getBounds().left+", "+getBounds().top+", "+getBounds().right+", "+getBounds().bottom);
            canvas.drawColor(Color.GREEN);
            canvas.drawRect(0,0,1,1,mPaint);
            for(int i=0;i<mData.size();i++) {
              Rect rect = getBounds();
                Log.d(TAG, "draw: bound "+i+ mData.get(i).getIcon().getBounds().toShortString());
              mData.get(i).getIcon().draw(canvas);
           }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            Log.d(TAG, "onBoundsChange: new = "+bounds.toString());
            super.onBoundsChange(bounds);
        }

        @Override
        public void setColorFilter( ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}

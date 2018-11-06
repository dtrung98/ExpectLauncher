package com.teamll.expectlauncher.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.w3c.dom.Attr;

public class ImageBackgroundLayout extends FrameLayout {
    public ImageBackgroundLayout( Context context) {
        super(context);
        init(null);
    }

    public ImageBackgroundLayout( Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ImageBackgroundLayout( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    protected void init(AttributeSet attrs) {

    }

   @Override
    protected void onDraw(Canvas canvas) {

   }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
    Bitmap bitmap;

}

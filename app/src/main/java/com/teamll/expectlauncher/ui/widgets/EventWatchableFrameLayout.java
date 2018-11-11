package com.teamll.expectlauncher.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class EventWatchableFrameLayout extends FrameLayout {
    private static final String TAG="EventWatchable";

    public EventWatchableFrameLayout(Context context) {
        super(context);
    }

    public EventWatchableFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EventWatchableFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    int i=0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = super.onInterceptTouchEvent(ev);
        i++;
        Log.d(TAG, "onInterceptTouchEvent: b = "+b+", i = "+i);
        return b;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean b = super.onTouchEvent(ev);
        i++;
        Log.d(TAG, "onTouchEvent: b = "+b+", i = "+i);
        return b;
    }

    private OnDispatchTouchEvent listener;

    public void setOnDispatchTouchEvent(OnDispatchTouchEvent listener) {
        this.listener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = false;
        if(listener!=null)
          b = listener.onDispatchTouch(this,ev);
       if(!b) return super.dispatchTouchEvent(ev);
       return true;
    }
}

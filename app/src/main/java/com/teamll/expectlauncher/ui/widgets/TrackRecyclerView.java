package com.teamll.expectlauncher.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class TrackRecyclerView extends RecyclerView {
    private static final String TAG="TrackRecyclerView";

    public TrackRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TrackRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.d(TAG, "onTouchEvent");
        return super.onTouchEvent(e);
    }
}

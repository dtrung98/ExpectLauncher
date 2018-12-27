package com.teamll.expectlauncher.ui.widgets;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetectorGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG ="SwipeDetector";


    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    protected int id;
    public void setAdaptiveView(View v) {
        id =v.getId();
    }
    public boolean onUp(MotionEvent e) {
        return false;
    }
    public boolean onMove(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            Log.d(TAG, "onFling: diffY = "+diffY);
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        result = onSwipeRight(e1,e2,velocityX,velocityY);
                    } else {
                        result = onSwipeLeft(e1,e2,velocityX,velocityY);
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        result = onSwipeBottom(e1,e2,velocityX,velocityY);
                    } else {
                        result = onSwipeTop(e1,e2,velocityX,velocityY);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public boolean onSwipeRight(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public boolean onSwipeLeft(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
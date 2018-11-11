package com.teamll.expectlauncher.ui.widgets;

import android.view.MotionEvent;
import android.view.View;

public interface OnDispatchTouchEvent {
    boolean onDispatchTouch(View v, MotionEvent ev);
}

package com.teamll.expectlauncher.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffsetWidth, mItemOffsetHeight;

    public CustomItemDecoration(int offsetWidth, int offsetHeight) {
       mItemOffsetWidth = offsetWidth;
       mItemOffsetHeight = offsetHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffsetWidth, mItemOffsetHeight, mItemOffsetWidth, mItemOffsetHeight);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }
}

package com.teamll.expectlauncher.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.util.BitmapEditor;
import com.teamll.expectlauncher.util.Tool;

public class MotionRoundedBitmapFrameLayout extends DarkenRoundedBackgroundFrameLayout implements Tool.WallpaperChangedNotifier {
   private static final String TAG="MotionRound";


    public MotionRoundedBitmapFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MotionRoundedBitmapFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlphaPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    Paint mShaderPaint;
    Paint mAlphaPaint;
    private Bitmap backBitmap;
    private Canvas backCanvas;
    private Rectangle drawRect = new Rectangle();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        backBitmap = null;
        initBitmap();
    }
    private void initBitmap() {
        if(backBitmap==null) {
            backBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            backCanvas = new Canvas(backBitmap);
            drawRect.setSize(getWidth()-getPaddingLeft()-getPaddingRight(), getHeight() - getPaddingTop()-getPaddingBottom());
            drawRect.setPosition(getPaddingLeft(),getPaddingTop());
        }
    }
    private Bitmap source_bitmap;
    private Rectangle rect_view_in_bitmap = new Rectangle();
    private Rectangle rect_parent_in_bitmap = new Rectangle();
    private float parentHeight, parentWidth;
    @Override
    public void onWallpaperChanged(Bitmap original, Bitmap blur) {
        int[] s;
        s = Tool.getScreenSize(getContext());
        parentHeight = s[1];
        parentWidth = s[0];
        source_bitmap = blur;
      //  source_bitmap = BitmapEditor.getResizedBitmap(original,(int)parentWidth,(int)parentHeight);
        if(mShaderPaint ==null) init();
        mShaderPaint.setShader(new BitmapShader(source_bitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT));


        float ratio_parent =  parentWidth/(parentHeight +0.0f);
        float ratio_source = source_bitmap.getWidth()/(source_bitmap.getHeight() +0.0f);

        if(ratio_parent> ratio_source) {
            // crop height of source
            rect_parent_in_bitmap.Width = source_bitmap.getWidth();
            rect_parent_in_bitmap.Height = (int) (rect_parent_in_bitmap.Width*parentHeight/parentWidth);

            rect_parent_in_bitmap.Left = 0;
            rect_parent_in_bitmap.Top = source_bitmap.getHeight()/2 - rect_parent_in_bitmap.Height/2;
        } else {
            // crop width of source
            // mean that
            rect_parent_in_bitmap.Height = source_bitmap.getHeight();
            rect_parent_in_bitmap.Width = (int) (rect_parent_in_bitmap.Height*parentWidth/parentHeight);

            rect_parent_in_bitmap.Top = 0;
            rect_parent_in_bitmap.Left = source_bitmap.getWidth()/2 - rect_parent_in_bitmap.Width/2;
        }
        invalidate();
    }
    private boolean drawMask = true;
    public void setBlurred(boolean b) {
        drawMask = b;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {

        initBitmap();

        if (source_bitmap != null&&drawMask)
            drawMask(canvas);

        super.onDraw(canvas);
    }
    private float alpha_blur = 1;
    public void setAlphaBlurPaint(float value,boolean shouldInvalidate) {
        alpha_blur = value;
        if(shouldInvalidate) invalidate();
    }
    private Rectangle rect_view_in_parent = new Rectangle();
    private void drawMask(Canvas canvas) {

        int[] p = new int[2];
        getLocationOnScreen(p);
        int left = p[0];
        int top =p[1];
        // hình chữ nhật của view trong parent
        rect_view_in_parent.setPosition( left + getPaddingLeft(),top+ getPaddingTop());
        rect_view_in_parent.setSize(getWidth() - getPaddingLeft() - getPaddingRight(),getHeight() - getPaddingTop() - getPaddingBottom());

        // hình chữ nhật của view trong bitmap
        // ép tỉ lệ
        rect_view_in_bitmap.Width = (int) (rect_view_in_parent.Width* rect_parent_in_bitmap.Width/parentWidth);
        rect_view_in_bitmap.Height = (int) (rect_view_in_parent.Height*rect_parent_in_bitmap.Height/(parentHeight+0.0f));
        rect_view_in_bitmap.Left = (int) (rect_parent_in_bitmap.Left + rect_view_in_parent.Left/(parentWidth+0.0f)*rect_parent_in_bitmap.Width);
        rect_view_in_bitmap.Top = (int) (rect_parent_in_bitmap.Top + rect_view_in_parent.Top/(parentHeight+0.0f)*rect_parent_in_bitmap.Height);

       if(!true) {

           mAlphaPaint.setAlpha((int) (255.0f*alpha_blur));
           canvas.drawBitmap(source_bitmap,
                   rect_view_in_bitmap.getRectGraphic(),
                   drawRect.getRectGraphic(),
                   mAlphaPaint);
       }
        else {
           mShaderPaint.setAlpha((int) (255.0f*alpha_blur));
            canvas.save();
          //  canvas.scale(1,1);
            canvas.translate(-rect_view_in_parent.Left,-rect_view_in_parent.Top);
            Path path = BitmapEditor.RoundedRect(rect_view_in_parent.Left,rect_view_in_parent.Top,rect_view_in_parent.Width + rect_view_in_parent.Left,rect_view_in_parent.Height+rect_view_in_parent.Top,maxRx*eachDP*number,maxRy*eachDP*number,round_type==ROUND_TYPE.ROUND_TOP);
         //  canvas.drawRect(getRectGraphic(drawRect), mSolidPaint);
           canvas.drawPath(path, mShaderPaint);
           canvas.restore();
       }
    }


}

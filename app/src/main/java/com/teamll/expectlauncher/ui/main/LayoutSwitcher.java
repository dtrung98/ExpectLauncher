package com.teamll.expectlauncher.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerAdapter;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.main.bottomsheet.CommonSettingBottomSheet;
import com.teamll.expectlauncher.ui.main.mainscreen.MainScreenFragment;
import com.teamll.expectlauncher.ui.widgets.DarkenRoundedBackgroundFrameLayout;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.ui.widgets.SwipeDetectorGestureListener;
import com.teamll.expectlauncher.util.Animation;
import com.teamll.expectlauncher.util.Tool;

public class LayoutSwitcher implements View.OnTouchListener {
    private static final String TAG="LayoutSwitcher";
    public interface EventSender {
        View getRoot();
    }
    enum MOVE_DIRECTION {
        NONE,
        MOVE_UP,
        MOVE_DOWN
    }
    public enum MODE {
        IN_MAIN_SCREEN,
        IN_APP_DRAWER
    }

    private MainScreenFragment mainScreen;
    private AppDrawerFragment mAppDrawer;
    private FrameLayout container;
    private Rectangle rect;
    private FrameLayout.LayoutParams appDrawerParams;
    private View appDrawerRootView;
    public RecyclerView recyclerView;

    private ImageView toggle;
    private int toggleOriginalY;
    private FrameLayout.LayoutParams toggleParams;
    private int recyclerMarginTop;


    private MODE mode = MODE.IN_MAIN_SCREEN;
    private float max_value = 0.65f;
    private boolean mainScreenIsHidden = false;
    private GestureDetector mGestureDetector ;
    ValueAnimator va;
    boolean isRunning = false;
    public void detachView() {
    //    if(true) return;

        mainScreen = null;
        mAppDrawer = null;
        container = null;
        rect = null;
        appDrawerParams = null;
        appDrawerRootView = null;
        recyclerView = null;

        toggle = null;
        toggleParams = null;

        mGestureDetector = null ;
        if(null!=va&&va.isRunning()) va.cancel();
        if(null!=va) va = null;
        isBind = false;

    }
    public boolean isViewAttached() {
        return isBind;
    }
    private boolean isBind = false;

    @SuppressLint("ClickableViewAccessibility")
    public void bind(Activity activity, MainScreenFragment mainScreen, AppDrawerFragment appDrawer) {
        if(isBind) return;
        isBind = true;
        this.mainScreen = mainScreen;
        this.mAppDrawer = appDrawer;
        container = activity.findViewById(R.id.container);

        rect = new Rectangle();
        rect.setSize(Tool.getScreenSize(activity));

        recyclerMarginTop = (int) (activity.getResources().getDimension(R.dimen.app_drawer_margin) + Tool.getStatusHeight(activity.getResources()));

        toggle = mainScreen.toggle;
        toggleParams = mainScreen.toggleParams;
        toggleOriginalY = toggleParams.topMargin;

        toggle.setOnTouchListener(new View.OnTouchListener() {
            private long savedTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                       savedTime = System.currentTimeMillis();
                     gestureListener.tempY0 = gestureListener.assignPosY0 = event.getRawY() + ( rect.Height -  toggleParams.topMargin - toggleParams.height + recyclerMarginTop);
                       gestureListener.down =true;
                        motion(toggleParams.topMargin + toggleParams.height-recyclerMarginTop);;
                   //     onTouchBoth(container,event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        return onTouchBoth(container,event);
                    case MotionEvent.ACTION_UP:
                        onTouchBoth(container,event);
                        if(System.currentTimeMillis() - savedTime <=300) {
                            motionUp();
                            return true;
                        }

                        return true;
                }
                return true;
            }
        });


        appDrawerRootView = appDrawer.getRoot();
        appDrawerParams = (FrameLayout.LayoutParams) appDrawerRootView.getLayoutParams();

        recyclerView = appDrawer.mRecyclerView;

        container.setOnTouchListener(this);
        recyclerView.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(activity, gestureListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==recyclerView.getId()) {
        }
        if(!isViewAttached()) return false;
       if(v.getId() ==recyclerView.getId()&& mAppDrawer.mAdapter.getMode()==AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.NORMAL) {
           {
               Log.d(TAG, "onTouch: recycler on touch "+logAction(event));

               return onTouchRecyclerView(v, event);
           }
       } else if(v.getId() == container.getId()) {
           if(event.getAction()==MotionEvent.ACTION_UP) mainScreen.onUp(event);
         return onTouchBoth(v,event);
       }
       return false;
    }

    /**
     * Di chuyển App Drawer lên xuống bằng cách thay đổi margin top của nó
     * @param id ID của view sự kiện chạm, container hoặc recyclerview
     * @param pos giá trị của margin top, từ 0 tới độ dài màn hìn
     */

    void translateAppDrawerByMarginTop(int id, int pos) {

        int saved = appDrawerParams.topMargin;
        appDrawerParams.topMargin = (id==R.id.recycler_view) ? pos : rect.Height + pos;

        // topMargin ở vị trí này thì dock bắt đầu thu nhỏ dần
        float startKeyDock = 3 / 4.0f * rect.Height - mainScreen.dockParams.height;

        // đi một khoảng này thì hiệu ứng thu nhỏ tối đa
        float distanceKey =  1/5.0f*rect.Height;

        // giá trị value : 0 -> 1
        float keyValue = (startKeyDock - appDrawerParams.topMargin)/ distanceKey;

        if(keyValue<0) keyValue = 0;


        int tgPos =  appDrawerParams.topMargin - (mainScreen.dockParams.height+mainScreen.dockParams.leftMargin) - toggleParams.height + recyclerMarginTop;
        toggleParams.topMargin = (tgPos> toggleOriginalY) ? toggleOriginalY : tgPos;
        float keyNormalize = (keyValue>1) ? 1 : keyValue;

            mainScreen.dockParams.topMargin = (int) (toggleParams.topMargin + toggleParams.height + keyNormalize * (mainScreen.dockParams.height + mainScreen.dockParams.leftMargin));
            toggleParams.topMargin = mainScreen.dockParams.topMargin - toggleParams.height;

            mainScreen.dock.setScaleX(1 - 0.25f * keyNormalize);
            mainScreen.dock.setScaleY(1 - 0.25f * keyNormalize);
            mainScreen.setAlpha(1 - keyNormalize);

        if(mAppDrawer.mSearchBackGround instanceof MotionRoundedBitmapFrameLayout)
        mAppDrawer.mSearchBackGround.invalidate();

          if(mAppDrawer.mSearchBackGround instanceof MotionRoundedBitmapFrameLayout)
            mAppDrawer.mRecyclerViewParent.invalidate();

          toggle.requestLayout();
        if(keyValue<=1) {
            mainScreen.dock.requestLayout();
        }
        if(appDrawerParams.topMargin<=0) mode =MODE.IN_APP_DRAWER;
        else if(appDrawerParams.topMargin>=appDrawerParams.height) mode = MODE.IN_MAIN_SCREEN;
        appDrawerRootView.requestLayout();
      //  appDrawerParams.topMargin = saved;
        // appDrawerRootView.setTranslationY(pos);

        updateShowHideMainScreen(false,appDrawerParams.topMargin/(rect.Height+0.0f));
    }
    private void updateShowHideMainScreen(boolean zero2One, float value) {


        float alpha_blur = value/max_value;
        if(alpha_blur>1) alpha_blur = 1;

        if(mAppDrawer.mRecyclerViewParent instanceof DarkenRoundedBackgroundFrameLayout) {

            ((DarkenRoundedBackgroundFrameLayout) mAppDrawer.mRecyclerViewParent).setRoundNumber(0.5f + value * (1.75f - 0.5f), true);
            ((DarkenRoundedBackgroundFrameLayout) mAppDrawer.mRecyclerViewParent).setAlphaBackground(value * max_value);

            if(mAppDrawer.mRecyclerViewParent instanceof MotionRoundedBitmapFrameLayout) {
                ((MotionRoundedBitmapFrameLayout) mAppDrawer.mRecyclerViewParent).setAlphaBlurPaint(alpha_blur, false);
                }
        }
        toggle.setAlpha(value);
    }

    private void showHideMainScreenWhenModeChanged() {
       if(appDrawerParams.topMargin>0&&!mainScreenIsHidden) {
           // run into effect
           mainScreenIsHidden = true;
           final ValueAnimator va = ValueAnimator.ofFloat(0,1);
           va.setDuration(250);
           va.addUpdateListener(animation -> updateShowHideMainScreen(true, (Float) animation.getAnimatedValue()));
           va.start();

       } else if(appDrawerParams.topMargin<=0&& mainScreenIsHidden) {
           mainScreenIsHidden = false;
           final ValueAnimator va = ValueAnimator.ofFloat(1,0);
           va.setDuration(250);
           va.addUpdateListener(animation -> updateShowHideMainScreen(false, (Float) animation.getAnimatedValue()));
           va.start();
       }
    }
    public void onBackPressed() {

        if(mode==MODE.IN_APP_DRAWER) {
            if(mAppDrawer.mAdapter.getMode()!=AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.NORMAL)
                mAppDrawer.mAdapter.switchMode(AppDrawerAdapter.APP_DRAWER_CONFIG_MODE.NORMAL);
            else
            motionDown();
        }
    }

    public SwipeGestureListener gestureListener = new SwipeGestureListener();
    class SwipeGestureListener extends SwipeDetectorGestureListener {
        public boolean down = false;
        private boolean flingMasked = false;
        public float assignPosY0 ;
        private MOVE_DIRECTION direction;

        private float tempY0 ;
        private boolean onMoveUp() {
            return direction==MOVE_DIRECTION.MOVE_UP;
        }
        private boolean onMoveDown() {
            return direction == MOVE_DIRECTION.MOVE_DOWN;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            if(flingMasked) {
                flingMasked = false;
                Log.d(TAG, "onUp: fling mask, cancelled handle");
                return true;
            }
           if(id==R.id.container&&mode==MODE.IN_MAIN_SCREEN) {

               if (onMoveUp() && appDrawerParams.topMargin <= 3 / 4.0f * rect.Height)
                   motionUp();
               else motionDown();
           } else if(id ==R.id.recycler_view &&mode==MODE.IN_APP_DRAWER){
               if((onMoveDown())&&appDrawerParams.topMargin>=1/5.0f*rect.Height) {
                   motionDown();
               }
               else {
                   motionUp();
               }
           }
            down = false;
            return true;
        }

        @Override
        public boolean onMove(MotionEvent e) {

           if(!down) {
               down = true;
               tempY0 = assignPosY0 = e.getRawY();
               direction = MOVE_DIRECTION.NONE;
               return false;
           } else {
               float y = e.getRawY();
               direction = (y>tempY0) ? MOVE_DIRECTION.MOVE_DOWN : (y==tempY0) ? MOVE_DIRECTION.NONE : MOVE_DIRECTION.MOVE_UP;

               if(tempY0==assignPosY0) {

                      if(direction == MOVE_DIRECTION.MOVE_DOWN&&id==R.id.recycler_view)
                   translateAppDrawerByMarginTop(id,(int) (e.getRawY()-assignPosY0));
                      else if(direction==MOVE_DIRECTION.MOVE_UP&&id==R.id.container)
                         translateAppDrawerByMarginTop(id,(int) (e.getRawY()-assignPosY0));
                      else {
                          return false;
                      }
               } else {
                   float delta = e.getRawY() - assignPosY0;
                   if(id==R.id.recycler_view &&delta<0) {
                       down = false;
                       translateAppDrawerByMarginTop(id,0);
                       return false;
                   }
                   translateAppDrawerByMarginTop(id, (int) delta);
               }
               tempY0 = y;
               return true;
           }

        }

        @Override
        public void onLongPress(MotionEvent e) {
          // mainScreen.selectWidget();

           CommonSettingBottomSheet fragment =  CommonSettingBottomSheet.newInstance(mode);
           if(mode ==MODE.IN_MAIN_SCREEN)
           fragment.setListener(mainScreen);
           else
               fragment.setAppDrawer(mAppDrawer);
           fragment.show(mAppDrawer.getActivity().getSupportFragmentManager(),
                    "song_popup_menu");
        }

        @Override
        public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
           if(mode == MODE.IN_MAIN_SCREEN) Log.d(TAG, "detect onFling top, from main screen.");
           else Log.d(TAG, "detect onFling top, but not from main screen.");
            if(mode == MODE.IN_MAIN_SCREEN) {
                flingMasked = true;
                motionUp();
            }
                return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "detect onFling");
             if(!super.onFling(e1, e2, velocityX, velocityY)) {
                 if(mode ==MODE.IN_APP_DRAWER) {
                     Log.d(TAG, "onFling: auto redirect to on wipe bottom.");
                     return onSwipeBottom(e1,e2,velocityX,velocityY);
                 }
                 else
                     Log.d(TAG, "onFling: you are not in appdrawer mode");
             }
            Log.d(TAG, "onFling: another swipe handled");
             return true;
        }

        @Override
        public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(mode == MODE.IN_APP_DRAWER) Log.d(TAG, "detect onFling bottom, from app drawer.");
            else Log.d(TAG, "detect onFling bottom, but not from app drawer.");
            if(mode == MODE.IN_APP_DRAWER) {
                flingMasked = true;
                motionDown();
            }
            return true;
        }
        

        @Override
        public boolean onDown(MotionEvent e) {
            down = true;
           tempY0 = assignPosY0 = e.getRawY();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(id==R.id.container) {
                motionUp();
                return true;
            }
            return false;
        }

    };

    private boolean checkOnTop(){
        return !recyclerView.canScrollVertically(-1);
    }

    private String logAction(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:return "Down";
            case MotionEvent.ACTION_MOVE:return "Move";
            case MotionEvent.ACTION_UP:return "UP";
        }
        return "Unsupported";
    }
    private boolean onTouchBoth(View v, MotionEvent event) {
        Log.d(TAG, "onTouchBoth: event = "+logAction(event));
        v.performClick();
        gestureListener.setAdaptiveView(v);
        if(event.getAction()!=MotionEvent.ACTION_UP&& mAppDrawer != null) mAppDrawer.setRecyclerMoving(true);
        else if(mAppDrawer!=null) mAppDrawer.setRecyclerMoving(false);
        boolean b = mGestureDetector.onTouchEvent(event) ;
        boolean c = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: c = gestureListener.onMove(event);break;
                case MotionEvent.ACTION_UP:    gestureListener.onUp(event); break;
            }
        return b||c;
    }
    private boolean onTouchRecyclerView(View v, MotionEvent event) {
        if(mAppDrawer !=null&& mAppDrawer.handleIfMenuIsShown())
        return true;
        return (checkOnTop()) && onTouchBoth(v,event);
    }

    private void motion(int to){
        motion(appDrawerParams.topMargin,to);
    }
    private void motionUp() {
        motion(appDrawerParams.topMargin,0);
    }
    private void motionDown(){
        motion(appDrawerParams.topMargin,appDrawerParams.height);
    }

    private void motion(int yFrom, int yTo) {

        Log.d(TAG, "motion: you are going to motion.");
        if(yTo ==appDrawerParams.topMargin) return;
        if(va!=null&&isRunning) {
            Log.d(TAG, "motion: motion cancelled.");
            va.cancel();
            yFrom = appDrawerParams.topMargin;
          //  return;
        }
        Log.d(TAG, "motion: motion is running");
        isRunning = true;
        va = ValueAnimator.ofInt(yFrom,yTo);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int)animation.getAnimatedValue();
                if(value<0) value=0;
                  translateAppDrawerByMarginTop(R.id.recycler_view,value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });

            va.setInterpolator(Animation.getInterpolator(4, 0.5f));
            va.setDuration((long) (300 + 200 * Math.abs((yTo-yFrom+0.0f)/rect.Height)));
        va.start();
    }


}

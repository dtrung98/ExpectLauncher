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
import android.widget.Toast;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerFragment;
import com.teamll.expectlauncher.ui.main.bottomsheet.RoundedBottomSheetDialogFragment;
import com.teamll.expectlauncher.ui.main.mainscreen.MainScreenFragment;
import com.teamll.expectlauncher.ui.widgets.SwipeDetectorGestureListener;
import com.teamll.expectlauncher.utils.Animation;
import com.teamll.expectlauncher.utils.Tool;

public class LayoutSwitcher implements View.OnTouchListener {
    private static final String TAG="LayoutSwitcher";
    public interface EventSender {
        View getRoot();
        View getEventSenderView();
    }

    private MainScreenFragment mainScreen;
    private AppDrawerFragment appDrawer;
    private FrameLayout container;
    private Rectangle rect;
    private FrameLayout.LayoutParams appDrawerParams;
    private View appDrawerRootView;
    private RecyclerView recyclerView;

    private ImageView toggle;
    private int toggleOriginalY;
    private FrameLayout.LayoutParams toggleParams;
    private int recyclerMarginTop;

    @SuppressLint("ClickableViewAccessibility")
    LayoutSwitcher(Activity activity, MainScreenFragment mainScreen, AppDrawerFragment appDrawer) {
        this.mainScreen = mainScreen;
        this.appDrawer = appDrawer;
        container = activity.findViewById(R.id.container);

        rect = new Rectangle();
        rect.setSize(Tool.getScreenSize(activity));

        recyclerMarginTop = (int) (activity.getResources().getDimension(R.dimen.recyclerview_margin) + Tool.getStatusHeight(activity.getResources()));

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
                   //     _onTouch(container,event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        return _onTouch(container,event);
                    case MotionEvent.ACTION_UP:
                        _onTouch(container,event);
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

        recyclerView = (RecyclerView)appDrawer.getEventSenderView();

        container.setOnTouchListener(this);
        recyclerView.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(activity, gestureListener);
    }

    private float yWhenTouchDown;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      //  Log.d(TAG, "onTouch: topMargin = "+appDrawerParams.topMargin);
       if(v.getId() ==recyclerView.getId()) {
          return onTouchRecyclerView(v,event);
       } else if(v.getId() == container.getId()) {
           if(event.getAction()==MotionEvent.ACTION_UP) mainScreen.onUp();
         return _onTouch(v,event);
       }
       return false;
    }
    public enum MODE {
        IN_MAIN_SCREEN,
        IN_APP_DRAWER
    }
    private MODE mode = MODE.IN_MAIN_SCREEN;

    void controlPos(int id,int pos) {
        Log.d(TAG, "controlPos");
        if(id==R.id.recyclerview) {
            appDrawerParams.topMargin = pos;
            int tgPos = pos- toggleParams.height + recyclerMarginTop;
            toggleParams.topMargin = (tgPos> toggleOriginalY) ? toggleOriginalY : tgPos;
        }
        else {
            appDrawerParams.topMargin = rect.Height + pos  ;
            int tgPos = appDrawerParams.topMargin- toggleParams.height + recyclerMarginTop;
            toggleParams.topMargin = (tgPos> toggleOriginalY) ? toggleOriginalY : tgPos;
        }
        appDrawerRootView.requestLayout();
        appDrawer.recyclerParent.invalidate();
        toggle.requestLayout();
        if(appDrawerParams.topMargin<=0) mode =MODE.IN_APP_DRAWER;
        else if(appDrawerParams.topMargin>=appDrawerParams.height) mode = MODE.IN_MAIN_SCREEN;
        runControlEffect();

    }
    private float max_value = 0.65f;
    private boolean inEffectMode = false;
    private void runControlEffect() {
        Log.d(TAG, "runControlEffect: mode ="+ mode);
       if(appDrawerParams.topMargin>0&&!inEffectMode) {
           // run into effect
           inEffectMode = true;
           ValueAnimator va = ValueAnimator.ofFloat(0,1);
           va.setDuration(350);
           va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator animation) {
                  float value = (float) animation.getAnimatedValue();
                   appDrawer.recyclerParent.setRoundNumber(0.5f + value*(1.75f-0.5f),true);

                   float alpha_blur = value/max_value;
                   if(alpha_blur>1) alpha_blur = 1;
                   appDrawer.recyclerParent.setAlphaBlurPaint(alpha_blur,false);
                   appDrawer.recyclerParent.setAlphaBackground(value*max_value);
                   mainScreen.widgetContainer.setAlpha(value);

                   toggle.setAlpha(value);
                   if(value>=0.5f) {
                       Tool.WHITE_TEXT_THEME = false;
                       appDrawer.mAdapter.notifyDataSetChanged();
                   }
               }
           });
           va.start();
       } else if(appDrawerParams.topMargin<=0&&inEffectMode) {
           inEffectMode = false;
           final ValueAnimator va = ValueAnimator.ofFloat(1,0);
           va.setDuration(350);
           va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator animation) {
                   float value = (float) animation.getAnimatedValue();
                   appDrawer.recyclerParent.setRoundNumber(0.5f + value*(1.75f-0.5f),true);

                   float alpha_blur = value/max_value;
                   if(alpha_blur>1) alpha_blur = 1;
                   appDrawer.recyclerParent.setAlphaBlurPaint(alpha_blur,false);
                   appDrawer.recyclerParent.setAlphaBackground(max_value*value);
                   mainScreen.widgetContainer.setAlpha(value);
                   toggle.setAlpha(value);
                   if(value<=0.5f) {
                       Tool.WHITE_TEXT_THEME = true;
                       appDrawer.mAdapter.notifyDataSetChanged();
                   }
               }
           });
           va.start();
       }
    }
    public void onBackPressed() {
        if(mode==MODE.IN_APP_DRAWER) {
            motionDown();
        }
    }

    private float yWhenOnTop = -1;
    int[] xy = new int[2];
    enum MOVE_DIRECTION {
        NONE,
        MOVE_UP,
        MOVE_DOWN
    }
    private GestureDetector mGestureDetector ;
    public SwipeGestureListener gestureListener = new SwipeGestureListener();
    class SwipeGestureListener extends SwipeDetectorGestureListener {
        public boolean down = false;

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

            Log.d(TAG, "onUp");
           if(id==R.id.container&&mode==MODE.IN_MAIN_SCREEN) {

               if (onMoveUp() && appDrawerParams.topMargin <= 1 / 2.0f * rect.Height)
                   motionUp();
               else motionDown();
           } else if(id ==R.id.recyclerview&&mode==MODE.IN_APP_DRAWER){
               Log.d(TAG, " onMoveDown =  "+onMoveDown());
               if((onMoveDown())&&appDrawerParams.topMargin>=1/4.0f*rect.Height) {
                   Log.d(TAG, "onUp: recycler should move down");
                   motionDown();
               }
               else {
                   Log.d(TAG, "onUp: recycler should move up");
                   motionUp();
               }
           }
            down = false;
            return true;
        }

        @Override
        public boolean onMove(MotionEvent e) {
            Log.d(TAG, "onMove, down = " + down+", assignPosY0 = "+assignPosY0);
           if(!down) {
               down = true;
               tempY0 = assignPosY0 = e.getRawY();
               direction = MOVE_DIRECTION.NONE;
               Log.d(TAG, "onMove: down just true");
               return false;
           } else {
               float y = e.getRawY();
               direction = (y>tempY0) ? MOVE_DIRECTION.MOVE_DOWN : (y==tempY0) ? MOVE_DIRECTION.NONE : MOVE_DIRECTION.MOVE_UP;

               if(tempY0==assignPosY0) {
                   Log.d(TAG, "onMove: first move after down");

                      if(direction == MOVE_DIRECTION.MOVE_DOWN&&id==R.id.recyclerview)
                   controlPos(id,(int) (e.getRawY()-assignPosY0));
                      else if(direction==MOVE_DIRECTION.MOVE_UP&&id==R.id.container)
                         controlPos(id,(int) (e.getRawY()-assignPosY0));
                      else {
                          return false;
                      }
               } else {
                   float delta = e.getRawY() - assignPosY0;
                   if(id==R.id.recyclerview&&delta<0) {
                       down = false;
                       controlPos(id,0);
                       return false;
                   }
                   controlPos(id, (int) delta);
               }
               tempY0 = y;
               return true;
           }

        }

        @Override
        public void onLongPress(MotionEvent e) {
          // mainScreen.selectWidget();
           RoundedBottomSheetDialogFragment fragment =  RoundedBottomSheetDialogFragment.newInstance(mode);
           if(mode ==MODE.IN_MAIN_SCREEN)
           fragment.setListener(mainScreen);
           else
               fragment.setListener(appDrawer);
           fragment.show(appDrawer.getActivity().getSupportFragmentManager(),
                    "song_popup_menu");
        }

        @Override
        public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeTop");
            if(id==R.id.container)
                motionUp();
                return true;
        }

        @Override
        public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeBottom");
            if(id==R.id.recyclerview)
                motionDown();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            down = true;
           tempY0 = assignPosY0 = e.getRawY();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed");
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

    private boolean inDownRecycler = false;
    private boolean _onTouch(View v, MotionEvent event) {
        v.performClick();
        gestureListener.setAdaptiveView(v);

        boolean b = mGestureDetector.onTouchEvent(event) ;
        boolean c = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: c = gestureListener.onMove(event);break;
                case MotionEvent.ACTION_UP:    gestureListener.onUp(event); break;
            }
        return b||c;
    }
    private boolean onTouchRecyclerView(View v, MotionEvent event) {
        return (checkOnTop()) &&_onTouch(v,event);
    }
    ValueAnimator va;
    boolean isRunning = false;
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
        if(yTo ==appDrawerParams.topMargin) return;
        if(va!=null&&isRunning) {
            va.cancel();
            yFrom = appDrawerParams.topMargin;
          //  return;
        }
        isRunning = true;
        va = ValueAnimator.ofInt(yFrom,yTo);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                  controlPos(R.id.recyclerview,(int) animation.getAnimatedValue());
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
            }
        });

        va.setInterpolator(Animation.getInterpolator(4,1.5f));
        va.setDuration((long) (300 + 350 * Math.abs((yTo-yFrom+0.0f)/rect.Height)));

        va.start();
    }

}

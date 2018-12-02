package com.teamll.expectlauncher.util;

import android.graphics.Color;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    private static final float[] FACTORS = {0.1f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f};
    private static final int TEXT_SIZE = 12;
    private static final int ANIMATION_DURATION = 1000;
    private static final int START_DELAY = 3000;
    private static final int FINISH_DELAY = 3000;
    private static final int ANIMATION_DELAY = 2000;


    private static List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#FF3B30"));
        colorList.add(Color.parseColor("#FF9501"));
        colorList.add(Color.parseColor("#FFCC00"));
        colorList.add(Color.parseColor("#4CDA64"));
        colorList.add(Color.parseColor("#5AC8FB"));
        colorList.add(Color.parseColor("#007AFF"));
        colorList.add(Color.parseColor("#5855D6"));
        colorList.add(Color.parseColor("#FF2C55"));
        colorList.add(Color.parseColor("#8E24AA"));
        colorList.add(Color.parseColor("#607d8b"));
        colorList.add(Color.parseColor("#827717"));
        //   Collections.shuffle(colorList);
        return colorList;
    }

    public static List<Interpolator> getInterpolatorList() {
        List<Interpolator> interpolatorList = new ArrayList<>();
        interpolatorList.add(new LinearInterpolator());
        interpolatorList.add(new AccelerateInterpolator());
        interpolatorList.add(new DecelerateInterpolator());
        interpolatorList.add(new AccelerateDecelerateInterpolator());
        interpolatorList.add(new OvershootInterpolator());
        interpolatorList.add(new AnticipateInterpolator());
        interpolatorList.add(new AnticipateOvershootInterpolator());
        interpolatorList.add(new BounceInterpolator());
        interpolatorList.add(new FastOutLinearInInterpolator());
        interpolatorList.add(new FastOutSlowInInterpolator());
        interpolatorList.add(new LinearOutSlowInInterpolator());
        return interpolatorList;
    }
    public  static  Interpolator getInterpolator(int id)
    {
        switch (id)
        {
            case 0:return new LinearInterpolator();
            case 1:return new AccelerateInterpolator();
            case 2:return new DecelerateInterpolator();
            case 3:return new AccelerateDecelerateInterpolator();
            case 4:return new OvershootInterpolator();
            case 5:return new AnticipateInterpolator();
            case 6:return new AnticipateOvershootInterpolator();
            case 7:return new BounceInterpolator();
            case 8:return new FastOutLinearInInterpolator();
            case 9:return new LinearInterpolator();
            case 10:return new LinearOutSlowInInterpolator();
            default: return null;
        }
    }
    public  static  Interpolator getInterpolator(int id, float... tension)
    {
        switch (id)
        {
            case 0:return new LinearInterpolator();
            case 1:return new AccelerateInterpolator(tension[0]);
            case 2:return new DecelerateInterpolator(tension[0]);
            case 3:return new AccelerateDecelerateInterpolator();
            case 4:return new OvershootInterpolator(tension[0]);
            case 5:return new AnticipateInterpolator(tension[0]);
            case 6:
                if(tension.length==1)
                return new AnticipateOvershootInterpolator(tension[0]);
                return new AnticipateOvershootInterpolator(tension[0],tension[1]);
            case 7:return new BounceInterpolator();
            case 8:return new FastOutLinearInInterpolator();
            case 9:return new LinearInterpolator();
            case 10:return new LinearOutSlowInInterpolator();
            default: return null;
        }
    }


    private static List<Interpolator> getInterpolatorList(Class aClass) {
        if (aClass == null) {
            return getInterpolatorList();
        }
        List<Interpolator> interpolatorList = new ArrayList<>();
        for (float factor : FACTORS) {
            try {
                //noinspection unchecked
                interpolatorList.add(         (Interpolator) aClass.getConstructor(float.class).newInstance(factor));
            } catch (Exception ignored) {
            }
        }
        return interpolatorList;
    }
}
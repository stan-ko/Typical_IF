package com.twotoasters.jazzylistview.effects;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.twotoasters.jazzylistview.JazzyEffect;

public class GrowEffect implements JazzyEffect {

    private static final float INITIAL_SCALE_FACTOR = 0.01f;

    @Override
    public void initView(View item, int position, int scrollDirection) {
        ViewHelper.setPivotX(item, item.getWidth() / 2);
        ViewHelper.setPivotY(item, item.getHeight() / 2);
        ViewHelper.setScaleX(item, INITIAL_SCALE_FACTOR);
        ViewHelper.setScaleY(item, INITIAL_SCALE_FACTOR);
    }

    @Override
    public void setupAnimation(View item, int position, int scrollDirection, ViewPropertyAnimator animator) {
        animator.scaleX(1).scaleY(1);
    }
}

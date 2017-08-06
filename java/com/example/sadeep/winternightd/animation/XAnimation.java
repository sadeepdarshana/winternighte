package com.example.sadeep.winternightd.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.HorizontalScrollView;

import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.temp.d;

/**
 * Created by Sadeep on 6/18/2017.
 */
public class XAnimation {
    private XAnimation() {}

    public static final int DIMENSION_WIDTH = 0;
    public static final int DIMENSION_HEIGHT = 1;


    public static void hScroll(final HorizontalScrollView view, int duration, int amount){
        hScroll( view,  duration,  amount,0);
    }

    public static void hScroll(final HorizontalScrollView view, int duration, int amount, int delay){
        ValueAnimator animator = ValueAnimator.ofInt(0,amount).setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int preVal = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.scrollBy(value-preVal,0);
                preVal = value;
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setStartDelay(delay);
        set.start();
    }

    public static void vScroll(final View view, int duration, int amount){
        vScroll( view,  duration,  amount,0);
    }

    public static void vScroll(final View view, int duration, int amount, int delay){
        ValueAnimator animator = ValueAnimator.ofInt(0,amount).setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int preVal = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.scrollBy(0,value-preVal);
                preVal = value;
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setStartDelay(delay);
        set.start();
    }




    public static ValueAnimator changeDimension(final View view, int duration, final int dimension, int start, int end){
        return changeDimension(view, duration, dimension, start, end,0);
    }
    public static ValueAnimator changeDimension(final View view, int duration, final int dimension, int start, int end,int delay){
        return changeDimension(view, duration, dimension, start, end,delay,null);
    }

    public static ValueAnimator changeDimension(final View view, int duration, final int dimension, int start, int end, final OnEndCallback onEndCallback){
        return changeDimension(view, duration, dimension, start, end,0,onEndCallback);
    }

    public static ValueAnimator changeDimension(final View view, int duration, final int dimension, int start, int end, int delay, final OnEndCallback onEndCallback){

        final ValueAnimator valueAnimator;
        if(dimension==0)valueAnimator = ValueAnimator.ofInt(start,end).setDuration(duration);
        else valueAnimator = ValueAnimator.ofInt(start,end).setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                if(dimension==0)view.getLayoutParams().width = value.intValue();
                else if(dimension==1)view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });



        valueAnimator.addListener(new Animator.AnimatorListener() {
            private boolean canceled = false;
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(canceled)return;
                if(onEndCallback!=null)onEndCallback.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                canceled=true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        AnimatorSet set = new AnimatorSet();
        set.play(valueAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setStartDelay(delay);
        set.start();

        return valueAnimator;
    }


    public static void changeBackgroundColor(final View view, int duration, int start, int end, int delay){

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), start, end);
        colorAnimation.setDuration(duration); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.setInterpolator(new AccelerateInterpolator(.8f));
        colorAnimation.setStartDelay(delay);
        colorAnimation.start();
    }

    public static void squeezeAndRemove(final View view, int duration, final int dimension,int delay){
        int start=0;
        if(dimension==DIMENSION_WIDTH)start=view.getWidth();
        if(dimension==DIMENSION_HEIGHT)start=view.getHeight();
        changeDimension(view,duration,dimension,start,0,delay);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(view.getParent()!=null&&(view.getHeight()==0||view.getWidth()==0))((ViewGroup)view.getParent()).removeView(view);
            }
        }, delay + duration + 300);
    }

    public static void addAndExpand(View view, ViewGroup parent,int indexInParent, int duration, int dimension, int delay) {
        int end=0;
        if(dimension==DIMENSION_WIDTH)end=Utils.getWidth(view);
        if(dimension==DIMENSION_HEIGHT)end=Utils.getHeight(view);

        try {
            if (dimension == DIMENSION_WIDTH) end = Math.max(view.getLayoutParams().width,end);
            if (dimension == DIMENSION_HEIGHT) end = Math.max(view.getLayoutParams().height,end);
        }catch (Exception e){}

        addAndExpand(view,parent,indexInParent,duration,dimension,delay,end,0);
    }

    public static void addAndExpand(final View view, ViewGroup parent, int indexInParent, int duration, final int dimension, int delay, int end, final int endParam) {

        if(view.getParent()!=null)((ViewGroup)view.getParent()).removeView(view);
        parent.addView(view,indexInParent);

        changeDimension(view,duration,dimension,0,end,delay);

        if(endParam!=0)view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dimension==DIMENSION_WIDTH)view.getLayoutParams().width=endParam;
                if(dimension==DIMENSION_HEIGHT)view.getLayoutParams().height=endParam;
            }
        }, delay + duration + 100);
    }
}

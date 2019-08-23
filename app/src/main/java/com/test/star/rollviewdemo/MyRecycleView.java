package com.test.star.rollviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Describe：
 * Author:zhuokai.zeng
 * CreateTime:2019/8/14
 */
public class MyRecycleView extends RecyclerView implements GestureDetector.OnGestureListener {
    private String TAG="MyRecycleView";
    private GestureDetector mGestureDetector;
    private int prePos=-1;
    View headerView=null;
    View footerView=null;
    View centerView=null;
    View crruentView=null;
    private int itemViewHeight=-1;
    float scale=1.5f;
    private int upFlag=0;
//    LinearSnapHelper mLinearSnapHelper;
    private int mCurrentItemOffset=0;
    private LinearLayoutManager layoutManager;

    public MyRecycleView(@NonNull Context context) {
        super(context);
        initView();
    }

    public MyRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mGestureDetector=new GestureDetector(getContext(),this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown: ");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: ");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp: ");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll: "+distanceY);
        //dy - up, + dowm

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: ");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling: ");
        //mCurrentItemOffset+=distanceY;
        //onScrolledChangedCallback();
        return false;
    }
}

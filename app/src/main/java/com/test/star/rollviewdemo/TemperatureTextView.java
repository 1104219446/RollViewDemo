package com.test.star.rollviewdemo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Describe：
 * Author:zhuokai.zeng
 * CreateTime:2019/8/13
 */
public class TemperatureTextView extends RelativeLayout {

    private TextView mMainTextView;
    private TextView mRightTextView;
    private TextView mDotTextView;
    private Context mActivity;
    private ViewGroup mViewgroup;

    public TemperatureTextView(Context context, ViewGroup viewGroup) {
        super(context);
        mActivity=context;
        mViewgroup=viewGroup;
        initView();
    }

    public TemperatureTextView(Context context,AttributeSet attrs) {
        super(context, attrs);
        mActivity=context;
        initView();
    }

    public TemperatureTextView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity=context;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TemperatureTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mActivity=context;
        initView();
    }

    private void initView() {
        View view=LayoutInflater.from(mActivity).inflate(R.layout.roll_item,mViewgroup,false);
        mMainTextView=view.findViewById(R.id.main_text);
        mRightTextView=view.findViewById(R.id.right_text);
        mDotTextView=view.findViewById(R.id.dot_text);
        addView(view);
    }

    public void setMainText(String string){
        mMainTextView.setText(string);
    }

    public void setRightText(String string){
        mRightTextView.setText(string);
    }

    public void setMainColor(int color){
        mMainTextView.setTextColor(color);
    }

    public void setRightColor(int color){
        mRightTextView.setTextColor(color);
    }

    public void setDotTextColor(int color){
        mDotTextView.setTextColor(color);
    }

}

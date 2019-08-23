package com.test.star.rollviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Describe：
 * Author:zhuokai.zeng
 * CreateTime:2019/8/13
 */
public class RollViewAdapter extends RecyclerView.Adapter<RollViewAdapter.MyViewHolder> {

    private Context mActivity;
    private HashMap<String,Object>mData;
    private ArrayList<String>mTextData;
    private ArrayList<String>mRightData;

    public RollViewAdapter(Context context, HashMap<String,Object>mp){
        super();
        mActivity=context;
        mData=mp;
        mTextData= (ArrayList<String>) mData.get("maintext");
        mRightData= (ArrayList<String>) mData.get("righttext");
        Log.d("Mysize", "getItemCount: "+mTextData.size());
    }

    @NonNull
    @Override
    public RollViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=new TemperatureTextView(mActivity,viewGroup);
        MyViewHolder holder=new MyViewHolder(view);
        //holder.getView().getLayoutParams().height=viewGroup.getHeight()/3;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RollViewAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.view.setMainText(mTextData.get(i));
        viewHolder.view.setMainColor(mActivity.getResources().getColor(R.color.white));
        viewHolder.view.setRightText(mRightData.get(i));
        viewHolder.view.setRightColor(mActivity.getResources().getColor(R.color.white));
        viewHolder.view.setDotTextColor(mActivity.getResources().getColor(R.color.white));
        viewHolder.getView().setTag(i);
    }

    @Override
    public int getItemCount() {
        //Log.d("Mysize", "getItemCount: "+mTextData.size());
        return mTextData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TemperatureTextView view;

        public MyViewHolder(View itemView){
            super(itemView);
            this.view= (TemperatureTextView) itemView;
        }

        public View getView() {
            return view;
        }
    }
}

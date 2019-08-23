package com.test.star.rollviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    MyRecycleView mRollView;

    HashMap<String,Object>mData;
    ArrayList<String>mTextList1;
    ArrayList<String>mTextList2;

    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        mRollView=findViewById(R.id.my_recycleview);
//        layoutManager=new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        initData();
//        RecyclerView.Adapter mAdapter=new RollViewAdapter(this,mData);
//        mRollView.setAdapter(mAdapter);
//        mRollView.setLayoutManager(layoutManager);
    }


    private void initData() {
        mData=new HashMap<>();
        mTextList1=new ArrayList<>();
        mTextList2=new ArrayList<>();
        for(int i=15;i<=31;i++){
            mTextList1.add(String.valueOf(i));
            mTextList2.add("."+String.valueOf(0));
            mTextList1.add(String.valueOf(i));
            mTextList2.add("."+String.valueOf(5));
        }
        mData.put("maintext",mTextList1);
        mData.put("righttext",mTextList2);
    }

}

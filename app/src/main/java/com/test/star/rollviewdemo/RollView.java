package com.test.star.rollviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Describe：
 * Author:zhuokai.zeng
 * CreateTime:2019/8/14
 */

public class RollView extends View implements GestureDetector.OnGestureListener {

    //六只画笔
    private Paint mPaintNormal;
    private Paint mPaintSelect;
    private Paint mPaintNormalRight;
    private Paint mPaintSelectRight;
    private Paint mPaintNormalDot;
    private Paint mPaintSelectDot;

    //行距与mTextSizeNormal之比，保证View内显示的内容在适当的位置
    private float RATE = 2.8f;
    public static final float SPEED = 5;

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private String TAG="RollView";
    private float mTextSizeSelect;
    private float mTextSizeNormal;

    private float mPaddingStart;
    private float mPaddingEnd;
    //手指滑动的距离 负数向上
    private static float mMoveDistance;
    //透明度
    private float mTextAlphaSelect;
    private float mTextAlphaNormal;
    private ArrayList<String>mainTextData;
    private ArrayList<String>rightTextData;
    //选中的位置
    private int mSelectPosition=10;
    private float mStartTouchY;
    private float mainToRightDis=60;
    private OnSelectListener mOnSelectListener=null;
    private ScrollTask mScrollTask;
    private FilingTask mFilingTask;
    private Handler mHandler;
    private Timer mTimer;
    private long firstTouchTime;
    private long finalTouchTime;
    private boolean isRoll=false;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMinVelocity=300,mMaxVelocity=8000;
    private GestureDetector mGestureDetector;
    private boolean isInCenter=false;
    private int mFlingDirection=0;//-1 向上滑 1 向下滑
    private final int FLING_UP=1;
    private final int FLING_DOWN=-1;


    public RollView(Context context) {
        super(context);
        mContext=context;
        initData();
        initView();
    }

    public RollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initData();
        initView();
    }

    public RollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initData();
        initView();
    }

    private void initData() {
        mainTextData=new ArrayList<>();
        rightTextData=new ArrayList<>();
        for(int i=15;i<=31;i++){
            mainTextData.add(String.valueOf(i));
            rightTextData.add("."+String.valueOf(0));
            mainTextData.add(String.valueOf(i));
            rightTextData.add("."+String.valueOf(5));
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    private void initView() {
        mHandler=new MHandler(this);
        mScrollTask =new ScrollTask(mHandler);
        mTimer=new Timer();
        mScroller=new Scroller(getContext());
        mGestureDetector=new GestureDetector(getContext(),this);
        initPaint();
    }

    private void initPaint() {
        mPaintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintNormalRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelectRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelectDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintNormalDot = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintNormal.setTextAlign(Paint.Align.CENTER);
        mPaintNormalDot.setTextAlign(Paint.Align.CENTER);
        mPaintNormalRight.setTextAlign(Paint.Align.CENTER);
        mPaintSelect.setTextAlign(Paint.Align.CENTER);
        mPaintSelectRight.setTextAlign(Paint.Align.CENTER);
        mPaintSelectDot.setTextAlign(Paint.Align.CENTER);


//        mPaintNormal.setTextSize(mTextSizeNormal);
//        mPaintSelect.setTextSize(mTextSizeSelect);
        mPaintSelect.setColor(Color.WHITE);
        mPaintSelectDot.setColor(Color.WHITE);
        mPaintSelectRight.setColor(Color.WHITE);
        mPaintNormal.setColor(Color.WHITE);
        mPaintNormalRight.setColor(Color.WHITE);
        mPaintNormalDot.setColor(Color.WHITE);

        //mTimer = new Timer();
        mTextAlphaSelect = 255;
        mTextAlphaNormal = 30;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        mTextSizeSelect = mContext.getResources().getDisplayMetrics().density * 70 / 3;
        mTextSizeSelect=heightSize/5;
        mTextSizeNormal = mTextSizeSelect / 2;
        Log.d(TAG, "onMeasure: mTextSizeSelect:"+mTextSizeSelect);
        Log.d(TAG, "onMeasure: mTextSizeNormal:"+mTextSizeNormal);
        //画笔默认宽高
        mPaintSelect.setTextSize(mTextSizeSelect);
        mPaintSelectRight.setTextSize(mTextSizeSelect/2);
        mPaintSelectDot.setTextSize(mTextSizeSelect/4);
        mPaintNormal.setTextSize(mTextSizeNormal);
        mPaintNormalRight.setTextSize(mTextSizeNormal/2);
        mPaintNormalDot.setTextSize(mTextSizeNormal/4);

        int mDefaultWidth = (int) (mPaintSelect.measureText("15") + mPaintSelectRight.measureText(".5") * 2
        +mPaintSelectDot.measureText("o"));
        Paint.FontMetricsInt mAnIntSelect = mPaintSelect.getFontMetricsInt();
        Paint.FontMetricsInt mAnIntNormal = mPaintNormal.getFontMetricsInt();
        int mDefaultHeight = mAnIntSelect.bottom - mAnIntSelect.top + (mAnIntNormal.bottom - mAnIntNormal.top) * 4;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mDefaultWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mDefaultHeight);
        }else{
            setMeasuredDimension(widthSize, heightSize);
        }
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker==null){
                    mVelocityTracker=VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);
                mFlingDirection=0;
                mStartTouchY = event.getY();
                //firstTouchTime=System.currentTimeMillis();
                isRoll=true;
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mMoveDistance += (event.getY() - mStartTouchY);
                Log.d(TAG, "onTouchEvent: actionMove:mMovedis"+mMoveDistance);
                if (mMoveDistance > RATE * mTextSizeNormal / 2) {//向下滑动
                    moveTailToHead();
                    mMoveDistance = mMoveDistance - RATE * mTextSizeNormal;
                } else if (mMoveDistance < -RATE * mTextSizeNormal / 2) {//向上滑动
                    moveHeadToTail();
                    mMoveDistance = mMoveDistance + RATE * mTextSizeNormal;
                }
                mStartTouchY = event.getY();
                Log.d(TAG, "onTouchEvent: actionFinalMove:mMovedis"+mMoveDistance);
                invalidate();
//                Log.i(TAG, "-mMoveDistance---" + mMoveDistance);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);

                Log.d(TAG+"3", "onTouchEvent: velocity"+mFlingDirection);
                if (Math.abs(mMoveDistance) < 0.0001) {
                    mMoveDistance = 0;
                    isRoll=false;
                    invalidate();
                    return true;
                }
                mVelocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
                int velocity= (int) mVelocityTracker.getYVelocity();
                mFlingDirection=velocity>0?FLING_DOWN:FLING_UP;
                Log.d(TAG+4, "onTouchEvent: velocity"+velocity);
                if (Math.abs(velocity) > mMinVelocity) {
//                    if (mFilingTask != null) {
//                        mFilingTask.cancel();
//                        mFilingTask = null;
//                    }
//
//                    mFilingTask = new FilingTask(mHandler);
//                    mTimer.schedule(mFilingTask, 0, 10);
                    Log.d("Myveclot", "onTouchEvent: veclot"+velocity);
                    isInCenter=false;
                    mScroller.fling(0, (int) mMoveDistance
                            ,0,mFlingDirection==FLING_UP?velocity:-velocity,0,0,
                            0,8000);
//                    Log.d("myFinalY", "onTouchEvent: veclot"+getCloseAbsDis(mScroller.getFinalY()));
//                    mScroller.setFinalY(getCloseAbsDis(mScroller.getFinalY()));

                }else{
                    if (mScrollTask != null) {
                        mScrollTask.cancel();
                        mScrollTask = null;
                    }
                    mScrollTask = new ScrollTask(mHandler);
                    mTimer.schedule(mScrollTask, 0, 10);
                }
                //控制fling
//                float firstVelocityX=mVelocityTracker.getYVelocity();
//                finalTouchTime=System.currentTimeMillis();
//                long interval=finalTouchTime-firstTouchTime;
//                jugeFling(interval);
        }
        return true;
    }

//    private int computeDistanceToEndPoint(int remainder) {
//        if (Math.abs(remainder) > mHalfItemHeight)
//            if (mScrollOffsetY < 0)
//                return -mItemHeight - remainder;
//            else
//                return mItemHeight - remainder;
//        else
//            return -remainder;
//    }

//    private void jugeFling(long interval) {
//        if(interval<1500){
////            mMoveDistance+=mMoveDistance/interval*500;
//        }
//    }

    private void moveTailToHead() {
        if (mainTextData.size() > 0&&rightTextData.size()>0) {
            String tail = mainTextData.get(mainTextData.size() - 1);
            mainTextData.remove(mainTextData.size() - 1);
            mainTextData.add(0, tail);
            tail=rightTextData.get(rightTextData.size()-1);
            rightTextData.remove(rightTextData.size()-1);
            rightTextData.add(0,tail);
        }
    }

    private void moveHeadToTail() {
        if (mainTextData.size() > 0&&rightTextData.size()>0) {
            String head = mainTextData.get(0);
            mainTextData.remove(0);
            mainTextData.add(head);
            head = rightTextData.get(0);
            rightTextData.remove(0);
            rightTextData.add(head);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaddingStart=getPaddingStart();
        mPaddingEnd=getPaddingEnd();
        //绘制中间位置
        draw(canvas, 1, 0, mPaintSelect,mPaintSelectRight,mPaintSelectDot);
        //绘制上方数据
        for (int i = 1; i < mSelectPosition - 1; i++) {
            draw(canvas, -1, i, mPaintNormal,mPaintNormalRight,mPaintNormalDot);
        }
        //绘制下方数据
        for (int i = 1; (mSelectPosition + i) < mainTextData.size(); i++) {
            draw(canvas, 1, i, mPaintNormal,mPaintNormalRight,mPaintNormalDot);
        }
        invalidate();
    }

    private void draw(Canvas canvas, int type, int position,Paint mainPaint,Paint rightPaint,Paint dotPaint) {
        float space = RATE * mTextSizeNormal * position + type * mMoveDistance;
        float scale = parabola(mHeight / 5.0f, space);
        int mainAlpha = (int) ((mTextAlphaSelect - mTextAlphaNormal) * scale + mTextAlphaNormal);
        float mainSize = (mTextSizeSelect - mTextSizeNormal) * scale + mTextSizeNormal;
        dotPaint.setTextSize(mainSize/4);
        dotPaint.setAlpha(mainAlpha);
        mainPaint.setTextSize(mainSize);
        mainPaint.setAlpha(mainAlpha);
        rightPaint.setTextSize(mainSize/2);
        rightPaint.setAlpha(mainAlpha);
        if(!isRoll&&position!=0){
            dotPaint.setAlpha(0);
            mainPaint.setAlpha(0);
            rightPaint.setAlpha(0);
        }
        Log.d(TAG, "draw: mMove:"+mMoveDistance);
        float x = mWidth / 2.0f;
        float y = mHeight / 2.0f + type * space;
        Paint.FontMetricsInt fmi = mainPaint.getFontMetricsInt();
        float baseline = y + (fmi.bottom - fmi.top) / 2.0f - fmi.descent;
        float width = mainPaint.measureText("15")/3;
        canvas.drawText(mainTextData.get(mSelectPosition + type * position),
                x - width / 2 + mPaddingStart - mPaddingEnd, baseline, mainPaint);
        //right
        Paint.FontMetricsInt rightFmi=rightPaint.getFontMetricsInt();
        baseline=y - (fmi.bottom - fmi.top) / 2.0f +(rightFmi.bottom-rightFmi.top)+rightFmi.descent;
        canvas.drawText(rightTextData.get(mSelectPosition + type * position),
                x - width / 2 + mPaddingStart - mPaddingEnd
                        + mainPaint.measureText(mainTextData.get(mSelectPosition + type * position))
                -(mainToRightDis+mainToRightDis*scale),
                baseline, rightPaint);
        //dot
        fmi=dotPaint.getFontMetricsInt();
        baseline=y + (fmi.bottom - fmi.top) / 2.0f- (fmi.bottom-fmi.top)+fmi.descent;
        //Log.d(TAG, "draw: right"+rightPaint.measureText(rightTextData.get(mSelectPosition + type * position)));
        canvas.drawText("o",
                x - width / 2 + mPaddingStart - mPaddingEnd + mainPaint
                        .measureText(mainTextData.get(mSelectPosition + type * position)),
                baseline, dotPaint);
    }

    private float parabola(float zero, float x) {
        float y = (float) (1 - Math.pow(x / zero, 2));
        return y < 0 ? 0 : y;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        mScroller.fling(0,(int)mMoveDistance,(int)velocityX,(int)velocityY,0
//                ,0,0, (int) ((32-mSelectPosition)*mTextSizeNormal));
        return false;
    }

    static class MHandler extends Handler {
        private WeakReference<View> mWeakReference;
        private final static int STATE_SCROLL=0x123;
        private final static int STATE_FILING=0x1234;


        MHandler(View view) {
            this.mWeakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STATE_SCROLL:onScroll();break;
//                case STATE_FILING:onFiling();break;
            }

        }
//
//        private void onFiling() {
//            RollView mRollView = (RollView) mWeakReference.get();
//            if (mRollView  != null) {
//                if (Math.abs(RollView.mMoveDistance) < SPEED) {
//                    mRollView.mMoveDistance = 0;
//                    if (mRollView.mScrollTask != null) {
//                        mRollView.mScrollTask.cancel();
//                        mRollView.mScrollTask = null;
//                        if (mRollView.mOnSelectListener != null)
//                            mRollView.mOnSelectListener.onSelect(mRollView,
//                                    mRollView.mainTextData.get(mRollView.mSelectPosition)
//                                            +mRollView.rightTextData.get(mRollView.mSelectPosition));
//                    }
//                    mRollView.isRoll=false;
//                    mRollView.isInCenter=true;
//                } else {
//                    //控制方向
//                    mRollView.mMoveDistance = mRollView.mMoveDistance - mRollView.mMoveDistance /
//                            Math.abs(mRollView.mMoveDistance) * SPEED;
//                }
//                mRollView.invalidate();
//            }
//        }

        public void onScroll(){
            RollView mRollView = (RollView) mWeakReference.get();
            if (mRollView  != null) {
                if (Math.abs(RollView.mMoveDistance) < SPEED) {
                    mRollView.mMoveDistance = 0;
                    if (mRollView.mScrollTask != null) {
                        mRollView.mScrollTask.cancel();
                        mRollView.mScrollTask = null;
                        if (mRollView.mOnSelectListener != null)
                            mRollView.mOnSelectListener.onSelect(mRollView,
                                    mRollView.mainTextData.get(mRollView.mSelectPosition)
                                            +mRollView.rightTextData.get(mRollView.mSelectPosition));
                    }
                    mRollView.isRoll=false;
                    mRollView.isInCenter=true;
                    mRollView.mFlingDirection=0;
                } else {
                    //控制方向
                    mRollView.mMoveDistance = mRollView.mMoveDistance - mRollView.mMoveDistance /
                            Math.abs(mRollView.mMoveDistance) * SPEED;
                    Log.d(mRollView.TAG, "onScroll: mMovedis"+mRollView.mMoveDistance);
                }
                mRollView.invalidate();
            }
        }
    }



    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            if(mFlingDirection==FLING_DOWN){
                mMoveDistance+=mScroller.getCurrY();
                Log.d(TAG+"1", "computeScroll: FLING_DOWN"+mMoveDistance);
                Log.d(TAG+"currY", "computeScroll: FLING_DOWN"+mScroller.getCurrY());
            }else if(mFlingDirection==FLING_UP){
                mMoveDistance-=mScroller.getCurrY();
                Log.d(TAG+"currY", "computeScroll: FLING_UP"+mScroller.getCurrY());
                Log.d(TAG+"1", "computeScroll: FLING_UP"+mMoveDistance);
            }
            Log.d(TAG+"2", "computeScroll: currY"+mScroller.getCurrY());
            Log.d(TAG+"2", "computeScroll: FinalY"+mScroller.getFinalY());
            Log.d(TAG+"2", "computeScroll: Velocity"+mScroller.getCurrVelocity());
        }else{

            Log.d(TAG, "computeScroll: distance:"+mMoveDistance);

            if (Math.abs(mMoveDistance) < 0.0001) {
                mMoveDistance = 0;
                //isRoll=false;
                //postInvalidate();
            }
            if (mMoveDistance > RATE * mTextSizeNormal / 2) {//向下滑动
                moveTailToHead();
                mMoveDistance = mMoveDistance - RATE * mTextSizeNormal;
                Log.d(TAG+"1", "computeScroll: DowmDis"+mMoveDistance);
            } else if (mMoveDistance < -RATE * mTextSizeNormal / 2) {//向上滑动
                moveHeadToTail();
                mMoveDistance = mMoveDistance + RATE * mTextSizeNormal;
                Log.d(TAG+"1", "computeScroll: UpDis"+mMoveDistance);
            }
            if(!isInCenter){
                isInCenter=true;
                if (mScrollTask != null) {
                    mScrollTask.cancel();
                    mScrollTask = null;
                }
                mScrollTask = new ScrollTask(mHandler);
                mTimer.schedule(mScrollTask, 0, 10);
            }
        }
    }

    class ScrollTask extends TimerTask {
        Handler handler;

        ScrollTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Message msg=handler.obtainMessage();
            msg.what=MHandler.STATE_SCROLL;
            handler.sendMessage(msg);
        }
    }

    class FilingTask extends TimerTask {
        Handler handler;

        FilingTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Message msg=handler.obtainMessage();
            msg.what=MHandler.STATE_FILING;
            handler.sendMessage(msg);
        }
    }

    public interface OnSelectListener {
        void onSelect(View view, String tmp);
    }

    public int getmSelectPosition() {
        return mSelectPosition;
    }

    public void setmSelectPosition(int mSelectPosition) {
        this.mSelectPosition = mSelectPosition;
    }


    public int getCloseAbsDis(int move){
        for(int i=1;;i++){
            int disFront= (int) ((i-1)*mTextSizeNormal*RATE);
            int disFoot= (int) ((i*mTextSizeNormal)*RATE);
            if(disFront<=Math.abs(move)&&Math.abs(move)<=disFoot){
                int frontdis=Math.abs(move)-disFront;
                int footdis=disFoot-Math.abs(move);
                if(move>0)
                return frontdis<footdis?disFront:disFoot;
                else
                return frontdis<footdis?-disFront:-disFoot;
            }
        }
    }


    public static void main(String[] args){
        int sum=0;
        for(int i=2;i<100;i++){
            if(juge(i)){
                sum+=i;
                System.out.println(i);
                System.out.println("\n");
            }
        }
        System.out.println(sum);
    }

    public static boolean juge(int num){
        for(int i=2;i<num;i++){
            if(num%i==0)return false;
        }
        return true;
    }

}

package com.fearlessbear.scrollerexercise;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.HashMap;

/**
 * Created by BigFaceBear on 2018.03.16
 */

public class ScrollLayout extends ViewGroup {
    Scroller mScroller;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;

    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;

    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;

    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    //key为pointerId，value为每个pointerId前一次移动的X坐标
    HashMap<Integer, Float> mLastMoveXMap;

    public ScrollLayout(Context context) {
        super(context);
    }

    public ScrollLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mLastMoveXMap = new HashMap<>();
    }

    public ScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
            }
            // 初始化左右边界值
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int pid = ev.getPointerId(0);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mXDown = ev.getX();
                mLastMoveXMap.put(pid, mXDown);
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getX();
                float diff = Math.abs(mXMove - mXDown);
                mLastMoveXMap.put(pid, ev.getX(0));

                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

       /* String event = null;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                event = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                event = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_UP:
                event = "ACTION_UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                event = "ACTION_CANCEL";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                event = "ACTION_POINTER_DOWN";
                break;
            case MotionEvent.ACTION_POINTER_INDEX_MASK:
                event = "ACTION_POINTER_INDEX_MASK";
                break;
            case MotionEvent.ACTION_POINTER_INDEX_SHIFT:
                event = "ACTION_POINTER_INDEX_SHIFT";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                event = "ACTION_POINTER_UP";
                break;
            case MotionEvent.ACTION_MASK:
                event = "ACTION_MASK";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                event = "ACTION_OUTSIDE";
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                event = "ACTION_POINTER_2_DOWN";
                break;
            default:
                event = "unknown";

        }*/
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_POINTER_1_DOWN:
//                event = "ACTION_POINTER_1_DOWN";
//                break;
//            case MotionEvent.ACTION_POINTER_1_UP:
//                event = "ACTION_POINTER_1_UP";
//                break;
//            case MotionEvent.ACTION_POINTER_2_DOWN:
//                event = "ACTION_POINTER_2_DOWN";
//                break;
//            case MotionEvent.ACTION_POINTER_2_UP:
//                event = "ACTION_POINTER_2_UP";
//                break;
//            case MotionEvent.ACTION_POINTER_3_DOWN:
//                event = "ACTION_POINTER_3_DOWN";
//                break;
//            case MotionEvent.ACTION_POINTER_3_UP:
//                event = "ACTION_POINTER_3_UP";
//                break;
//
//        }
//        Log.i("bear", "event is " + event);
        int pointerIndex;
        int pointerId;

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = ev.getActionIndex();
                pointerId = ev.getPointerId(pointerIndex);
                mLastMoveXMap.put(pointerId, ev.getX(pointerIndex));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = ev.getActionIndex();
                pointerId = ev.getPointerId(pointerIndex);
                mLastMoveXMap.remove(pointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                //拿到主手指的当前X坐标
                mXMove = ev.getX();

                //拿到当前主手指上一次移动的X坐标
                pointerId = ev.getPointerId(0);
                float lastMoveX = mLastMoveXMap.get(pointerId);

                int scrolledX = (int) (lastMoveX - mXMove);
                if (getScrollX() + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + scrolledX + getWidth() > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }

                scrollBy(scrolledX, 0);

                for (int i = 0; i < ev.getPointerCount(); i++) {
                    pointerId = ev.getPointerId(i);
                    mLastMoveXMap.put(pointerId, ev.getX(i));
                }
                return true;
            case MotionEvent.ACTION_UP:
                mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 1000);
                invalidate();
                return true;
        }


        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}

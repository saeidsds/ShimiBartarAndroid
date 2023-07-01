package com.multiplatform.helper;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

public class CustomSwipeToRefresh extends SwipyRefreshLayout {

       private int mTouchSlop;
       private float mPrevX;

       public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
              super(context, attrs);

              mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
       }

       @Override
       public boolean onInterceptTouchEvent(MotionEvent event) {

              switch (event.getAction()) {
                     case MotionEvent.ACTION_DOWN:
                            mPrevX = MotionEvent.obtain(event).getX();
                            break;

                     case MotionEvent.ACTION_MOVE:
                            final float eventX = event.getX();
                            float xDiff = Math.abs(eventX - mPrevX);

                            if (xDiff > mTouchSlop) {
                                   return false;
                            }
              }

              return super.onInterceptTouchEvent(event);
       }
}



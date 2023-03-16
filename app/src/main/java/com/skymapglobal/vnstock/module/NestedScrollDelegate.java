package com.skymapglobal.vnstock.module;


import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.tradingview.lightweightcharts.view.gesture.TouchDelegate;

public class NestedScrollDelegate implements TouchDelegate {
    private Context context;
    private NestedScrollListener listener;
    private Integer lastXDown = 0;
    private Double limitToScrollParent = 0.0;
    private Double scrollingPart = 0.8;
    private GestureDetector gestureDetector;

    public NestedScrollDelegate(Context context, NestedScrollListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void beforeTouchEvent(@NonNull ViewGroup viewGroup) {
        if (gestureDetector == null) {
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                    viewGroup.requestDisallowInterceptTouchEvent(true);
                }
            };
            gestureDetector = new GestureDetector(context, listener);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull ViewGroup view, @NonNull MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        Integer eventX = Math.round(event.getX());
        Integer deltaX = lastXDown - eventX;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isHorizontalScroll(deltaX)) {
                    view.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            case MotionEvent.ACTION_DOWN:
                if (eventX > limitToScrollParent) {
                    view.requestDisallowInterceptTouchEvent(true);
                }
                lastXDown = eventX;
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                view.requestDisallowInterceptTouchEvent(false);
                lastXDown = 0;
                return false;
            default:
                return false;
        }
    }

    private Boolean isHorizontalScroll(Integer deltaX) {
        return Math.abs(deltaX) > dpToPx(10);
    }

    private Integer dpToPx(Integer value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics()));
    }
}
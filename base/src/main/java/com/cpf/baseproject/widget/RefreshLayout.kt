@file:Suppress("unused")

package com.cpf.baseproject.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration

/**
 * Created by cpf on 2017/12/14.
 * 自定义SwipeRefreshLayout控件，解决左右滑动冲突问题
 */
class RefreshLayout : SwipeRefreshLayout {

    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsDrag = false//是否拖动中

    private var mTouchSlop = 0

    constructor(context: Context?) : super(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {

            MotionEvent.ACTION_DOWN -> {
                mStartX = ev.x
                mStartY = ev.y
                mIsDrag = false
            }

            MotionEvent.ACTION_MOVE -> {

                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsDrag) {
                    return false
                }
                //获取当前手的位置

                val endY = ev.y
                val endX = ev.x
                val distanceX = Math.abs(endX - mStartX)
                val distanceY = Math.abs(endY - mStartY)
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsDrag = true
                    return false
                }
                mIsDrag = false
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mIsDrag = false
        }


        return super.onInterceptTouchEvent(ev)
    }
}
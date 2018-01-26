@file:Suppress("DEPRECATION", "unused")

package com.cpf.baseproject.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView


class SwipeBackLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {

    private var dragEdge = DragEdge.TOP

    private val viewDragHelper: ViewDragHelper

    private var target: View? = null

    private var scrollChild: View? = null

    private var verticalDragRange = 0

    private var horizontalDragRange = 0

    private var draggingState = 0

    private var draggingOffset: Int = 0

    /**
     * Whether allow to pull this layout.
     */
    private var enablePullToBack = true

    /**
     * the anchor of calling finish.
     */
    private var finishAnchor = 0f

    private var enableFlingBack = true

    private var swipeBackListener: SwipeBackListener? = null

    private val dragRange: Int
        get() {
            return when (dragEdge) {
                SwipeBackLayout.DragEdge.TOP, SwipeBackLayout.DragEdge.BOTTOM -> verticalDragRange
                SwipeBackLayout.DragEdge.LEFT, SwipeBackLayout.DragEdge.RIGHT -> horizontalDragRange
            }
        }

    enum class DragEdge {
        LEFT,

        TOP,

        RIGHT,

        BOTTOM
    }

    fun setDragEdge(dragEdge: DragEdge) {
        this.dragEdge = dragEdge
    }

    /**
     * Set the anchor of calling finish.
     *
     * @param offset
     */
    fun setFinishAnchor(offset: Float) {
        finishAnchor = offset
    }

    /**
     * Whether allow to finish activity by fling the layout.
     *
     * @param b
     */
    fun setEnableFlingBack(b: Boolean) {
        enableFlingBack = b
    }

    @Deprecated("")
    fun setOnPullToBackListener(listener: SwipeBackListener) {
        swipeBackListener = listener
    }

    fun setOnSwipeBackListener(listener: SwipeBackListener) {
        swipeBackListener = listener
    }

    init {

        viewDragHelper = ViewDragHelper.create(this, 1.0f, ViewDragHelperCallBack())
    }

    fun setScrollChild(view: View) {
        scrollChild = view
    }

    fun setEnablePullToBack(b: Boolean) {
        enablePullToBack = b
    }

    private fun ensureTarget() {
        if (target == null) {
            if (childCount > 1) {
                throw IllegalStateException("SwipeBackLayout must contains only one direct child")
            }
            target = getChildAt(0)

            if (scrollChild == null && target != null) {
                if (target is ViewGroup) {
                    findScrollView(target as ViewGroup)
                } else {
                    scrollChild = target
                }

            }
        }
    }

    /**
     * Find out the scrollable child view from a ViewGroup.
     *
     * @param viewGroup
     */
    private fun findScrollView(viewGroup: ViewGroup) {
        scrollChild = viewGroup
        if (viewGroup.childCount > 0) {
            val count = viewGroup.childCount
            var child: View
            for (i in 0 until count) {
                child = viewGroup.getChildAt(i)
                if (child is AbsListView || child is ScrollView || child is ViewPager || child is WebView) {
                    scrollChild = child
                    return
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) return

        val child = getChildAt(0)

        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = childLeft + childWidth
        val childBottom = childTop + childHeight
        child.layout(childLeft, childTop, childRight, childBottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount > 1) {
            throw IllegalStateException("SwipeBackLayout must contains only one direct child.")
        }

        if (childCount > 0) {
            val measureWidth = View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY)
            val measureHeight = View.MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY)
            getChildAt(0).measure(measureWidth, measureHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        verticalDragRange = h
        horizontalDragRange = w

        finishAnchor = when (dragEdge) {
            SwipeBackLayout.DragEdge.TOP, SwipeBackLayout.DragEdge.BOTTOM -> if (finishAnchor > 0) finishAnchor else verticalDragRange * BACK_FACTOR
            SwipeBackLayout.DragEdge.LEFT, SwipeBackLayout.DragEdge.RIGHT -> if (finishAnchor > 0) finishAnchor else horizontalDragRange * BACK_FACTOR
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var handled = false
        ensureTarget()
        if (isEnabled) {
            if (ev.x < 100)
            //当x<100,即左侧边缘才起作用
                handled = viewDragHelper.shouldInterceptTouchEvent(ev)
        } else {
            viewDragHelper.cancel()
        }
        return if (!handled) super.onInterceptTouchEvent(ev) else handled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun canChildScrollUp(): Boolean {
        return ViewCompat.canScrollVertically(scrollChild!!, -1)
    }

    fun canChildScrollDown(): Boolean {
        return ViewCompat.canScrollVertically(scrollChild!!, 1)
    }

    private fun canChildScrollRight(): Boolean {
        return ViewCompat.canScrollHorizontally(scrollChild!!, -1)
    }

    private fun canChildScrollLeft(): Boolean {
        return ViewCompat.canScrollHorizontally(scrollChild!!, 1)
    }

    private fun finish() {
        val act = context as Activity
        act.finish()
        act.overridePendingTransition(0, android.R.anim.fade_out)
    }

    private inner class ViewDragHelperCallBack : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === target && enablePullToBack
        }

        override fun getViewVerticalDragRange(child: View?): Int {
            return verticalDragRange
        }

        override fun getViewHorizontalDragRange(child: View?): Int {
            return horizontalDragRange
        }

        override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {

            var result = 0

            if (dragEdge == DragEdge.TOP && !canChildScrollUp() && top > 0) {
                val topBound = paddingTop
                val bottomBound = verticalDragRange
                result = Math.min(Math.max(top, topBound), bottomBound)
            } else if (dragEdge == DragEdge.BOTTOM && !canChildScrollDown() && top < 0) {
                val topBound = -verticalDragRange
                val bottomBound = paddingTop
                result = Math.min(Math.max(top, topBound), bottomBound)
            }

            return result
        }

        override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {

            var result = 0

            if (dragEdge == DragEdge.LEFT && !canChildScrollRight() && left > 0) {
                val leftBound = paddingLeft
                val rightBound = horizontalDragRange
                result = Math.min(Math.max(left, leftBound), rightBound)
            } else if (dragEdge == DragEdge.RIGHT && !canChildScrollLeft() && left < 0) {
                val leftBound = -horizontalDragRange
                val rightBound = paddingLeft
                result = Math.min(Math.max(left, leftBound), rightBound)
            }

            return result
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == draggingState) return

            if ((draggingState == ViewDragHelper.STATE_DRAGGING || draggingState == ViewDragHelper.STATE_SETTLING) && state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.
                if (draggingOffset == dragRange) {
                    finish()
                }
            }

            draggingState = state
        }


        override fun onViewPositionChanged(changedView: View?, left: Int, top: Int, dx: Int, dy: Int) {
            draggingOffset = when (dragEdge) {
                SwipeBackLayout.DragEdge.TOP, SwipeBackLayout.DragEdge.BOTTOM -> Math.abs(top)
                SwipeBackLayout.DragEdge.LEFT, SwipeBackLayout.DragEdge.RIGHT -> Math.abs(left)
            }

            //The proportion of the sliding.
            var fractionAnchor = draggingOffset.toFloat() / finishAnchor
            if (fractionAnchor >= 1) fractionAnchor = 1f

            var fractionScreen = draggingOffset.toFloat() / dragRange.toFloat()
            if (fractionScreen >= 1) fractionScreen = 1f

            if (swipeBackListener != null) {
                swipeBackListener!!.onViewPositionChanged(fractionAnchor, fractionScreen)
            }
        }

        override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
            if (draggingOffset == 0) return

            if (draggingOffset == dragRange) return

            var isBack = false

            if (enableFlingBack && backBySpeed(xvel, yvel)) {
                isBack = !canChildScrollUp()
            } else if (draggingOffset >= finishAnchor) {
                isBack = true
            } else if (draggingOffset < finishAnchor) {
                isBack = false
            }

            val finalLeft: Int
            val finalTop: Int
            when (dragEdge) {
                SwipeBackLayout.DragEdge.LEFT -> {
                    finalLeft = if (isBack) horizontalDragRange else 0
                    smoothScrollToX(finalLeft)
                }
                SwipeBackLayout.DragEdge.RIGHT -> {
                    finalLeft = if (isBack) -horizontalDragRange else 0
                    smoothScrollToX(finalLeft)
                }
                SwipeBackLayout.DragEdge.TOP -> {
                    finalTop = if (isBack) verticalDragRange else 0
                    smoothScrollToY(finalTop)
                }
                SwipeBackLayout.DragEdge.BOTTOM -> {
                    finalTop = if (isBack) -verticalDragRange else 0
                    smoothScrollToY(finalTop)
                }
            }

        }
    }

    private fun backBySpeed(xvel: Float, yvel: Float): Boolean {
        when (dragEdge) {
            SwipeBackLayout.DragEdge.TOP, SwipeBackLayout.DragEdge.BOTTOM -> if (Math.abs(yvel) > Math.abs(xvel) && Math.abs(yvel) > AUTO_FINISHED_SPEED_LIMIT) {
                return if (dragEdge == DragEdge.TOP) !canChildScrollUp() else !canChildScrollDown()
            }
            SwipeBackLayout.DragEdge.LEFT, SwipeBackLayout.DragEdge.RIGHT -> if (Math.abs(xvel) > Math.abs(yvel) && Math.abs(xvel) > AUTO_FINISHED_SPEED_LIMIT) {
                return if (dragEdge == DragEdge.LEFT) !canChildScrollLeft() else !canChildScrollRight()
            }
        }
        return false
    }

    private fun smoothScrollToX(finalLeft: Int) {
        if (viewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(this@SwipeBackLayout)
        }
    }

    private fun smoothScrollToY(finalTop: Int) {
        if (viewDragHelper.settleCapturedViewAt(0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this@SwipeBackLayout)
        }
    }

    interface SwipeBackListener {

        /**
         * Return scrolled fraction of the layout.
         *
         * @param fractionAnchor relative to the anchor.
         * @param fractionScreen relative to the screen.
         */
        fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float)

    }

    companion object {

        private val TAG = "SwipeBackLayout"


        private val AUTO_FINISHED_SPEED_LIMIT = 2000.0

        private val BACK_FACTOR = 0.5f
    }

}


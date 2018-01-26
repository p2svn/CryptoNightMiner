@file:Suppress("DEPRECATION")

package com.cpf.baseproject.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import com.cpf.baseproject.R
import com.cpf.baseproject.widget.SwipeBackLayout

abstract class BaseActivity : AppCompatActivity() {

    private var mLoadResource = false
    private var mPresenter: BasePresenter? = null
    private var swipeBackLayout: SwipeBackLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (canSwipeBack()) {
            setContentView(getContainer())
            swipeBackLayout!!.addView(createView())
        } else {
            if (initActionBarLayout() > 0) {
                setContentView(createView())
            } else {
                setContentView(initLayout())
            }
        }
        mLoadResource = true
        mPresenter = createPresenter()
        mPresenter?.attach(this)
        mPresenter?.getLifeObserver()?.onCreate()
    }


    override fun onStart() {
        super.onStart()
        mPresenter?.getLifeObserver()?.onStart()
        if (mLoadResource) {
            mLoadResource = false
            initView()
            initEvent()
            initData()
        }
    }

    private fun getContainer(): View {
        val container = RelativeLayout(this)
        swipeBackLayout = SwipeBackLayout(this)
        swipeBackLayout!!.setDragEdge(SwipeBackLayout.DragEdge.LEFT)
        val ivShadow = ImageView(this)
        ivShadow.setBackgroundColor(resources.getColor(R.color.swipe_back_bg))
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        container.addView(ivShadow, params)
        container.addView(swipeBackLayout)
        swipeBackLayout!!.setOnSwipeBackListener(object : SwipeBackLayout.SwipeBackListener {
            override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) {
                ivShadow.alpha = 1 - fractionScreen
            }
        })
        return container
    }

    open fun createView(): View {
        val mRootView = LinearLayoutCompat(this)
        mRootView.setBackgroundColor(resources.getColor(R.color.md_white_1000))
        mRootView.orientation = LinearLayoutCompat.VERTICAL
        if (initActionBarLayout() > 0) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            mRootView.addView(LayoutInflater.from(this).inflate(initActionBarLayout(), mRootView, false))
        }
        if (initLayout() > 0) {
            mRootView.addView(LayoutInflater.from(this).inflate(initLayout(), mRootView, false))
        }
        return mRootView
    }

    abstract fun canSwipeBack(): Boolean

    abstract fun initActionBarLayout(): Int

    abstract fun initLayout(): Int

    abstract fun initView()

    abstract fun initEvent()

    abstract fun initData()

    open fun createPresenter(): BasePresenter? {
        return null
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getLifeObserver()?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter?.getLifeObserver()?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPresenter?.getLifeObserver()?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.getLifeObserver()?.onDestroy()
        mPresenter?.detach()
    }

}

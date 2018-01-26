@file:Suppress("unused")

package com.cpf.baseproject.base

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    private var mPresenter: BasePresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(initLayout(), container, false)

    abstract fun initLayout(): Int

    abstract fun initView()

    abstract fun initEvent()

    abstract fun initData()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter?.attach(context)
        mPresenter?.getLifeObserver()?.onCreate()
        initView()
        initEvent()
        initData()
    }

    fun <T : Activity> getCurrActivity(): T {
        @Suppress("UNCHECKED_CAST")
        return activity as T
    }

    open fun createPresenter(): BasePresenter? {
        return null
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.getLifeObserver()?.onStart()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.getLifeObserver()?.onDestroy()
        mPresenter?.detach()
    }
}
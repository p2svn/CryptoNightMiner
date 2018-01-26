package com.cpf.baseproject.base

import android.content.Context

@Suppress("unused")
abstract class BasePresenter {

    var mContext: Context? = null

    open fun attach(context: Context) {
        this.mContext = context
    }

    open fun detach() {
        mContext = null
    }

    abstract fun getLifeObserver(): ILifeObserver?

}
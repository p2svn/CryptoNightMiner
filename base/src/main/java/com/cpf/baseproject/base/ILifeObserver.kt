package com.cpf.baseproject.base

/**
 * Created by cpf on 2017/12/8.
 */
interface ILifeObserver {

    fun onCreate()

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onDestroy()
}
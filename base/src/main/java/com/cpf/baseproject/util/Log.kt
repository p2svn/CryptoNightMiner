package com.cpf.baseproject.util

import android.util.Log
import com.cpf.baseproject.BuildConfig

/**
 *log输出
 */

fun logPrint(any: Any?) {

    if (any != null && BuildConfig.DEBUG) {
        Log.e("APP", any.toString())
    }

}
package com.cpf.baseproject.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences
 */
fun getSharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences("APP", Context.MODE_PRIVATE)

fun putInt(context: Context, key: String, value: Int) {
    getSharedPref(context).edit().putInt(key, value).apply()
}

fun putString(context: Context, key: String, value: String) {
    getSharedPref(context).edit().putString(key, value).apply()
}

fun putBoolean(context: Context, key: String, value: Boolean) {
    getSharedPref(context).edit().putBoolean(key, value).apply()
}

fun putFloat(context: Context, key: String, value: Float) {
    getSharedPref(context).edit().putFloat(key, value).apply()
}

fun putLong(context: Context, key: String, value: Long) {
    getSharedPref(context).edit().putLong(key, value).apply()
}
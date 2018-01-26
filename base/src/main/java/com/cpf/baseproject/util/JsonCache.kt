@file:Suppress("unused")

package com.cpf.baseproject.util

import android.content.Context
import com.google.gson.Gson

val gson = Gson()

fun put(context: Context, key: String, value: Any) {
    putString(context, key, getBase64(gson.toJson(value)))
}

fun <T> get(context: Context, key: String, clazz: Class<T>): T? {
    return try {
        gson.fromJson(getFromBase64(getSharedPref(context).getString(key, "")), clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


inline fun <reified T> getList(context: Context, key: String): List<T>? {
    return try {
        gson.fromJson(getFromBase64(getSharedPref(context).getString(key, "")), emptyArray<T>().javaClass).toList()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


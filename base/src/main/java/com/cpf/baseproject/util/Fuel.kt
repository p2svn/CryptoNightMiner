package com.cpf.baseproject.util

import android.content.Context
import com.cpf.baseproject.R
import com.cpf.baseproject.bean.JsonMessage
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success

/**
 * Created by cpf on 2017/12/11.
 * Fuel扩展
 */

fun Request.responseString(context: Context, success: (jsonMessage: JsonMessage) -> Unit, failure: () -> Unit) {
    responseString { _, _, result ->
        result.success {
            logPrint(result.get())
            var jsonMessage: JsonMessage? = null
            try {
                jsonMessage = gson.fromJson<JsonMessage>(result.get(), JsonMessage::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (jsonMessage != null) {
                success(jsonMessage)
            } else {
                failure()
                toast(context, context.getString(R.string.data_parse_exception))
            }
        }
        result.failure {
            failure()
            toast(context, context.getString(R.string.net_connect_timeout))
        }
    }
}
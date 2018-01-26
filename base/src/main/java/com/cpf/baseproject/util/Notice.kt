package com.cpf.baseproject.util

import android.content.Context
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.widget.Toast

/**
 * toast
 */
fun toast(context: Context, string: String) {
    Toast.makeText(context.applicationContext, string, Toast.LENGTH_SHORT).show()
}

fun toast(context: Context, strInt: Int) {
    toast(context, context.getString(strInt))
}

/**
 * md中的底部通知
 */
fun snackBar(view: View, string: String, type: Int) {
    snackBar(view, string, type, null, null)
}

fun snackBar(view: View, string: String, type: Int, actionStr: String?, clickListener: View.OnClickListener?) {
    val snackBar = Snackbar.make(view, string, type)
    if (!TextUtils.isEmpty(actionStr) || clickListener != null) {
        snackBar.setAction(actionStr, clickListener)
    }
    snackBar.show()
}

fun snackBar(view: View, strInt: Int, type: Int) {
    snackBar(view, view.context.getString(strInt), type, null, null)
}

fun snackBar(view: View, strInt: Int, type: Int, actionStr: String?, clickListener: View.OnClickListener?) {
    snackBar(view, view.context.getString(strInt), type, actionStr, clickListener)
}
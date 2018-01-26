package com.cpf.baseproject.util

import android.os.Build
import android.support.annotation.RequiresApi
import android.webkit.WebView

/**
 * Created by cpf on 2017/12/16.
 * webView扩展
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun WebView.execJS(jsString: String) {
    logPrint(jsString)
    evaluateJavascript("javascript:" + jsString, null)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun WebView.execJS(jsString: String, block: (result: String) -> Unit) {
    evaluateJavascript("javascript:" + jsString, {
        block(it)
    })
}
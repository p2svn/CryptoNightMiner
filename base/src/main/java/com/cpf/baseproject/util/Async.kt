@file:Suppress("unused")

package com.cpf.baseproject.util

import android.os.Handler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 全局线程池
 */
var threadService: ExecutorService = Executors.newCachedThreadPool()
/**
 * 单任务线程池
 */
var singleThreadService: ExecutorService = Executors.newSingleThreadExecutor()
/**
 * 全局handler
 */
val handler = Handler()

/**
 * 启动异步任务
 */
fun async(block: () -> Unit) {
    threadService.execute {
        block()
    }
}

/**
 * ui线程任务
 */
fun ui(block: () -> Unit) {
    handler.post {
        block()
    }
}





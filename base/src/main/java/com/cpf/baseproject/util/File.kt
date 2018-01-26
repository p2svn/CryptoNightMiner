package com.cpf.baseproject.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

fun copyFilesFromAssets(context: Context, assetsPath: String, savePath: String) {
    try {
        val fileNames = context.assets.list(assetsPath)// 获取assets目录下的所有文件及目录名
        if (fileNames.isNotEmpty()) {// 如果是目录
            val file = File(savePath)
            file.mkdirs()// 如果文件夹不存在，则递归
            for (fileName in fileNames) {
                copyFilesFromAssets(context, assetsPath + "/" + fileName,
                        savePath + "/" + fileName)
            }
        } else {// 如果是文件
            val inputStream = context.assets.open(assetsPath)
            val fos = FileOutputStream(File(savePath))
            val buffer = ByteArray(1024)
            var byteCount = inputStream.read(buffer)
            while (byteCount != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount)// 将读取的输入流写入到输出流
                byteCount = inputStream.read(buffer)
            }
            fos.flush()// 刷新缓冲区
            inputStream.close()
            fos.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
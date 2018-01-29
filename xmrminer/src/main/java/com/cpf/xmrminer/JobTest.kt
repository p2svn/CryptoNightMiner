@file:Suppress("unused")

package com.cpf.xmrminer

import com.cpf.baseproject.util.logPrint
import com.cpf.cryptonight.Miner

/**
 * Created by cpf on 2017/12/28.
 * Job
 */
class JobTest(private val miner: MinerManager,
              private val start: Int,
              private val jobBean: Job.JobBean,
              private val block: () -> Unit) : Runnable {

    override fun run() {
        val startTime = System.currentTimeMillis()
        val blobByte = HexUtil.unhexlify(jobBean.blob)
        val nonce = start
        ByteArray(4)
        val result = ByteArray(32)
        blobByte[39] = nonce.toByte()
        blobByte[40] = (nonce shr 8).toByte()
        blobByte[41] = (nonce shr 16).toByte()
        blobByte[42] = (nonce shr 24).toByte()
        Miner.fastHash(blobByte, result)
        val resultStr = HexUtil.hexlify(result)
        val rs = resultStr.substring(resultStr.length - jobBean.target.length)
        val str = miner.swapEndian(rs)
        str.toLong(16)
        miner.mSleepTime = System.currentTimeMillis() - startTime
        logPrint("Execution algorithm time-consuming ${miner.mSleepTime}")
        block()
    }
}
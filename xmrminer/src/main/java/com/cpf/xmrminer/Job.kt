package com.cpf.xmrminer

import com.cpf.baseproject.util.logPrint
import com.cpf.cryptonight.Miner
import org.json.JSONObject

/**
 * Created by cpf on 2017/12/28.
 * Job
 */
class Job(private val miner: MinerManager,
          private val start: Int,
          private val space: Int,
          private val jobBean: JobBean) : Runnable {

    override fun run() {
        val blobByte = HexUtil.unhexlify(jobBean.blob)
        var nonce = start
        val nonceByte = ByteArray(4)
        val result = ByteArray(32)
        while (nonce < 0x7fffffff) {
            if (!miner.isRunning) return
            if (miner.mJobID != jobBean.jobId) return
            blobByte[39] = nonce.toByte()
            blobByte[40] = (nonce shr 8).toByte()
            blobByte[41] = (nonce shr 16).toByte()
            blobByte[42] = (nonce shr 24).toByte()
            Miner.fastHash(blobByte, result)
            val resultStr = HexUtil.hexlify(result)
            val rs = resultStr.substring(resultStr.length - jobBean.target.length)
            var byteArray = HexUtil.unhexlify(rs)
            byteArray = miner.byteArraySort(byteArray)
            val str = HexUtil.hexlify(byteArray)
            val tarL = str.toLong(16)
            if (tarL < jobBean.target.toLong(16)) {
                logPrint("submit____id${miner.mId}___thread${start}_____${jobBean.target}_____${str}_____$resultStr")
                nonceByte[0] = nonce.toByte()
                nonceByte[1] = (nonce shr 8).toByte()
                nonceByte[2] = (nonce shr 16).toByte()
                nonceByte[3] = (nonce shr 24).toByte()
                send(HexUtil.hexlify(nonceByte), resultStr)
            }
            nonce += space
            miner.mHashCount++
            if (miner.mSpeed < 1f) {
                try {
                    Thread.sleep((miner.mSleepTime * (1 - miner.mSpeed)).toLong())
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun send(nonceHex: String, tar: String) {
        val jsonObj = JSONObject()
        jsonObj.put("id", miner.mId++)
        jsonObj.put("method", "submit")
        jsonObj.put("jsonrpc", "2.0")
        val json = JSONObject()
        json.put("id", jobBean.id)
        json.put("job_id", jobBean.jobId)
        json.put("nonce", nonceHex)
        json.put("result", tar)
        jsonObj.put("params", json)
        miner.mPrintWrite?.println(jsonObj.toString())
        miner.mPrintWrite?.flush()
    }

    class JobBean {
        var blob: String = ""
        var target: String = ""
        var jobId: String = ""
        var id: String = ""
    }
}
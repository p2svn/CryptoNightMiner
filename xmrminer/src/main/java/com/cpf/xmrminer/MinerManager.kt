package com.cpf.xmrminer

import com.cpf.baseproject.util.async
import com.cpf.baseproject.util.logPrint
import org.json.JSONObject
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by cpf on 2017/12/28.
 * MinerManager
 */
class MinerManager {

    var mId = 0
    private var mSocket: Socket? = null
    var mPrintWrite: PrintWriter? = null
    private var mScanner: Scanner? = null
    private var mEvent: MinerEventListener? = null
    private var mMsg: MinerMsgListener? = null
    var isRunning = false
    private var mThreadPool: ExecutorService? = null
    private var mUrl: String = ""
    private var mUser: String = ""
    private var mPWD: String = "x"
    var mThreadNum = 1
    var mSpeed = 0.9f
    var mShareCount = 0L
    var mHashCount = 0L
    private var mSubID = ""
    var mJobID = ""
    var mSleepTime = 0L

    fun byteArraySort(byteArray: ByteArray): ByteArray {
        val temp = ByteArray(byteArray.size)
        for (i in 0 until byteArray.size) {
            temp[i] = byteArray[byteArray.size - 1 - i]
        }
        return temp
    }

    private fun connect() {
        try {
            mMsg?.message("connect...")
            val strArray = mUrl.split(":")
            logPrint(strArray[0] + ":" + strArray[1])
            mSocket = Socket(strArray[0], strArray[1].toInt())
            mPrintWrite = PrintWriter(mSocket?.getOutputStream())
            mScanner = Scanner(mSocket?.getInputStream())
            val jsonObj = JSONObject()
            jsonObj.put("login", mUser)
            jsonObj.put("pass", mPWD)
            val json = JSONObject()
            json.put("id", mId++)
            json.put("method", "login")
            json.put("params", jsonObj)
            mPrintWrite?.println(json.toString())
            mPrintWrite?.flush()
            mMsg?.message("Pool connection is successful")
        } catch (e: Exception) {
            e.printStackTrace()
            mEvent?.error()
            mMsg?.message("connection is failure")
        }
        if (mSocket != null && mSocket!!.isConnected) {
            start()
        }
    }

    private fun handleJob(jobBean: Job.JobBean) {
        synchronized(MinerManager::class) {
            if (mSleepTime == 0L) {
                logPrint("Hash algorithm error")
                stopMiner()
            }
            mMsg?.message("Received job")
            var difficulty = 0
            try {
                val byteArray = HexUtil.unhexlify(jobBean.target)
                val sortArray = byteArraySort(byteArray)
                val str = HexUtil.hexlify(sortArray)
                val targetL = str.toLong(16)
                jobBean.target = str
                difficulty = (Int.MAX_VALUE / targetL).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                mMsg?.message("Difficulty calculation error")
            }
            if (difficulty > 0) {
                mMsg?.message("The current difficulty is $difficulty")
                for (i in 0 until mThreadNum) {
                    mThreadPool?.execute(Job(this, i, mThreadNum, jobBean))
                }
            }
        }
    }

    private fun start() {
        mEvent?.start()
        mMsg?.message("Thread:$mThreadNum")
        mMsg?.message("Speed:${mSpeed * 100}%")
        isRunning = true
        mThreadPool = Executors.newFixedThreadPool(mThreadNum)
        while (isRunning && mSocket!!.isConnected) {
            if (mScanner == null) return
            var result: String? = null
            try {
                result = mScanner!!.nextLine()
                logPrint(result)
            } catch (e: Exception) {

            }
            if (!isRunning || result == null) return
            var json: JSONObject? = null
            try {
                json = JSONObject(result)
            } catch (e: Exception) {
                e.printStackTrace()
                mEvent?.error()
                mMsg?.message("Json parsing error")
            }
            if (json != null) {
                when {
                    json.has("method") -> {
                        when {
                            json.getString("method") == "job" -> {
                                //job
                                val params = json.getJSONObject("params")
                                val jobBean = Job.JobBean()
                                jobBean.jobId = params.getString("job_id")
                                jobBean.blob = params.getString("blob")
                                jobBean.target = params.getString("target")
                                mJobID = jobBean.jobId
                                jobBean.id = mSubID
                                handleJob(jobBean)
                            }
                        }
                    }
                    json.has("result") && !json.isNull("result") && json.isNull("error") -> {
                        val resultObject = json.getJSONObject("result")
                        when {
                            resultObject.has("job") -> {
                                //job
                                val job = resultObject.getJSONObject("job")
                                val jobBean = Job.JobBean()
                                jobBean.jobId = job.getString("job_id")
                                jobBean.blob = job.getString("blob")
                                jobBean.target = job.getString("target")
                                jobBean.id = resultObject.getString("id")
                                mSubID = jobBean.id
                                mJobID = jobBean.jobId
                                if (mSleepTime == 0L) {
                                    async {
                                        try {
                                            val byteArray = HexUtil.unhexlify(jobBean.target)
                                            val sortArray = byteArraySort(byteArray)
                                            val str = HexUtil.hexlify(sortArray)
                                            val jobBeanTest = Job.JobBean()
                                            jobBeanTest.jobId = jobBean.jobId
                                            jobBeanTest.target = str
                                            jobBeanTest.blob = jobBean.blob
                                            jobBeanTest.id = jobBean.id
                                            JobTest(this, 0, jobBeanTest, {
                                                handleJob(jobBean)
                                            }).run()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            mSleepTime = 0
                                        }
                                    }
                                }
                            }
                            resultObject.has("status") -> {
                                if (resultObject.getString("status") == "OK") {
                                    mShareCount++
                                }
                            }
                        }
                    }
                    json.has("error") -> {
                        val error = json.getJSONObject("error")
                        logPrint(error.toString())
                        when {
                            error.getString("message").contains("Incorrect") -> {
                                mMsg?.message("Submit error")
                            }
                            error.getString("message").contains("Invalid") -> {
                                mMsg?.message("Submit is invalid")
                            }
                        }
                    }
                    else -> {
                        mMsg?.message("Unknown json")
                        logPrint(result)
                    }
                }
            }
        }
        if (isRunning) {
            mMsg?.message("Abnormal network, try to reconnect")
            connect()
        } else {
            mEvent?.stop()
        }
    }

    fun startMiner(url: String, user: String, pwd: String) {
        if (!url.contains(":")) {
            mMsg?.message("url is invalid")
            return
        }
        mUrl = url
        mUser = user
        mPWD = pwd
        async {
            connect()
        }
    }

    fun stopMiner() {
        mEvent?.stop()
        mMsg?.message("Mining stop")
        isRunning = false
        try {
            mThreadPool?.shutdownNow()
        } catch (e: Exception) {
        }
        mThreadPool = null
        mPrintWrite?.close()
        mScanner?.close()
        mSocket?.close()
        mPrintWrite = null
        mScanner = null
        mSocket = null
    }


    fun setMinerEventListener(event: MinerEventListener) {
        this.mEvent = event
    }

    fun setMinerMsgListener(msg: MinerMsgListener) {
        this.mMsg = msg
    }

}

package com.cpf.xmrminer

import android.text.method.ScrollingMovementMethod
import com.cpf.baseproject.base.BaseActivity
import com.cpf.baseproject.util.addTextChangedListener
import com.cpf.baseproject.util.setOnSeekBarChangeListener
import com.cpf.baseproject.util.ui
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern


/**
 * Created by cpf on 2017/12/28.
 */
class MainActivity : BaseActivity() {

    private var miner = MinerManager()
    private var maxThread = 1
    private var timer: Timer? = null

    override fun canSwipeBack(): Boolean = false

    override fun initActionBarLayout(): Int = 0

    override fun initLayout(): Int = R.layout.activity_main

    override fun initView() {

    }

    override fun initEvent() {
        miner.setMinerEventListener(object : MinerEventListener {
            override fun start() {
                ui {
                    run.text = stopStr
                    url.isEnabled = false
                    user.isEnabled = false
                    pwd.isEnabled = false
                    startTimer()
                }
            }

            override fun stop() {
                ui {
                    run.text = runStr
                    url.isEnabled = true
                    user.isEnabled = true
                    pwd.isEnabled = true
                    stopTimer()
                }
            }

            override fun error() {

            }

        })
        content.movementMethod = ScrollingMovementMethod.getInstance()
        miner.setMinerMsgListener(object : MinerMsgListener {
            override fun message(msg: String) {
                ui {
                    refreshLogView(msg + "\n")
                }
            }
        })

        run.setOnClickListener {
            changeStatus()
        }
        speedSeekBar.setOnSeekBarChangeListener({
            speedText.text = ((it + 1) * 10).toString().plus("%")
        }, {
            miner.mSpeed = (it + 1) / 10f
        })
        threadSeekBar.setOnSeekBarChangeListener({
            threadText.text = (it + 1).toString()
        }, {
            miner.mThreadNum = it + 1
        })
        url.addTextChangedListener {
            if (!url.text.isNullOrEmpty()) {
                urlLayout.isErrorEnabled = false
            }
        }
        user.addTextChangedListener {
            if (!user.text.isNullOrEmpty()) {
                userLayout.isErrorEnabled = false
            }
        }
        pwd.addTextChangedListener {
            if (!pwd.text.isNullOrEmpty()) {
                pwdLayout.isErrorEnabled = false
            }
        }
    }

    fun refreshLogView(msg: String) {
        content.append(msg)
        val offset = content.lineCount * content.lineHeight
        if (offset > content.height) {
            content.scrollTo(0, offset - content.height)
        }
    }

    private var runStr: String? = null
    private var stopStr: String? = null

    private fun changeStatus() {
        if (runStr == null) {
            runStr = getString(R.string.run)
        }
        if (stopStr == null) {
            stopStr = getString(R.string.stop)
        }
        if (url.text.isNullOrEmpty()) {
            urlLayout.error = getString(R.string.url_tip)
            return
        }
        if (user.text.isNullOrEmpty()) {
            userLayout.error = getString(R.string.user_tip)
            return
        }
        if (pwd.text.isNullOrEmpty()) {
            pwdLayout.error = getString(R.string.pwd_tip)
            return
        }
        if (run.text == runStr) {
            miner.mThreadNum = threadText.text.toString().toInt()
            miner.mSpeed = (speedSeekBar.progress + 1) / 10f
            miner.startMiner(url.text.toString(), user.text.toString(), pwd.text.toString())
        } else {
            miner.stopMiner()
        }
    }

    private fun startTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            var tempHash = 0L
            override fun run() {
                ui {
                    hash.text = formatHash((miner.mHashCount - tempHash) / 3)
                    total.text = formatHash(miner.mHashCount)
                    accepted.text = miner.mShareCount.toString()
                    tempHash = miner.mHashCount
                }
            }

        }, 1000, 3000)
    }

    private fun formatHash(count: Long): String {
        return when {
            count < 1000f -> {
                count.toString()
            }
            count >= 1000f -> {
                (count / 1000f).toString() + "K"
            }
            count >= 1000f * 1000f -> {
                (count / 1000f * 1000f).toString() + "M"
            }
            count >= 1000f * 1000f * 1000f -> {
                (count / 1000f * 1000f * 1000f).toString() + "G"
            }
            else -> {
                (count / 1000f * 1000f * 1000f).toString() + "G"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        miner.stopMiner()
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    override fun initData() {
        maxThread = getNumCores()
        threadSeekBar.max = maxThread - 1
        threadSeekBar.progress = maxThread - 1
        threadText.text = maxThread.toString()
    }

    private fun getNumCores(): Int {
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean = Pattern.matches(getString(R.string.cpu_match), pathname.name)
        }
        return try {
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles(CpuFilter())
            files.size
        } catch (e: Exception) {
            1
        }
    }
}

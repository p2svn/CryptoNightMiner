package com.cpf.baseproject.util

import android.widget.SeekBar

/**
 * Created by cpf on 2017/12/19.
 * SeekBar
 */
fun SeekBar.setOnSeekBarChangeAfterListener(block: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            block(seekBar!!.progress)
        }

    })
}

fun SeekBar.setOnSeekBarChangeBeforeListener(block: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            block(seekBar!!.progress)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }

    })
}

fun SeekBar.setOnSeekBarChangeListener(change: (progress: Int) -> Unit, stop: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                change(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            stop(seekBar!!.progress)
        }

    })
}

fun SeekBar.setOnSeekBarChangeListener(change: (progress: Int) -> Unit, start: (progress: Int) -> Unit, stop: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                change(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            start(seekBar!!.progress)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            stop(seekBar!!.progress)
        }

    })
}
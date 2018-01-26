package com.cpf.baseproject.util

import android.text.Editable
import android.widget.EditText
import com.cpf.baseproject.widget.TextChangeListener

/**
 * edittext扩展
 */
fun EditText.addTextChangedListener(block: () -> Unit) {
    addTextChangedListener(object : TextChangeListener() {
        override fun afterTextChanged(s: Editable?) {
            block()
        }
    })
}

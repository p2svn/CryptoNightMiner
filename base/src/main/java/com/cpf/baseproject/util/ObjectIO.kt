@file:Suppress("unused")

package com.cpf.baseproject.util

import android.content.Context
import android.util.Base64
import java.io.*

@Suppress("UNCHECKED_CAST")
fun <T> getObjectByIO(context: Context, key: String): T? {
    return try {
        val file = context.filesDir
        val fis = FileInputStream(File(file, key))
        val bytes = ByteArray(fis.available())
        fis.read(bytes)
        val byteIS = ByteArrayInputStream(Base64.decode(bytes, Base64.DEFAULT))
        val ois = ObjectInputStream(byteIS)
        val t: T = ois.readObject() as T
        ois.close()
        byteIS.close()
        fis.close()
        t
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun putObjectByIO(context: Context, key: String, serializable: Serializable): Boolean {
    try {
        val file = context.filesDir
        val fos = FileOutputStream(File(file, key))
        val byteOS = ByteArrayOutputStream()
        val oos = ObjectOutputStream(byteOS)
        oos.writeObject(serializable)
        oos.flush()
        oos.close()
        fos.write(Base64.encode(byteOS.toByteArray(), Base64.DEFAULT))
        fos.flush()
        fos.close()
        byteOS.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}


@Suppress("UNCHECKED_CAST")
fun <T> getObjectByString(content: String): T? {
    return try {
        val byteIS = ByteArrayInputStream(Base64.decode(content.toByteArray(Charsets.ISO_8859_1), Base64.DEFAULT))
        val ois = ObjectInputStream(byteIS)
        val t: T = ois.readObject() as T
        ois.close()
        byteIS.close()
        t
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getStringByObject(any: Any): String {
    return try {
        val byteOS = ByteArrayOutputStream()
        val oos = ObjectOutputStream(byteOS)
        oos.writeObject(any)
        oos.flush()
        oos.close()
        byteOS.close()
        Base64.encode(byteOS.toByteArray(), Base64.DEFAULT).toString(Charsets.ISO_8859_1)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

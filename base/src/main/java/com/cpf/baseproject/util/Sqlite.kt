@file:Suppress("unused")

package com.cpf.baseproject.util

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.annotation.NonNull

enum class EncodeMode {
    Serializable,
    JSON
}

private var mEncodeMode = EncodeMode.JSON

fun initSQLiteEncodeMode(@NonNull encodeMode: EncodeMode) {
    mEncodeMode = encodeMode
}

fun getDatabase(context: Context): SQLiteDatabase =
        context.openOrCreateDatabase("App.db", Context.MODE_PRIVATE, null)

/**
 * 处理加密
 */
private fun handleEncode(str: String): String = getBase64(str)


/**
 * 处理解密
 */
private fun handleDecode(str: String): String = getFromBase64(str)


/**
 * 创建表,如果存在则不会创建
 */
fun <T> createTableIfNotExist(context: Context, clazz: Class<T>): Boolean {
    try {
        val db = getDatabase(context)
        val sql = StringBuffer()
        sql.append("create table if not exists `${clazz.simpleName}`(" +
                "`_id` integer primary key autoincrement,`")
        val fields = clazz.declaredFields
        for (field in fields) {
            if (field.name != "serialVersionUID" && !field.name.contains("$")) {
                field.isAccessible = true
                val value = field.type
                when (value) {
                    String::class.java -> {
                        sql.append(field.name + "` TEXT,`")
                    }
                    Char::class.java -> {
                        sql.append(field.name + "` TEXT,`")
                    }
                    Boolean::class.java -> {
                        sql.append(field.name + "` INTEGER,`")
                    }
                    Int::class.java -> {
                        sql.append(field.name + "` INTEGER,`")
                    }
                    Short::class.java -> {
                        sql.append(field.name + "` INTEGER,`")
                    }
                    Byte::class.java -> {
                        sql.append(field.name + "` INTEGER,`")
                    }
                    Long::class.java -> {
                        sql.append(field.name + "` BIGINT,`")
                    }
                    Float::class.java -> {
                        sql.append(field.name + "` REAL,`")
                    }
                    Double::class.java -> {
                        sql.append(field.name + "` REAL,`")
                    }
                    else -> {
                        sql.append(field.name + "` TEXT,`")
                    }
                }
            }
        }
        sql.delete(sql.length - 2, sql.length)
        sql.append(");")
        logPrint(sql.toString())
        db.execSQL(sql.toString())
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

/**
 * 增
 */
fun insert(context: Context, any: Any): Boolean {
    try {
        when (any) {
            is Collection<*> -> any.forEach {
                if (it != null) {
                    insert(context, it)
                }
            }
            is Array<*> -> any.forEach {
                if (it != null) {
                    insert(context, it)
                }
            }
            else -> {
                val db = getDatabase(context)
                val fields = any.javaClass.declaredFields
                val sql = StringBuffer().append("insert into `${any.javaClass.simpleName}`(")
                for (field in fields) {
                    if (field.name != "serialVersionUID" && !field.name.contains("$")) {
                        field.isAccessible = true
                        if (field.get(any) != null) {
                            sql.append("`").append(field.name).append("`,")
                        }
                    }
                }
                sql.deleteCharAt(sql.length - 1)
                sql.append(") values(")
                for (field in fields) {
                    if (field.name != "serialVersionUID" && !field.name.contains("$")) {
                        field.isAccessible = true
                        var value = field.get(any)
                        if (value != null) {
                            when (value) {
                                is String -> {
                                    value = handleEncode(value)
                                }
                                is Char -> {
                                }
                                is Boolean -> {
                                    value = if (value) 1 else 0
                                }
                                is Int -> {
                                }
                                is Short -> {
                                }
                                is Byte -> {
                                }
                                is Long -> {
                                }
                                is Float -> {
                                }
                                is Double -> {
                                }
                                else -> {
                                    value = if (mEncodeMode == EncodeMode.Serializable) {
                                        getStringByObject(value)
                                    } else {
                                        handleEncode(gson.toJson(value))
                                    }
                                }
                            }
                            sql.append("'").append(value).append("'").append(",")
                        }
                    }
                }

                sql.deleteCharAt(sql.length - 1)
                sql.append(");")
                logPrint(sql)
                db.execSQL(sql.toString())
                db.close()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

/**
 * 增
 */
fun insert(context: Context, sql: String, args: Array<Any>?): Boolean {
    try {
        val db = getDatabase(context)
        if (args != null) {
            db.execSQL(sql, args)
        } else {
            db.execSQL(sql)
        }
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

/**
 * 删
 */
fun delete(context: Context, sql: String, args: Array<Any>?): Boolean = insert(context, sql, args)

/**
 * 改
 */
fun update(context: Context, sql: String, args: Array<Any>?): Boolean = insert(context, sql, args)

/**
 * 判断是否存在
 */
fun exist(context: Context, sql: String, args: Array<String>?): Boolean? {
    val result: Boolean?
    try {
        val db = getDatabase(context)
        val cursor = db.rawQuery(sql, args)
        result = cursor.moveToFirst()
        cursor.close()
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return result
}

/**
 * 查询
 * 返回第一条数据
 */
fun <T> rawQuerySingle(context: Context, sql: String, args: Array<String>?, clazz: Class<T>): T? {
    var t: T? = null
    try {
        val db = getDatabase(context)
        val cursor = db.rawQuery(sql, args)
        if (cursor.moveToFirst()) {
            t = handleValue(clazz, cursor)
        }
        cursor.close()
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return t
}

/**
 * 查询
 */
fun <T> rawQuery(context: Context, sql: String, args: Array<String>?, clazz: Class<T>): List<T>? {
    val list = arrayListOf<T>()
    try {
        val db = getDatabase(context)
        val cursor = db.rawQuery(sql, args)
        while (cursor.moveToNext()) {
            val t = handleValue(clazz, cursor)
            if (t != null) {
                list.add(t)
            }
        }
        cursor.close()
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return list
}

/**
 * 查询
 * 返回查询的所有数据
 */
fun <T> rawQueryAll(context: Context, clazz: Class<T>): List<T>? {
    val list = arrayListOf<T>()
    try {
        val db = getDatabase(context)
        val cursor = db.rawQuery("select * from ${clazz.simpleName}", null)
        while (cursor.moveToNext()) {
            val t = handleValue(clazz, cursor)
            if (t != null) {
                list.add(t)
            }
        }
        cursor.close()
        db.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return list
}


fun <T> handleValue(clazz: Class<T>, cursor: Cursor): T? {
    try {
        val t: T = clazz.newInstance()
        val fields = clazz.declaredFields
        for (field in fields) {
            if (field.name != "serialVersionUID" && !field.name.contains("$")) {
                field.isAccessible = true
                val value = field.type
                when (value) {
                    String::class.java -> {
                        field.set(t, handleDecode(cursor.getString(cursor.getColumnIndex(field.name))))
                    }
                    Char::class.java -> {
                        field.set(t, cursor.getString(cursor.getColumnIndex(field.name)))
                    }
                    Boolean::class.java -> {
                        field.set(t, cursor.getInt(cursor.getColumnIndex(field.name)) == 1)
                    }
                    Int::class.java -> {
                        field.set(t, cursor.getInt(cursor.getColumnIndex(field.name)))
                    }
                    Short::class.java -> {
                        field.set(t, cursor.getShort(cursor.getColumnIndex(field.name)))
                    }
                    Byte::class.java -> {
                        field.set(t, cursor.getInt(cursor.getColumnIndex(field.name)))
                    }
                    Long::class.java -> {
                        field.set(t, cursor.getLong(cursor.getColumnIndex(field.name)))
                    }
                    Float::class.java -> {
                        field.set(t, cursor.getFloat(cursor.getColumnIndex(field.name)))
                    }
                    Double::class.java -> {
                        field.set(t, cursor.getDouble(cursor.getColumnIndex(field.name)))
                    }
                    else -> {
                        val str = cursor.getString(cursor.getColumnIndex(field.name))
                        if (str != null) {
                            if (mEncodeMode == EncodeMode.Serializable) {
                                field.set(t, getObjectByString(str))
                            } else {
                                field.set(t, gson.fromJson(handleDecode(str), field.type))
                            }
                        }
                    }
                }
            }
        }
        return t
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

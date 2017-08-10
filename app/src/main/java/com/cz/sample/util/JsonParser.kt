package com.cz.sample.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.cz.sample.model.Channel

import java.lang.reflect.Type
import java.util.ArrayList

/**
 */
object JsonParser {
    private val gson = Gson()
    /**
     * 获得list<T>解析集

     * @param result
     * *
     * @param clazz
     * *
     * @param <T>
     * *
     * @return
    </T></T> */
    fun <T> getLists(result: String, clazz: Class<T>): ArrayList<T> {
        var type: Type? = null
        if (Channel::class.java == clazz) {
            type = object : TypeToken<ArrayList<Channel>>() {}.type
        }
        return gson.fromJson<ArrayList<T>>(result, type)
    }


}

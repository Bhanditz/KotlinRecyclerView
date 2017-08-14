package com.cz.recyclerlibrary

/**
 * Created by Administrator on 2017/5/21.
 */

interface IRecyclerAdapter<out E> {
    fun getItems(): List<E>

    fun getNullableItem(position: Int):E?

    fun getItem(position: Int): E
}

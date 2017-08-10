package com.ldzs.pulltorefreshrecyclerview.model

import java.util.ArrayList
import java.util.Arrays

/**
 * Created by Administrator on 2017/5/21.
 */

class Sticky1Item {
    val headerItems: MutableList<String>
    val item: String

    constructor(item: String) {
        this.item = item
        this.headerItems = ArrayList<String>()
    }

    constructor(array: Array<String>?, item: String) {
        this.item = item
        this.headerItems = ArrayList<String>()
        if (null != array) {
            this.headerItems.addAll(Arrays.asList(*array))
        }
    }
}

package com.cz.recyclerlibrary.callback

import android.view.View

import com.cz.recyclerlibrary.adapter.tree.TreeAdapter

/**
 * Created by cz on 16/3/17.
 */
interface OnNodeItemClickListener<E> {
    fun onNodeItemClick(node: TreeAdapter.TreeNode<E>, v: View, position: Int)
}

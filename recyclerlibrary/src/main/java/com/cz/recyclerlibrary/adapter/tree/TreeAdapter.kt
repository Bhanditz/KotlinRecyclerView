package com.cz.recyclerlibrary.adapter.tree

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.OnNodeItemClickListener

import java.util.ArrayList
import java.util.LinkedList

/**
 * Created by cz on 17/8/14
 * 一个RecyclerView的树形管理Adapter对象
 * 2级  父级,子级
 * 多级  父级,根->
 * root

 */
abstract class TreeAdapter<E>(context: Context, protected val rootNode: TreeNode<E>) : RecyclerView.Adapter<BaseViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    protected val originalItems = ArrayList<E>()
    protected val nodeItems= ArrayList<TreeNode<E>>()//树的列表展示节点
    private var listener: OnNodeItemClickListener<E>? = null
    private var headerCount: Int = 0//头控件数


    init { refreshItems() }

    fun setHeaderCount(headerCount: Int) {
        this.headerCount = headerCount
    }

    /**
     * 获取节点内所有可展开节点
     * 这里效率稍微了点,但可以接受
     */
    @Synchronized private fun getNodeItems(rootNode: TreeNode<E>): ArrayList<TreeNode<E>> {
        val nodeItems = ArrayList<TreeNode<E>>()
        val nodes = LinkedList<TreeNode<E>>()
        nodes.add(rootNode)
        while (!nodes.isEmpty()) {
            val node = nodes.pollFirst()
            if (this.rootNode === node || node.expand && !node.child.isEmpty()) {
                val child = node.child
                val size = child.size
                (size - 1 downTo 0)
                        .map { child[it] }
                        .forEach { nodes.offerFirst(it) }
            }
            if (node !== rootNode) {
                nodeItems.add(node)
            }
        }
        return nodeItems
    }


    /**
     * 绑定节点信息

     * @param holder
     * *
     * @param node
     * *
     * @param position
     */
    abstract fun onBindViewHolder(holder: BaseViewHolder, node: TreeNode<E>, e: E, viewType: Int, position: Int)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val node = getNode(position)
        onBindViewHolder(holder, node, node.e, getItemViewType(position), position)
        holder.itemView.setOnClickListener { v ->
            val itemPosition = holder.adapterPosition - headerCount
            val node = getNode(itemPosition)
            val expand = node.expand
            node.expand = true//置为true,取得当前展开后的节点
            val items = getItems(node)
            val addNodes = getNodeItems(node)
            node.expand = !expand//更新展开状态
            if (!addNodes.isEmpty()) {
                val size = addNodes.size
                onNodeExpand(node, holder, !expand)
                if (expand) {
                    originalItems.removeAll(items)
                    nodeItems.removeAll(addNodes)
                    //关闭动作
                    notifyItemRangeRemoved(itemPosition + 1, size)
                } else {
                    originalItems.addAll(itemPosition + 1, items)
                    nodeItems.addAll(itemPosition + 1, addNodes)
                    //展开动作
                    notifyItemRangeInserted(itemPosition+1, size)
                }
            } else if (null != listener) {
                listener?.onNodeItemClick(node, v, itemPosition)
            }
        }
    }


    /**
     * 子类实现,节点展开或关闭

     * @param node
     * *
     * @param holder
     * *
     * @param expand
     */
    protected open fun onNodeExpand(node: TreeNode<E>, holder: BaseViewHolder, expand: Boolean) {}

    /**
     * 获得列表对应位置节点

     * @param position
     * *
     * @return
     */
    fun getNode(position: Int): TreeNode<E> =nodeItems[position]

    /**
     * 获得对应节点内容

     * @param position
     * *
     * @return
     */
    fun getItem(position: Int): E =nodeItems[position].e

    fun getItems(): List<E> =originalItems

    override fun getItemCount(): Int =nodeItems.size

    /**
     * 设置某个一分级隐藏与显示

     * @param level
     */
    open fun setLevelExpand(level: Int, expand: Boolean) {}

    open fun setOnNodeItemClickListener(listener: OnNodeItemClickListener<E>) {
        this.listener = listener
    }

    open fun onNodeItemClick(action:(TreeAdapter.TreeNode<E>?, View?,Int)->Unit){
        this.listener=object :OnNodeItemClickListener<E>{
            override fun onNodeItemClick(node: TreeNode<E>, v: View, position: Int) {
                action(node,v,position)
            }
        }
    }

    /**
     * 获取节点内所有可展开节点
     * 这里效率稍微了点,但可以接受
     */
    private fun getItems(rootNode: TreeNode<E>): ArrayList<E> {
        val nodeItems = ArrayList<E>()
        val nodes = LinkedList<TreeNode<E>>()
        nodes.add(rootNode)
        while (!nodes.isEmpty()) {
            val node = nodes.pollFirst()
            if (this.rootNode === node || node.expand && !node.child.isEmpty()) {
                val child = node.child
                val size = child.size
                for (i in size - 1 downTo 0) {
                    val childNode = child[i]
                    nodes.offerFirst(childNode)
                }
            }
            if (node !== rootNode) {
                nodeItems.add(node.e)
            }
        }
        return nodeItems
    }

    private fun getItems(nodes: List<TreeNode<E>>): ArrayList<E> =(0..nodes.size - 1).mapTo(ArrayList<E>()) { nodes[it].e }

    /**
     * 移除指定节点

     * @param node
     */
    fun removeNode(node: TreeNode<E>?) {
        if (null != node) {
            val childNodes = node.child
            //移除节点内,所有子节点
            if (node.expand && !childNodes.isEmpty()) {
                val size = childNodes.size
                //这里之所以反向减少.是因为正向减少的话.这边减,在递归里,child的条目在减少.正向会引起size,没减,但child减少的角标越界问题.反向则不会
                for (i in size - 1 downTo 0) {
                    val treeNode = childNodes[i]
                    removeNode(treeNode)
                }
            }
            val index = nodeItems.indexOf(node)
            if (0 <= index) {
                remove(index)
            }
        }
    }

    /**
     * 移除当前展示任一位置节点
     */
    fun removeNode(position: Int) =removeNode(nodeItems[position])

    /**
     * 移除指定根节点
     */
    fun removeRootNode(position: Int) =removeNode(rootNode.child[position])

    /**
     * 获取条目在节点位置

     * @param e
     * *
     * @return
     */
    fun indexOfItem(e: E): Int =originalItems.indexOf(e)

    /**
     * 设置指定条目取值

     * @param index
     * *
     * @param e
     */
    operator fun set(index: Int, e: E) {
        originalItems[index] = e
        nodeItems[index].e = e
        notifyItemChanged(index)
    }

    /**
     * 按位置移除

     * @param position
     */
    private fun remove(position: Int) {
        originalItems.removeAt(position)
        val node = nodeItems.removeAt(position)
        notifyItemRemoved(position)
        //移除根节点内节点指向
        val parent = node.parent
        if (null != parent) {
            parent.child.remove(node)
            //通知父条目改动
            notifyItemChanged(nodeItems.indexOf(parent))
        }
    }

    fun insertNode(e: E) =insertNode(TreeNode(rootNode, e))

    /**
     * 插入节点

     * @param node
     */
    fun insertNode(node: TreeNode<E>) {
        //这是认祖归宗罗
        node.parent = rootNode
        rootNode.child.add(node)
        val nodeItems = ArrayList<TreeNode<E>>()
        nodeItems.add(node)
        val items = getNodeItems(node)
        if (!items.isEmpty()) {
            nodeItems.addAll(items)
        }
        val itemCount = itemCount
        this.nodeItems.addAll(nodeItems)
        this.originalItems.addAll(getItems(nodeItems))
        notifyItemRangeInserted(itemCount, nodeItems.size)
    }


    /**
     * 创建view对象

     * @param parent
     * *
     * @param layout
     * *
     * @return
     */
    protected fun createView(parent: ViewGroup, layout: Int): View {
        return layoutInflater.inflate(layout, parent, false)
    }

    fun refreshItems() {
        this.originalItems.clear()
        this.nodeItems.clear()
        val nodes = getNodeItems(this.rootNode)
        if (null != nodes) {
            this.originalItems.addAll(getItems(nodes))
            this.nodeItems.addAll(nodes)
        }
    }

    /**
     * 树节点
     * @param <E>
    </E> */
    class TreeNode<E>(var expand: Boolean//是否展开
                      , var parent: TreeNode<E>?//父节点
                      , var e: E//节点
    ) {
        var child= ArrayList<TreeNode<E>>()//子节点
        var level: Int = 0//当前节点级 0 1 2

        constructor(e: E) : this(false, null, e)

        constructor(parent: TreeNode<E>, e: E) : this(false, parent, e)

        init {
            val parent=parent
            level = if (null == parent) 0 else parent.level + 1
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            var result = false
            val r = o as TreeNode<E>?
            if (null != e && null != r?.e) {
                result = e == r.e
            }
            return result
        }

        override fun toString(): String {
            return e.toString()
        }
    }
}

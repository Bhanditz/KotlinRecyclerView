package com.cz.sample.ui.adapter

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem

import com.cz.sample.R
import com.cz.sample.adapter.FileAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.recyclerlibrary.adapter.tree.TreeAdapter
import com.cz.recyclerlibrary.anim.FadeInDownAnimator
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_tree.*

import java.io.File
import java.util.LinkedList

import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by cz on 16/1/22.
 * 一个无限展开的RecyclerView数据适配器演示
 */
@ToolBar
class TreeAdapterViewActivity : ToolBarActivity() {
    lateinit var subscribe:Subscription
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = FadeInDownAnimator()
        recyclerView.layoutManager=LinearLayoutManager(this)

        val progressDialog=ProgressDialog(this).apply {
            setMessage(getString(R.string.scan_files))
            show()
        }
        subscribe = Observable.create<TreeAdapter.TreeNode<File>> {
            it.onNext(getAllFileNode())
            it.onCompleted()
        }.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    progressDialog.dismiss()
                    recyclerView.adapter=FileAdapter(this, it)
                }) { throwable -> throwable.printStackTrace() }
    }

    /**
     * TODO 获取第一级文件树.懒加载,待实现
     * @return
     */
    private fun getFileNode(): TreeAdapter.TreeNode<File>{
        val rootFile = Environment.getExternalStorageDirectory()
        val rootNode = TreeAdapter.TreeNode(rootFile)
        val filesArray = rootFile.listFiles()
        if (null != filesArray) {
            val length = filesArray.size
            (length - 1 downTo 0)
                    .map { TreeAdapter.TreeNode(rootNode, filesArray[it]) }
                    .forEach { rootNode.child.add(it) }
        }
        return rootNode
    }

    /**
     * 获取整棵文件树,较耗时

     * @return
     */
    private fun getAllFileNode(): TreeAdapter.TreeNode<File>{
        val rootFile = Environment.getExternalStorageDirectory()
        val files = LinkedList<File>()
        val fileNodes = LinkedList<TreeAdapter.TreeNode<File>>()
        val rootNode = TreeAdapter.TreeNode(rootFile)
        fileNodes.offerFirst(rootNode)
        files.offerFirst(rootFile)
        while (!files.isEmpty()) {
            val file = files.pollFirst()
            val fileTreeNode = fileNodes.removeFirst()
            if (file.isDirectory) {
                var filesArray: Array<File>? = null
                try {
                    filesArray = file.listFiles()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (null != filesArray) {
                    val length = filesArray.size
                    for (i in length - 1 downTo 0) {
                        files.addFirst(filesArray[i])
                        val childNode = TreeAdapter.TreeNode(fileTreeNode, filesArray[i])
                        fileNodes.addFirst(childNode)
                        fileTreeNode.child.add(childNode)
                    }
                }
            }
        }
        return rootNode
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val adapter=recyclerView.adapter as TreeAdapter<File>
        if (id == R.id.action_add) {
            val file = File("abc")
            adapter.insertNode(file)
            return true
        } else if (id == R.id.action_remove) {
            //adapter.removeNode(1) 移除当前列表展示位置节点,不分子父关系移除
            adapter.removeRootNode(1)//移除指定位置根节点
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        subscribe.unsubscribe()
        super.onDestroy()
    }
}

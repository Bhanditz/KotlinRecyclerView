package com.cz.sample

import android.app.Activity
import com.cz.sample.model.SampleItem
import com.cz.sample.ui.adapter.*
import com.cz.sample.ui.drag.CustomDragActivity
import com.cz.sample.ui.drag.DynamicDragActivity
import com.cz.sample.ui.drag.GridDragActivity
import com.cz.sample.ui.drag.LinearDragActivity
import com.cz.sample.ui.layoutmanager.GalleryActivity
import com.cz.sample.ui.layoutmanager.HorizontalLayoutActivity
import com.cz.sample.ui.layoutmanager.VerticalLayoutActivity
import com.cz.sample.ui.layoutmanager.WheelDateActivity
import com.cz.sample.ui.recyclerview.GridPullToRefreshActivity
import com.cz.sample.ui.recyclerview.PullToRefreshActivity
import com.cz.sample.ui.recyclerview.PullToRefreshExpandActivity
import com.cz.sample.ui.sticky.*
import cz.kotlinwidget.ui.NewViewPagerListActivity

/**
 * Created by Administrator on 2017/6/8.
 */
class FuncTemplate {
    companion object {
        val items = mutableListOf<SampleItem<Activity>>()
        val groupItems = mutableMapOf<Int, List<SampleItem<Activity>>>()
        fun item(closure: SampleItem<Activity>.() -> Unit) {
            items.add(SampleItem<Activity>().apply(closure))
        }

        //分组模板
        fun group(closure: () -> Unit) {
            closure.invoke()
            groupItems += items.groupBy { it.pid }
        }

        operator fun get(id: Int?) = groupItems[id]

        operator fun contains(id: Int?) = groupItems.any { it.key == id }

        init {
            group {
                item {
                    id = 1
                    title = "RefreshActivity"
                    desc = "一组RecyclerView基础操作,演示常规刷新,展开等操作"
                    item {
                        pid = 1
                        title = "RecyclerView"
                        desc = "刷新并自由添加头/尾等操作"
                        clazz = PullToRefreshActivity::class.java
                    }
                    item {
                        pid = 1
                        title = "GridRecyclerView"
                        desc = "刷新分格并自由添加头/尾等操作"
                        clazz = GridPullToRefreshActivity::class.java
                    }
                    item {
                        pid = 1
                        title = "ExpandRecyclerView"
                        desc = "演示ExpandRecyclerView展示效果"
                        clazz = PullToRefreshExpandActivity::class.java
                    }
                    item {
                        pid = 1
                        title = "PagerRecyclerView"
                        desc = "演示RecyclerView添加ViewPager头效果"
                        clazz = NewViewPagerListActivity::class.java
                    }

                }
                item {
                    id = 2
                    title = "增强Adapter演示"
                    desc = "动态添加头尾/树形展示/动态插入/Cursor装载,以及选中的扩展Adapter"
                    item {
                        pid = 2
                        title = "HeaderAdapter"
                        desc = "包装设计使之支持动态添加/移除头尾"
                        clazz = HeaderAdapterActivity::class.java
                    }
                    item {
                        pid = 2
                        title = "TreeAdapter"
                        desc = "扩展Adapter使之支持无限分层操作"
                        clazz = TreeAdapterViewActivity::class.java
                    }
                    item {
                        pid = 2
                        title = "DynamicAdapter"
                        desc = "包装另一层角标映射使之能无缝添加任何位置自定义条目"
                        clazz = DynamicAdapterActivity::class.java
                    }
//                    item {
//                        pid = 2
//                        title = "CursorAdapter"
//                        desc = "扩展Adapter支持Cursor直接装载"
//                        clazz = CursorAdapterActivity::class.java
//                    }
                    item {
                        pid = 2
                        title = "SelectAdapter"
                        desc = "装饰设计使之支持简单的选中模式,如单选,多选,块选等"
                        clazz = SelectAdapterActivity::class.java
                    }
                }
                item {
                    id = 3
                    title = "增加StickyHeader效果"
                    desc = "扩展使RecyclerView支持复杂的Sticky效果"
                    item {
                        pid = 3
                        title = "StickyActivity1"
                        desc = "演示一个普通的Sticky效果"
                        clazz = Sticky1SampleActivity::class.java
                    }
                    item {
                        pid = 3
                        title = "StickyActivity2"
                        desc = "演示一个复杂的Sticky效果"
                        clazz = Sticky2SampleActivity::class.java
                    }
                    item {
                        pid = 3
                        title = "StickyActivity3"
                        desc = "演示添加头与尾的Sticky效果"
                        clazz = Sticky3SampleActivity::class.java
                    }
                    item {
                        pid = 3
                        title = "StickyActivity4"
                        desc = "演示GridLinearLayout的Sticky效果"
                        clazz = Sticky4SampleActivity::class.java
                    }
                    item {
                        pid = 3
                        title = "StickyActivity5"
                        desc = "演示GridLinearLayout的Sticky效果,加入动态移除自动更新测试"
                        clazz = Sticky5SampleActivity::class.java
                    }
                }

                item {
                    id = 4
                    title = "拖动的RecyclerView"
                    desc = "扩展使之支持Linear/Grid的拖动,以及其他局部拖动控制"
                    item {
                        pid = 4
                        title = "LinearDrag"
                        desc = "演示LinearLayoutManager拖动效果"
                        clazz = LinearDragActivity::class.java
                    }
                    item {
                        pid = 4
                        title = "GridDrag"
                        desc = "演示Grid的拖动效果"
                        clazz = GridDragActivity::class.java
                    }
                    item {
                        pid = 4
                        title = "DynamicDrag"
                        desc = "演示动态条目拖动效果"
                        clazz = DynamicDragActivity::class.java
                    }
                    item {
                        pid = 4
                        title = "Sample"
                        desc = "演示资讯的联动拖动排版"
                        clazz = CustomDragActivity::class.java
                    }
                }

                item {
                    id = 5
                    title = "自定义LayoutManager扩展功能"
                    desc = "自定义Wheel/Gallery/TableView"
                    item {
                        pid = 5
                        title = "VerticalLayout"
                        desc = "演示纵向的居中LayoutManager"
                        clazz = VerticalLayoutActivity::class.java
                    }
                    item {
                        pid = 5
                        title = "HorizontalLayout"
                        desc = "演示横向的居中LayoutManager"
                        clazz = HorizontalLayoutActivity::class.java
                    }
                    item {
                        pid = 5
                        title = "Gallery"
                        desc = "演示Gallery图片展示"
                        clazz = GalleryActivity::class.java
                    }
                    item {
                        pid = 5
                        title = "Wheel"
                        desc = "演示日期展示"
                        clazz = WheelDateActivity::class.java
                    }
                    /*
                    item {
                        pid = 5
                        title = "Table"
                        desc = "演示表格数据"
                        clazz = TableActivity::class.java
                    }*/
                }

            }
        }
    }
}
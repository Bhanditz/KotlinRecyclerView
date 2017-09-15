package com.cz.recyclerlibrary.layoutmanager.table;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by cz on 2017/1/20.
 * 一个表格控件
 */

public class TableView extends RecyclerView {

    public TableView(Context context) {
        this(context,null, 0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setLayoutManager(new TableLayoutManager());
    }


    /**
     * @deprecated
     * @param layout
     */
    @Override
    public void setLayoutManager(LayoutManager layout) {
    }


}

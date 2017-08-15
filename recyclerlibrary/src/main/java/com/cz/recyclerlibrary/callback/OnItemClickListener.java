package com.cz.recyclerlibrary.callback;

import android.view.View;

/**
 * Created by cz on 2017/8/15.
 */
public interface OnItemClickListener {
    /**
     * @param v:点击控件
     * @param position:全局位置
     * @param adapterPosition:当前adapter位置
     */
    void onItemClick(View v, int position, int adapterPosition);
}

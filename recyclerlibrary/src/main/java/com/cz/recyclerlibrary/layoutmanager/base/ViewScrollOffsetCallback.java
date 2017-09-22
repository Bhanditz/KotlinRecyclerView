package com.cz.recyclerlibrary.layoutmanager.base;

import android.view.View;

/**
 * Created by cz on 2017/1/20.
 */

public interface ViewScrollOffsetCallback {
    /**
     *
     * @param view 当前运算view
     * @param position 当前运算位置
     * @param centerPosition 当前屏幕中间位置
     * @param offset 当前运算的偏移量
     * @param minScroll 根据minScroll运算后的,最小偏移量
     */
    void onViewScrollOffset(View view, int position, int centerPosition, float offset,float minScroll);
}

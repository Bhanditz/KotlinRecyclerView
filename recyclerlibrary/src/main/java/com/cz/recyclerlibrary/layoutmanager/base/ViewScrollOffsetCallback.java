package com.cz.recyclerlibrary.layoutmanager.base;

import android.view.View;

/**
 * Created by cz on 2017/1/20.
 */

public interface ViewScrollOffsetCallback {
    void onViewScrollOffset(View view, int position, int centerPosition, float offset);
}

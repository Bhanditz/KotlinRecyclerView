package cz.refreshlayout.library

/**
 * Created by czz on 2016/8/13.
 */
enum class RefreshState {
    //无状态
    NONE,
    //开始拖动
    START_PULL,
    //松开释放状态
    RELEASE_TO_CANCEL,
    //松开刷新状态
    RELEASE_TO_REFRESHING,
    //正在刷新状态
    REFRESHING,
    //正在刷新,又处于用户拖动状态.不松手
    REFRESHING_DRAGGING,
    //刷新完成状态
    REFRESHING_COMPLETE
}

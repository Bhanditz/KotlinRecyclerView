package cz.refreshlayout.library

/**
 * Created by cz on 16/1/20
 * the refresh mode
 */
enum class RefreshMode {
    BOTH, PULL_START, PULL_END, DISABLED;
    /**
     * @return disable refresh
     */
    fun disable(): Boolean =this == DISABLED

    /**
     * @return enable header refresh
     */
    fun enableStart(): Boolean =this == PULL_START||this==BOTH

    /**
     * @return enable footer refresh
     */
    fun enableEnd(): Boolean =this == PULL_END||this==BOTH

}
package com.cz.recyclerlibrary.callback

/**
 * Interface to listen move in ItemTouchHelper.Callback
 * Created by Alessandro on 15/01/2016.
 */
interface CallbackItemTouch {

    /**
     * Called when an item has been dragged
     * @param oldPosition start position
     * *
     * @param newPosition end position
     */
    fun onItemMove(oldPosition: Int, newPosition: Int)
}

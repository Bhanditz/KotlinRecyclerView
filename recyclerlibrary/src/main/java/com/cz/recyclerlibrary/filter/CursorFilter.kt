/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Duplicate of the android.Widget.CursorFilter in order to make it public
 */

package com.cz.recyclerlibrary.filter

import android.database.Cursor
import android.widget.Filter

/*
 * Duplicate of the android.Widget.CursorFilter in order to make it public
 */
class CursorFilter(internal var filterClient: CursorFilterClient) : Filter() {

    override fun convertResultToString(resultValue: Any): CharSequence {
        return filterClient.convertToString(resultValue as Cursor)
    }

    override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
        val cursor = filterClient.runQueryOnBackgroundThread(constraint)

        val results = Filter.FilterResults()
        if (cursor != null) {
            results.count = cursor.count
            results.values = cursor
        } else {
            results.count = 0
            results.values = null
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
        val oldCursor = filterClient.cursor

        if (results.values != null && results.values !== oldCursor) {
            filterClient.changeCursor(results.values as Cursor)
        }
    }

    interface CursorFilterClient {
        fun convertToString(cursor: Cursor): CharSequence

        fun runQueryOnBackgroundThread(constraint: CharSequence): Cursor?

        val cursor: Cursor

        fun changeCursor(cursor: Cursor)
    }
}
package com.cz.sample.ui.layoutmanager

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener

import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.adapter.DateAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_date.*

import java.text.DecimalFormat
import java.util.Calendar
import java.util.LinkedList

/**
 * Created by cz on 1/18/17.
 */
@ToolBar
class WheelDateActivity : ToolBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date)
        setTitle(intent.getStringExtra("title"))
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                when (seekBar.id) {
                    R.id.seekYear -> yearText.text = getString(R.string.year_value, 2007 + progress)
                    R.id.seekMonth -> monthText.text = getString(R.string.month_value, progress + 1)
                    R.id.seekDay -> dayText.text = getString(R.string.day_value, progress + 1)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar)=Unit
            override fun onStopTrackingTouch(seekBar: SeekBar)=Unit
        }
        seekYear.setOnSeekBarChangeListener(listener)
        seekMonth.setOnSeekBarChangeListener(listener)
        seekDay.setOnSeekBarChangeListener(listener)

        scrollButton.setOnClickListener {
            wheel1.smoothScrollToPosition(seekYear.progress)
            wheel2.smoothScrollToPosition(seekMonth.progress)
            wheel3.smoothScrollToPosition(seekDay.progress)
        }

        val calendar = Calendar.getInstance()
        val items = LinkedList<Int>()
        for (i in 0..9) {
            calendar.add(Calendar.YEAR, -1)
            items.offerFirst(calendar.get(Calendar.YEAR))
        }
        val monthItems = (1..12).toList()
        val dayItems = (1..30).toList()
        val dateAdapter1 = DateAdapter(this, null, items)
        val dateAdapter2 = DateAdapter(this, "00", monthItems)
        val dateAdapter3 = DateAdapter(this, "00", dayItems)

        wheel1.adapter = dateAdapter1
        wheel2.adapter = dateAdapter2
        wheel3.adapter = dateAdapter3

        val formatter = DecimalFormat("00")
        val onSelectPositionChangedListener = object : OnSelectPositionChangedListener {
            override fun onSelectPositionChanged(view: View?, position: Int,lastPosition:Int) {
                val year = dateAdapter1.getItem(position)
                val month = dateAdapter2.getItem(position)
                val day = dateAdapter3.getItem(position)
                dateText.text = getString(R.string.date_value, year, formatter.format(month), formatter.format(day))
            }
        }
        wheel1.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
        wheel2.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
        wheel3.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
    }
}

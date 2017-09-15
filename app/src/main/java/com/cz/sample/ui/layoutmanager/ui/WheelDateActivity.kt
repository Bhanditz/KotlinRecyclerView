package com.cz.sample.ui.layoutmanager.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

import com.cz.recyclerlibrary.layoutmanager.wheel.WheelView
import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.adapter.DateAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_date.*

import java.text.DecimalFormat
import java.util.ArrayList
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

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
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
        val onSelectPositionChangedListener = object : WheelView.OnSelectPositionChangedListener {
            override fun onSelectPositionChanged(view: View?, position: Int) {
                val year = dateAdapter1.getItem(wheel1.selectPosition)
                val month = dateAdapter2.getItem(wheel2.selectPosition)
                val day = dateAdapter3.getItem(wheel3.selectPosition)
                dateText.text = getString(R.string.date_value, year, formatter.format(month), formatter.format(day))
            }
        }
        wheel1.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
        wheel2.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
        wheel3.setOnSelectPositionChangedListener(onSelectPositionChangedListener)
    }
}

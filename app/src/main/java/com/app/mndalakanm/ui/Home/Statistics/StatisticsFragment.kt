package com.app.mndalakanm.ui.Home.Statistics

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.naqdi.chart.model.Line
import  com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    lateinit var binding: FragmentStatisticsBinding
    val intervalList = listOf("00:00", "06:00", "12:00", "18:00")
    val rangeList = listOf("00", "30m", "60m")
    val lineList = arrayListOf<Line>().apply {
        add(Line("Time", Color.BLUE, listOf(10f, 280f, 88f, 70f)))
        // add(Line("Line 2", Color.RED, listOf(300f, 40f, 38f, 180f, 403f, 201f)))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)

        binding.chainChartView.setData(lineList, intervalList, rangeList)
        binding.chainChartView.apply {
            setLineSize(2f)    // size as dp
            setNodeSize(6F)    //size as dp
            setTextSize(10f)
            setTextColor(R.color.colorPrimary)    //color as int
            setFontFamily(Typeface.SANS_SERIF)    //font as typeface
        }
        return binding.root
    }
}
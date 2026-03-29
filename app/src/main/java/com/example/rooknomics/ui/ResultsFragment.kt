package com.example.rooknomics.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rooknomics.databinding.FragmentResultsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ResultsFragment : Fragment() {
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        loadDummyData()
    }

    private fun setupChart() {
        val chart = binding.lineChart
        chart.description.isEnabled = false
        chart.legend.textColor = Color.WHITE
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val yAxisLeft = chart.axisLeft
        yAxisLeft.textColor = Color.WHITE
        yAxisLeft.setDrawGridLines(true)

        chart.axisRight.isEnabled = false
    }

    private fun loadDummyData() {
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 100f))
        entries.add(Entry(2f, 105f))
        entries.add(Entry(3f, 110f))
        entries.add(Entry(4f, 108f))
        entries.add(Entry(5f, 115f))

        val dataSet = LineDataSet(entries, "Equity Curve")
        dataSet.color = Color.parseColor("#00FF85") // emerald
        dataSet.valueTextColor = Color.WHITE
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate() 
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.rooknomics.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.rooknomics.data.api.BacktestApi
import com.example.rooknomics.data.models.TradeLog
import com.example.rooknomics.data.network.ApiClient
import com.example.rooknomics.data.repository.BacktestRepository
import com.example.rooknomics.databinding.FragmentResultsBinding
import com.example.rooknomics.ui.viewmodel.SimState
import com.example.rooknomics.ui.viewmodel.SimViewModelFactory
import com.example.rooknomics.ui.viewmodel.SimulationViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat

class ResultsFragment : Fragment() {
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    private val simulationViewModel: SimulationViewModel by activityViewModels {
        SimViewModelFactory(
            BacktestRepository(
                ApiClient.getClient(requireContext()).create(BacktestApi::class.java)
            )
        )
    }

    private val currencyFormat = DecimalFormat("$#,##0.00")
    private val percentFormat = DecimalFormat("0.00'%'")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()

        simulationViewModel.simState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is SimState.Idle -> {
                    binding.llChartPlaceholder.visibility = View.VISIBLE
                    binding.lineChart.visibility = View.GONE
                    binding.llTradeLogsContainer.removeAllViews()
                }
                is SimState.Loading -> {
                    binding.llChartPlaceholder.visibility = View.VISIBLE
                    binding.lineChart.visibility = View.GONE
                    binding.llTradeLogsContainer.removeAllViews()
                    // Add a "loading" label inside the placeholder
                    val tv = binding.llChartPlaceholder.findViewWithTag<TextView>("loading_tv")
                    if (tv == null) {
                        val label = TextView(requireContext()).apply {
                            tag = "loading_tv"
                            text = "Running simulation..."
                            textSize = 12f
                            setTextColor(Color.parseColor("#9CA3AF"))
                        }
                        binding.llChartPlaceholder.addView(label)
                    }
                }
                is SimState.Success -> {
                    binding.llChartPlaceholder.visibility = View.GONE
                    binding.lineChart.visibility = View.VISIBLE

                    val results = state.response
                    val perf = results.performance

                    binding.tvMetricTotalReturn.text = percentFormat.format(perf.totalReturn)
                    binding.tvMetricAnnualized.text = percentFormat.format(perf.benchmarkReturn)
                    binding.tvMetricMaxDrawdown.text = perf.maxDrawdown?.let { percentFormat.format(it) } ?: "—"
                    binding.tvMetricWinRate.text = perf.winRate?.let { percentFormat.format(it) } ?: "—"

                    binding.tvVerdictStatus.text = results.verdict.status.uppercase()
                    binding.tvVerdictSummary.text = results.verdict.summary
                    if (results.verdict.status.equals("OUTPERFORMED", ignoreCase = true)) {
                        binding.tvVerdictStatus.setTextColor(Color.parseColor("#00FF85")) // emerald
                    } else if (results.verdict.status.equals("UNDERPERFORMED", ignoreCase = true)) {
                        binding.tvVerdictStatus.setTextColor(Color.parseColor("#FBBF24")) // amber
                    } else {
                        binding.tvVerdictStatus.setTextColor(Color.parseColor("#9CA3AF")) // grey
                    }

                    binding.tvRiskStrategyReturn.text = percentFormat.format(perf.totalReturn)
                    binding.tvRiskBenchmarkReturn.text = percentFormat.format(perf.benchmarkReturn)

                    binding.tvTradeCount.text = "${perf.numberOfTrades} TRADES"

                    populateTradesList(results.trades)
                    updateChart(results.trades, perf)
                    updateRadarChart(perf)
                }
                is SimState.Error -> {
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
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

    private fun updateChart(trades: List<TradeLog>, perf: com.example.rooknomics.data.models.PerformanceMetrics) {
        val strategyEntries = ArrayList<Entry>()

        // Anchor at initial capital
        strategyEntries.add(Entry(0f, 10000f))
        for (i in trades.indices) {
            strategyEntries.add(Entry((i + 1).toFloat(), trades[i].totalValue.toFloat()))
        }
        strategyEntries.add(Entry((trades.size + 1).toFloat(), perf.finalValue.toFloat()))

        val strategyDataSet = LineDataSet(strategyEntries, "Strategy")
        strategyDataSet.color = Color.parseColor("#9CA3AF") // grey like the website
        strategyDataSet.valueTextColor = Color.TRANSPARENT
        strategyDataSet.lineWidth = 2f
        strategyDataSet.setDrawCircles(false)
        strategyDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        strategyDataSet.setDrawFilled(true)
        strategyDataSet.fillColor = Color.parseColor("#374151")
        strategyDataSet.fillAlpha = 60

        // S&P 500 benchmark — draw as a straight line from 10000 to benchmarkFinalValue
        val benchmarkEnd = perf.benchmarkFinalValue ?: (10000 * (1 + perf.benchmarkReturn / 100)).toFloat()
        val benchmarkEntries = ArrayList<Entry>()
        benchmarkEntries.add(Entry(0f, 10000f))
        benchmarkEntries.add(Entry((trades.size + 1).toFloat(), benchmarkEnd.toFloat()))

        val benchmarkDataSet = LineDataSet(benchmarkEntries, "S&P 500")
        benchmarkDataSet.color = Color.parseColor("#00FF85") // emerald like the website
        benchmarkDataSet.valueTextColor = Color.TRANSPARENT
        benchmarkDataSet.lineWidth = 2f
        benchmarkDataSet.setDrawCircles(false)
        benchmarkDataSet.mode = LineDataSet.Mode.LINEAR
        benchmarkDataSet.setDrawFilled(true)
        benchmarkDataSet.fillColor = Color.parseColor("#00FF85")
        benchmarkDataSet.fillAlpha = 20

        val lineData = LineData(strategyDataSet, benchmarkDataSet)
        binding.lineChart.data = lineData
        binding.lineChart.legend.textColor = Color.WHITE
        binding.lineChart.invalidate()
    }

    private fun updateRadarChart(perf: com.example.rooknomics.data.models.PerformanceMetrics) {
        val radarChart = binding.radarChart

        // Normalize values to 0–100 scale for radar axes
        val returnsScore = (perf.totalReturn.coerceIn(-100.0, 200.0) + 100) / 3.0
        val stabilityScore = 100.0 - (perf.dailyVolatility?.coerceIn(0.0, 5.0)?.times(20) ?: 50.0)
        val drawdownScore = 100.0 - (perf.maxDrawdown?.coerceIn(0.0, 100.0) ?: 50.0)
        val costsScore = 80.0 // fixed fee structure = 0
        val liquidityScore = (perf.numberOfTrades.coerceIn(0, 100).toDouble() / 100.0) * 100.0

        val entries = listOf(
            RadarEntry(returnsScore.toFloat()),
            RadarEntry(stabilityScore.toFloat()),
            RadarEntry(drawdownScore.toFloat()),
            RadarEntry(costsScore.toFloat()),
            RadarEntry(liquidityScore.toFloat())
        )

        val dataSet = RadarDataSet(entries, "Risk Profile")
        dataSet.color = Color.parseColor("#00FF85")
        dataSet.fillColor = Color.parseColor("#00FF85")
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 60
        dataSet.lineWidth = 2f
        dataSet.setDrawHighlightCircleEnabled(true)
        dataSet.setDrawHighlightIndicators(false)

        val data = RadarData(dataSet)
        data.setValueTextSize(8f)
        data.setDrawValues(false)

        radarChart.data = data

        val labels = arrayOf("Returns", "Stab", "Drawdown", "Costs", "city")
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        radarChart.xAxis.textColor = Color.parseColor("#9CA3AF")
        radarChart.xAxis.textSize = 10f

        radarChart.yAxis.setDrawLabels(false)
        radarChart.yAxis.axisMinimum = 0f
        radarChart.yAxis.axisMaximum = 100f

        radarChart.webColor = Color.parseColor("#374151")
        radarChart.webColorInner = Color.parseColor("#374151")
        radarChart.webAlpha = 100

        radarChart.description.isEnabled = false
        radarChart.legend.isEnabled = false

        radarChart.invalidate()
    }

    private fun populateTradesList(trades: List<TradeLog>) {
        binding.llTradeLogsContainer.removeAllViews()

        for (trade in trades.take(50)) {
            val row = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(dpToPx(24), dpToPx(16), dpToPx(24), dpToPx(16))
            }

            val tvDate = createCell(trade.date, 1f, Color.parseColor("#9CA3AF"))
            val tvAction = createCell(trade.type, 1f, if (trade.type == "BUY") Color.parseColor("#60A5FA") else Color.WHITE, isBold = true)
            val tvPrice = createCell(currencyFormat.format(trade.price), 1f, Color.WHITE)

            val pnlStr = if (trade.pnl == null) "—" else {
                if (trade.pnl > 0) "+${currencyFormat.format(trade.pnl)}" else currencyFormat.format(trade.pnl)
            }
            val pnlColor = if (trade.pnl != null && trade.pnl < 0) Color.parseColor("#EF4444") else Color.parseColor("#00FF85")
            val tvPnl = createCell(pnlStr, 1f, if (trade.pnl == null) Color.parseColor("#9CA3AF") else pnlColor)

            val tvCumulative = createCell(currencyFormat.format(trade.totalValue), 1.2f, Color.WHITE, isBold = true)

            row.addView(tvDate)
            row.addView(tvAction)
            row.addView(tvPrice)
            row.addView(tvPnl)
            row.addView(tvCumulative)

            binding.llTradeLogsContainer.addView(row)

            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))
                setBackgroundColor(Color.parseColor("#374151"))
            }
            binding.llTradeLogsContainer.addView(divider)
        }
    }

    private fun createCell(textValue: String, weight: Float, textColor: Int, isBold: Boolean = false): TextView {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
            text = textValue
            textSize = 10f
            setTextColor(textColor)
            if (isBold) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
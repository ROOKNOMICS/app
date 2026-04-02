package com.example.rooknomics.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
                }
                is SimState.Loading -> {
                    binding.llChartPlaceholder.visibility = View.VISIBLE
                    binding.lineChart.visibility = View.GONE
                }
                is SimState.Success -> {
                    binding.llChartPlaceholder.visibility = View.GONE
                    binding.lineChart.visibility = View.VISIBLE

                    val results = state.response
                    val perf = results.performance

                    binding.tvMetricTotalReturn.text = percentFormat.format(perf.totalReturn)
                    binding.tvMetricAnnualized.text = percentFormat.format(perf.benchmarkReturn)
                    binding.tvMetricMaxDrawdown.text = perf.maxDrawdown?.let{percentFormat.format(it)}?:"-"
                    binding.tvMetricWinRate.text = perf.winRate?.let{percentFormat.format(it)}?:"-"

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
                    updateChart(results.trades,perf)
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

//    private fun updateChart(trades: List<TradeLog>) {
//        if (trades.isEmpty()) return

    private fun updateChart(trades: List<TradeLog>,perf:com.example.rooknomics.data.models.PerformanceMetrics){


        val entries = ArrayList<Entry>()

        entries.add(Entry(0f,10000f))

        for (i in trades.indices) {
            val t = trades[i]
            entries.add(Entry((i+1).toFloat(), t.totalValue.toFloat()))
        }

        entries.add(Entry((trades.size+1).toFloat(),perf.finalValue.toFloat()))
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
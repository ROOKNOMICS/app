package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.R
import com.example.rooknomics.data.api.BacktestApi
import com.example.rooknomics.data.models.BacktestRequest
import com.example.rooknomics.data.models.MaCrossConfig
import com.example.rooknomics.data.models.RsiConfig
import com.example.rooknomics.data.models.RulesConfig
import com.example.rooknomics.data.network.ApiClient
import com.example.rooknomics.data.repository.BacktestRepository
import com.example.rooknomics.databinding.FragmentBuilderBinding
import com.example.rooknomics.ui.viewmodel.SimViewModelFactory
import com.example.rooknomics.ui.viewmodel.SimulationViewModel

class BuilderFragment : Fragment() {
    private var _binding: FragmentBuilderBinding? = null
    private val binding get() = _binding!!

    private val simulationViewModel: SimulationViewModel by activityViewModels {
        SimViewModelFactory(
            BacktestRepository(
                ApiClient.getClient(requireContext()).create(BacktestApi::class.java)
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val maTypes = listOf("SMA – Simple", "EMA – Exponential", "WMA – Weighted")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, maTypes)
        binding.spinnerMaType.adapter = adapter

        binding.btnRunSimulationBuilder.setOnClickListener {
            val isRsiEnabled = binding.switchMomentum.isChecked
            val isMaEnabled = binding.switchTrend.isChecked

            val activeRules = mutableListOf<String>()
            if (isRsiEnabled) activeRules.add("RSI Entry")
            if (isMaEnabled) activeRules.add("MA Crossover")

            if (activeRules.isEmpty()) {
                Toast.makeText(requireContext(), "Please enable at least one rule", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rsiConfig = if (isRsiEnabled) {
                RsiConfig(
                    enabled = true,
                    period = binding.sliderRsiPeriod.value.toInt(),
                    buyBelow = binding.sliderRsiBuy.value.toInt(),
                    sellAbove = binding.sliderRsiSell.value.toInt()
                )
            } else null

            val maTypeStr = binding.spinnerMaType.selectedItem?.toString()?.split(" ")?.get(0) ?: "SMA"
            val maConfig = if (isMaEnabled) {
                MaCrossConfig(
                    enabled = true,
                    type = maTypeStr,
                    fastPeriod = binding.sliderMaShort.value.toInt(),
                    slowPeriod = binding.sliderMaLong.value.toInt()
                )
            } else null

            // Default fallback values based on the mockup context
            val request = BacktestRequest(
                name = "Builder Strategy (AAPL)",
                symbol = "AAPL",
                startDate = "2024-01-01",
                endDate = "2024-12-31",
                capital = 10000,
                activeRules = activeRules,
                rulesConfig = RulesConfig(rsi = rsiConfig, maCross = maConfig)
            )

            simulationViewModel.reset() //for every new runBacktest() call puts Vm back to Idle state
            simulationViewModel.runBacktest(request)

            try {
                findNavController().navigate(R.id.action_builderFragment_to_resultsFragment)
            } catch (e: Exception) {
                // Ignore if double clicked or bad state
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
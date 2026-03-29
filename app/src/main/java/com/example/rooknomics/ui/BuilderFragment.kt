package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.R
import com.example.rooknomics.databinding.FragmentBuilderBinding

class BuilderFragment : Fragment() {
    private var _binding: FragmentBuilderBinding? = null
    private val binding get() = _binding!!

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
            // Usually the bottom nav results click, but if forced from builder we navigate:
            // Assuming action exists:
            // findNavController().navigate(R.id.action_builderFragment_to_resultsFragment)
            
            // To ensure smooth bottom nav syncing, if user hits run, we can also manually toggle bottom nav selection
            // But standard fragment action is robust:
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

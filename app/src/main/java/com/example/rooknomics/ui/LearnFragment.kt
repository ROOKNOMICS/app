package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rooknomics.databinding.FragmentLearnBinding

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val items = listOf(
            LearnItem("Backtesting", "Strategy Backtesting", "Strategy"),
            LearnItem("VaR", "Value at Risk", "Risk"),
            LearnItem("Beta", "Beta Coefficient", "Market"),
            LearnItem("Momentum", "Momentum Trading", "Strategy")
        )
        binding.rvLearn.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.rvLearn.adapter = LearnAdapter(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

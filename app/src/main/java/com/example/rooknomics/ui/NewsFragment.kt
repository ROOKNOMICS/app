package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rooknomics.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val items = listOf(
            NewsItem("Here are 3 themes that drove another super positive market session...", "CNBC", "18 hours ago"),
            NewsItem("Worried about Strait of Hormuz inflation to come?", "Bloomberg", "20 hours ago"),
            NewsItem("Fed Chair Jerome Powell signals rate cuts likely later this year", "WSJ", "1 day ago")
        )
        binding.rvNews.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.rvNews.adapter = NewsAdapter(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

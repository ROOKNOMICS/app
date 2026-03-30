package com.example.rooknomics.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.R
import com.example.rooknomics.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ GET USER NAME (API → SharedPrefs fallback)
        val sharedPref = requireActivity()
            .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        val nameFromStorage = sharedPref.getString("NAME", null)

        // If API already saved name → use it
        val finalName = nameFromStorage ?: "User"

        // Extract initial
        val initial = if (finalName.isNotEmpty()) {
            finalName.trim()[0].uppercaseChar().toString()
        } else {
            "U"
        }

        binding.btnProfile.text = initial

        // 🎯 Animation
        val floatAnim = android.view.animation.AnimationUtils
            .loadAnimation(requireContext(), R.anim.anim_float)
        binding.ivGoldCoins.startAnimation(floatAnim)

        // 🔁 Navigation
        binding.btnToBuilder.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_builderFragment)
        }

        binding.btnRunSimBottom.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_builderFragment)
        }

        binding.btnViewDemo.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_builderFragment)
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
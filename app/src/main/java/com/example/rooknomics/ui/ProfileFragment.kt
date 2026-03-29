package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load User Data
        val sharedPref = requireActivity().getSharedPreferences("USER_SESSION", android.content.Context.MODE_PRIVATE)
        val email = sharedPref.getString("EMAIL", "24cseaiml055@jssaten.ac.in") ?: "24cseaiml055@jssaten.ac.in"
        val name = sharedPref.getString("NAME", "Jssaten") ?: "Jssaten"
        
        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvInfoEmail.text = email
        binding.tvProfileAvatar.text = name.firstOrNull()?.uppercase() ?: "J"
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSignOut.setOnClickListener {
            // Usually returns to Login
            try {
                findNavController().navigate(com.example.rooknomics.R.id.loginFragment)
            } catch (e: Exception) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

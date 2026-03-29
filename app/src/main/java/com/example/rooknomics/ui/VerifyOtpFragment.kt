package com.example.rooknomics.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.R
import com.example.rooknomics.data.api.AuthApi
import com.example.rooknomics.data.models.VerifyOtpRequest
import com.example.rooknomics.data.network.ApiClient
import com.example.rooknomics.data.repository.AuthRepository
import com.example.rooknomics.databinding.FragmentVerifyOtpBinding
import com.example.rooknomics.ui.viewmodel.AuthState
import com.example.rooknomics.ui.viewmodel.AuthViewModel
import com.example.rooknomics.ui.viewmodel.AuthViewModelFactory

class VerifyOtpFragment : Fragment() {

    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    // Dynamic Retrofit ViewModel injection
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(ApiClient.getClient(requireContext()).create(AuthApi::class.java)))
    }

    private var userEmail: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Retrieve email passed via SharedPreferences or Bundle from LoginFragment
        val sharedPref = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        userEmail = sharedPref.getString("PENDING_EMAIL", "") ?: ""

        binding.tvOtpMessage.text = "We've sent a 6-digit code to $userEmail"

        binding.btnVerify.setOnClickListener {
            val otpCode = binding.etOtp.text.toString().trim()
            if (otpCode.length == 6) {
                authViewModel.verifyOtp(VerifyOtpRequest(userEmail, otpCode))
            } else {
                Toast.makeText(context, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // LiveData Observer for Network State
        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnVerify.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    
                    // The Interceptor has already grabbed the JWT Set-Cookie under the hood!
                    // Save the user profile to our Session layer dynamically
                    with(sharedPref.edit()) {
                        putString("EMAIL", state.response.user?.email)
                        putString("NAME", state.response.user?.name)
                        apply()
                    }
                    // Transition straight to the core Dashboard
                    val action = R.id.action_verifyOtpFragment_to_dashboardFragment
                    // Verify if this action exists in nav_graph or manually navigate
                    try {
                        findNavController().navigate(action)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigating...", Toast.LENGTH_SHORT).show()
                    }
                }
                is AuthState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    Toast.makeText(context, "Verification Error: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> { binding.pbLoading.visibility = View.GONE }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

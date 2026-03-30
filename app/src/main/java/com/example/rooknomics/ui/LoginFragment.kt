package com.example.rooknomics.ui

import android.content.Context
import android.os.Bundle
import android.text.InputType
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
import com.example.rooknomics.data.models.LoginRequest
import com.example.rooknomics.data.models.RegisterRequest
import com.example.rooknomics.data.network.ApiClient
import com.example.rooknomics.data.repository.AuthRepository
import com.example.rooknomics.databinding.FragmentLoginBinding
import com.example.rooknomics.ui.viewmodel.AuthState
import com.example.rooknomics.ui.viewmodel.AuthViewModel
import com.example.rooknomics.ui.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isLoginMode = true

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(
                ApiClient.getClient(requireContext()).create(AuthApi::class.java)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        // 🔁 Switch Login/Signup
        binding.llFooter.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        // 🔐 PASSWORD TOGGLE FIX (MAIN THING)
        var isPasswordVisible = false

        binding.ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_line)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_off_line)
            }

            isPasswordVisible = !isPasswordVisible

            // cursor end pe rakho
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        // 🔑 LOGIN / SIGNUP
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val name = binding.etName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                authViewModel.login(LoginRequest(email, password))
            } else {
                if (name.isEmpty()) {
                    Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                authViewModel.register(RegisterRequest(name, email, password))
            }
        }

        // 📡 OBSERVER
        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Authenticating..."
                }

                is AuthState.Success -> {
                    binding.btnLogin.isEnabled = true

                    if (isLoginMode) {
                        val sharedPref = requireActivity()
                            .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

                        with(sharedPref.edit()) {
                            putString("EMAIL", state.response.user?.email ?: binding.etEmail.text.toString())
                            putString("NAME", state.response.user?.name ?: "RookUser")
                            apply()
                        }

                        try {
                            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                        } catch (_: Exception) {}
                    } else {
                        val sharedPref = requireActivity()
                            .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

                        with(sharedPref.edit()) {
                            putString("PENDING_EMAIL", binding.etEmail.text.toString())
                            apply()
                        }

                        try {
                            findNavController().navigate(R.id.action_loginFragment_to_verifyOtpFragment)
                        } catch (_: Exception) {}
                    }
                }

                is AuthState.Error -> {
                    binding.btnLogin.isEnabled = true
                    updateUI()
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    binding.btnLogin.isEnabled = true
                }
            }
        })
    }

    private fun updateUI() {
        if (isLoginMode) {
            binding.llInputName.visibility = View.GONE
            binding.tvForgotPassword.visibility = View.VISIBLE
            binding.btnLogin.text = "Sign In"
            binding.tvFooterPrefix.text = "Don't have an account? "
            binding.tvFooterAction.text = "Sign up"
        } else {
            binding.llInputName.visibility = View.VISIBLE
            binding.tvForgotPassword.visibility = View.GONE
            binding.btnLogin.text = "Create Account"
            binding.tvFooterPrefix.text = "Already have an account? "
            binding.tvFooterAction.text = "Sign in"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
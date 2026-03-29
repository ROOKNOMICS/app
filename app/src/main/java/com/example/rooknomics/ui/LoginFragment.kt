package com.example.rooknomics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rooknomics.R
import com.example.rooknomics.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isLoginMode = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        updateUI()

        binding.llFooter.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        }
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

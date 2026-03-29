package com.example.rooknomics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rooknomics.data.models.*
import com.example.rooknomics.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    // Hold email for OTP flow
    var registrationEmail: String = ""

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.login(request)
                if (result.isSuccessful && result.body() != null) {
                    _authState.value = AuthState.Success(result.body()!!)
                } else {
                    _authState.value = AuthState.Error(result.message() ?: "Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.register(request)
                if (result.isSuccessful && result.body() != null) {
                    registrationEmail = request.email // Save email for next OTP screen
                    _authState.value = AuthState.Success(result.body()!!)
                } else {
                    _authState.value = AuthState.Error(result.message() ?: "Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun verifyOtp(request: VerifyOtpRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.verifyOtp(request)
                if (result.isSuccessful && result.body() != null) {
                    _authState.value = AuthState.Success(result.body()!!)
                } else {
                    _authState.value = AuthState.Error(result.message() ?: "OTP Verification failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

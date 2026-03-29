package com.example.rooknomics.data.repository

import com.example.rooknomics.data.api.AuthApi
import com.example.rooknomics.data.models.*

class AuthRepository(private val authApi: AuthApi) {

    suspend fun login(request: LoginRequest) = authApi.login(request)
    
    suspend fun register(request: RegisterRequest) = authApi.register(request)
    
    suspend fun verifyOtp(request: VerifyOtpRequest) = authApi.verifyOtp(request)
    
    suspend fun resendOtp(request: ResendOtpRequest) = authApi.resendOtp(request)
    
    suspend fun getMe() = authApi.getMe()
    
    suspend fun logout() = authApi.logout()
}

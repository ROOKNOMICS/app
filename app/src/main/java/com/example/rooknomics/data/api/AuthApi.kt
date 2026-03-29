package com.example.rooknomics.data.api

import com.example.rooknomics.data.models.AuthResponse
import com.example.rooknomics.data.models.LoginRequest
import com.example.rooknomics.data.models.LogoutResponse
import com.example.rooknomics.data.models.RegisterRequest
import com.example.rooknomics.data.models.ResendOtpRequest
import com.example.rooknomics.data.models.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("api/auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<LogoutResponse>
}

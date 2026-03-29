package com.example.rooknomics.data.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String

)

data class ResendOtpRequest(
    val email: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String?
)

data class AuthResponse(
    val message: String?,
    val user: User?,
    val email: String?
)

data class LogoutResponse(
    val message: String
)

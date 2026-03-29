package com.example.rooknomics.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Intercepts HTTP calls to manage the complex Node API Authentication.
 * 1. Automatically injects 'Cookie: token=JWT' for `/auth` and `/simulations` queries.
 * 2. Automatically injects 'Authorization: Bearer JWT' for `/backtests` specific routes.
 * 3. Catches and parses 'Set-Cookie' on /login & /verify-otp to extract the physical JWT and save it to native SharedPreferences.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlString = originalRequest.url.toString()
        val token = sessionManager.fetchAuthToken()

        // 1. DYNAMIC INJECTION: Modifying outgoing requests based on Endpoint requirements
        val requestBuilder = originalRequest.newBuilder()
        
        if (token != null) {
            if (urlString.contains("/api/backtests")) {
                // Backtest endpoints demand Bearer Tokens natively
                requestBuilder.header("Authorization", "Bearer $token")
            } else {
                // Standard internal protect middleware relies on generic cookie headers
                requestBuilder.header("Cookie", "token=$token")
            }
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)
        
        // 2. COOKIE EXTRACTION: The Vercel Node backend natively uses Set-Cookie instead of JWT JSON body return
        // We have to grab it manually from the raw Network Header and rip the JWT out of it
        if (urlString.contains("/api/auth/login") || urlString.contains("/api/auth/verify-otp")) {
            val cookies = response.headers("Set-Cookie")
            for (cookie in cookies) {
                if (cookie.contains("token=")) {
                    // Cookie string format: "token=eyJhbGci...; Path=/; HttpOnly"
                    val rawToken = cookie.substringAfter("token=").substringBefore(";")
                    sessionManager.saveAuthToken(rawToken)
                    break 
                }
            }
        }

        return response
    }
}

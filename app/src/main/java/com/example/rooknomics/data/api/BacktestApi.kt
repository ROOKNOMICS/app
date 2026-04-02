package com.example.rooknomics.data.api

import com.example.rooknomics.data.models.BacktestResults
import com.example.rooknomics.data.models.BacktestRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BacktestApi {
    @POST("api/backtest")
    suspend fun runBacktest(@Body request: BacktestRequest): Response<BacktestResults>
}

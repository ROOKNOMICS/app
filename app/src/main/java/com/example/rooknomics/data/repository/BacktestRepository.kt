package com.example.rooknomics.data.repository

import com.example.rooknomics.data.api.BacktestApi
import com.example.rooknomics.data.models.BacktestRequest

class BacktestRepository(private val api: BacktestApi) {
    suspend fun runBacktest(request: BacktestRequest) = api.runBacktest(request)
}

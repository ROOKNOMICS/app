package com.example.rooknomics.data.models

data class BacktestRequest(
    val name: String,
    val symbol: String,
    val startDate: String,
    val endDate: String,
    val capital: Int,
    val activeRules: List<String>,
    val rulesConfig: RulesConfig
)

data class RulesConfig(
    val rsi: RsiConfig? = null,
    val maCross: MaCrossConfig? = null
)

data class RsiConfig(val enabled: Boolean, val period: Int, val buyBelow: Int, val sellAbove: Int)
data class MaCrossConfig(val enabled: Boolean, val type: String, val fastPeriod: Int, val slowPeriod: Int)

data class BacktestApiResponse(
    val message: String,
    val backtestId: String,
    val results: BacktestResults
)

data class BacktestResults(
    val performance: PerformanceMetrics,
    val trades: List<TradeLog>,
    val verdict: Verdict
)

data class PerformanceMetrics(
    val totalReturn: Double,
    val benchmarkReturn: Double,
    val finalValue: Double,
    val benchmarkFinalValue: Double?,
    val maxDrawdown: Double?,
    val sharpeRatio: Double?,
    val dailyVolatility: Double?,
    val numberOfTrades: Int,
    val winRate: Double?,
    val profitFactor: Double?,
    val avgHoldingDays: Double?
)

data class TradeLog(
    val date: String,
    val type: String,
    val price: Double,
    val shares: Int,
    val signal: String,
    val pnl: Double?,
    val pnlPct: Double?,
    val totalValue: Double,
    val holdingDays: Int?
)

data class Verdict(
    val status: String,
    val summary: String,
    val insights: List<String>
)

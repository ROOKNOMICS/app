package com.example.rooknomics.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rooknomics.R

data class LearnItem(val title: String, val subtitle: String, val category: String)

class LearnAdapter(private val items: List<LearnItem>) :
    RecyclerView.Adapter<LearnAdapter.LearnViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class LearnViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val subtitle: TextView = view.findViewById(R.id.tv_subtitle)
        val category: TextView = view.findViewById(R.id.tv_chip_category)
        val description: TextView = view.findViewById(R.id.tv_description)
        val readMore: TextView = view.findViewById(R.id.tv_read_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_learn_card, parent, false)
        return LearnViewHolder(view)
    }

    override fun onBindViewHolder(holder: LearnViewHolder, position: Int) {
        val item = items[position]

                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.category.text = item.category

                val isExpanded = expandedPositions.contains(position)

        // 🔥 DIFFERENT CONTENT (THIS IS THE KEY FIX)
        val shortText = getShortDescription(position)
        val fullText = getFullDescription(position)

        holder.description.text = if (isExpanded) fullText else shortText

        holder.readMore.text = if (isExpanded) "Read less" else "Read more"

        holder.readMore.setOnClickListener {
            if (isExpanded) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = items.size

    // 🔥 SHORT TEXT
    private fun getShortDescription(position: Int): String {
        return when (position) {
            0 -> "Backtesting is applying a strategy to past market data..."
            1 -> "Value at Risk estimates potential loss in investments..."
            2 -> "Beta measures stock volatility relative to market..."
            3 -> "Momentum trading focuses on trends and continuation..."
            else -> "Financial concept overview..."
        }
    }

    // 🔥 FULL TEXT (EXPANDED)
    private fun getFullDescription(position: Int): String {
        return when (position) {
            0 -> "Backtesting is the process of applying a trading strategy to historical market data to evaluate its effectiveness. It helps traders understand how their strategy would have performed in the past and optimize parameters before deploying real capital."
            1 -> "Value at Risk (VaR) is a statistical measure used to assess the level of financial risk within a portfolio over a specific time frame. It estimates the maximum expected loss under normal market conditions at a given confidence level."
            2 -> "Beta is a measure of a stock's volatility compared to the overall market. A beta greater than 1 indicates higher volatility, while less than 1 suggests lower volatility. It is widely used in risk assessment and portfolio management."
            3 -> "Momentum trading is a strategy that aims to capitalize on existing market trends. Traders buy assets that are rising and sell those that are falling, assuming that the trend will continue for some time."
            else -> "Detailed explanation of the concept..."
        }
    }
}
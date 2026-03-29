package com.example.rooknomics.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rooknomics.R

data class LearnItem(val title: String, val subtitle: String, val category: String)

class LearnAdapter(private val items: List<LearnItem>) : RecyclerView.Adapter<LearnAdapter.LearnViewHolder>() {

    class LearnViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val subtitle: TextView = view.findViewById(R.id.tv_subtitle)
        val category: TextView = view.findViewById(R.id.tv_chip_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_learn_card, parent, false)
        return LearnViewHolder(view)
    }

    override fun onBindViewHolder(holder: LearnViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.category.text = item.category
    }

    override fun getItemCount() = items.size
}

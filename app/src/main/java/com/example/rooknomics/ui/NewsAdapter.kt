package com.example.rooknomics.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rooknomics.R

data class NewsItem(val title: String, val source: String, val time: String)

class NewsAdapter(private val items: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val source: TextView = view.findViewById(R.id.tv_source)
        val time: TextView = view.findViewById(R.id.tv_timestamp)
        val image: ImageView = view.findViewById(R.id.iv_cover) // ✅ IMPORTANT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.source.text = item.source
        holder.time.text = item.time

        // 🔥 USE YOUR EXISTING IMAGES
        when (position) {
            0 -> holder.image.setImageResource(R.drawable.img_1)
            1 -> holder.image.setImageResource(R.drawable.img_2)
            2 -> holder.image.setImageResource(R.drawable.img_3)
            else -> holder.image.setImageResource(R.drawable.img_1)
        }
    }

    override fun getItemCount() = items.size
}
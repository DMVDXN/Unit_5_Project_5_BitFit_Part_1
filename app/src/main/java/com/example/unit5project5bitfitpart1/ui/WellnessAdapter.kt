package com.example.unit5project5bitfitpart1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unit5project5bitfitpart1.data.WellnessEntry
import com.example.unit5project5bitfitpart1.databinding.ItemEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WellnessAdapter : RecyclerView.Adapter<WellnessAdapter.VH>() {

    private val items = mutableListOf<WellnessEntry>()
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun submit(list: List<WellnessEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.binding.tvAmount.text = "Water Cups: ${e.amountCups}"
        holder.binding.tvNote.text = if (e.note.isBlank()) "(no note)" else e.note
        holder.binding.tvDate.text = fmt.format(Date(e.createdAt))
    }

    override fun getItemCount(): Int = items.size
}

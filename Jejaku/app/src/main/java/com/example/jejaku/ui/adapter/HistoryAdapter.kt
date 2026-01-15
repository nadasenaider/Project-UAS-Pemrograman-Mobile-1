package com.example.jejaku.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jejaku.data.LocationEntity
import com.example.jejaku.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView Adapter for displaying location history with delete functionality
 */
class HistoryAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<LocationEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for history item
     */
    class HistoryViewHolder(
        private val binding: ItemHistoryBinding,
        private val onItemClick: (Long) -> Unit,
        private val onDeleteClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: LocationEntity) {
            binding.apply {
                tvLocationName.text = location.locationName
                tvDate.text = location.date
                tvHistoryNote.text = location.note
                tvHistoryLocation.text = "Lat: ${location.latitude}, Long: ${location.longitude}"

                // Card/Item click listener - navigate to detail
                root.setOnClickListener {
                    onItemClick(location.id)
                }

                // Delete button click listener
                btnDelete.setOnClickListener {
                    onDeleteClick(location.id)
                }
            }
        }

        /**
         * Format timestamp to readable date string
         */
        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    class HistoryDiffCallback : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem == newItem
        }
    }
}

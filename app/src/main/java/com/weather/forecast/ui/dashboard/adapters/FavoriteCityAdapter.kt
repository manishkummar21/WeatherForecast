package com.weather.forecast.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weather.forecast.databinding.ItemAddressBinding
import com.weather.forecast.entities.FavoriteCity

class FavoriteCityAdapter() :
    ListAdapter<FavoriteCity, FavoriteCityAdapter.FavoriteItem>(
        DiffCallback()
    ) {

    private var itemClickListener: ((FavoriteCity) -> Unit)? = null


    class DiffCallback : DiffUtil.ItemCallback<FavoriteCity>() {
        override fun areItemsTheSame(
            oldItem: FavoriteCity,
            newItem: FavoriteCity
        ): Boolean {
            return oldItem.placeID == newItem.placeID
        }

        override fun areContentsTheSame(
            oldItem: FavoriteCity,
            newItem: FavoriteCity
        ): Boolean {
            return oldItem == newItem
        }
    }

    class FavoriteItem(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItem {
        return FavoriteItem(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FavoriteItem,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.title.text = item.title
        holder.binding.subtitle.text = item.subtitle

        holder.binding.liked.visibility = View.GONE

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }


    }

    fun setItemClickListener(listener: (item: FavoriteCity) -> Unit) {
        itemClickListener = listener
    }

}
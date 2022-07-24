package com.weather.forecast.ui.dashboard.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.weather.forecast.R
import com.weather.forecast.databinding.ItemAddressBinding

class LocationSearchAdapter(private val favCities: MutableList<String>) :
    ListAdapter<AutocompletePrediction, LocationSearchAdapter.LocationSearchItem>(
        DiffCallback()
    ) {

    private var itemClickListener: ((AutocompletePrediction) -> Unit)? = null

    private var favItemClickListener: ((AutocompletePrediction) -> Unit)? = null


    class DiffCallback : DiffUtil.ItemCallback<AutocompletePrediction>() {
        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.placeId == newItem.placeId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem == newItem
        }
    }

    class LocationSearchItem(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationSearchItem {
        return LocationSearchItem(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LocationSearchItem,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.title.text = item.getPrimaryText(null)
        holder.binding.subtitle.text = item.getSecondaryText(null)

//
        holder.binding.liked.setImageDrawable(
            if (favCities.contains(item.placeId)) ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.ic_heart_selected
            ) else ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_heart_non_selected)
        )


        holder.binding.liked.setOnClickListener {
            favItemClickListener?.invoke(item)
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }


    }

    fun setItemClickListener(listener: (item: AutocompletePrediction) -> Unit) {
        itemClickListener = listener
    }


    fun setFavItemClickListener(listener: (item: AutocompletePrediction) -> Unit) {
        favItemClickListener = listener
    }


}
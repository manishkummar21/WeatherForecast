package com.weather.forecast.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weather.forecast.R
import com.weather.forecast.Utils.getName
import com.weather.forecast.Utils.toMilliSecond
import com.weather.forecast.data.location.model.LocationDetails
import com.weather.forecast.data.weather.model.WeatherData
import com.weather.forecast.databinding.ItemCurrentTemperatureBinding
import com.weather.forecast.ui.IComponentClickListener
import java.text.SimpleDateFormat
import java.util.*

class CurrentTemperatureAdapter(
    private val locationDetails: LocationDetails,
    private val unit: String,
    private val itemClickListener: IComponentClickListener
) :
    ListAdapter<WeatherData, CurrentTemperatureAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private val calendarInstance by lazy { Calendar.getInstance() }

    private val timeFormat by lazy {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }


    class DiffCallback : DiffUtil.ItemCallback<WeatherData>() {
        override fun areItemsTheSame(
            oldItem: WeatherData,
            newItem: WeatherData
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: WeatherData,
            newItem: WeatherData
        ): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(val binding: ItemCurrentTemperatureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCurrentTemperatureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        holder.binding.location.text = locationDetails.getName()

        holder.binding.currentTime.text = String.format(
            holder.itemView.context.resources.getString(R.string.day_time),
            timeFormat.let {
                calendarInstance.timeInMillis = item.dt.toMilliSecond()
                it.format(calendarInstance.time)
            })

        holder.binding.currentTemperatureDes.text = item.weather.last().description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        holder.binding.currentTemperature.text = String.format(
            holder.itemView.context.resources.getString(R.string.degree_unit), item.temp.toString()
        )

        holder.binding.pressure.text = String.format(
            holder.itemView.context.resources.getString(R.string.pressure_hpa_unit), item.pressure.toString()
        )

        holder.binding.humditiy.text = String.format(
            holder.itemView.context.resources.getString(R.string.humdity_unit), item.humidity.toString()
        )

        when (unit) {
            "metric" -> {
                holder.binding.wind.text = String.format(
                    holder.itemView.context.resources.getString(R.string.wind_km_unit), item.wind_speed.toString()
                )

            }

            "imperial" -> {
                holder.binding.wind.text = String.format(
                    holder.itemView.context.resources.getString(R.string.wind_m_unit), item.wind_speed.toString()
                )
            }
        }

        holder.binding.location.setOnClickListener {
            itemClickListener.onLocationClick()
        }

    }
}
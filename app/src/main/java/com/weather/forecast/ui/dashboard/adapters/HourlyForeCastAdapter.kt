package com.weather.forecast.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.weather.forecast.R
import com.weather.forecast.Utils.toMilliSecond
import com.weather.forecast.data.weather.model.WeatherData
import com.weather.forecast.databinding.ItemHourlyForecastBinding
import java.text.SimpleDateFormat
import java.util.*


class HourlyForeCastAdapter(private val requestManager: RequestManager) :
    ListAdapter<WeatherData, HourlyForeCastAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private val calendarInstance by lazy { Calendar.getInstance() }

    private val timeFormat by lazy {
        SimpleDateFormat("hh a", Locale.getDefault())
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

    class ViewHolder(val binding: ItemHourlyForecastBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHourlyForecastBinding.inflate(
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

        holder.binding.day.text = timeFormat.let {
            calendarInstance.timeInMillis = item.dt.toMilliSecond()
            it.format(calendarInstance.time)
        }

        holder.binding.temperature.text = String.format(
            holder.itemView.context.resources.getString(R.string.degree_unit), item.temp.toString()
        )

        requestManager.load("https://openweathermap.org/img/w/${item.weather.last().icon}.png")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.weatherImg)

    }
}
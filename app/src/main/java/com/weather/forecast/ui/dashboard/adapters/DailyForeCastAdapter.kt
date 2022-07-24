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
import com.weather.forecast.data.weather.model.DailyWeatherData
import com.weather.forecast.data.weather.model.WeatherData
import com.weather.forecast.databinding.ItemDailyForecastBinding
import com.weather.forecast.databinding.ItemHourlyForecastBinding
import java.text.SimpleDateFormat
import java.util.*


class DailyForeCastAdapter(private val requestManager: RequestManager,) :
    ListAdapter<DailyWeatherData, DailyForeCastAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private val calendarInstance by lazy { Calendar.getInstance() }

    private val day by lazy {
        SimpleDateFormat("EEEE", Locale.getDefault())
    }

    private val dateMonth by lazy {
        SimpleDateFormat("dd MMM", Locale.getDefault())
    }


    class DiffCallback : DiffUtil.ItemCallback<DailyWeatherData>() {
        override fun areItemsTheSame(
            oldItem: DailyWeatherData,
            newItem: DailyWeatherData
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: DailyWeatherData,
            newItem: DailyWeatherData
        ): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDailyForecastBinding.inflate(
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


        holder.binding.weekDay.text = day.let {
            calendarInstance.timeInMillis = item.dt.toMilliSecond()
            it.format(calendarInstance.time)
        }

        holder.binding.date.text = dateMonth.let {
            calendarInstance.timeInMillis = item.dt.toMilliSecond()
            it.format(calendarInstance.time)
        }

        holder.binding.temperature.text = String.format(
            holder.itemView.context.resources.getString(R.string.degree_unit), item.temp.day.toString()
        )

        requestManager.load("https://openweathermap.org/img/w/${item.weather.last().icon}.png")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.weatherImg)

    }
}
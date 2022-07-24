package com.weather.forecast.base

import android.content.Context
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.weather.forecast.data.base.Components
import com.weather.forecast.data.base.CurrentLocationTemperatureInfo
import com.weather.forecast.data.base.DailyForeCastInfo
import com.weather.forecast.data.base.HourlyForeCastInfo
import com.weather.forecast.ui.IComponentClickListener
import com.weather.forecast.ui.dashboard.adapters.CurrentTemperatureAdapter
import com.weather.forecast.ui.dashboard.adapters.DailyForeCastAdapter
import com.weather.forecast.ui.dashboard.adapters.HourlyForeCastAdapter


class ComponentsAdapter(
    val context: Context,
    val components: MutableList<Components>,
    val itemClickListener: IComponentClickListener
) {
    private val requestManager: RequestManager by lazy {
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions().apply(
                    RequestOptions.diskCacheStrategyOf(
                        DiskCacheStrategy.ALL
                    )
                )
            )
    }

    private val adapter: ConcatAdapter = ConcatAdapter()

    init {
        addAdapter()
    }

    fun setComponents(data: List<Components>) {

        //clear content
        val adapters = adapter.adapters
        adapters.forEach {
            adapter.removeAdapter(it)
        }

        components.clear()
        components.addAll(data)

        //redraw
        addAdapter()
    }

    private fun addAdapter() {
        components.forEachIndexed { index, it ->
            when (it) {
                is CurrentLocationTemperatureInfo -> {
                    val itemAdapter = CurrentTemperatureAdapter(it.locationDetails, it.unit, itemClickListener)
                    itemAdapter.submitList(mutableListOf(it.data))
                    adapter.addAdapter(itemAdapter)
                }

                is HourlyForeCastInfo -> {
                    val itemAdapter = HourlyForeCastAdapter(
                        requestManager
                    )

                    itemAdapter.submitList(it.data)

                    adapter.addAdapter(
                        getBaseAdapter(
                            context, 0,
                            itemAdapter, "Hourly Forecast"
                        )
                    )
                }

                is DailyForeCastInfo -> {
                    val itemAdapter = DailyForeCastAdapter(
                        requestManager
                    )

                    itemAdapter.submitList(it.data)

                    adapter.addAdapter(
                        getBaseAdapter(
                            context, 1,
                            itemAdapter, "Daily Forecast"
                        )
                    )
                }
            }
        }
    }

    fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }

    fun getAdapter(): ConcatAdapter {
        return adapter
    }

    companion object {

        fun getBaseAdapter(
            context: Context,
            orientation: Int,
            adapter: RecyclerView.Adapter<*>,
            sectionTitle: String = "",
        ): BaseConcatAdapter {
            return BaseConcatAdapter(context, orientation, sectionTitle, adapter)
        }
    }
}
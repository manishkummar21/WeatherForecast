package com.weather.forecast.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.weather.forecast.R
import com.weather.forecast.databinding.ComponentHorizontalBinding


class BaseConcatAdapter(
    private val context: Context,
    private val orientation: Int,
    private val sectionTitle: String,
    private val itemAdapter: RecyclerView.Adapter<*>,
) :
    RecyclerView.Adapter<BaseConcatAdapter.LinearViewHolder>() {


    class LinearViewHolder(val binding: ComponentHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearViewHolder {

        val view = ComponentHorizontalBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )

        val layoutManager = LinearLayoutManager(context, orientation, false)

        val mDividerItemDecoration = DividerItemDecoration(
            context,
            layoutManager.orientation
        )

        ContextCompat.getDrawable(
            context,
            if (orientation == 0) R.drawable.divider_horizontal_24dp else R.drawable.divider_linear_24dp
        )?.let {
            mDividerItemDecoration.setDrawable(it)
        }

        view.list.layoutManager = layoutManager

        view.list.addItemDecoration(mDividerItemDecoration)

        return LinearViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinearViewHolder, position: Int) {
        holder.binding.title.text = sectionTitle
        holder.binding.list.adapter = itemAdapter
    }
}
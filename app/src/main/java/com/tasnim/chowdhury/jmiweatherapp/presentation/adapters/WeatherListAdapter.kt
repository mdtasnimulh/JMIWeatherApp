package com.tasnim.chowdhury.jmiweatherapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tasnim.chowdhury.jmiweatherapp.databinding.WeatherItemLayoutBinding
import com.tasnim.chowdhury.jmiweatherapp.domain.model.WeatherModel

class WeatherListAdapter : RecyclerView.Adapter<WeatherListAdapter.MainViewHolder>() {

    private var weatherValue: MutableList<WeatherModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            WeatherItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if (position < weatherValue.size) {
            val item = weatherValue[position]
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return weatherValue.size
    }

    inner class MainViewHolder(private val binding: WeatherItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherModel: WeatherModel) {
            binding.cityName.text = weatherModel.cityName
            binding.condition.text = weatherModel.condition
            val tempStr = "${weatherModel.temp.toInt()}°C"
            binding.temperature.text = tempStr

            setSeparator(adapterPosition, binding.separatorView)
        }
    }

    fun addWeatherData(data: List<WeatherModel>) {
        weatherValue.clear()
        weatherValue.addAll(data)
        notifyDataSetChanged()
    }

    private fun setSeparator(position: Int, separatorView: View) {
        if (position == weatherValue.size-1) {
            separatorView.visibility = View.GONE
        } else {
            separatorView.visibility = View.VISIBLE
        }
    }

}
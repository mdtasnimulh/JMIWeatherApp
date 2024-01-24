package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasnim.chowdhury.jmiweatherapp.databinding.FragmentWeatherListBinding
import com.tasnim.chowdhury.jmiweatherapp.domain.model.WeatherModel
import com.tasnim.chowdhury.jmiweatherapp.presentation.adapters.WeatherListAdapter
import com.tasnim.chowdhury.jmiweatherapp.presentation.pages.list.viewModel.WeatherViewModel
import com.tasnim.chowdhury.jmiweatherapp.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherListFragment : Fragment() {

    private lateinit var binding: FragmentWeatherListBinding
    private lateinit var weatherAdapter: WeatherListAdapter
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentWeatherListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupObserver()
        setupClicks()
    }

    private fun setupAdapter() {
        weatherAdapter = WeatherListAdapter()
        binding.weatherListRv.adapter = weatherAdapter
        binding.weatherListRv.setHasFixedSize(true)
        binding.weatherListRv.itemAnimator = DefaultItemAnimator()
        binding.weatherListRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        weatherViewModel.fetchWeatherList()
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.weatherState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.weatherListRv.visibility = View.GONE

                        Log.d("WeatherListFragment", "Loading___")
                    }

                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.weatherListRv.visibility = View.VISIBLE

                        it.data?.let {  weatherResponse ->
                            val list: List<WeatherModel> = weatherResponse.list!!.map { response ->
                                WeatherModel(
                                    id = response.id,
                                    feelsLike = response.main.feelsLike,
                                    temp = response.main.temp,
                                    condition = response.weather[0].main,
                                    cityName = response.name,
                                    lat = response.coord.lat,
                                    lon = response.coord.lon,
                                    humidity = response.main.humidity,
                                    windSpeed = response.wind.speed,
                                    maxTemp = response.main.tempMax,
                                    minTemp = response.main.tempMin,
                                    icon = response.weather[0].icon
                                )
                            }

                            Log.d("WeatherListFragment", "$list")
                            weatherAdapter.addWeatherData(list)
                        }
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.weatherListRv.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                        Log.d("WeatherListFragment", "${it.message}")
                    }
                }
            }
        }
    }

    private fun setupClicks() {
        weatherAdapter.itemClick = { weatherData ->
            findNavController().navigate(
                WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailsFragment(weatherData)
            )
        }
    }
}
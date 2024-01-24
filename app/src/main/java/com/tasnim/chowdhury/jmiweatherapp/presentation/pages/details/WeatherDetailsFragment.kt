package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment() {

    private lateinit var binding: FragmentWeatherDetailsBinding
    private var map: GoogleMap? = null
    private val args by navArgs<WeatherDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentWeatherDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        initView()
        setupData()
        setupClicks()
    }

    private fun initView() {
        binding.mapView.getMapAsync {
            map = it
            it.uiSettings.apply {
                isZoomControlsEnabled = false
                isZoomGesturesEnabled = false
                isScrollGesturesEnabled = false
            }
            val latLng = LatLng(args.weatherDetails.lat, args.weatherDetails.lon)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            val markerOptions = MarkerOptions().apply {
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                position(latLng)
            }
            it.addMarker(markerOptions)
        }
    }

    private fun setupData() {
        val humidityStr = "Humidity: ${args.weatherDetails.humidity}"
        val windSpeedStr = "Wind Speed: ${args.weatherDetails.windSpeed}"
        val maxStr = "Max. Temp: ${args.weatherDetails.maxTemp.toInt()}°C"
        val minStr = "Max. Temp: ${args.weatherDetails.minTemp.toInt()}°C"
        val tempStr = "${args.weatherDetails.temp.toInt()}°C"
        binding.cityName.text = args.weatherDetails.cityName
        binding.condition.text = args.weatherDetails.condition
        binding.humidity.text = humidityStr
        binding.windSpeed.text = windSpeedStr
        binding.maxTemp.text = maxStr
        binding.minTemp.text = minStr
        binding.temperature.text = tempStr
        setWeatherIcon(args.weatherDetails.icon)
    }

    private fun setupClicks() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setWeatherIcon(icon: String){
        if (icon == "01d"){
            binding.tempIcon.setImageResource(R.drawable.clear_sky_day)
        }
        if (icon == "01n"){
            binding.tempIcon.setImageResource(R.drawable.clear_sky_night)
        }
        if (icon == "02d"){
            binding.tempIcon.setImageResource(R.drawable.few_clouds_d)
        }
        if (icon == "02n"){
            binding.tempIcon.setImageResource(R.drawable.few_clouds_n)
        }
        if (icon == "03d" || icon == "03n"){
            binding.tempIcon.setImageResource(R.drawable.scater_clouds)
        }
        if (icon == "04d" || icon == "04n"){
            binding.tempIcon.setImageResource(R.drawable.broken_clouds)
        }
        if (icon == "09d"){
            binding.tempIcon.setImageResource(R.drawable.rain_d)
        }
        if (icon == "09n"){
            binding.tempIcon.setImageResource(R.drawable.rain_n)
        }
        if (icon == "10d"){
            binding.tempIcon.setImageResource(R.drawable.rain_d)
        }
        if (icon == "10n"){
            binding.tempIcon.setImageResource(R.drawable.rain_n)
        }
        if (icon == "11d"){
            binding.tempIcon.setImageResource(R.drawable.thunder_d)
        }
        if (icon == "11n"){
            binding.tempIcon.setImageResource(R.drawable.thunder_n)
        }
        if (icon == "13d" || icon == "13n"){
            binding.tempIcon.setImageResource(R.drawable.snow)
        }
        if (icon == "50d"){
            binding.tempIcon.setImageResource(R.drawable.mist_d)
        }
        if (icon == "50n"){
            binding.tempIcon.setImageResource(R.drawable.mist_n)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}
package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            for (isGranted in permissions.values) {
                if (!isGranted){
                    allGranted = false
                    break
                }
            }

            if (allGranted) {
                Toast.makeText(requireContext(), "All Permissions Are Granted\nThank You.", Toast.LENGTH_SHORT).show()
            } else {
                requestANPermissions()
                Toast.makeText(requireContext(), "Please Allow All Permission to use this app.", Toast.LENGTH_SHORT).show()
            }
        }

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

        requestANPermissions()
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

    private fun requestANPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions: ArrayList<String> = arrayListOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
            val permissionsToRequest: ArrayList<String> = ArrayList()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (permissionsToRequest.isEmpty()) {
                // all permissions are already granted
            } else {
                val permissionsArray: Array<String> = permissionsToRequest.toTypedArray()
                var shouldShowRational = false
                for (permission in permissionsArray) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        shouldShowRational = true
                        break
                    }
                }
                if (shouldShowRational) {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setMessage("Please allow all permission, other wise this app will not work!")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            requestPermissionLauncher.launch(permissionsArray)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    dialog.create()
                    dialog.show()
                } else {
                    requestPermissionLauncher.launch(permissionsArray)
                }
            }
        }
    }

}
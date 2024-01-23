package com.tasnim.chowdhury.jmiweatherapp.presentation.pages.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tasnim.chowdhury.jmiweatherapp.R
import com.tasnim.chowdhury.jmiweatherapp.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment() {

    private lateinit var binding: FragmentWeatherDetailsBinding

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

        initView()
    }

    private fun initView() {

    }

}
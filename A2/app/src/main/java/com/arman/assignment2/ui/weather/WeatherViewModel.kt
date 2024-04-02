package com.arman.assignment2.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arman.assignment2.api.WeatherApi
import com.arman.assignment2.models.WeatherApiResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private val BASE_URL = "https://archive-api.open-meteo.com";
    private val _weatherData = MutableLiveData<WeatherApiResponse?>(null)
    val weatherData: LiveData<WeatherApiResponse?> = _weatherData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchHistoricalWeather(latitude: Float, longitude: Float, startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true;

            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherApi = retrofit.create(WeatherApi::class.java)
                val response = weatherApi.getHistoricalWeatherData(latitude, longitude, "temperature_2m", startDate, endDate).execute()

                if (response.isSuccessful) {
                    val weatherData = response.body()
                    // Handle weather data
                    println(
                        "Latitude: ${weatherData?.latitude}, Longitude: ${weatherData?.longitude}, Generation Time: ${weatherData?.generationtimeInMs}"
                    )
                } else {
                    // Handle API errors
                    println("Error in API response")
                }

            } catch (exception: Exception) {
                println("Error in network request");
                _errorMessage.value = exception.message;
            } finally {
                _isLoading.value = false;
            }
        }
    }
}
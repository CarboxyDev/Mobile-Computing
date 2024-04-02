package com.arman.assignment2.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arman.assignment2.api.WeatherApi
import com.arman.assignment2.models.WeatherApiResponse
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
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

    val loggingInterceptor = HttpLoggingInterceptor().setLevel(Level.BODY)


    fun fetchHistoricalWeather(latitude: Float, longitude: Float, startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true;
            println("Fetching weather data...")
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherApi = retrofit.create(WeatherApi::class.java)
                val response = weatherApi.getHistoricalWeatherData(latitude, longitude, "temperature_2m", startDate, endDate).execute()

                if (response.isSuccessful) {
                    val weatherData = response.body()
                    // Handle weather data
                    println("Fetched weather data: ");
                    println(
                        "Temps: ${weatherData?.hourly?.temps}"
                    )
                    _weatherData.value = weatherData;
                } else {
                    // Handle API errors
                    println("Error in API response")
                    println(
                        "Error message: ${response.errorBody()?.string()}"
                    )
                    _errorMessage.value = "API error"
                }

            } catch (exception: Exception) {
                println("Error in network request");
                println(
                    "Error message: ${exception.message}"
                )
                if (exception.message == null) {
                    _errorMessage.value = "Network error"
                } else {
                    _errorMessage.value = exception.message
                }
            } finally {
                _isLoading.value = false;
            }
        }
    }
}
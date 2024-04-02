package com.arman.assignment2.ui.weather

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arman.assignment2.api.WeatherApi
import com.arman.assignment2.models.WeatherApiError
import com.arman.assignment2.models.WeatherApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private val BASE_URL = "https://archive-api.open-meteo.com";
    private val _weatherData = MutableStateFlow<WeatherApiResponse?>(null);
    val weatherData: StateFlow<WeatherApiResponse?> = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


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
                    _weatherData.value = response.body()
                } else {
                    // Handle API errors
                    println("Error in API response")
                    println(
                        "Error message: ${response.errorBody()?.string()}"
                    )
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val error = Gson().fromJson(errorBody, WeatherApiError::class.java)
                        _errorMessage.value = error.reason
                    } else {
                        _errorMessage.value = "API error"
                    }
                    throw Exception("API error")
                }

            } catch (exception: Exception) {
                if (exception.message == "API error") {
                    // Do nothing
                } else {
                    // Handle network errors
                    println("Error in network request");
                    println(
                        "Error message: ${exception.message}"
                    )
                    if (exception.message == null) {
                        _errorMessage.value = "Network error"
                    } else {
                        _errorMessage.value = exception.message
                    }
                }

            } finally {
                _isLoading.value = false;
            }
        }
    }
}
package com.arman.assignment2.ui.weather

import android.app.Application
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.arman.assignment2.api.WeatherApi
import com.arman.assignment2.data.db.AppDatabase
import com.arman.assignment2.data.db.WeatherData
import com.arman.assignment2.models.WeatherApiError
import com.arman.assignment2.models.WeatherApiResponse
import com.arman.assignment2.ui.theme.Colors
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    private val BASE_URL = "https://archive-api.open-meteo.com";
    private val _weatherData = MutableStateFlow<WeatherApiResponse?>(null);
    val weatherData: StateFlow<WeatherApiResponse?> = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    val loggingInterceptor = HttpLoggingInterceptor().setLevel(Level.BODY)

    private val database = Room.databaseBuilder(
        context = getApplication<Application>(),
        AppDatabase::class.java,
        "weather_db"
    ).build()

    private val weatherDataDao = database.weatherDataDao()



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
                val response = weatherApi.getHistoricalWeatherData(latitude, longitude, "temperature_2m_max,temperature_2m_min", startDate, endDate).execute()

                if (response.isSuccessful) {
                    val weatherData = response.body();
                    val maxAndMinTemps = getMaxAndMinTemps(
                        weatherData?.daily?.maxTemps,
                        weatherData?.daily?.minTemps
                    )


                    /** Insert into local database for caching */
                    val data = WeatherData(
                        date = startDate,
                        minTemp = maxAndMinTemps.second,
                        maxTemp = maxAndMinTemps.first,
                        longitude = longitude,
                        latitude = latitude
                    )

                    weatherDataDao.insertWeatherData(weatherData = data)

                    println("Fetched weather data: ");
                    println(weatherData)
                    _weatherData.value = response.body();
                    _errorMessage.value = null;
                } else {
                    // Handle API errors
                    println("Error in API response")
                    val errorBody = response.errorBody()?.string();
                    println("Error body -> $errorBody")
                    if (errorBody != null) {
                        val error = Gson().fromJson(errorBody, WeatherApiError::class.java)
                        val errorReason = error.reason

                        if (errorReason.contains("end_date")) {
                            _errorMessage.value = "No weather data available for this date"
                        }
                        else if (
                            errorReason.contains("start_date")
                        ) {
                            _errorMessage.value = "No weather data available for this date"
                        }
                        else {
                            _errorMessage.value = error.reason
                        }

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


fun getMaxAndMinTemps(
    maxTemps: List<Float>?,
    minTemps: List<Float>?
): Pair<Float?, Float?> {
    if (maxTemps == null || minTemps == null) {
        return Pair(null, null);
    }
    else if (maxTemps.isEmpty() || minTemps.isEmpty()) {
        return Pair(null, null);
    }
    else {
        val maxTemp: Float? = maxTemps[0]
        val minTemp: Float? = minTemps[0]
        if (maxTemp == null || minTemp == null) {
            return Pair(null, null);

        } else {
            return Pair(maxTemp, minTemp);
        }
    }

}
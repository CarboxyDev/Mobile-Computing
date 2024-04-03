package com.arman.assignment2.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.arman.assignment2.api.WeatherApi
import com.arman.assignment2.data.db.AppDatabase
import com.arman.assignment2.data.db.WeatherData
import com.arman.assignment2.models.Daily
import com.arman.assignment2.models.DailyUnits
import com.arman.assignment2.models.WeatherApiError
import com.arman.assignment2.models.WeatherApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

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
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.value = true;
            println("Fetching weather data...")
            try {
                /* Try fetching from cache first */
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherApi = retrofit.create(WeatherApi::class.java)
                val response = weatherApi.getHistoricalWeatherData(
                    latitude,
                    longitude,
                    "temperature_2m_max,temperature_2m_min",
                    startDate,
                    endDate
                ).execute()

                if (response.isSuccessful) {
                    val weatherData = response.body();
                    val maxAndMinTemps = getMaxAndMinTemps(
                        weatherData?.daily?.maxTemps,
                        weatherData?.daily?.minTemps
                    )
                    /** Check if already cached, don't insert if already there */

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
                        } else if (
                            errorReason.contains("start_date")
                        ) {
                            _errorMessage.value = "No weather data available for this date"
                        } else {
                            _errorMessage.value = error.reason
                        }

                    } else {
                        _errorMessage.value = "API error"
                    }
                    throw Exception("API error")
                }

            } catch (exception: UnknownHostException) {
                println("No internet connection");
                /* Attempt to get weather data from cache */
                println("Scan cache for date = `$startDate`")
                val weatherData = weatherDataDao.getWeatherByDate(startDate)
                println("WeatherData in cache -> $weatherData");
                if (weatherData == null) {
                    _errorMessage.value = "No internet connection or cached weather data available"
                } else {
                    println("Cache entry found. Aggregating data.")
                    val temps = weatherData.let {
                        Daily(
                            maxTemps = listOf(it.maxTemp),
                            minTemps = listOf(it.minTemp),
                            time = listOf(startDate)
                        )
                    }
                    val cachedData = WeatherApiResponse(
                        latitude = weatherData.latitude,
                        longitude = weatherData.longitude,
                        dailyUnits = DailyUnits(
                            time = "seconds",
                            maxTemps = "celsius",
                            minTemps = "celsius"
                        ),
                        daily = temps,
                        utcOffsetInSeconds = 0,
                        generationtimeInMs = 0.0,
                        timezoneAbbr = "GMT",
                        timezone = "GMT",
                        elevation = 0.0f,
                    )
                    _weatherData.value = cachedData;
                    _errorMessage.value = null;
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
    maxTemps: List<Float?>?,
    minTemps: List<Float?>?
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
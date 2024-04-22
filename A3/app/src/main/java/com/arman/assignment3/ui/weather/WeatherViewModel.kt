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
import kotlin.system.exitProcess

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
                    val max = maxAndMinTemps.first;
                    val min = maxAndMinTemps.second;

                    var completed = false;

                    if (max == null || min == null) {
                        /* Date weather forecast not available as it is in the future */
                        val aggregatedData = fetchAccumulatedWeatherData(latitude, longitude, startDate, endDate);
                        _weatherData.value = aggregatedData;
                        completed = true;
                    }

                    /** Check if already cached, don't insert if already there */
                    val weatherDataCached = weatherDataDao.getWeatherByDate(startDate)
                    if (weatherDataCached == null) {
                        /** Else Insert into local database for caching */
                        val data = WeatherData(
                            date = startDate,
                            maxTemp = max,
                            minTemp = min,
                            longitude = longitude,
                            latitude = latitude
                        )

                        weatherDataDao.insertWeatherData(weatherData = data)
                    }

                    println("Fetched weather data: ");
                    println(weatherData)
                    if (!completed) {
                        _weatherData.value = response.body();
                        completed = true;
                    }
                    _errorMessage.value = null;
                } else {
                    // Handle API errors
                    println("Error in API response")
                    val errorBody = response.errorBody()?.string();
                    println("Error body -> $errorBody")
                    if (errorBody != null) {
                        val error = Gson().fromJson(errorBody, WeatherApiError::class.java)
                        val errorReason = error.reason

                        if (errorReason.contains("end_date") || errorReason.contains("start_date")) {
                            // Prediction time
                            /* Date weather forecast not available as it is in the future */
                            val aggregatedData = fetchAccumulatedWeatherData(latitude, longitude, startDate, endDate);
                            _weatherData.value = aggregatedData;
                            _errorMessage.value = null;

                            val maxAndMinTemps = getMaxAndMinTemps(
                                aggregatedData.daily.maxTemps,
                                aggregatedData.daily.minTemps
                            )
                            val max = maxAndMinTemps.first;
                            val min = maxAndMinTemps.second;

                            /** Check if already cached, don't insert if already there */
                            val weatherDataCached = weatherDataDao.getWeatherByDate(startDate)
                            if (weatherDataCached == null) {
                                /** Else Insert into local database for caching */
                                val data = WeatherData(
                                    date = startDate,
                                    maxTemp = max,
                                    minTemp = min,
                                    longitude = longitude,
                                    latitude = latitude
                                )

                                weatherDataDao.insertWeatherData(weatherData = data)
                            }

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


private suspend fun fetchAccumulatedWeatherData(
    latitude: Float,
    longitude: Float,
    startDate: String,
    endDate: String,
): WeatherApiResponse {
    val data = mutableListOf<WeatherApiResponse>();
    val retrofit = Retrofit.Builder()
        .baseUrl("https://archive-api.open-meteo.com")
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY)).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val currentYear = startDate.split("-")[0].toInt();
    for (year in currentYear - 1 downTo currentYear - 10) {
        println("Fetching data for year $year")
        val weatherApi = retrofit.create(WeatherApi::class.java)
        val response = weatherApi.getHistoricalWeatherData(
            latitude,
            longitude,
            "temperature_2m_max,temperature_2m_min",
            "$year-${startDate.split("-")[1]}-${startDate.split("-")[2]}",
            "$year-${endDate.split("-")[1]}-${endDate.split("-")[2]}"
        ).execute()
        if (response.isSuccessful) {
            data.add(response.body()!!)
        }
    }
    println("Accumulated data -> $data");
    val temps = Daily(
        maxTemps = data.flatMap { it.daily.maxTemps },
        minTemps = data.flatMap { it.daily.minTemps },
        time = data.flatMap { it.daily.time }
    )
    println("average temps -> $temps")
    val averageTemps = Daily(
        maxTemps = calculateAverageOfFloatList(temps.maxTemps)?.let { listOf(it) } ?: listOf(
            null
        ),
        minTemps = calculateAverageOfFloatList(temps.minTemps)?.let { listOf(it) } ?: listOf(
            null
        ),
        time = listOf(startDate)
    )
    println("average temps for last 10 years aggregated-> $averageTemps")
    val aggregatedData = WeatherApiResponse(
        latitude = latitude,
        longitude = longitude,
        dailyUnits = DailyUnits(
            time = "seconds",
            maxTemps = "celsius",
            minTemps = "celsius"
        ),
        daily = averageTemps,
        utcOffsetInSeconds = 0,
        generationtimeInMs = 0.0,
        timezoneAbbr = "GMT",
        timezone = "GMT",
        elevation = 0.0f,
    )
    return aggregatedData;

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

fun calculateAverageOfFloatList(data: List<Float?>): Float? {
    val validValues = data.filterNotNull() // Filter out null values
    return if (validValues.isNotEmpty()) {
        validValues.sum() / validValues.size.toFloat()
    } else {
        null // Return null if there are no valid values
    }
}
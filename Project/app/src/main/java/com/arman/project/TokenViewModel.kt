package com.arman.project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.arman.project.api.TokenApi
import com.arman.project.models.TokenResponse
import com.arman.project.models.TokenResponseError
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

class TokenViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    private val BASE_URL = "https://mc-backend-eight.vercel.app";
    private val _data = MutableStateFlow<TokenResponse?>(null);
    val data: StateFlow<TokenResponse?> = _data.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    val loggingInterceptor = HttpLoggingInterceptor().setLevel(Level.BODY)


    fun fetchToken(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.value = true;
            println("Fetching token...")
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(TokenApi::class.java)
                val response = api.fetchToken(username, password).execute()

                if (response.isSuccessful) {
                    val responseData = response.body();
                    val token = responseData?.token;
                    println(token);
                    _data.value = responseData;
                    _errorMessage.value = null;
                } else {
                    // Handle API errors
                    println("Error in API response")
                    val errorBody = response.errorBody()?.string();
                    println("Error body -> $errorBody")
                    if (errorBody != null) {
                        val error = Gson().fromJson(errorBody, TokenResponseError::class.java)
                        val errorReason = error.error;

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


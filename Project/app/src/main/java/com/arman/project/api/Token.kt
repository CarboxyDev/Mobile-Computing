package com.arman.project.api

import com.arman.project.models.TokenResponse
import com.arman.project.models.TokenResponseError
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface TokenApi {
    @GET("/token")
    fun fetchToken(
        @Query("username") username: String,
        @Query("password") password: String,
    ): Call<TokenResponse>
}
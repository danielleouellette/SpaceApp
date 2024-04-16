package com.nscc.spaceapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// creating service interface to fetch data using method NasaApodService
interface NasaApodService {
    @GET("planetary/apod")
    fun fetchApodImage(@Query("api_key") apiKey: String): Call<ApodResponse>

    data class ApodResponse(val date: String, val explanation: String, val title: String, val url: String)
}

// build instance of NasaApodService method including baseURL to be appended by the @GET, a
// converter to de-serialize JSON data. then builds and creates the implementation.
fun createApiService(): NasaApodService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(NasaApodService::class.java)
}
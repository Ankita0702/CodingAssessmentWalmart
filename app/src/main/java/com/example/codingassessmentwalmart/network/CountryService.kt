package com.example.codingassessmentwalmart.network

import com.example.codingassessmentwalmart.data.Country
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CountryService {

    @GET("db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
    fun getCountries(): Single<List<Country>>

    companion object {
        private const val BASE_URL =
            "https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/"

        private val retrofit: Retrofit by lazy {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build()
        }

        val instance: CountryService by lazy {
            retrofit.create(CountryService::class.java)
        }
    }
}

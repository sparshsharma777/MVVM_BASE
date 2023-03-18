package com.ss.instagramdownloader.networking

import com.ss.instagramdownloader.models.TopHeadlinesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface AppApi {

    @GET
    suspend fun checkImageUrl(@Url url: String): Response<ResponseBody>

    @Headers("X-Api-Key: 9f6482a584804376874b848980b7a044")
    @GET("top-headlines")
    suspend fun getTopHeadlinesWithResultWrapper(@Query("country") country: String): TopHeadlinesResponse
}
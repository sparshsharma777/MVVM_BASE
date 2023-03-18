package com.ss.instagramdownloader.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class Article(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("urlToImage")
    val imageUrl: String = "",

)
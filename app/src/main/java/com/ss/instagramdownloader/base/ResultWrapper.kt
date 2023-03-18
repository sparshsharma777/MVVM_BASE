package com.ss.instagramdownloader.base

import androidx.annotation.Keep
import com.ss.instagramdownloader.models.ErrorOrFailResponse


@Keep
sealed class ResultWrapper<out T> {
    val code: Int? = null
    data class Success<out T>(val value: T,val apiCode: Int? = null) : ResultWrapper<T>()
    data class GenericError(val apiCode: Int? = null, val error: ErrorOrFailResponse? = null) : ResultWrapper<Nothing>()
    data class Loading(val apiCode: Int? = null, val error: ErrorOrFailResponse? = null) : ResultWrapper<Nothing>()  //to represent the loading state if needed
    data class NetworkError(val apiCode: Int? = null, val error:ErrorOrFailResponse? = null) : ResultWrapper<Nothing>()  //to represent the error state if needed

}
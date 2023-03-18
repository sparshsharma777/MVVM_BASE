package com.ss.instagramdownloader.base

import android.util.Log
import com.ss.instagramdownloader.models.ErrorOrFailResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiRequestCode: Int, dispatcher: CoroutineDispatcher, apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                val response = apiCall.invoke()

                response as Response<*>
                if (response.isSuccessful) {
                    Log.d("response", "success")
                    Log.d("response", response.code().toString())
                    ResultWrapper.Success(response,apiRequestCode)
                } else {
                    Log.d("error-code-raw", response.raw().code.toString())
                    Log.d("error-code-response", response.code().toString())
                    //  val errorResponse = Gson().fromJson(response.errorBody().toString(), BaseApiResponse::class.java)
                    val errorResponse = getDefaultErrorObject()

                    errorResponse.apiRequestCode = apiRequestCode
                    errorResponse.code = response.raw().code  // code to handle 404, 403
                    //  val errorResponse= ErrorUtils.parseError(response)
                    ResultWrapper.GenericError(apiRequestCode, errorResponse)

                }


            } catch (throwable: Throwable) {
                when (throwable) {
                    is SocketTimeoutException, is IOException ->

                        ResultWrapper.NetworkError(
                            apiRequestCode, getDefaultErrorObject(
                                throwable.localizedMessage ?: throwable.message ?: ""
                            )
                        )
                    is HttpException -> {
                        val code = throwable.code()
                        val errorObject = getDefaultErrorObject()
                        errorObject.code = code
                        ResultWrapper.GenericError(apiRequestCode, errorObject)
                    }
                    else -> {
                        ResultWrapper.GenericError(apiRequestCode, getDefaultErrorObject())
                        //something went wrong state
                    }
                }
            }

        }
    }


    private fun getDefaultErrorObject(defaultMessage: String? = null): ErrorOrFailResponse {
        return ErrorOrFailResponse()
    }





}
package com.ss.instagramdownloader.ui.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ss.instagramdownloader.base.BaseRepository
import com.ss.instagramdownloader.base.ResultWrapper
import com.ss.instagramdownloader.db.AppDB
import com.ss.instagramdownloader.models.Article
import com.ss.instagramdownloader.models.TopHeadlinesResponse
import com.ss.instagramdownloader.networking.AppApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import javax.inject.Inject



class DemoRepo @Inject constructor(private val apiService: AppApi, private val appDb: AppDB) :
    BaseRepository(){






    suspend fun checkWithBase(country: String,apiRequestCode:Int,dispatcher: CoroutineDispatcher): Flow<ResultWrapper<TopHeadlinesResponse>> {
        return flow<ResultWrapper<TopHeadlinesResponse>> {
            val result=safeApiCall(apiRequestCode, dispatcher) { apiService.getTopHeadlinesWithResultWrapper(country)}
            emit(result)
        }.catch { e->
            Log.d("ex",e.toString())

        }.flowOn(Dispatchers.IO)

    }

    suspend fun demoLiveData(country: String,apiRequestCode:Int,dispatcher: CoroutineDispatcher): ResultWrapper<TopHeadlinesResponse> {
        return safeApiCall(apiRequestCode, dispatcher) {
            apiService.getTopHeadlinesWithResultWrapper(country)
        }
    }





    suspend fun getTopHeadlines(country: String): Flow<List<Article>> {
        return flow {
            emit(apiService.getTopHeadlinesWithResultWrapper(country))
        }.map {
            it.articles
        }
    }


    suspend fun checkImageUrl(dispatcher: CoroutineDispatcher, imageUrl:String): ResultWrapper<retrofit2.Response<ResponseBody>> {
        return safeApiCall(111, dispatcher) {
            apiService.checkImageUrl(imageUrl)
        }
    }
}


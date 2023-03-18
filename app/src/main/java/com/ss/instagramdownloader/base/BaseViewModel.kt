package com.ss.instagramdownloader.base

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.ss.instagramdownloader.models.ErrorOrFailResponse
import com.ss.instagramdownloader.utils.SingleLiveEvent
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean



abstract class BaseViewModel(private val myApplication: Application) :
    AndroidViewModel(myApplication),
    DefaultLifecycleObserver {

    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val messageData = SingleLiveEvent<ErrorOrFailResponse>()
    protected val loading = MutableLiveData<Boolean>()
    private val sessionExpired = MutableLiveData<Boolean>()
    protected var isImagePoolingEnabled: AtomicBoolean = AtomicBoolean(true)

    protected val context
        get() = getApplication<Application>()


    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun getSessionExpiryData(): LiveData<Boolean> {
        return sessionExpired
    }


    fun logOut() {

    }

    fun getMessageData(): SingleLiveEvent<ErrorOrFailResponse> {
        return messageData
    }


//    protected fun handleSuccessErrorResponse(
//        response: ResultWrapper<*>,
//        msg: String = "",
//        code: Int = 0
//    ) {
//        when (response) {
//
//            is ResultWrapper.NetworkError -> {
//                getMessageData().value = response.error?.message
//
//            }
//            is ResultWrapper.GenericError -> {
//                getMessageData().value = response.error?.message
//
//            }
//            else -> {
//                getMessageData().value = "Something went wrong! "
//
//            }
//        }
//
//    }


    /*
     since we are handling the generic response/ error here so the  success/ failure must also be generic
     ex:
       private val _observer= MutableLiveData<Resource<Any>>(Resource.loading(11))
       val observer: LiveData<Resource<Any>>
        get() = _observer
     */
//    protected fun <T> handleCentralizedSuccessErrorResponse(
//        observer: MutableLiveData<Resource<T>>,
//        response: ResultWrapper<*>,
//        msg: String = "",
//        apiRequestCode: Int = 0
//    ) {
//        when (response) {
//            is ResultWrapper.Success -> {
//                observer.value = Resource.success(response.value as T, apiRequestCode)
//            }
//            is ResultWrapper.NetworkError -> {
//                getMessageData().value = response.error?.message
//            }
//            is ResultWrapper.GenericError -> {
//                getMessageData().value = response.error?.message
//            }
//            else -> {
//                getMessageData().value = msg
//
//            }
//        }
//    }

    fun beginDownload(url: String) {
        // url:String="https://images.unsplash.com/photo-1575936123452-b67c3203c357?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2070&q=80"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString(), "Gencraft/" + System.currentTimeMillis() + ".png"
        )
        if (!file.exists()) {
            file.mkdirs()
        }

        // val file: File = Util.createDocumentFile(fileName, context)
        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Visibility of the download Notification
            //public path

            // .setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),"Gencraft/"+System.currentTimeMillis()+".png")
            //local path
            .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
            .setTitle("Gencraft Image") // Title of the Download Notification
            .setDescription("Downloading File....") // Description of the Download Notification
            .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
            .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
            .setMimeType("image/*")
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val downloadID =
            downloadManager?.enqueue(request) // enqueue puts the download request in the queue.
        Log.d("download enqueue", "id=" + downloadID.toString())

        viewModelScope.launch(Dispatchers.IO) {
            if (downloadManager != null && downloadID != null)
                queryDownloadFileStatus(downloadManager, downloadID)

        }

    }


    private suspend fun showSuccessFailToast(isSuccess: Boolean) {

        withContext(Dispatchers.Main) {
            if (isSuccess)
                Toast.makeText(context, "Image Downloaded Successfully ", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, "Image Downloading  Failed", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun queryDownloadFileStatus(
        downloadManager: DownloadManager,
        downloadID: Long
    ) {
        // using query method
        var continueDownload = AtomicBoolean(true)
        while (continueDownload.get()) {
            delay(3000)
            val cursor: Cursor? =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor?.moveToFirst()!!) {
                val status: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.ERROR_FILE_ERROR,
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE,
                    DownloadManager.ERROR_UNKNOWN,
                    DownloadManager.ERROR_HTTP_DATA_ERROR,
                    DownloadManager.ERROR_INSUFFICIENT_SPACE,
                    DownloadManager.ERROR_DEVICE_NOT_FOUND,
                    DownloadManager.STATUS_FAILED -> {
                        continueDownload.set(false)
                        showSuccessFailToast(false)
                        cursor.close()
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        continueDownload.set(false)
                        showSuccessFailToast(true)
                        cursor.close()
                    }
                    else -> {
                        //can handle other cases if needed
                    }

                }
            }
            cursor.close()
        }

    }


    /*
     here we  can observe the lifecycle events of the viewmodel owner
     */
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d("LifecycleOwner", "onCreate")

    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("LifecycleOwner", "onStart")

    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Log.d("LifecycleOwner", "onResume")
        isImagePoolingEnabled.set(true)


    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.d("LifecycleOwner", "onPause")

    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("LifecycleOwner", "onStop")
        isImagePoolingEnabled.set(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Log.d("LifecycleOwner", "onDestroy")

    }

    protected fun showShortToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }


}
package com.ss.instagramdownloader.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*


object AppUtils {
    private const val DATE_PATTERN="yyyy-MM-dd'T'HH:mm:ss" //2023-03-01T12:50:21.443'
    private const val DEST_PATTERN="dd-MMMM-yyyy"

    fun getAppTimeZone():String{
        return GregorianCalendar().timeZone.id
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo
        return (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected)
    }

    fun convertStrDateIntoCalendarObj(stringDate: String, formatOfDate: String): Calendar {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(formatOfDate, Locale.ENGLISH)
        cal.time = sdf.parse(stringDate)!! // all done
        return cal
    }

    fun convertFormatOfDate(stringDate: String?): String {
        return if (stringDate != null) {
            val cal = convertStrDateIntoCalendarObj(stringDate, DATE_PATTERN) // source pattern
            getDateInFormatFromCalendarObj(cal, DEST_PATTERN)  //dest pattern
        } else
            ""
    }

    fun getDateInFormatFromCalendarObj(cal: Calendar, destinationSource: String): String {
        val format = SimpleDateFormat(destinationSource, Locale.ENGLISH)
        return format.format(cal.time)
    }



}
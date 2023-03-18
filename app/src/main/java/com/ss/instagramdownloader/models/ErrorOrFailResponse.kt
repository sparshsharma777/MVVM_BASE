package com.ss.instagramdownloader.models

import androidx.annotation.Keep


@Keep
data class ErrorOrFailResponse(
    var clientId: String? = null,
    var status: String? = null, var
    msg: String? = "something went wrong",
    var code: Int? = null,
    var apiRequestCode: Int = 0
)
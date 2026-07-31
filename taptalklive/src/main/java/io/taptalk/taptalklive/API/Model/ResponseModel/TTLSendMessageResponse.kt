package io.taptalk.taptalklive.API.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class TTLSendMessageResponse(
    @field:JsonProperty("success")
    var success: Boolean? = null,

    @field:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("reason")
    var reason: String? = null
): Parcelable

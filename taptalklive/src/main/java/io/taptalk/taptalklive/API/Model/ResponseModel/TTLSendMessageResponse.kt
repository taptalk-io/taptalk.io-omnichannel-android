package io.taptalk.taptalklive.API.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TTLSendMessageResponse(
    @JsonProperty("success")
    var success: Boolean? = null,

    @JsonProperty("message")
    var message: String? = null,

    @JsonProperty("reason")
    var reason: String? = null
): Parcelable

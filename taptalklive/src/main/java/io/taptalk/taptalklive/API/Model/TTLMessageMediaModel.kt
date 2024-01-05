package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TTLMessageMediaModel(
    @JsonProperty("fileURL")
    var fileURL: String?,
    @JsonProperty("caption")
    var caption: String?,
    @JsonProperty("filename")
    var filename: String?
) : Parcelable

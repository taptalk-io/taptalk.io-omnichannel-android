package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class TTLMessageMediaModel(
    @field:JsonProperty("fileURL")
    var fileURL: String?,
    @field:JsonProperty("caption")
    var caption: String?,
    @field:JsonProperty("filename")
    var filename: String?
) : Parcelable

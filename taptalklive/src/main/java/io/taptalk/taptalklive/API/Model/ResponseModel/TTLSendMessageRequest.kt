package io.taptalk.taptalklive.API.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.taptalklive.API.Model.TTLMessageMediaModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TTLSendMessageRequest(
    @JsonProperty("id")
    var id: Long?,
    @JsonProperty("createdTime")
    var createdTime: Long?,
    @JsonProperty("localID")
    var localID: String?,
    @JsonProperty("replyToLocalID")
    var replyToLocalID: String?,
    @JsonProperty("messageType")
    var messageType: String?,
    @JsonProperty("text")
    var text: String?,
    @JsonProperty("image")
    var image: TTLMessageMediaModel?,
    @JsonProperty("video")
    var video: TTLMessageMediaModel?,
    @JsonProperty("file")
    var file: TTLMessageMediaModel?
) : Parcelable
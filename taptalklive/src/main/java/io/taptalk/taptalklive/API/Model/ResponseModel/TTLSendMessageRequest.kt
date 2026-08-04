package io.taptalk.taptalklive.API.Model.ResponseModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import io.taptalk.taptalklive.API.Model.TTLMessageMediaModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TTLSendMessageRequest(
    @field:JsonProperty("id")
    var id: Long?,
    @field:JsonProperty("createdTime")
    var createdTime: Long?,
    @field:JsonProperty("localID")
    var localID: String?,
    @field:JsonProperty("replyToLocalID")
    var replyToLocalID: String?,
    @field:JsonProperty("messageType")
    var messageType: String?,
    @field:JsonProperty("text")
    var text: String?,
    @field:JsonProperty("image")
    var image: TTLMessageMediaModel?,
    @field:JsonProperty("video")
    var video: TTLMessageMediaModel?,
    @field:JsonProperty("file")
    var file: TTLMessageMediaModel?
) : Parcelable
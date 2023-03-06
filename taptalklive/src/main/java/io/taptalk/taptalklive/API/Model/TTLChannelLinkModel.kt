package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TTLChannelLinkModel(
    @field:JsonProperty("id") var id: Long = 0L,
    @field:JsonProperty("sequence") var sequence: Int = 0,
    @field:JsonProperty("channel") var channel: String = "",
    @field:JsonProperty("url") var url: String = "",
    @field:JsonProperty("sendPageURL") var sendPageURL: Boolean = false,
    @field:JsonProperty("title") var title: String = "",
    @field:JsonProperty("targetID") var targetID: String = "",
    @field:JsonProperty("message") var message: String = "",
    @field:JsonProperty("isEnabled") var isEnabled: Boolean = false,
    @field:JsonProperty("createdTime") var createdTime: Long = 0L,
    @field:JsonProperty("updatedTime") var updatedTime: Long = 0L,
    @field:JsonProperty("deletedTime") var deletedTime: Long = 0L,
) : Parcelable

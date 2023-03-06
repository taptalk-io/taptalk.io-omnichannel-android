package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TTLScfPathModel(
    @field:JsonProperty("itemID") var itemID: Long = 0L,
    @field:JsonProperty("pathID") var pathID: Long = 0L,
    @field:JsonProperty("parentID") var parentID: Long = 0L,
    @field:JsonProperty("sequence") var sequence: Int = 0,
    @field:JsonProperty("title") var title: String = "",
    @field:JsonProperty("content") var content: String = "",
    @field:JsonProperty("type") var type: String = "",
    @field:JsonProperty("createdTime") var createdTime: Long = 0L,
    @field:JsonProperty("updatedTime") var updatedTime: Long = 0L,
    @field:JsonProperty("deletedTime") var deletedTime: Long = 0L,
    @field:JsonProperty("topics") var topics: List<TTLTopicModel> = ArrayList(),
    @field:JsonProperty("childItems") var childItems: List<TTLScfPathModel> = ArrayList(),
) : Parcelable

package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TTLLauncherConfigsModel(
    @field:JsonProperty("hexColor") var hexColor: String = "",
    @field:JsonProperty("maxOpenCases") var maxOpenCases: Int = 0,
    @field:JsonProperty("randomizeFilename") var randomizeFilename: Boolean = false,
) : Parcelable

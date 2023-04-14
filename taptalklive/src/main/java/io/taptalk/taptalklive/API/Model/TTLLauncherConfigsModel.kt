package io.taptalk.taptalklive.API.Model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize data class TTLLauncherConfigsModel(
    @field:JsonProperty("hexColor") var hexColor: String = ""
) : Parcelable

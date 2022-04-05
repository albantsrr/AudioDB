package com.supdeweb.audiodb.network.service

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.supdeweb.audiodb.model.TrendingDto

data class TrendingResponse(
    @SerializedName("trending")
    @Expose
    val trending: List<TrendingDto>? = null,
)
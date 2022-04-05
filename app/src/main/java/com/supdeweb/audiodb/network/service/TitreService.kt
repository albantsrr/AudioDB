package com.supdeweb.audiodb.network.service

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.supdeweb.audiodb.model.TitreDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TitreService {

    @GET(TRENDING_TRITRES)
    fun getTrendingTracks(): Call<TrendingResponse>

    /**
     *
     */
    @GET(TITRES_ALBUM)
    fun getTracksByAlbum(
        @Query("m") albumId: String,
    ): Call<TrackListResponse>

    /**
     *
     */
    @GET(TOP_TITRES_ARTISTE)
    fun getTopTracksByArtistName(
        @Query("s") artistName: String,
    ): Call<TrackListResponse>


    companion object {
        //TRACK
        private const val TITRES_ALBUM = "track.php"
        private const val TRENDING_TRITRES = "trending.php?country=us&type=itunes&format=singles"
        private const val TOP_TITRES_ARTISTE = "track-top10.php"
    }
}


data class TrackListResponse(
    @SerializedName("track")
    @Expose
    val titres: List<TitreDto>? = null,
)


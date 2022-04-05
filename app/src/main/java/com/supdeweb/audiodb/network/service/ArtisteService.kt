package com.supdeweb.audiodb.network.service

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.supdeweb.audiodb.model.ArtistDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtisteService {
    /**
     *
     */
    @GET(ARTIST_DETAIL)
    fun getArtistDetail(
        @Query("i") artistId: String,
    ): Call<ArtistDetailResponse>

    /**
     *
     */
    @GET(SEARCH_ARTIST)
    fun getArtistsByName(
        @Query("s") artistName: String,
    ): Call<ArtistDetailResponse>

    companion object {
        //ALBUM
        private const val ARTIST_DETAIL = "artist.php"
        private const val SEARCH_ARTIST = "search.php"
    }
}

data class ArtistDetailResponse(
    @SerializedName("artists")
    @Expose
    val artists: List<ArtistDto>? = null,
)
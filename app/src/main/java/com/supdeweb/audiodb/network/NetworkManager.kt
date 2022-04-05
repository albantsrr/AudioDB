package com.supdeweb.audiodb.network

import com.supdeweb.audiodb.network.service.AlbumService
import com.supdeweb.audiodb.network.service.ArtisteService
import com.supdeweb.audiodb.network.service.TitreService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private const val API_KEY = "523532"
    private const val BASE_URL = "https://theaudiodb.com/api/v1/json/${API_KEY}/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()


    val albumService: AlbumService get() = retrofit.create(AlbumService::class.java)

    val titreService: TitreService get() = retrofit.create(TitreService::class.java)

    val artisteService: ArtisteService get() = retrofit.create(ArtisteService::class.java)
}

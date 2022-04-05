package com.supdeweb.audiodb.model

fun List<TrendingDto>.albumDtoAsModel(): List<AlbumModel> {
    return map {
        AlbumModel(
            id = it.idAlbum ?: throw IllegalAccessException("Must to have an album id"),
            albumName = it.strAlbum,
            artistId = it.idArtist,
            artistName = it.strArtist,
            style = null,
            sales = null,
            description = null,
            imageUrl = it.strAlbumThumb,
            isFavorite = false,
            chartPlace = it.intChartPlace?.toInt(),
            score = null,
            scoreVotes = null,
            year = null,
        )
    }
}

fun List<TrendingDto>.titreDtoAsModel(): List<TitreModel> {
    return map {
        TitreModel(
            id = it.idTrack ?: throw IllegalAccessException("Must to have a titres id"),
            name = it.strTrack,
            artistId = it.idArtist,
            artistName = it.strArtist,
            style = null,
            description = null,
            imageUrl = it.strTrackThumb,
            chartPlace = it.intChartPlace?.toInt(),
            albumId = it.idAlbum,
            score = null,
        )
    }
}
package com.supdeweb.audiodb.model

fun List<TitreDto>.dtoAsModel(): List<TitreModel> {
    return map {
        TitreModel(
            id = it.idTrack,
            name = it.strTrack,
            artistId = it.idArtist,
            artistName = it.strArtist,
            style = it.strStyle,
            score = it.intScore,
            description = it.strDescriptionFR,
            imageUrl = it.strTrackThumb,
            albumId = it.idAlbum,
            chartPlace = null,
        )
    }
}

fun List<TitreModel>.modelAsEntity(): List<TitreEntity> {
    return map {
        TitreEntity(
            id = it.id,
            title = it.name,
            artistId = it.artistId,
            artistName = it.artistName,
            style = it.style,
            score = it.score,
            description = it.description,
            imageUrl = it.imageUrl,
            albumId = it.albumId,
        )
    }
}

fun List<TitreEntity>.entitiesAsModel(): List<TitreModel> {
    return map {
        TitreModel(
            id = it.id,
            name = it.title,
            artistId = it.artistId,
            artistName = it.artistName,
            style = it.style,
            score = it.score,
            description = it.description,
            imageUrl = it.imageUrl,
            albumId = it.albumId,
            chartPlace = null,
        )
    }
}

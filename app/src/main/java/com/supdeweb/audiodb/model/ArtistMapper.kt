package com.supdeweb.audiodb.model


fun List<ArtistDto>.dtoAsModel(): List<ArtistModel> {
    return map {
        ArtistModel(
            id = it.idArtist,
            name = it.strArtist,
            country = it.strCountry,
            genre = it.strGenre,
            description = it.strBiographyEN,
            imageUrl = it.strArtistThumb,
            isFavorite = false
        )
    }
}

fun List<ArtistEntity>.entitiesAsModel(): List<ArtistModel> {
    return map {
        ArtistModel(
            id = it.id,
            name = it.name,
            country = it.country,
            genre = it.genre,
            description = it.description,
            imageUrl = it.imageUrl,
            isFavorite = it.isFavorite
        )
    }
}

fun ArtistEntity.asModel(): ArtistModel {
    return ArtistModel(
        id = this.id,
        name = this.name,
        country = this.country,
        genre = this.genre,
        description = this.description,
        imageUrl = this.imageUrl,
        isFavorite = this.isFavorite
    )
}

fun ArtistModel.asEntity(): ArtistEntity {
    return ArtistEntity(
        id = this.id,
        name = this.name,
        country = this.country,
        genre = this.genre,
        description = this.description,
        imageUrl = this.imageUrl,
        isFavorite = this.isFavorite
    )
}

fun ArtistDto.asModel(): ArtistModel {
    return ArtistModel(
        id = this.idArtist,
        name = this.strArtist,
        country = this.strCountry,
        genre = this.strGenre,
        description = this.strBiographyEN,
        imageUrl = this.strArtistThumb,
        isFavorite = false
    )
}
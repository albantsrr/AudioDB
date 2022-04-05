package com.supdeweb.audiodb.screen.search.adapter

import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.databinding.AdapterItemAlbumBinding
import com.supdeweb.audiodb.databinding.AdapterItemArtisteBinding
import com.supdeweb.audiodb.databinding.AdapterItemTitreBinding
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.screen.details.album.AlbumDetailScreenFragment
import com.supdeweb.audiodb.screen.details.artiste.ArtistDetaiScreenFragment.Companion.ARTISTE_ID

sealed class AllRecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class HeaderViewHolder(
        private val binding: AdapterItemTitreBinding,
    ) : AllRecyclerViewHolder(binding) {
        fun bind(title: String) {
            binding.adapterItemTitleTvTitle.text = title
        }
    }

    class AllArtistViewHolder(
        private val binding: AdapterItemArtisteBinding,
    ) : AllRecyclerViewHolder(binding) {
        fun bind(artist: ArtistModel) {
            binding.adapterItemArtisteTvLabel.text = artist.name
            val radius =
                binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_16dp)
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_launcher_foreground)
            Glide.with(binding.adapterItemArtisteIv)
                .setDefaultRequestOptions(requestOptions)
                .load(artist.imageUrl)
                .transform(RoundedCorners(radius), CenterCrop())
                .into(binding.adapterItemArtisteIv)
            val bundle = bundleOf(ARTISTE_ID to artist.id)
            itemView.setOnClickListener {
                itemView.findNavController().navigate(R.id.artistDetaiScreenFragment, bundle)
            }
        }
    }

    class AllAlbumViewHolder(
        private val binding: AdapterItemAlbumBinding,
    ) : AllRecyclerViewHolder(binding) {
        fun bind(album: AlbumModel) {
            binding.adapterItemAlbumTvLabel.text = album.albumName
            binding.adapterItemAlbumTvFirstSubtitle.text = album.artistName
            val radius =
                binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_16dp)
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_launcher_foreground)
            Glide.with(binding.adapterItemAlbumIv)
                .setDefaultRequestOptions(requestOptions)
                .load(album.imageUrl)
                .transform(RoundedCorners(radius), CenterCrop())
                .into(binding.adapterItemAlbumIv)
            val bundle = bundleOf(AlbumDetailScreenFragment.ALBUM_ID to album.id)
            itemView.setOnClickListener {
                itemView.findNavController().navigate(R.id.albumDetailScreenFragment, bundle)
            }
        }
    }
}

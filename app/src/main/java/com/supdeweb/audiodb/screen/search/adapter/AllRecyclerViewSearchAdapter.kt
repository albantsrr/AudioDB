package com.supdeweb.audiodb.screen.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.databinding.AdapterItemAlbumBinding
import com.supdeweb.audiodb.databinding.AdapterItemArtisteBinding
import com.supdeweb.audiodb.databinding.AdapterItemTitreBinding
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.ArtistModel

class AllRecyclerViewSearchAdapter :
    RecyclerView.Adapter<AllRecyclerViewHolder>() {

    var items = listOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AllRecyclerViewHolder {
        return when (viewType) {
            R.layout.adapter_item_titre -> AllRecyclerViewHolder.HeaderViewHolder(
                AdapterItemTitreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.adapter_item_album -> AllRecyclerViewHolder.AllAlbumViewHolder(
                AdapterItemAlbumBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.adapter_item_artiste -> AllRecyclerViewHolder.AllArtistViewHolder(
                AdapterItemArtisteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalAccessException("Invalid ViewHolder")
        }
    }

    override fun onBindViewHolder(holder: AllRecyclerViewHolder, position: Int) {
        return when (holder) {
            is AllRecyclerViewHolder.HeaderViewHolder -> holder.bind(items[position] as String)
            is AllRecyclerViewHolder.AllArtistViewHolder -> holder.bind(items[position] as ArtistModel)
            is AllRecyclerViewHolder.AllAlbumViewHolder -> holder.bind(items[position] as AlbumModel)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> R.layout.adapter_item_titre
            is AlbumModel -> R.layout.adapter_item_album
            is ArtistModel -> R.layout.adapter_item_artiste
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }
}
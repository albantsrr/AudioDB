package com.supdeweb.audiodb.screen.classement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.databinding.AdapterClassementItemBinding
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.screen.details.album.AlbumDetailScreenFragment.Companion.ALBUM_ID

class AlbumAdapter : ListAdapter<AlbumModel, AlbumAdapter.AlbumViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {

        val binding: AdapterClassementItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_classement_item, parent,
            false
        )

        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        getItem(position)?.let { album ->
            val radius =
                holder.binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_16dp)
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_launcher_foreground)
            Glide.with(holder.binding.adapterClassementIvDisplayImage)
                .setDefaultRequestOptions(requestOptions)
                .load(album.imageUrl)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(holder.binding.adapterClassementIvDisplayImage)

            holder.binding.adapterClassementTvFirstText.text = album.albumName
            holder.binding.adapterClassementTvSecondText.text = album.artistName
            holder.binding.adapterClassementTvNumber.text = ((position.plus(1)).toString())

            // CLICK ACTION
            holder.itemView.setOnClickListener {
                val bundle = bundleOf(ALBUM_ID to album.id)
                holder.itemView.findNavController().navigate(R.id.albumDetailScreenFragment, bundle)
            }
        }
    }

    class AlbumViewHolder(var binding: AdapterClassementItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    companion object {
        private val diffCallback = object :
            DiffUtil.ItemCallback<AlbumModel>() {

            override fun areItemsTheSame(
                oldItem: AlbumModel,
                newItem: AlbumModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: AlbumModel,
                newItem: AlbumModel,
            ): Boolean {
                return oldItem.albumName == newItem.albumName
                        && oldItem.isFavorite == newItem.isFavorite
                        && oldItem.imageUrl == newItem.imageUrl
                        && oldItem.sales == newItem.sales
                        && oldItem.style == newItem.style
            }
        }
    }
}

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
import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.screen.details.artiste.ArtistDetaiScreenFragment.Companion.ARTISTE_ID

class TitreScreenAdapter : ListAdapter<TitreModel, TitreScreenAdapter.TrackViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        val binding: AdapterClassementItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_classement_item, parent,
            false
        )

        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        getItem(position)?.let { track ->
            val radius =
                holder.binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_16dp)
            val requestOptions = RequestOptions()
                .error(R.drawable.ic_launcher_foreground)
            Glide.with(holder.binding.adapterClassementIvDisplayImage)
                .setDefaultRequestOptions(requestOptions)
                .load(track.imageUrl)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(holder.binding.adapterClassementIvDisplayImage)

            holder.binding.adapterClassementTvFirstText.text = track.name
            holder.binding.adapterClassementTvSecondText.text = track.artistName
            holder.binding.adapterClassementTvNumber.text = ((position.plus(1)).toString())
            holder.itemView.setOnClickListener {
                val bundle = bundleOf(ARTISTE_ID to track.artistId)
                holder.itemView.findNavController().navigate(R.id.artistDetaiScreenFragment, bundle)
            }
        }
    }

    class TrackViewHolder(var binding: AdapterClassementItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    companion object {
        private val diffCallback = object :
            DiffUtil.ItemCallback<TitreModel>() {

            override fun areItemsTheSame(
                oldItem: TitreModel,
                newItem: TitreModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TitreModel,
                newItem: TitreModel,
            ): Boolean {
                return oldItem.name == newItem.name
                        && oldItem.imageUrl == newItem.imageUrl
                        && oldItem.score == newItem.score
                        && oldItem.style == newItem.style
            }
        }
    }
}

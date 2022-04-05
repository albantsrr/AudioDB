package com.supdeweb.audiodb.screen.details.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.databinding.AdapterTitreDetailItemBinding
import com.supdeweb.audiodb.model.TitreModel

class TitreScreenDetailAdapter : ListAdapter<TitreModel, TitreScreenDetailAdapter.TitreViewHolder>(
    diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitreViewHolder {

        val binding: AdapterTitreDetailItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_titre_detail_item, parent,
            false
        )

        return TitreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TitreViewHolder, position: Int) {
        getItem(position)?.let { titre ->
            holder.binding.adapterTitreDetailTvTitre.text = titre.name
            holder.binding.adapterTitreDetailTvNumber.text = ((position.plus(1)).toString())
        }
    }

    override fun getItemCount() = currentList.size

    class TitreViewHolder(var binding: AdapterTitreDetailItemBinding) :
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
            }
        }
    }
}

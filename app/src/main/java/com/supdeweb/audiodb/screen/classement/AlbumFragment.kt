package com.supdeweb.audiodb.screen.classement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.databinding.FragmentAlbumBinding
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlbumFragment : Fragment() {

    private lateinit var viewModel: AlbumViewModel

    private lateinit var binding: FragmentAlbumBinding

    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, null, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecyclerView()
        observeViewModel()
        initButton()
    }

    private fun initViewModel() {
        context?.let {
            val vmFactory =
                AlbumViewModelFactory(
                    AlbumRepository.getInstance(it)
                )
            viewModel = ViewModelProvider(this, vmFactory)[AlbumViewModel::class.java]
        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            collectStates()
        }
    }

    private suspend fun collectStates() {
        viewModel.albumState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentAlbumPb.visibility = View.GONE
                    binding.fragmentAlbumLlError.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentAlbumPb.visibility = View.GONE
                    binding.fragmentAlbumLlError.visibility = View.VISIBLE
                    binding.fragmentAlbumTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentAlbumPb.visibility = View.VISIBLE
                    binding.fragmentAlbumLlError.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentAlbumPb.visibility = View.GONE
                    binding.fragmentAlbumLlError.visibility = View.GONE
                    adapter.submitList(it.albums)
                }
            }
        }
    }

    private fun initButton() {
        binding.fragmentAlbumBtError.setOnClickListener {
            viewModel.getTrendingAlbums()
        }
    }

    private fun initRecyclerView() {
        adapter = AlbumAdapter()
        binding.fragmentAlbumRv.adapter = adapter
        binding.fragmentAlbumRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
}
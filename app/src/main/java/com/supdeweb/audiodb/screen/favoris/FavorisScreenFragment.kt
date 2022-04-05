package com.supdeweb.audiodb.screen.favoris

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
import com.supdeweb.audiodb.screen.StateEnum
import com.supdeweb.audiodb.databinding.FragmentFavorisBinding
import com.supdeweb.audiodb.features.GetFavoriteAlbumsFlow
import com.supdeweb.audiodb.features.GetArtisteFavorisFlow
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.screen.search.adapter.AllRecyclerViewSearchAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavorisScreenFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel

    private lateinit var binding: FragmentFavorisBinding

    private lateinit var adapter: AllRecyclerViewSearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favoris, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        observeViewModel()
        initRecyclerView()
    }


    private fun initViewModel() {
        val getFavoriteAlbumsFlow =
            GetFavoriteAlbumsFlow(AlbumRepository.getInstance(requireContext()))
        val getArtisteFavorisFlow =
            GetArtisteFavorisFlow(ArtistRepository.getInstance(requireContext()))
        val vmFactory =
            FavorisScreenViewModelFactory(
                getFavoriteAlbumsFlow,
                getArtisteFavorisFlow
            )
        viewModel = ViewModelProvider(this, vmFactory)[FavoriteViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            collectStates()
        }
    }

    private suspend fun collectStates() {
        lifecycleScope.launch {
            onAlbumStateChanged()
        }
        lifecycleScope.launch {
            onArtistStateChanged()
        }
    }

    private suspend fun onAlbumStateChanged() {
        viewModel.albumState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.LOADING -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.VISIBLE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                    if (it.albums.isNullOrEmpty()) {
                        binding.fragmentFavoriteTvEmptyList.visibility = View.VISIBLE
                    } else {
                        binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                        adapter.items += listOf("Albums")
                        adapter.items += it.albums
                    }
                }
            }
        }
    }

    private suspend fun onArtistStateChanged() {
        viewModel.artistState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.LOADING -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.VISIBLE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentFavoritePbAlbums.visibility = View.GONE
                    binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                    if (it.artists.isNullOrEmpty()) {
                        binding.fragmentFavoriteTvEmptyList.visibility = View.VISIBLE
                    } else {
                        binding.fragmentFavoriteTvEmptyList.visibility = View.GONE
                        adapter.items += listOf("Artistes")
                        adapter.items += it.artists
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        adapter = AllRecyclerViewSearchAdapter()
        binding.fragmentSearchRv.adapter = adapter
        binding.fragmentSearchRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

}
package com.supdeweb.audiodb.screen.details.artiste

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.screen.StateEnum
import com.supdeweb.audiodb.databinding.FragmentArtistDetailBinding
import com.supdeweb.audiodb.features.GetAlbumsWithArtistFlow
import com.supdeweb.audiodb.features.GetArtisteDetailFlow
import com.supdeweb.audiodb.features.UpdateArtisteFavorisFlow
import com.supdeweb.audiodb.features.GetTopTitresWithArtisteFlow
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.screen.details.album.TitreScreenDetailAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ArtistDetaiScreenFragment : Fragment() {

    private lateinit var viewModel: ArtistDetailViewModel

    private lateinit var binding: FragmentArtistDetailBinding

    private lateinit var adapterTitre: TitreScreenDetailAdapter

    private lateinit var adapterAlbums: ArtistDetailWithAlbumAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_detail, null, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val artistId = arguments?.getString(ARTISTE_ID)
            ?: throw IllegalAccessException("No artistId value pass")

        initViewModel(artistId)
        myViewModel()
        myButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_favoris, menu)

        binding.fragmentArtistDetailTb.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuDetail_item_favorite -> {
                    favClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun initViewModel(artistId: String) {
        adapterTitre = TitreScreenDetailAdapter()
        binding.fragmentArtistDetailRvTracks.adapter = adapterTitre
        binding.fragmentArtistDetailRvTracks.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapterAlbums = ArtistDetailWithAlbumAdapter()
        binding.fragmentArtistDetailRvAlbums.adapter = adapterAlbums
        binding.fragmentArtistDetailRvAlbums.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val getArtisteDetailFlow =
            GetArtisteDetailFlow(ArtistRepository.getInstance(requireContext()))
        val getAlbumsWithArtistFlow =
            GetAlbumsWithArtistFlow(AlbumRepository.getInstance(requireContext()))
        val getTopTitresWithArtisteFlow =
            GetTopTitresWithArtisteFlow(
                TitreRepository.getInstance(requireContext()),
                ArtistRepository.getInstance(requireContext()),
            )
        val updateArtisteFavorisFlow =
            UpdateArtisteFavorisFlow(ArtistRepository.getInstance(requireContext()))
        val vmFactory =
            ArtistDetaiScreenViewModelFactory(
                artistId,
                getArtisteDetailFlow,
                getAlbumsWithArtistFlow,
                getTopTitresWithArtisteFlow,
                updateArtisteFavorisFlow
            )
        viewModel = ViewModelProvider(this, vmFactory)[ArtistDetailViewModel::class.java]

    }

    private fun myViewModel() {
        lifecycleScope.launchWhenStarted {
            collectStates()
        }
    }

    private fun collectStates() {
        lifecycleScope.launch {
            stateArtiste()
        }
        lifecycleScope.launch {
            stateAlbums()
        }
        lifecycleScope.launch {
            stateTitres()
        }
        lifecycleScope.launch {
            viewModel.errorMessageState().collect { errorMessage ->
                errorMessage?.let {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun stateArtiste() {
        viewModel.artistState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailClHeader.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailClHeader.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.VISIBLE
                    binding.fragmentArtistDetailTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentArtistDetailClHeader.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailPb.visibility = View.VISIBLE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailClHeader.visibility = View.VISIBLE
                    binding.fragmentArtistDetailClContent.visibility = View.VISIBLE

                    binding.fragmentArtistDetailTvArtistDesc.text = it.artist?.description
                    binding.fragmentArtistDetailTvArtistName.text = it.artist?.name
                    binding.fragmentArtistDetailTvArtistCountry.text =
                        "${it.artist?.country ?: ""} ${it.artist?.genre ?: ""}"
                    it.artist?.imageUrl?.let { url -> setBackground(url) }
                    it.artist?.isFavorite?.let { isFav -> initFavoriteIcon(isFav) }
                }
            }
        }
    }

    private suspend fun stateAlbums() {
        viewModel.albumsState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.VISIBLE
                    binding.fragmentArtistDetailTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentArtistDetailClContent.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailPb.visibility = View.VISIBLE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    binding.fragmentArtistDetailLlError.visibility = View.GONE
                    binding.fragmentArtistDetailClContent.visibility = View.VISIBLE

                    binding.fragmentArtistDetailTvAlbums.text = "Albums"

                    it.albums?.let { albums -> adapterAlbums.submitList(it.albums) }
                }
            }
        }
    }

    private suspend fun stateTitres() {
        viewModel.tracksState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentArtistDetailLlTracks.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentArtistDetailLlTracks.visibility = View.GONE
                }
                StateEnum.LOADING -> {
                    binding.fragmentArtistDetailLlTracks.visibility = View.GONE
                    binding.fragmentArtistDetailPb.visibility = View.VISIBLE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentArtistDetailPb.visibility = View.GONE
                    if (it.titres.isNullOrEmpty()) {
                        binding.fragmentArtistDetailLlTracks.visibility =
                            View.GONE
                    } else {
                        binding.fragmentArtistDetailLlTracks.visibility =
                            View.VISIBLE
                        adapterTitre.submitList(it.titres)
                    }
                }
            }
        }
    }

    private fun myButtons() {
        binding.fragmentArtistDetailTb.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.fragmentArtistDetailBtError.setOnClickListener {
            viewModel.refreshData()
        }
    }

    private fun setBackground(imageUrl: String) {
        val requestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_foreground)
        Glide.with(binding.fragmentArtistDetailIvBackground)
            .setDefaultRequestOptions(requestOptions)
            .load(imageUrl)
            .into(binding.fragmentArtistDetailIvBackground)
    }

    private fun setFavoriteIcon(id: Int) {
        binding.fragmentArtistDetailTb.menu.findItem(R.id.menuDetail_item_favorite)?.icon =
            ContextCompat.getDrawable(requireContext(), id)
    }

    private fun initFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            setFavoriteIcon(R.drawable.ic_baseline_star_24)
        } else setFavoriteIcon(R.drawable.ic_baseline_star_border_24)
    }


    private fun favClick() {
        lifecycleScope.launch {
            val isFav = viewModel.artistState().first().artist?.isFavorite ?: false
            viewModel.updateFavoriteArtist(!isFav)
            initFavoriteIcon(isFav)
        }
    }

    companion object {
        const val ARTISTE_ID = "ARTISTE_ID"
    }
}
package com.supdeweb.audiodb.screen.details.album

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.databinding.FragmentAlbumDetailBinding
import com.supdeweb.audiodb.features.GetAlbumDetailFlow
import com.supdeweb.audiodb.features.UpdateAlbumFavoris
import com.supdeweb.audiodb.features.GetTitresWithAlbumFlow
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlbumDetailScreenFragment : Fragment() {

    private lateinit var viewModel: AlbumDetailViewModel

    private lateinit var binding: FragmentAlbumDetailBinding

    private lateinit var adapter: TitreScreenDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail, null, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumId = arguments?.getString(ALBUM_ID)
            ?: throw IllegalAccessException("No albumId value pass")

        initViewModel(albumId)
        observeViewModel()
        initButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_favoris, menu)

        binding.fragmentAlbumDetailTb.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuDetail_item_favorite -> {
                    onUserClickOnFavorite()
                    true
                }
                else -> false
            }
        }
    }

    private fun initViewModel(albumId: String) {
        adapter = TitreScreenDetailAdapter()
        binding.fragmentAlbumDetailRvTracks.adapter = adapter
        binding.fragmentAlbumDetailRvTracks.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val getTitresWithAlbumFlow =
            GetTitresWithAlbumFlow(TitreRepository.getInstance(requireContext()))
        val getAlbumDetailFlow =
            GetAlbumDetailFlow(AlbumRepository.getInstance(requireContext()))
        val updateAlbumFavoris =
            UpdateAlbumFavoris(AlbumRepository.getInstance(requireContext()))

        val vmFactory =
            AlbumDetailScreenViewModelFactory(
                albumId,
                getTitresWithAlbumFlow,
                getAlbumDetailFlow,
                updateAlbumFavoris,
            )
        viewModel = ViewModelProvider(this, vmFactory)[AlbumDetailViewModel::class.java]
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
            onTrackStateChanged()
        }
        lifecycleScope.launch {
            viewModel.errorMessageState().collect { errorMessage ->
                errorMessage?.let {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun onAlbumStateChanged() {
        viewModel.albumState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailClHeader.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                    binding.fragmentAlbumDetailClHeader.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.VISIBLE
                    binding.fragmentAlbumDetailTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentAlbumDetailClHeader.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailPb.visibility = View.VISIBLE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailClHeader.visibility = View.VISIBLE
                    binding.fragmentAlbumDetailLlMark.visibility =
                        if (it.album?.score != null || it.album?.scoreVotes != null) View.VISIBLE
                        else View.GONE
                    binding.fragmentAlbumDetailTvAlbumDesc.visibility =
                        if (it.album?.description != null) View.VISIBLE
                        else View.GONE

                    binding.fragmentAlbumDetailTvTitle.text = it.album?.albumName
                    binding.fragmentAlbumDetailTvMark.text = it.album?.score.toString()
                    binding.fragmentAlbumDetailTvVote.text = "${it.album?.scoreVotes} votes"
                    binding.fragmentAlbumDetailTvAlbumDesc.text = it.album?.description
                    it.album?.imageUrl?.let { url ->
                        initHeaderBackground(url)
                        initHeaderImage(url)
                    }
                    it.album?.isFavorite?.let { isFav -> initFavoriteIcon(isFav) }
                }
            }
        }
    }


    private suspend fun onTrackStateChanged() {
        viewModel.trackListState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentAlbumDetailClContent.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentAlbumDetailClContent.visibility = View.GONE
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.VISIBLE
                    binding.fragmentAlbumDetailTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentAlbumDetailClContent.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailPb.visibility = View.VISIBLE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentAlbumDetailPb.visibility = View.GONE
                    binding.fragmentAlbumDetailLlError.visibility = View.GONE
                    binding.fragmentAlbumDetailClContent.visibility = View.VISIBLE
                    binding.fragmentAlbumDetailTvTrackNumber.text = "Titres"
                    adapter.submitList(it.titres)
                }
            }
        }
    }

    private fun initButtons() {
        binding.fragmentAlbumDetailTb.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.fragmentAlbumDetailBtError.setOnClickListener {
            viewModel.refreshData()
        }
    }

    private fun initHeaderBackground(imageUrl: String) {
        val requestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_foreground)
        Glide.with(binding.fragmentAlbumDetailIvBackground)
            .setDefaultRequestOptions(requestOptions)
            .load(imageUrl)
            .into(binding.fragmentAlbumDetailIvBackground)
    }

    private fun initHeaderImage(imageUrl: String) {
        val radius = binding.root.context.resources.getDimensionPixelSize(R.dimen.radius_16dp)
        val requestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_foreground)
        Glide.with(binding.fragmentAlbumDetailIvThumb)
            .setDefaultRequestOptions(requestOptions)
            .load(imageUrl)
            .transform(RoundedCorners(radius))
            .into(binding.fragmentAlbumDetailIvThumb)
    }

    private fun setFavoriteIcon(id: Int) {
        binding.fragmentAlbumDetailTb.menu.findItem(R.id.menuDetail_item_favorite)?.icon =
            ContextCompat.getDrawable(requireContext(), id)
    }

    private fun initFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            setFavoriteIcon(R.drawable.ic_baseline_star_24)
        } else setFavoriteIcon(R.drawable.ic_baseline_star_border_24)
    }

    private fun onUserClickOnFavorite() {
        lifecycleScope.launch {
            val isFav = viewModel.albumState().first().album?.isFavorite ?: false
            viewModel.updateFavoriteAlbum(!isFav)
            initFavoriteIcon(isFav)
        }
    }

    companion object {
        const val ALBUM_ID = "ALBUM_ID"
    }
}
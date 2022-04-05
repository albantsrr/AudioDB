package com.supdeweb.audiodb.screen.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.databinding.FragmentSearchBinding
import com.supdeweb.audiodb.features.GetAlbumsWithArtistNameFlow
import com.supdeweb.audiodb.features.GetArtisteWithNameFlow
import com.supdeweb.audiodb.screen.search.adapter.AllRecyclerViewSearchAdapter
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SearchScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, null, false)

        textListener = object : TextWatcher {
            private var searchFor =
                binding.fragmentSearchVSearch.searchBarEtFill.text.toString()

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText != searchFor) {
                    searchFor = searchText

                    textChangedJob?.cancel()
                    textChangedJob = lifecycleScope.launch(Dispatchers.Main) {
                        delay(500L)
                        if (searchText == searchFor) {
                            adapter.items = emptyList()
                            screenViewModel.observeArtistsByName(searchText)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        observeViewModel()
        initRecyclerView()
        initButtons()
    }

    override fun onResume() {
        super.onResume()
        binding.fragmentSearchVSearch.searchBarEtFill
            .addTextChangedListener(textListener)
    }

    override fun onPause() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        binding.fragmentSearchVSearch.searchBarEtFill
            .removeTextChangedListener(textListener)
        super.onPause()
    }


    override fun onDestroy() {
        textChangedJob?.cancel()
        super.onDestroy()
    }

    /**
     * the view model
     */
    private lateinit var screenViewModel: SearchScreenViewModel

    /**
     * the binding
     */
    private lateinit var binding: FragmentSearchBinding

    /**
     *
     */
    private lateinit var adapter: AllRecyclerViewSearchAdapter

    private var textChangedJob: Job? = null
    private lateinit var textListener: TextWatcher


    /**
     * init [SearchScreenViewModel] with its factories
     */
    private fun initViewModel() {


        // init use cases
        val getArtisteWithNameFlow =
            GetArtisteWithNameFlow(ArtistRepository.getInstance(requireContext()))
        val getAlbumsWithArtistNameFlow =
            GetAlbumsWithArtistNameFlow(AlbumRepository.getInstance(requireContext()))

        val vmFactory =
            SearchScreenViewModelFactory(
                getArtisteWithNameFlow,
                getAlbumsWithArtistNameFlow,
            )
        screenViewModel = ViewModelProvider(this, vmFactory)[SearchScreenViewModel::class.java]
    }

    /**
     * observe value in [SearchScreenViewModel]
     */
    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            collectStates()
        }
    }

    /**
     * collect states
     */
    private suspend fun collectStates() {
        lifecycleScope.launch {
            onArtistsStateChanged()
        }
        lifecycleScope.launch {
            onAlbumsStateChanged()
        }

    }

    private suspend fun onArtistsStateChanged() {
        screenViewModel.artistState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.GONE
                    binding.fragmentSearchRv.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.VISIBLE
                    binding.fragmentSearchTvError.text = it.errorMessage
                    binding.fragmentSearchRv.visibility = View.GONE
                }
                StateEnum.LOADING -> {
                    binding.fragmentSearchPb.visibility = View.VISIBLE
                    binding.fragmentSearchLlError.visibility = View.GONE
                    binding.fragmentSearchRv.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.GONE
                    binding.fragmentSearchRv.visibility = View.VISIBLE
                    if (it.artists.isNullOrEmpty()) {
                        binding.fragmentSearchTvEmptyList.visibility = View.VISIBLE
                    } else {
                        binding.fragmentSearchTvEmptyList.visibility = View.GONE
                        adapter.items += listOf("Artistes")
                        adapter.items += it.artists
                    }
                }
            }
        }
    }

    private suspend fun onAlbumsStateChanged() {
        screenViewModel.albumState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.VISIBLE
                    binding.fragmentSearchTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentSearchPb.visibility = View.VISIBLE
                    binding.fragmentSearchLlError.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentSearchPb.visibility = View.GONE
                    binding.fragmentSearchLlError.visibility = View.GONE
                    if (!it.albums.isNullOrEmpty()) {
                        binding.fragmentSearchTvEmptyList.visibility = View.GONE
                        adapter.items += listOf("Albums")
                        adapter.items += it.albums

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

    private fun initButtons() {
        binding.fragmentSearchBtError.setOnClickListener {
            screenViewModel.observeArtistsByName(binding.fragmentSearchVSearch.searchBarEtFill.text.toString())
        }

        binding.fragmentSearchVSearch.searchBarIvClose.setOnClickListener {
            binding.fragmentSearchVSearch.searchBarEtFill.setText("")
            adapter.items = emptyList()
        }
    }

}
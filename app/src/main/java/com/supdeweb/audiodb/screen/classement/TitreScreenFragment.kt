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
import com.supdeweb.audiodb.databinding.FragmentTitreBinding
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TitreScreenFragment : Fragment() {


    private lateinit var viewModel: TitreScreenViewModel

    private lateinit var binding: FragmentTitreBinding

    private lateinit var adapter: TitreScreenAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_titre, null, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        observeViewModel()
        initButton()
    }

    private fun initViewModel() {
        adapter = TitreScreenAdapter()
        binding.fragmentTitreRv.adapter = adapter
        binding.fragmentTitreRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        context?.let {
            val vmFactory =
                TitreScreenViewModelFactory(
                    TitreRepository.getInstance(it)
                )
            viewModel = ViewModelProvider(this, vmFactory)[TitreScreenViewModel::class.java]
        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            collectStates()
        }
    }

    private suspend fun collectStates() {
        viewModel.trackState().collect {
            when (it.currentStateEnum) {
                StateEnum.IDLE -> {
                    binding.fragmentTitrePb.visibility = View.GONE
                    binding.fragmentTitreLlError.visibility = View.GONE
                }
                StateEnum.ERROR -> {
                    binding.fragmentTitrePb.visibility = View.GONE
                    binding.fragmentTitreLlError.visibility = View.VISIBLE
                    binding.fragmentTitreTvError.text = it.errorMessage
                }
                StateEnum.LOADING -> {
                    binding.fragmentTitrePb.visibility = View.VISIBLE
                    binding.fragmentTitreLlError.visibility = View.GONE
                }
                StateEnum.SUCCESS -> {
                    binding.fragmentTitrePb.visibility = View.GONE
                    binding.fragmentTitreLlError.visibility = View.GONE
                    adapter.submitList(it.titres)
                }
            }
        }
    }

    private fun initButton() {
        binding.fragmentTitreBtError.setOnClickListener {
            viewModel.getTrendingTracks()
        }
    }

}
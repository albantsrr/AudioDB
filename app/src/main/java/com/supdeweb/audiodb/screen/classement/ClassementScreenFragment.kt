package com.supdeweb.audiodb.screen.classement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.supdeweb.audiodb.R
import com.supdeweb.audiodb.databinding.FragmentClassementBinding

class ClassementScreenFragment : Fragment() {

    private lateinit var binding: FragmentClassementBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_classement, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initViewModel()
    }

    private fun initViewModel() {}

    private fun initViewPager() {
        binding.fragmentClassementVp.adapter =
            ClassementScreenAdapter(childFragmentManager, this.lifecycle)

        TabLayoutMediator(
            binding.fragmentClassementTl,
            binding.fragmentClassementVp
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Titres"
                }
                1 -> {
                    tab.text = "Albums"
                }
            }
        }.attach()
    }

}
package com.supdeweb.audiodb.screen.classement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.repository.TitreRepository


class TitreScreenViewModelFactory(
    private val titreRepo: TitreRepository,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TitreScreenViewModel::class.java)) {
            return TitreScreenViewModel(titreRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
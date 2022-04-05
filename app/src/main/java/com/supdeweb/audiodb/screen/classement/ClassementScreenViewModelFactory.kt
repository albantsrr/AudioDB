package com.supdeweb.audiodb.screen.classement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ClassementScreenViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassementScreenViewModel::class.java)) {
            return ClassementScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
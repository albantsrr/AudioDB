package com.supdeweb.audiodb.screen.classement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TitreScreenViewModel(private val titreRepo: TitreRepository) : ViewModel() {
    /**
     *
     */
    private val titresFlow = MutableStateFlow(
        TitreState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun trackState(): Flow<TitreState> {
        return titresFlow.asStateFlow()
    }

    init {
        getTrendingTracks()
    }

    fun getTrendingTracks() {
        titreRepo.getTrendingTracks { res ->
            viewModelScope.launch {
                titresFlow.emit(
                    TitreState(
                        currentStateEnum = StateEnum.LOADING,
                    )
                )
                when (res.status) {
                    Status.SUCCESS -> {
                        titresFlow.emit(
                            TitreState(
                                currentStateEnum = StateEnum.SUCCESS,
                                titres = res.data?.sortedBy { it.chartPlace }
                            )
                        )
                    }
                    Status.ERROR -> {
                        titresFlow.emit(
                            TitreState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = res.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        titresFlow.emit(
                            TitreState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }
}

data class TitreState(
    val currentStateEnum: StateEnum,
    val titres: List<TitreModel>? = null,
    val errorMessage: String? = null,
)

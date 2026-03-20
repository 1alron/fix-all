package io.alron.fixall.presentation.home

import io.alron.fixall.domain.model.Branch

sealed interface HomeState {
    data object Error : HomeState
    data object Loading : HomeState

    data class Content(val branches: List<Branch>) : HomeState
}
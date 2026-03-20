package io.alron.fixall.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.domain.AuthManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}
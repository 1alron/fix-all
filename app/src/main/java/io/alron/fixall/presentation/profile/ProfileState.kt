package io.alron.fixall.presentation.profile

import io.alron.fixall.domain.model.LoyaltyInfo
import io.alron.fixall.domain.model.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val loyaltyInfo: LoyaltyInfo? = null,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, List<String>>? = null,
    val isUpdating: Boolean = false,
    val isUploadingAvatar: Boolean = false
)

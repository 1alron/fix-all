package io.alron.fixall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.ProfileRepository
import io.alron.fixall.presentation.RootNavHost
import io.alron.fixall.presentation.theme.FixAllTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixAllTheme {
                RootNavHost(
                    authManager = authManager,
                    profileRepository = profileRepository
                )
            }
        }
    }
}
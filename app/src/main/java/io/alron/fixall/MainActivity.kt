package io.alron.fixall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.alron.fixall.auth.presentation.components.AuthTopBar
import io.alron.fixall.auth.presentation.login.LoginScreen
import io.alron.fixall.ui.theme.FixAllTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixAllTheme {
                Scaffold(
                    topBar = {
                        AuthTopBar(
                            modifier = Modifier
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
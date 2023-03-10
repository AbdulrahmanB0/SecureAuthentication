package com.practise.secureauthentication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.practise.secureauthentication.presentation.navigation.NavigationSystem
import com.practise.secureauthentication.presentation.screens.destinations.LoginScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.ProfileScreenDestination
import com.practise.secureauthentication.presentation.theme.SecureAuthenticationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureAuthenticationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val startRoute = if (viewModel.uiState.isLoggedIn) {
                            ProfileScreenDestination
                        } else {
                            LoginScreenDestination
                        }
                        NavigationSystem(startRoute)
                    }
                }

            }
        }
    }
}


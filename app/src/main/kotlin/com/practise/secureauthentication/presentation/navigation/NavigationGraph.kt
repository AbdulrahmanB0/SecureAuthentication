package com.practise.secureauthentication.presentation.navigation

import androidx.compose.runtime.Composable
import com.practise.secureauthentication.presentation.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

@Composable
fun NavigationSystem() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}
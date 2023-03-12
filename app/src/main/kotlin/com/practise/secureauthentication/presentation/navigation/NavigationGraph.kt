package com.practise.secureauthentication.presentation.navigation

import androidx.compose.runtime.Composable
import com.practise.secureauthentication.presentation.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.spec.Route

@Composable
fun NavigationSystem(startRoute: Route = NavGraphs.root.startRoute) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        startRoute = startRoute,
    )
}


package com.practise.secureauthentication.presentation.screens.profile

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination()
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState = viewModel.uiState
    Scaffold(
        topBar = {
            ProfileTopBar(
                menuExpanded = uiState.topBarMenuExpanded,
                onSave = { /*TODO*/ },
                onDeleteAllConfirmed = { /*TODO*/  },
                onMenuExpandedChange = viewModel::setMenuExpandedState
            )
        },
    ) {

    }


}
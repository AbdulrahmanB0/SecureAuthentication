package com.practise.secureauthentication.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.practise.secureauthentication.R
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.presentation.screens.SimpleTopAppBar
import com.practise.secureauthentication.presentation.screens.destinations.LoginScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@RootNavGraph(start = true)
@Composable
fun LoginScreen (
    viewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val uiState = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    fun navigateToProfile() {
        navigator.navigate(ProfileScreenDestination) {
            popUpTo(LoginScreenDestination.route) { inclusive = true }
        }
    }

    val activityLauncher = googleSignInLauncher(
        onTokenReceived = { viewModel.verifyTokenOnBackend(it) },
        onDialogDismissed = { viewModel.setSignInResource(RemoteResource.Idle) },
        oneTapClient = viewModel.oneTapClient
    )
    LaunchedEffect(key1 = uiState.signInResource) {
        if(uiState.signInResource is RemoteResource.Success)
            navigateToProfile()
        else if (uiState.signInResource is RemoteResource.Failure)
            snackBarHostState.showSnackbar(context.getString(R.string.something_wrong))

    }

    LaunchedEffect(Unit) {
        launch {
            if(uiState.signedIn)
                viewModel.signInWithGoogle(activityLauncher)
        }
        launch {
            viewModel.messages.collectLatest {
                snackBarHostState.showSnackbar(it.asString(context))
            }
        }
    }

    LaunchedEffect(uiState.connected) {
        if(!uiState.connected)
            snackBarHostState.showSnackbar(
                message = context.getString(R.string.no_internet),
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
    }

    Scaffold(
        topBar = { SimpleTopAppBar(title = stringResource(R.string.app_name)) },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        ScreenContent(
            uiState = uiState,
            paddingValues = paddingValues,
            onSignInClicked = { viewModel.signInWithGoogle(activityLauncher) }
        )
    }

}

@Composable
private fun ScreenContent(
    uiState: LoginViewModel.UiState,
    paddingValues: PaddingValues,
    onSignInClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(120.dp),
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Logo"
        )
        Text(
            text = stringResource(R.string.signin_title),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )
        Text(
            modifier = Modifier
                .alpha(0.6f)
                .padding(bottom = 40.dp, top = 4.dp),
            text = stringResource(R.string.signin_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        GoogleButton(
            primaryText = stringResource(R.string.sign_in_with_google),
            secondaryText = stringResource(R.string.please_wait),
            loadingState = uiState.signInResource is RemoteResource.Loading,
            onClick = {
                if(uiState.connected)
                    onSignInClicked()
            }
        )
    }



}
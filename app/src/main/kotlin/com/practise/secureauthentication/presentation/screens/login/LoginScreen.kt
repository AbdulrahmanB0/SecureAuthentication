package com.practise.secureauthentication.presentation.screens.login

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.practise.secureauthentication.R
import com.practise.secureauthentication.presentation.screens.SimpleTopAppBar
import com.practise.secureauthentication.presentation.screens.destinations.LoginScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination
@RootNavGraph(start = true)
@Composable
fun LoginScreen (
    viewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val uiState = viewModel.uiState
    val errorsSnackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val somethingWrongMsg = stringResource(id = R.string.something_wrong)
    val activityLauncher = googleSignInLauncher(
        onTokenReceived = {
            viewModel.verifyTokenOnBackend(
                tokenId = it,
                onSuccess = {
                    viewModel.resetStates()
                    viewModel.saveSignedInState(true)
                    navigator.navigate(ProfileScreenDestination) {
                        popUpTo(LoginScreenDestination.route) { inclusive = true }
                    }
                },
                onFailure = {
                    viewModel.resetStates()
                    scope.launch {
                        errorsSnackBarHostState.showSnackbar(somethingWrongMsg)
                    }
                }
            )
        },
        onDialogDismissed = viewModel::resetStates,
        oneTapClient = viewModel.oneTapClient
    )

    LaunchedEffect(uiState.signedIn) {
        navigator.navigate(ProfileScreenDestination) {
            popUpTo(LoginScreenDestination.route) { inclusive = true }
        }
    }

    AnimatedVisibility(visible = !uiState.signedIn) {
        ScreenContent(
            viewModel = viewModel,
            coroutineScope = scope,
            snackbarHostState = errorsSnackBarHostState,
            launcher = activityLauncher
        )
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScreenContent(
    viewModel: LoginViewModel,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
) {
    val uiState = viewModel.uiState
    val callerBlockMsg = stringResource(R.string.caller_blocked)
    val noCredentialsMsg = stringResource(R.string.account_not_found)
    val noInternetMsg = stringResource(R.string.no_internet)
    Scaffold(
        topBar = { SimpleTopAppBar(title = stringResource(R.string.app_name)) },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) { paddingValues ->

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
                loadingState = uiState.loading,
                onClick = {
                    if(uiState.connected)
                        viewModel.signInWithGoogle(
                            launcher = launcher,
                            onCallerBlocked = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(callerBlockMsg)
                                }
                            },
                            onNoCredentials = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(noCredentialsMsg)
                                }
                            }
                        )
                    else
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(noInternetMsg)
                        }
                }
            )
        }

    }

}
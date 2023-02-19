package com.practise.secureauthentication.presentation.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.practise.secureauthentication.R
import com.practise.secureauthentication.domain.model.Resource
import com.practise.secureauthentication.presentation.screens.SimpleTopAppBar
import com.practise.secureauthentication.presentation.screens.destinations.LoginScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate

private const val TAG = "LoginScreen"
@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(true)
@Destination
@Composable
fun LoginScreen (
    viewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState = viewModel.uiState
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading by remember(uiState.tokenVerificationResource, uiState.oneTapSignInResource) {
        derivedStateOf {
            uiState.oneTapSignInResource is Resource.Loading ||
                    uiState.oneTapSignInResource is Resource.Success ||
                    uiState.tokenVerificationResource is Resource.Loading
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.messageFlow.conflate().collectLatest {
            Log.d(TAG, "MessageState: $it")
            snackBarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = { SimpleTopAppBar(title = "Secure Authentication") },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = if(uiState.error) colorScheme.errorContainer else colorScheme.primaryContainer,
                contentColor = if(uiState.error) colorScheme.onErrorContainer else colorScheme.onPrimaryContainer,
            )
        } }
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
                loadingState = isLoading,
                onClick = viewModel::signInWithGoogle
            )
            
            DividerWithCenteredText(text = "OR", color = Color.LightGray)

            OutlinedTextField(
                modifier = Modifier.onFocusChanged { viewModel.isEmail(uiState.email) },
                value = uiState.email,
                onValueChange = viewModel::setEmailValue,
                label = { Text(text = stringResource(R.string.email)) },
            )


        }

        StartActivityForSignInWithGoogleResult(
            oneTapClient = viewModel.oneTapClient,
            oneTapSignInResource = uiState.oneTapSignInResource,
            onTokenReceived = viewModel::verifyTokenOnBackend,
            onDialogDismissed = viewModel::resetOneTapState,
            detectBlockedCaller = viewModel::detectBlockedCaller,
            onNoCredentialsFound = { viewModel.onErrorOccurred(context.getString(R.string.account_not_found)) },
            onCallerBlocked = { viewModel.onErrorOccurred(context.getString(R.string.caller_blocked)) }
        )

        TokenVerificationStateHandler(
            tokenVerificationResource= uiState.tokenVerificationResource,
            onSuccess = {
                viewModel.setErrorState(false)
                viewModel.saveSignedInState(true)
                navigator.navigate(ProfileScreenDestination) {
                    popUpTo(LoginScreenDestination.route) { inclusive = true }
                }
            },
            onFailure = { viewModel.onErrorOccurred("Connection to Ktor Backend failed!") }
        )
    }

}
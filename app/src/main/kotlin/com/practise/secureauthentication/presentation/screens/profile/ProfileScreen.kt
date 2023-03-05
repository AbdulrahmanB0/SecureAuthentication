package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practise.secureauthentication.R
import com.practise.secureauthentication.data.RemoteResource
import com.practise.secureauthentication.data.network.ApiErrors
import com.practise.secureauthentication.presentation.screens.destinations.DeleteAccountDialogDestination
import com.practise.secureauthentication.presentation.screens.destinations.LoginScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.ProfileScreenDestination
import com.practise.secureauthentication.presentation.screens.destinations.SignOutDialogDestination
import com.practise.secureauthentication.presentation.screens.login.GoogleButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest

@Destination
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    deleteAccountResult: ResultRecipient<DeleteAccountDialogDestination,Boolean>,
    signOutResult: ResultRecipient<SignOutDialogDestination, Boolean>,
) {

    val uiState = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val inBackgroundLoading by remember(uiState.userUpdate, uiState.userDelete, uiState.userSignOut) {
        with(uiState) {
            mutableStateOf(userUpdate is RemoteResource.Loading || userDelete is RemoteResource.Loading || userSignOut is RemoteResource.Loading)
        }
    }

    fun navigateBackToLogin() {
        navigator.navigate(LoginScreenDestination) {
            popUpTo(ProfileScreenDestination.route) { inclusive = true }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.informMessages.collectLatest {
            snackbarHostState.showSnackbar(it.asString(context))
        }
    }

    LaunchedEffect(key1 = uiState.connected) {
        if(!uiState.connected) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.no_internet),
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    deleteAccountResult.onNavResult {
        if (it is NavResult.Value && it.value) {
            viewModel.deleteAccount { navigateBackToLogin() }
        }
    }

    signOutResult.onNavResult {
        if (it is NavResult.Value && it.value) {
            viewModel.signOut { navigateBackToLogin() }
        }
    }

    LaunchedEffect(key1 = uiState.userUpdate) {
        if(uiState.userUpdate is RemoteResource.Success) {
            snackbarHostState.showSnackbar(context.getString(R.string.update_info_success))
        }
        else if(uiState.userUpdate is RemoteResource.Failure) {
            with(snackbarHostState) {
                when (uiState.userUpdate.error) {
                    ApiErrors.UNAUTHORIZED -> {
                        val result = showSnackbar(
                            message = context.getString(R.string.session_timedout),
                            actionLabel = context.getString(R.string.sign_in),
                            duration = SnackbarDuration.Indefinite
                        )
                        if(result == SnackbarResult.ActionPerformed)
                            navigateBackToLogin()
                        Unit

                    }

                    ApiErrors.NO_INTERNET -> showSnackbar(context.getString(R.string.no_internet))
                    ApiErrors.INTERNAL -> showSnackbar(context.getString(R.string.service_unavailable))
                    else -> showSnackbar(context.getString(R.string.unknown_error))
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                ProfileTopBar(
                    menuExpanded = uiState.topBarMenuExpanded,
                    onSave = { viewModel.updateUserInfo() },
                    onDeleteAccountClicked = { navigator.navigate(DeleteAccountDialogDestination) },
                    onMenuExpandedChange = viewModel::setMenuExpandedState
                )
                AnimatedVisibility(visible = inBackgroundLoading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        },
    ) {
        ScreenContent(
            uiState = uiState,
            navigator = navigator,
            onFirstNameChanged = viewModel::setFirstName,
            onLastNameChanged = viewModel::setLastName,
            refreshContent = viewModel::refresh,
            paddingValues = it,
            navigateToLogin ={ navigateBackToLogin() }
        )
    }
}


@Composable
private fun ScreenContent(
    uiState: ProfileViewModel.UiState,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navigator: DestinationsNavigator,
    navigateToLogin: () -> Unit,
    refreshContent: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        item {
            Box(modifier = Modifier.fillMaxSize()) {
                uiState.userInfo.let {
                    when (it) {
                        is RemoteResource.Loading -> {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }

                        is RemoteResource.Success-> {
                            val user = it.data
                            CentralContent(
                                modifier = Modifier.align(Alignment.Center),
                                profilePhotoUrl = user.profilePhoto.url,
                                firstName = uiState.firstNameText,
                                firstNameError = uiState.firstNameError,
                                lastName = uiState.lastNameText,
                                lastNameError = uiState.lastNameError,
                                emailAddress = user.emailAddress.value,
                                onFirstNameChanged = onFirstNameChanged,
                                onLastNameChanged = onLastNameChanged,
                                onSignOutClicked = { navigator.navigate(SignOutDialogDestination) }
                            )
                        }

                        is RemoteResource.Failure -> {
                            it.data?.let { user ->
                                // Load the user info from the cache if it exists
                                CentralContent(
                                    modifier = Modifier.align(Alignment.Center),
                                    profilePhotoUrl = user.profilePhoto.url,
                                    firstName = uiState.firstNameText,
                                    firstNameError = uiState.firstNameError,
                                    lastName = uiState.lastNameText,
                                    lastNameError = uiState.lastNameError,
                                    emailAddress = user.emailAddress.value,
                                    onFirstNameChanged = onFirstNameChanged,
                                    onLastNameChanged = onLastNameChanged,
                                    onSignOutClicked = { navigator.navigate(SignOutDialogDestination) }
                                )
                            } ?:
                            when (it.error) {
                                ApiErrors.UNAUTHORIZED -> LaunchedEffect(Unit) { navigateToLogin() }
                                else -> UnknownError(
                                    message = stringResource(id = R.string.unknown_error),
                                    onRetryClick = refreshContent
                                )
                            }
                        }

                        else -> {}

                    }

                }
            }

        }
    }

}

@Composable
fun CentralContent(
    modifier: Modifier = Modifier,
    profilePhotoUrl: String,
    firstName: String,
    firstNameError: Boolean,
    lastName: String,
    lastNameError: Boolean,
    emailAddress: String,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSignOutClicked: () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier
                .padding(40.dp)
                .size(150.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(profilePhotoUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_placeholder)
                .build(),
            contentDescription = stringResource(id = R.string.profile_photo),
        )

        OutlinedTextField(
            value = firstName,
            isError = firstNameError,
            onValueChange = onFirstNameChanged,
            label = { Text(text = stringResource(id = R.string.first_name)) },
        )

        OutlinedTextField(
            value = lastName,
            isError = lastNameError,
            onValueChange = onLastNameChanged,
            label = { Text(text = stringResource(id = R.string.last_name)) },
        )

        OutlinedTextField(
            value = emailAddress,
            onValueChange = { },
            enabled = false,
            label = { Text(text = stringResource(id = R.string.email)) }
        )

        GoogleButton(
            modifier = Modifier
                .width(TextFieldDefaults.MinWidth)
                .padding(top = 16.dp),
            primaryText = stringResource(id = R.string.sign_out),
            secondaryText = stringResource(id = R.string.sign_out),
            onClick = onSignOutClicked
        )
    }
}

@Composable
fun UnknownError(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp),
            imageVector = Icons.Rounded.Warning,
            contentDescription = stringResource(R.string.warning)
        )
        Text(text = message, style = MaterialTheme.typography.titleLarge)
        Button(
            modifier = Modifier
                .width(TextFieldDefaults.MinWidth)
                .padding(top = 16.dp),
            onClick = onRetryClick
        ) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}
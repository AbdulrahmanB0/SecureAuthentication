package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.practise.secureauthentication.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Composable
fun ConfirmationDialog(
    message: String,
    title: String? = null,
    icon: ImageVector? = null,
    confirmButtonText: String = stringResource(id = R.string.yes),
    dismissButtonText: String = stringResource(id = R.string.no),
    onResultReceived: (Boolean) -> Unit,
) {
    AlertDialog(
        title = title?.let { { Text(text = it) } },
        text = { Text(text = message) },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        confirmButton = {
            TextButton(onClick = { onResultReceived(true) }) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onResultReceived(false) }) {
                Text(text = dismissButtonText)
            }
        },
        onDismissRequest = { onResultReceived(false) }
    )
}

@Composable
@Destination(style = DestinationStyle.Dialog::class)
fun DeleteAccountDialog(resultBackNavigator: ResultBackNavigator<Boolean>) {
    ConfirmationDialog(
        message = stringResource(id = R.string.delete_account_msg),
        title = stringResource(id = R.string.delete_account_title),
        icon = Icons.Default.Warning,
        onResultReceived = { resultBackNavigator.navigateBack(result = it) },
    )
}

@Composable
@Destination(style = DestinationStyle.Dialog::class)
fun SignOutDialog(resultBackNavigator: ResultBackNavigator<Boolean>) {
    ConfirmationDialog(
        message = stringResource(id = R.string.sign_out_msg),
        title = stringResource(id = R.string.sign_out_title),
        onResultReceived = { resultBackNavigator.navigateBack(result = it) },
    )
}
package com.practise.secureauthentication.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import domain.model.TokenId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(title: String) {
    TopAppBar(title = {
            Text(text = title)
        },
    )
}


@Suppress("unused")
fun startActivityForSharingText(
    tokenId: TokenId,
    context: Context
) {
    val intentSender = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, tokenId.value)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(intentSender, "This is sharing title demo")
    context.startActivity(shareIntent)
}

@Composable
fun ErrorSnackbar(it: SnackbarData) {
    Snackbar(
        snackbarData = it,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
}
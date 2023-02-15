package com.practise.secureauthentication.presentation.screens.profile

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.practise.secureauthentication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    menuExpanded: Boolean,
    onSave: () -> Unit,
    onDeleteAllConfirmed: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.profile)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
        ),
        actions = {
            SaveAction(onSave)
            DeleteAllAction(
                menuExpanded = menuExpanded,
                onDeleteAllConfirmed = onDeleteAllConfirmed,
                onMenuExpandedChange = onMenuExpandedChange
            )
        }
    )
}

@Composable
fun SaveAction(onSave: () -> Unit) {
    IconButton(onClick = onSave) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = "Save Icon",
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun DeleteAllAction(
    menuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onDeleteAllConfirmed: () -> Unit
) {
    IconButton(onClick = { onMenuExpandedChange(true) }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu),
            contentDescription = "Vertical Menu",
            tint = MaterialTheme.colorScheme.onSecondary
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { onMenuExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.delete_account)) },
                onClick = {
                    onMenuExpandedChange(false)
                    onDeleteAllConfirmed()
                }
            )
        }
    }
}

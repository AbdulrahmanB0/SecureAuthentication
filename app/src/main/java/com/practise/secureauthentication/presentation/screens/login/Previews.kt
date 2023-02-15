package com.practise.secureauthentication.presentation.screens.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun DividerWithCenteredTextPreview() {
    DividerWithCenteredText(
        modifier = Modifier.fillMaxWidth(),
        text = "OR"
    )
}
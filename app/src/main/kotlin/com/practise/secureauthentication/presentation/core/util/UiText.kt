package com.practise.secureauthentication.presentation.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class FixedString(val string: String): UiText
    class StringResource(@StringRes val stringRes: Int, vararg val args: Any): UiText


    @Composable
    fun asString(): String = when(this) {
        is FixedString -> string
        is StringResource -> stringResource(id = stringRes, *args)
    }

    fun asString(context: Context): String = when(this) {
        is FixedString -> string
        is StringResource -> context.getString(stringRes, *args)
    }
}
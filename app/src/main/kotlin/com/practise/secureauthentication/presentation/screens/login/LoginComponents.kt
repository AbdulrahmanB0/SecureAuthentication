package com.practise.secureauthentication.presentation.screens.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.SignInClient
import com.practise.secureauthentication.R
import com.practise.secureauthentication.presentation.theme.LoadingBlue
import domain.model.TokenId

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loadingState: Boolean = false,
    primaryText: String,
    secondaryText: String,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit
) {

    val buttonText = rememberSaveable(loadingState) {
        if(loadingState)
            secondaryText
        else primaryText
    }

    OutlinedButton(
        modifier = modifier
            .height(40.dp)
            .animateContentSize(tween(300)),
        onClick = {
            if(!loadingState)
                onClick()
        },
        border = BorderStroke(1.dp, color = Color.LightGray),
        contentPadding = PaddingValues(8.dp),
        shape = RoundedCornerShape(4.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google icon",
            modifier = Modifier
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = buttonText,
        )
        if(loadingState)
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.LoadingBlue,
                strokeWidth = 2.dp
            )
    }
}
@Composable
@Suppress("unused")
fun DividerWithCenteredText(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
    text: String,
    textStyle: TextStyle = TextStyle.Default
) {
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(0.4f),
            thickness = dividerThickness,
            color = color
        )
        Text(
            modifier = Modifier
                .padding(10.dp)
                .weight(.1f),
            text = text,
            style = textStyle,
            color = color
        )
        Divider(
            modifier = Modifier.weight(.4f),
            thickness = dividerThickness,
            color = color
        )
    }
}

@Composable
fun googleSignInLauncher(
    onTokenReceived: (TokenId) -> Unit,
    onDialogDismissed: () -> Unit,
    oneTapClient: SignInClient,
): ActivityResultLauncher<IntentSenderRequest> {

    val tag = "GoogleSignInLauncher"
    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val credentials = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
            val tokenId = credentials.googleIdToken ?: return@rememberLauncherForActivityResult
            onTokenReceived(TokenId(tokenId))
        } else {
            Log.i(tag, "Login cancelled")
            onDialogDismissed()
        }
    }
    return activityLauncher
}
